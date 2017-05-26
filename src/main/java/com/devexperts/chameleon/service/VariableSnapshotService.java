package com.devexperts.chameleon.service;

/*-
 * #%L
 * Chameleon. Color Palette Management Tool
 * %%
 * Copyright (C) 2016 - 2017 Devexperts, LLC
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

import com.devexperts.chameleon.dto.CommitDTO;
import com.devexperts.chameleon.dto.PaletteDTO;
import com.devexperts.chameleon.dto.PaletteDiffViewDTO;
import com.devexperts.chameleon.dto.SaveVariableDTO;
import com.devexperts.chameleon.dto.VariableDTO;
import com.devexperts.chameleon.dto.VariableSnapshotDTO;
import com.devexperts.chameleon.dto.VariableViewDTO;
import com.devexperts.chameleon.entity.BaseEntity;
import com.devexperts.chameleon.entity.CommitEntity;
import com.devexperts.chameleon.entity.PaletteEntity;
import com.devexperts.chameleon.entity.VariableEntity;
import com.devexperts.chameleon.entity.VariableSnapshotEntity;
import com.devexperts.chameleon.repository.VariableSnapshotRepository;
import com.devexperts.chameleon.util.PreconditionUtils;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.devexperts.chameleon.util.ConverterUtils.opacityToPercent;
import static com.devexperts.chameleon.util.ConverterUtils.percentToOpacity;
import static com.devexperts.chameleon.util.ConverterUtils.toMap;

/**
 * This class is used as service for handle actions with variable snapshot
 *
 */
@Service
@Transactional
public class VariableSnapshotService {

    private final VariableSnapshotRepository repository;
    private final SmartValidator validator;

    private final PaletteService paletteService;
    private final VariableService variableService;
    private final CommitService commitService;

    private static final Logger logger = LoggerFactory.getLogger(VariableSnapshotService.class);

    private static final Function<FieldError, String> validationErrorText = error -> "Validation error field:"
            + error.getField()
            + " rejectedValue: "
            + error.getRejectedValue()
            + " "
            + error.getDefaultMessage();

    @Autowired
    public VariableSnapshotService(VariableSnapshotRepository repository,
                                   PaletteService paletteService,
                                   VariableService variableService,
                                   CommitService commitService,
                                   SmartValidator validator) {
        this.repository = repository;
        this.paletteService = paletteService;
        this.variableService = variableService;
        this.commitService = commitService;
        this.validator = validator;
    }

    public VariableSnapshotDTO getById(Long id) {
        VariableSnapshotEntity entity = repository.findOne(id);
        PreconditionUtils.checkNotFound(entity, id);

        return convertToDTO(entity);
    }

    public List<Long> saveEntityList(List<VariableSnapshotEntity> snapshots) {
        return snapshots.stream()
                .filter(this::isEntityValid)
                .map(repository::save)
                .map(entity -> {
                    variableService.save(entity.getVariableEntity());
                    return entity;
                })
                .map(BaseEntity::getId)
                .collect(Collectors.toList());
    }

    public Map<VariableEntity, List<VariableSnapshotEntity>> getMapByVariables(Long paletteId, Collection<VariableEntity> values) {
        return repository.findAllByPaletteEntityIdAndVariableEntityIn(paletteId, values).stream().collect(Collectors.groupingBy(VariableSnapshotEntity::getVariableEntity));
    }

    public List<Long> copy(Long sourcePaletteId, Long targetPaletteId) {

        if (sourcePaletteId.equals(targetPaletteId)) {
            return Collections.emptyList();
        }

        PaletteEntity targetPalette = paletteService.getPalette(targetPaletteId);

        CommitEntity lastCommit = commitService.getLastCommitByPaletteId(sourcePaletteId);

        List<VariableSnapshotEntity> snapshots = repository.findAllByCommitEntityId(lastCommit.getId());
        CommitEntity newCommit = commitService.createNewCommit(targetPalette);

        List<VariableSnapshotEntity> copiedSnapshots = snapshots.stream()
                .map(source -> new VariableSnapshotEntity(
                        source.getColor(),
                        source.getOpacity(),
                        targetPalette,
                        source.getVariableEntity(),
                        newCommit))
                .collect(Collectors.toList());

        return saveEntityList(copiedSnapshots);
    }

