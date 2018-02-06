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

import com.devexperts.chameleon.dto.UiComponentDTO;
import com.devexperts.chameleon.entity.UiComponentEntity;
import com.devexperts.chameleon.repository.UiComponentRepository;
import com.devexperts.chameleon.util.PreconditionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is used as service for handle actions with UI component
 * Not currently used
 */
@Service
@Transactional
public class UiComponentService {

	private final UiComponentRepository repository;

	@Autowired
	public UiComponentService(UiComponentRepository repository) {
		this.repository = repository;
	}

	public List<UiComponentDTO> findAll() {
		List<UiComponentEntity> enities = repository.findAll();
		PreconditionUtils.checkIsAnyNotNull(enities);
		return convertToDTO(enities);
	}

	public UiComponentDTO getById(Long id) {
		UiComponentEntity entity = repository.findOne(id);

		PreconditionUtils.checkNotFound(entity, id);

		return convertToDTO(entity);
	}

	public boolean isNameExist(String name) {
		return repository.findFirstByNameIgnoreCase(name) != null;
	}

	public Long save(UiComponentDTO dto) {
		UiComponentEntity entity = repository.save(convertToEntity(dto));
		return entity.getId();
	}

	private UiComponentEntity convertToEntity(UiComponentDTO dto) {
		UiComponentEntity entity;
		if (dto.getId() == null) {
			entity = new UiComponentEntity(dto.getName(), dto.getColor());
		} else {
			entity = repository.getOne(dto.getId());
			PreconditionUtils.checkNotFound(entity, dto.getId());
		}

		entity.setName(dto.getName());
		entity.setColor(dto.getColor());

		return entity;
	}

	private UiComponentDTO convertToDTO(UiComponentEntity entity) {
		return new UiComponentDTO(entity.getId(), entity.getName(), entity.getColor());
	}

	private List<UiComponentDTO> convertToDTO(List<UiComponentEntity> entities) {
		return entities.stream()
				.map(this::convertToDTO).
						collect(Collectors.toList());
	}
}
