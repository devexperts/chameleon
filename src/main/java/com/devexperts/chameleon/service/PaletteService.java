package com.devexperts.chameleon.service;

/*-
 * #%L
 * Chameleon. Color Palette Management Tool
 * %%
 * Copyright (C) 2016 - 2018 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.devexperts.chameleon.converter.CommitViewCellConverter;
import com.devexperts.chameleon.converter.PaletteViewCellConverter;
import com.devexperts.chameleon.dto.CommitDTO;
import com.devexperts.chameleon.dto.CommitViewCellDTO;
import com.devexperts.chameleon.dto.PaletteDTO;
import com.devexperts.chameleon.dto.PaletteDiffViewDTO;
import com.devexperts.chameleon.dto.PaletteViewCellDTO;
import com.devexperts.chameleon.dto.PaletteViewDTO;
import com.devexperts.chameleon.dto.SaveVariableDTO;
import com.devexperts.chameleon.dto.VariableDTO;
import com.devexperts.chameleon.dto.VariableSnapshotDTO;
import com.devexperts.chameleon.entity.PaletteEntity;
import com.devexperts.chameleon.entity.VariableSnapshotEntity;
import com.devexperts.chameleon.exception.EntityNotFoundCustomException;
import com.devexperts.chameleon.repository.PaletteRepository;
import com.devexperts.chameleon.repository.VariableRepository;
import com.devexperts.chameleon.repository.VariableSnapshotRepository;
import com.devexperts.chameleon.util.PreconditionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.devexperts.chameleon.util.ConverterUtils.opacityToPercent;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * This class is used as service for handle actions with palette
 *
 */
@Service
@Transactional
public class PaletteService {

    private final PaletteRepository repository;

    private final PaletteViewCellConverter viewCellConverter;

    private final CommitViewCellConverter commitViewCellConverter;

    private final VariableSnapshotRepository variableSnapshotRepository;

    private final CommitService commitService;

    @Autowired
    public PaletteService(PaletteRepository repository,
                          PaletteViewCellConverter viewCellConverter,
                          CommitViewCellConverter commitViewCellConverter,
                          VariableSnapshotRepository variableSnapshotRepository,
                          CommitService commitService) {
        this.repository = repository;
        this.viewCellConverter = viewCellConverter;
        this.commitViewCellConverter = commitViewCellConverter;
        this.variableSnapshotRepository = variableSnapshotRepository;
        this.commitService = commitService;
    }