    public VariableViewDTO buildVariableSnapshotView(Long variableId) {
        VariableDTO variable = getVariableDTO(variableId);
        List<PaletteDTO> palettes = paletteService.findAll();
        List<VariableSnapshotDTO> snapshots = getVariableSnapshotsForAllPalettes(variableId, palettes);

        return new VariableViewDTO(variable, palettes, snapshots);
    }

    public List<Long> save(List<SaveVariableDTO> viewDTOS) {
        List<Long> paletteIds = getPaletteIdsFromView(viewDTOS);
        List<CommitDTO> lastCommits = commitService.getLastCommitsByPaletteIds(paletteIds);
        Map<Long, CommitEntity> newCommitsByPaletteId = createNewCommitsMapByPalettes(paletteIds);
        Map<Long, List<VariableSnapshotDTO>> changedSnapshots = saveNewSnapshots(viewDTOS, newCommitsByPaletteId);
        copyPreviousSnapshotsToNewSnapshots(lastCommits, newCommitsByPaletteId, changedSnapshots);

        return changedSnapshots.keySet().stream().collect(Collectors.toList());
    }

    public PaletteDiffViewDTO buildPaletteDiffView(Long firstCommitId, Long secondCommitId) {

        List<Long> diffVariableIds = repository.getChangedVariablesByCommitIds(firstCommitId, secondCommitId).stream()
                .map(s -> s.longValue()).collect(Collectors.toList());

        List<Long> selectedCommitIds = new ArrayList<>();
        selectedCommitIds.add(firstCommitId);
        selectedCommitIds.add(secondCommitId);

        return paletteService.buildDiffView(selectedCommitIds, diffVariableIds);
    }

    public Boolean isSnapshotEmpty(VariableSnapshotDTO snapshot) {
        return snapshot == null || Strings.isNullOrEmpty(snapshot.getColor());
    }

    public Boolean isSnapshotDeleted(VariableSnapshotDTO snapshot) {
        return snapshot != null
                && snapshot.getVariableId() != null
                && snapshot.getPaletteId() != null
                && Strings.isNullOrEmpty(snapshot.getColor());
    }

    private void copyPreviousSnapshotsToNewSnapshots(List<CommitDTO> lastCommtis,
                                                     Map<Long, CommitEntity> newCommitsByPaletteId,
                                                     Map<Long, List<VariableSnapshotDTO>> changedSnapshots) {

        repository.save(lastCommtis.stream()
                .flatMap(lastCommit -> createSnapshotsWithNewCommit(newCommitsByPaletteId.get(lastCommit.getPaletteId()), lastCommit, changedSnapshots).stream())
                .collect(Collectors.toList()));
    }

    private List<VariableSnapshotEntity> createSnapshotsWithNewCommit(
            CommitEntity newCommit,
            CommitDTO lastCommit,
            Map<Long, List<VariableSnapshotDTO>> changedSnapshots) {

        return getNotModifiedSnapshots(changedSnapshots.get(lastCommit.getPaletteId()), lastCommit.getId()).stream()
				.map(s -> new VariableSnapshotEntity(
						s.getColor(),
						s.getOpacity(),
						s.getPaletteEntity(),
						s.getVariableEntity(),
						newCommit))
                .collect(Collectors.toList());
    }

    private List<VariableSnapshotEntity> getNotModifiedSnapshots(List<VariableSnapshotDTO> changedSnapshots, Long lastCommitId) {
        List<VariableSnapshotEntity> lastCommitSnpashots = repository.findAllByCommitEntityId(lastCommitId);

        if(changedSnapshots != null) {
            changedSnapshots.forEach(newSnapshot ->
                    lastCommitSnpashots.removeIf(snapshot ->
                            snapshot.getVariableEntity().getId().equals(newSnapshot.getVariableId())));
        }

        return lastCommitSnpashots;
    }

