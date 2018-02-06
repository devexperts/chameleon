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

import com.devexperts.chameleon.service.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * This class is used as rest service for the {@link CommitService}.
 * Commit stores one version for defined palette
 *
 */
@RestController
@RequestMapping(CommitController.COMMIT_PATH)
public class CommitController {

    public static final String COMMIT_PATH = "api/commits";

    public static final String COMMIT_PALETTE_PATH = "palette";

    private final CommitService commitService;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    /**
     * Returns commits by palette id
     *
     * @param paletteId
     * @return list of {@link com.devexperts.chameleon.dto.CommitDTO}
     */
    @RequestMapping(path = COMMIT_PALETTE_PATH + "/{paletteId}", method = GET)
    public ResponseEntity<?> read(@PathVariable("paletteId") Long paletteId) {
        return new ResponseEntity<>(commitService.getByPaletteId(paletteId), HttpStatus.OK);
    }
}