    public List<PaletteDTO> findAll() {
        List<PaletteEntity> entities = repository.findAllByActiveTrueOrderByNameAsc();
        PreconditionUtils.checkIsAnyNotNull(entities);
        return convertToDTO(entities);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public PaletteDTO getById(Long id) {
        PaletteEntity entity = repository.findOne(id);

        PreconditionUtils.checkNotFound(entity, id);

        return convertToDTO(entity);
    }

    public List<PaletteEntity> getByIds(List<Long> ids) {
        List<PaletteEntity> entities = repository.findAllByIdInAndActiveTrue(ids);
        checkIsAnyEntityNotFound(ids, entities);

        return entities;
    }

    public PaletteEntity getPalette(Long id) {
        PaletteEntity entity = repository.findOne(id);
        PreconditionUtils.checkNotFound(entity, id);
        return entity;
    }

    public PaletteEntity getPalette(String name) {
        PaletteEntity entity = repository.findFirstByActiveTrueAndNameIgnoreCase(name);
        PreconditionUtils.checkNotFound(entity, name);
        return entity;
    }

    public boolean isNameExist(String name) {
        return repository.findFirstByActiveTrueAndNameIgnoreCase(name) != null;
    }

    public Long save(PaletteDTO dto) {
        PaletteEntity allByName = repository.findAllByName(dto.getName());
        if (Objects.isNull(allByName)) {
            return repository.save(convertToEntity(dto)).getId();
        } else {
            return null;
        }
    }

    public PaletteEntity convertToEntity(PaletteDTO dto) {
        PaletteEntity entity;
        if (dto.getId() == null) {
            entity = new PaletteEntity(dto.getName());
        } else {
            entity = repository.getOne(dto.getId());
            PreconditionUtils.checkNotFound(entity, dto.getId());
        }

        entity.setName(dto.getName());

        return entity;
    }

    public List<PaletteEntity> convertToEntity(List<PaletteDTO> dto) {
        return dto.stream()
                .map(this::convertToEntity).
                        collect(toList());
    }

    public PaletteViewDTO buildVariableView(List<Long> paletteIds) {
        List<PaletteViewCellDTO> cells = getUnsortedCells(paletteIds);
        cells.sort(Comparator.comparing(PaletteViewCellDTO::getVariableName).thenComparing(PaletteViewCellDTO::getPaletteName));
        return new PaletteViewDTO(
                getPaletteViewColumns(paletteIds.size(), cells),
                getPaletteViewRows(paletteIds.size(), cells)
        );
    }

    /**
     * Method which replace getPaletteVariableViewMethod.
     *
     * First of all we gets all unique variables from snapshots. Then we are cross-join this variables with palettes.
     * It is necessary because if palette doesn't have variable which exists in list it must be set on empty
     * (with color and opacity equal to null).
     * After this action we have collection of (variableId, paletteId) which is already fully constructed table of:
     *             | palette1  | palette2  | palette3
     *    ----------+------------+------------+----------
     *    variable1 |           |          |
     *    variable2 |           |          |
     *
     * So, rawCrossJoinedCellsStream - it is that collection.
     *
     * Then, we gets snapshots for this table. And in the end we are merging first empty collection with contented
     * snapshots.
     *
     * In the end we gets:
     *
     *              | palette1  | palette2  | palette3
     *    ----------+------------+------------+----------
     *    variable1 | snapshot1 | snapshot2 | snapshot3
     *    variable2 | snapshot4 | snapshot5 | snapshot6
     *
     * Note: final collection is not sorted as we need!
     *
     * @see PaletteRepository
     * @param paletteIds
     * @return unsorted list of all cells.
     */

    public List<PaletteViewCellDTO> getUnsortedCells(List<Long> paletteIds) {
        List<Long> lastCommitIds = commitService.getLastCommitIdsByPaletteIds(paletteIds);

        List<PaletteEntity> palettes = repository.findAllByIdInAndActiveTrue(paletteIds);

        List<VariableSnapshotEntity> snapshots = variableSnapshotRepository.findAllByCommitEntityIdIn(lastCommitIds);

        Stream<PaletteViewCellDTO> snapshotCellsStream = snapshots.stream().map(this::mapSnapshotOnCell);
        Stream<PaletteViewCellDTO> rawCrossJoinedCellsStream = palettes.stream()
                .flatMap(palette ->
                        snapshots.stream()
                                .map(VariableSnapshotEntity::getVariableEntity)
                                .distinct()
                                .map(variable ->
                                        PaletteViewCellDTO
                                                .builder()
                                                .variableId(variable.getId())
                                                .paletteId(palette.getId())
                                                .paletteName(palette.getName())
                                                .variableName(variable.getName())
                                                .variableUsage(variable.getUsage())
                                                .build()));

        Map<SnapshotId, PaletteViewCellDTO> mergedMapOfConstructedAndEmptyCells = Stream.concat(snapshotCellsStream, rawCrossJoinedCellsStream)
                .collect(toMap(this::mapCellOnId, v -> v, (firstCell, secondCell) ->
                        firstCell.getVariableSnapshotId() != null ? firstCell : secondCell));

        return new ArrayList<>(mergedMapOfConstructedAndEmptyCells.values());
    }

    private PaletteViewCellDTO mapSnapshotOnCell(VariableSnapshotEntity snapshot) {
        return PaletteViewCellDTO
                .builder()
                .variableId(snapshot.getVariableEntity().getId())
                .paletteId(snapshot.getPaletteEntity().getId())
                .paletteName(snapshot.getPaletteEntity().getName())
                .color(snapshot.getColor())
                .opacity(snapshot.getOpacity())
                .variableUsage(snapshot.getVariableEntity().getUsage())
                .variableName(snapshot.getVariableEntity().getName())
                .variableSnapshotId(snapshot.getId())
                .build();
    }

    private SnapshotId mapCellOnId(PaletteViewCellDTO snapshot) {
        return new SnapshotId(
                snapshot.getVariableId(),
                snapshot.getPaletteId()
        );
    }

    public void setPaletteNotActive(long id) {
        PaletteEntity one = repository.getOne(id);
        if (Objects.nonNull(one)) {
            one.setActive(false);
        }
    }

    public PaletteDiffViewDTO buildDiffView(List<Long> selectedCommitIds, List<Long> diffVariableId) {

        List<CommitViewCellDTO> cells = repository.getPaletteDiffView(selectedCommitIds, diffVariableId).stream()
                .map(commitViewCellConverter::convert)
                .collect(toList());

        checkAndChangeFromToDirection(selectedCommitIds, cells);

        return new PaletteDiffViewDTO(
                getCommitViewColumns(selectedCommitIds.size(), cells),
                getCommitViewRows(selectedCommitIds.size(), cells)
        );
    }

    private void checkAndChangeFromToDirection(List<Long> selectedCommitIds, List<CommitViewCellDTO> cells) {
        if(selectedCommitIds.get(0) > selectedCommitIds.get(1)) {
            for(int i = 0; i < cells.size(); i+= 2) {
                CommitViewCellDTO tempCell = cells.set(i + 1, cells.get(i));
                cells.set(i, tempCell);
            }
        }
    }

    private void checkIsAnyEntityNotFound(List<Long> ids, List<PaletteEntity> entities) {
        List<Long> entityIds = entities.stream()
                .map(e -> e.getId())
                .collect(toList());

        List<Long> notFoundIds = new ArrayList<>();
        notFoundIds.addAll(ids);
        notFoundIds.removeAll(entityIds);
        if(notFoundIds.size() > 0) {
            throw new EntityNotFoundCustomException(notFoundIds.get(0));
        }
    }

    private List<SaveVariableDTO> getPaletteViewRows(Integer selectedPaletteIdsSize, List<PaletteViewCellDTO> cells) {
        List<SaveVariableDTO> rows = new ArrayList<>();
        for (int rowNumber = 0; rowNumber < cells.size(); rowNumber += selectedPaletteIdsSize) {
            rows.add(new SaveVariableDTO(
                    createVariableDTO(cells.get(rowNumber)),
                    createRow(selectedPaletteIdsSize, cells, rowNumber)));
        }
        return rows;
    }

    private List<SaveVariableDTO> getCommitViewRows(Integer selectedCommitIdsSize, List<CommitViewCellDTO> cells) {
        List<SaveVariableDTO> rows = new ArrayList<>();
        for (int rowNumber = 0; rowNumber < cells.size(); rowNumber += selectedCommitIdsSize) {
            rows.add(new SaveVariableDTO(
                    createVariableDTO(cells.get(rowNumber)),
                    createCommitRow(selectedCommitIdsSize, cells, rowNumber)));
        }
        return rows;
    }

    private List<VariableSnapshotDTO> createRow(int columnsCount, List<PaletteViewCellDTO> cells, int rowNumber) {
        List<VariableSnapshotDTO> columns = new ArrayList<>();
        for (int columnNumber = 0; columnNumber < columnsCount; columnNumber++) {
            PaletteViewCellDTO cell = cells.get(rowNumber + columnNumber);
            columns.add(new VariableSnapshotDTO(
                    cell.getVariableSnapshotId(),
                    cell.getPaletteId(),
                    cell.getVariableId(),
                    opacityToPercent(cell.getOpacity()).orElse(null),
                    cell.getColor()));
        }
        return columns;
    }

    private List<VariableSnapshotDTO> createCommitRow(int columnsCount, List<CommitViewCellDTO> cells, int rowNumber) {
        List<VariableSnapshotDTO> columns = new ArrayList<>();
        for (int columnNumber = 0; columnNumber < columnsCount; columnNumber++) {
            CommitViewCellDTO cell = cells.get(rowNumber + columnNumber);
            columns.add(new VariableSnapshotDTO(
                    cell.getVariableSnapshotId(),
                    cell.getPaletteId(),
                    cell.getVariableId(),
                    opacityToPercent(cell.getOpacity()).orElse(null),
                    cell.getColor()));
        }
        return columns;
    }

    private VariableDTO createVariableDTO(PaletteViewCellDTO cell) {
        return new VariableDTO(cell.getVariableId(), cell.getVariableName(), cell.getVariableUsage());
    }

    private VariableDTO createVariableDTO(CommitViewCellDTO cell) {
        return new VariableDTO(cell.getVariableId(), cell.getVariableName(), cell.getVariableUsage());
    }

    private List<PaletteDTO> getPaletteViewColumns(int columnCount, List<PaletteViewCellDTO> cells) {
        return cells.stream()
                .limit(columnCount)
                .map(cell -> new PaletteDTO(cell.getPaletteId(), cell.getPaletteName()))
                .collect(toList());
    }

    private List<CommitDTO> getCommitViewColumns(int columnCount, List<CommitViewCellDTO> cells) {
        return cells.stream()
                .limit(columnCount)
                .map(cell -> new CommitDTO(cell.getCommitId(), cell.getPaletteId(), cell.getUpdateTime()))
                .collect(toList());
    }

    private PaletteDTO convertToDTO(PaletteEntity entity) {
        return new PaletteDTO(entity.getId(), entity.getName());
    }

    private List<PaletteDTO> convertToDTO(List<PaletteEntity> entities) {
        return entities.stream()
                .map(this::convertToDTO).
                        collect(toList());
    }

    private class SnapshotId {

        private Long variableId;
        private Long paletteId;

        public SnapshotId(Long variableId, Long paletteId) {
            this.variableId = variableId;
            this.paletteId = paletteId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (getClass() != o.getClass()) return false;
            SnapshotId that = (SnapshotId) o;
            return Objects.equals(variableId, that.variableId) &&
                    Objects.equals(paletteId, that.paletteId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(variableId, paletteId);
        }
    }

}