    private Map<Long, CommitEntity> createNewCommitsMapByPalettes(List<Long> paletteIds) {
        return paletteService.getByIds(paletteIds).stream()
                .collect(Collectors.toMap(PaletteEntity::getId, palette -> commitService.createNewCommit(palette)));
    }

    private Map<Long, List<VariableSnapshotDTO>> saveNewSnapshots(List<SaveVariableDTO> viewDTOS, Map<Long, CommitEntity> newCommitByPaletteId) {
        Map<Long, List<VariableSnapshotDTO>> deletedSnapshots = getDeletedSnapshots(viewDTOS);
        Map<Long, List<VariableSnapshotDTO>> createdSnapshots = createSnapshotsWithNewCommit(viewDTOS, newCommitByPaletteId);

        return mergeMaps(deletedSnapshots, createdSnapshots);
    }

    private Map<Long, List<VariableSnapshotDTO>> mergeMaps(Map<Long, List<VariableSnapshotDTO>> firstMap, Map<Long, List<VariableSnapshotDTO>> secondMap) {
        Map<Long, List<VariableSnapshotDTO>> resultMap = new HashMap<>();
        resultMap.putAll(secondMap);
        firstMap.forEach((k, v) -> {
            List<VariableSnapshotDTO> snapshots = resultMap.putIfAbsent(k, v);
            if(snapshots != null) {
                snapshots.addAll(v);
            }
        });

        return resultMap;
    }

    private Map<Long, List<VariableSnapshotDTO>> createSnapshotsWithNewCommit(List<SaveVariableDTO> viewDTOS, Map<Long, CommitEntity> newCommitByPaletteId) {
        return viewDTOS.stream()
                .flatMap(dto -> saveAndConvert(dto, newCommitByPaletteId).stream())
                .map(this::convertToDTO)
                .collect(Collectors.groupingBy(s -> s.getPaletteId(), Collectors.toList()));
    }

    private Map<Long, List<VariableSnapshotDTO>> getDeletedSnapshots(List<SaveVariableDTO> viewDTOS) {
        return viewDTOS.stream()
                .flatMap(dto -> dto.getSnapshots().stream()
                        .filter(this::isSnapshotDeleted)
                        .collect(Collectors.toList())
                        .stream())
                .collect(Collectors.groupingBy(s -> s.getPaletteId(), Collectors.toList()));
    }

