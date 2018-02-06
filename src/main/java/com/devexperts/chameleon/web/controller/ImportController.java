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

import com.devexperts.chameleon.dto.AuthDTO;
import com.devexperts.chameleon.service.ConfluenceImportPaletteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * This class is used as rest service for the {@link ConfluenceImportPaletteService}.
 * Import palettes from external source
 */
@RestController
@RequestMapping(ImportController.IMPORT_PATH)
public class ImportController {
	public static final String IMPORT_PATH = "api/import";

	private final ConfluenceImportPaletteService importPaletteService;

	@Autowired
	public ImportController(ConfluenceImportPaletteService importPaletteService) {
		this.importPaletteService = importPaletteService;
	}

	/**
	 * Entry point for import process to confluence
	 *
	 * @param url color palette page url
	 * @param paletteName palette name
	 * @param authDTO authentication information
	 * @return import result message
	 */
	@RequestMapping(method = POST)
	@ResponseStatus(HttpStatus.OK)
	public String importPalette(@RequestParam String url, @RequestParam String paletteName,  @RequestBody AuthDTO authDTO) {
		return importPaletteService.importPalette(url, paletteName, authDTO.getUsername(), authDTO.getPassword());
	}
}
