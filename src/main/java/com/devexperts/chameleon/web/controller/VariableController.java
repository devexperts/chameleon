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

import com.devexperts.chameleon.dto.VariableDTO;
import com.devexperts.chameleon.entity.VariableEntity;
import com.devexperts.chameleon.service.VariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This class is used as rest service for the {@link VariableService}.
 * Handle actions with variable
 */
@RestController
@RequestMapping(VariableController.PATH)
public class VariableController {

    public static final String PATH = "api/variables";

    public static final String BY_NAME_PATH = "name";

    private final VariableService variableService;

    @Autowired
    public VariableController(VariableService variableService) {
        this.variableService = variableService;
    }

    /**
     * Returns variable by name
     *
     * @param variableName variable name
     * @return {@link VariableDTO}
     */
    @RequestMapping(path = BY_NAME_PATH + "/{variableName}", method = GET)
    public ResponseEntity<VariableDTO> getByName(@PathVariable("variableName") String variableName) {
        return new ResponseEntity<>(variableService.getByName(variableName), HttpStatus.OK);
    }

    /**
     * Returns variable by id
     *
     * @param id variable id
     * @return {@link VariableDTO}
     */
    @RequestMapping(method = GET, value = "/{variableId}")
    public ResponseEntity<VariableDTO> read(@PathVariable("variableId") Long id) {
        return new ResponseEntity<>(variableService.getById(id), HttpStatus.OK);
    }

    /**
     * Create variable by variableDTO
     *
     * @param variableDTO variabel DTO
     * @return created variable id
     */
    @RequestMapping(method = POST)
    public ResponseEntity<Long> create(@RequestBody @Valid VariableDTO variableDTO) {
        VariableEntity variableEntity = variableService.save(variableDTO);
        return new ResponseEntity<>(variableEntity.getId(), HttpStatus.CREATED);
    }
}
