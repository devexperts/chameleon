package com.devexperts.chameleon.web.controller;

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

import com.devexperts.chameleon.dto.PaletteDTO;
import com.devexperts.chameleon.dto.PaletteViewDTO;
import com.devexperts.chameleon.service.PaletteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static java.lang.Enum.valueOf;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This class is used as rest service for the {@link PaletteService}.
 * Handle actions with palette
 */
@RestController
@RequestMapping(PaletteController.PALETTE_PATH)
public class PaletteController {

    public static final String PALETTE_PATH = "api/palettes";
    public static final String PALETTE_VARIABLE_VIEW_PATH = "view/variables";
    public static final String PALETTE_RENAME_PATH = "/rename";
    public static final String PALETTE_REMOVE_PATH = "/remove/{id}";

    private final PaletteService paletteService;

    @Autowired
    public PaletteController(PaletteService paletteService) {
        this.paletteService = paletteService;
    }

    /**
     * Returns all palettes
     *
     * @return list of {@link PaletteDTO}
     */
    @RequestMapping(method = GET)
    public ResponseEntity<List<PaletteDTO>> list() {
        return new ResponseEntity<>(paletteService.findAll(), HttpStatus.OK);
    }

    /**
     * Returns palette by id
     *
     * @param id palette id
     * @return {@link PaletteDTO}
     */
    @RequestMapping(method = GET, value = "/{paletteId}")
    public ResponseEntity<PaletteDTO> read(@PathVariable("paletteId") Long id) {
        return new ResponseEntity<>(paletteService.getById(id), HttpStatus.OK);
    }

    /**
     * Returns palette variable view by palette ids
     *
     * @param paletteIds palette ids
     * @return {@link PaletteViewDTO}
     */
    @RequestMapping(path = PALETTE_VARIABLE_VIEW_PATH + "/{paletteIds}", method = GET)
    public ResponseEntity<PaletteViewDTO> read(@PathVariable("paletteIds") List<Long> paletteIds) {
        return new ResponseEntity<>(paletteService.buildVariableView(paletteIds), HttpStatus.OK);
    }

    /**
     * Create palette by paletteDTO
     * @param paletteDTO palette DTO
     * @return created palette id
     */
    @RequestMapping(method = POST)
    public ResponseEntity<?> create(@RequestBody @Valid PaletteDTO paletteDTO) {
        Long id = paletteService.save(paletteDTO);
        if (Objects.nonNull(id)) {
            return new ResponseEntity<>(id, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @RequestMapping(path = PALETTE_RENAME_PATH, method = POST)
    public ResponseEntity<?> rename(@RequestBody PaletteDTO paletteDTO) {
        Long id = paletteService.save(paletteDTO);
        if (Objects.nonNull(id)) {
            return new ResponseEntity<>(id, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
        }
    }

    @RequestMapping(path = PALETTE_REMOVE_PATH, method = GET)
    public ResponseEntity<?> delete(@PathVariable long id) {
        paletteService.setPaletteNotActive(id);
        return new ResponseEntity<Object>(id, HttpStatus.OK);
    }
}
