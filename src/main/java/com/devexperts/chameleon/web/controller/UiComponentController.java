package com.devexperts.chameleon.web.controller;

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

import com.devexperts.chameleon.dto.UiComponentDTO;
import com.devexperts.chameleon.service.UiComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This class is used as rest service for the {@link UiComponentService}.
 * Not currently used
 */
@RestController
@RequestMapping(UiComponentController.UICOMPONENT_PATH)
public class UiComponentController {

	public static final String UICOMPONENT_PATH = "api/uicomponents";

	private final UiComponentService uiComponentService;

	@Autowired
	public UiComponentController(UiComponentService uiComponentService) {
		this.uiComponentService = uiComponentService;
	}

	/**
	 * Returns all UI components
	 *
	 * @return {@link UiComponentDTO}
	 */
	@RequestMapping(method = GET)
	public ResponseEntity<List<UiComponentDTO>> list() {
		return new ResponseEntity<>(uiComponentService.findAll(), HttpStatus.OK);
	}

	/**
	 * Returns component by id
	 *
	 * @param id component id
	 * @return {@link UiComponentDTO}
	 */
	@RequestMapping(method = GET, value="/{componentId}")
	public ResponseEntity<UiComponentDTO> read(@PathVariable("componentId") Long id) {
		return new ResponseEntity<>(uiComponentService.getById(id), HttpStatus.OK);
	}

	/**
	 * Create UI component by uiComponentDTO
	 *
	 * @param uiComponentDTO UI component DTO
	 * @return created UI component id
	 */
	@RequestMapping(method = POST)
    public ResponseEntity<?> create(@RequestBody @Valid UiComponentDTO uiComponentDTO) {
        Long id = uiComponentService.save(uiComponentDTO);
		return new ResponseEntity<>(id, HttpStatus.CREATED);
	}
}