    private List<Long> getPaletteIdsFromView(List<SaveVariableDTO> viewDTOS) {
        return viewDTOS.stream()
                .flatMap(dto -> dto.getSnapshots().stream())
                .map(snapshot -> snapshot.getPaletteId())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<VariableSnapshotEntity> saveAndConvert(SaveVariableDTO dto, Map<Long, CommitEntity> commitByPaletteId) {
        VariableEntity variableEntity = variableService.save(dto.getVariable());

        return saveVariableSnapshots(dto, variableEntity, commitByPaletteId);
    }

    private List<VariableSnapshotEntity> saveVariableSnapshots(SaveVariableDTO dto, VariableEntity variableEntity, Map<Long, CommitEntity> commitByPaletteId) {
        return repository.save(dto.getSnapshots().stream()
                .filter(s -> !isSnapshotEmpty(s))
                .map(s -> createNewEntity(s, variableEntity, commitByPaletteId.get(s.getPaletteId())))
                .collect(Collectors.toList())
        );
    }

    private VariableDTO getVariableDTO(Long variableId) {
        if(variableId != null) {
            return  variableService.getById(variableId);
        }

        return null;
    }

    private List<VariableSnapshotDTO> getVariableSnapshotsForAllPalettes(Long variableID, List<PaletteDTO> palettes) {

        List<Long> lastCommitIds = commitService.getLastCommitIdsByPaletteIds(palettes.stream()
                    .map(PaletteDTO::getId)
                    .collect(Collectors.toList()));

        List<VariableSnapshotDTO> snapshots = repository.findAllVariableSnapshotEntityByVariableEntityIdAndCommitEntityIdIn(variableID, lastCommitIds).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<Long, VariableSnapshotDTO> snapshotsWithPaletteId = toMap(VariableSnapshotDTO::getPaletteId, snapshots);

        return palettes.stream()
                .map(PaletteDTO::getId)
                .map(paletteId -> snapshotsWithPaletteId.getOrDefault(paletteId, new VariableSnapshotDTO(null, paletteId, variableID, null, null)))
                .collect(Collectors.toList());
    }

    private Boolean isEntityValid(VariableSnapshotEntity entity) {
        Errors errors = new BindException(entity, entity.toString());
        validator.validate(entity, errors);
        if (errors.getAllErrors().isEmpty()) {
            return true;
        }
        errors.getAllErrors().forEach(error -> {
            if (error.getClass() == FieldError.class) {
                logger.error(validationErrorText.apply((FieldError) error));
            } else {
                logger.error(error.toString());
            }
        });
        return false;
    }

    private VariableSnapshotEntity convertToEntity(VariableSnapshotDTO dto) {
        VariableSnapshotEntity entity;
        if (dto.getId() == null) {
            entity = new VariableSnapshotEntity();
        } else {
            entity = repository.getOne(dto.getId());
            PreconditionUtils.checkNotFound(entity, dto.getId());
        }

        if (dto.getVariableId() != null ) {
            entity.setVariableEntity(variableService.getVariable(dto.getVariableId()));
        }

        entity.setPaletteEntity(paletteService.getPalette(dto.getPaletteId()));
        entity.setColor(dto.getColor());
        entity.setOpacity(percentToOpacity(dto.getOpacity()).orElse(null));

        return entity;
    }

    private VariableSnapshotEntity createNewEntity(VariableSnapshotDTO dto, VariableEntity variableEntity, CommitEntity commitEntity) {
        VariableSnapshotEntity entity = new VariableSnapshotEntity();
        entity.setVariableEntity(variableEntity);
        entity.setPaletteEntity(paletteService.getPalette(dto.getPaletteId()));
        entity.setCommitEntity(commitEntity);
        entity.setColor(dto.getColor());
        entity.setOpacity(percentToOpacity(dto.getOpacity()).orElse(null));

        return entity;
    }

    private VariableSnapshotDTO convertToDTO(VariableSnapshotEntity entity) {
        return new VariableSnapshotDTO(
                entity.getId(),
                entity.getPaletteEntity().getId(),
                entity.getVariableEntity().getId(),
                opacityToPercent(entity.getOpacity()).orElse(null),
                entity.getColor());
    }

    private List<VariableSnapshotDTO> convertToDTO(List<VariableSnapshotEntity> entities) {
        return entities.stream()
                .map(this::convertToDTO).
                        collect(Collectors.toList());
    }

    public Integer deleteByVariableId(Long variableId) {
        List<Long> paletteIds = paletteService.findAll().stream()
                .map(PaletteDTO::getId)
                .collect(Collectors.toList());

        List<CommitDTO> lastCommits = commitService.getLastCommitsByPaletteIds(paletteIds);
        Map<Long, List<VariableSnapshotDTO>> changedSnapshots = getSnapshotsByPalette(variableId, lastCommits);

        List<Long> editedPaletteIds = new ArrayList<>(changedSnapshots.keySet());
        lastCommits.removeIf(lc -> !editedPaletteIds.contains(lc.getPaletteId()));
        Map<Long, CommitEntity> newCommitsByPaletteId = createNewCommitsMapByPalettes(editedPaletteIds);

        copyPreviousSnapshotsToNewSnapshots(lastCommits, newCommitsByPaletteId, changedSnapshots);

        return editedPaletteIds.size();
    }

    private Map<Long, List<VariableSnapshotDTO>> getSnapshotsByPalette(Long variableId, List<CommitDTO> lastCommits) {
        return repository.findAllVariableSnapshotEntityByVariableEntityIdAndCommitEntityIdIn(
                variableId,
                lastCommits.stream().map(CommitDTO::getId).collect(Collectors.toList())).stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.groupingBy(s -> s.getPaletteId(), Collectors.toList()));
    }
}
