package com.devexperts.chameleon.converter;

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

import com.devexperts.chameleon.dto.PaletteViewCellDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Convert native query result to {@link PaletteViewCellDTO}.
 *
 */
@Component
public class PaletteViewCellConverter implements Converter<Object[], PaletteViewCellDTO> {

	private static int VARIABLE_ID_INDEX = 0;
	private static int VARIABLE_NAME_INDEX = 1;
	private static int VARIABLE_USAGE_INDEX = 2;
	private static int PALETTE_ID_INDEX = 3;
	private static int PALETTE_NAME_INDEX = 4;
	private static int VARIABLE_SNAPSHOT_ID_INDEX = 5;
	private static int VARIABLE_COLOR_INDEX = 6;
	private static int VARIABLE_OPACITY_INDEX = 7;

	public PaletteViewCellDTO convert(Object[] source) {
		return new PaletteViewCellDTO(
				((BigInteger) source[VARIABLE_ID_INDEX]).longValue(),
				(String) source[VARIABLE_NAME_INDEX],
				(String) source[VARIABLE_USAGE_INDEX],
				((BigInteger) source[PALETTE_ID_INDEX]).longValue(),
				(String) source[PALETTE_NAME_INDEX],
				source[VARIABLE_SNAPSHOT_ID_INDEX] == null ? null : ((BigInteger) source[VARIABLE_SNAPSHOT_ID_INDEX]).longValue(),
				source[VARIABLE_COLOR_INDEX] == null ? null : (String) source[VARIABLE_COLOR_INDEX],
				source[VARIABLE_OPACITY_INDEX] == null ? null : ((BigDecimal) source[VARIABLE_OPACITY_INDEX]).floatValue()
		);
	}
}
