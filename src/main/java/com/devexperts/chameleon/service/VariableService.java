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

import com.devexperts.chameleon.dto.VariableDTO;
import com.devexperts.chameleon.entity.VariableEntity;
import com.devexperts.chameleon.repository.VariableRepository;
import com.devexperts.chameleon.util.PreconditionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.devexperts.chameleon.util.ConverterUtils.toMap;

/**
 * This class is used as service for handle actions with variable
 *
 */
@Service
@Transactional
public class VariableService {

    private final VariableRepository repository;

    @Autowired
    public VariableService(VariableRepository variableRepository) {
        this.repository = variableRepository;
    }

    public VariableDTO getById(Long id) {
        VariableEntity entity = repository.findOne(id);

        PreconditionUtils.checkNotFound(entity, id);

        return convertToDTO(entity);
    }

    public VariableDTO getByName(String name) {
        VariableEntity entity = repository.findFirstByNameIgnoreCase(name);

        if (entity != null) {
            return convertToDTO(entity);
        }

        return null;
    }

    public Map<String, VariableEntity> getMapByNames(List<String> names) {
        return toMap(VariableEntity::getName, repository.findAllByNameIgnoreCaseIn(names).stream().collect(Collectors.toList()));
    }

    public boolean isNameExist(String name) {
        return repository.findFirstByNameIgnoreCase(name) != null;
    }

    public VariableEntity save(VariableDTO dto) {
        return repository.save(convertToEntity(dto));
    }

    public VariableEntity save(VariableEntity entity) {
        return repository.save(entity);
    }

    private VariableEntity convertToEntity(VariableDTO dto) {
        VariableEntity entity;
        if (dto.getId() == null) {
            entity = new VariableEntity(dto.getName(), dto.getUsage());
        } else {
            entity = repository.getOne(dto.getId());
            PreconditionUtils.checkNotFound(entity, dto.getId());
        }

        entity.setName(dto.getName());
        entity.setUsage(dto.getUsage());

        return entity;
    }

    public VariableDTO convertToDTO(VariableEntity entity) {
        return new VariableDTO(entity.getId(), entity.getName(), entity.getUsage());
    }

    private List<VariableDTO> convertToDTO(List<VariableEntity> entities) {
        return entities.stream()
                .map(this::convertToDTO).
                        collect(Collectors.toList());
    }

	public VariableEntity getVariable(Long id) {
        VariableEntity entity = repository.findOne(id);
        PreconditionUtils.checkNotFound(entity, id);
        return entity;
	}

    public VariableDTO delete(Long id) {
        VariableDTO dto = getById(id);
        repository.delete(dto.getId());
        return dto;
    }
}
