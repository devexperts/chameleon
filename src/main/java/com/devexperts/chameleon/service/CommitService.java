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

import com.devexperts.chameleon.converter.CommitConverter;
import com.devexperts.chameleon.dto.CommitDTO;
import com.devexperts.chameleon.entity.CommitEntity;
import com.devexperts.chameleon.entity.PaletteEntity;
import com.devexperts.chameleon.repository.CommitRepository;
import com.devexperts.chameleon.util.PreconditionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used as service for handle actions with commit
 *
 */
@Service
@Transactional
public class CommitService {

    private final CommitRepository repository;

    private final CommitConverter converter;

    @Autowired
    public CommitService(CommitRepository repository, CommitConverter converter) {
        this.repository = repository;
        this.converter = converter;
    }

    public CommitEntity createNewCommit(PaletteEntity paletteEntity) {
        return repository.save(new CommitEntity(paletteEntity));
    }

    public List<CommitDTO> getLastCommitsByPaletteIds(List<Long> paletteIds) {
        return repository.getLastCommitsByPaletteIds(paletteIds)
                .stream()
                .map(converter::convert)
                .collect(Collectors.toList());
    }

    public List<Long> getLastCommitIdsByPaletteIds(List<Long> paletteIds) {
        return repository.getLastCommitsByPaletteIds(paletteIds)
                .stream()
                .map(converter::convert)
                .map(CommitDTO::getId)
                .collect(Collectors.toList());
    }

    public CommitEntity getLastCommitByPaletteId(Long paletteId) {
        CommitEntity commitEntity = repository.findFirstByPaletteEntityIdOrderByUpdateTimeDesc(paletteId);
        PreconditionUtils.checkNotFound(commitEntity, "paletteId");

        return commitEntity;
    }

    public List<CommitDTO> getByPaletteId(Long paletteId) {
        return repository.findAllByPaletteEntityId(paletteId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CommitDTO convertToDTO(CommitEntity entity) {
        return new CommitDTO(entity.getId(), entity.getPaletteEntity().getId(), entity.getUpdateTime());
    }
}
