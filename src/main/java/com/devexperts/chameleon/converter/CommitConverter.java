package com.devexperts.chameleon.converter;

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
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;

/**
 * Convert native query result to {@link CommitDTO}.
 *
 */
@Component
public class CommitConverter implements Converter<Object[], CommitDTO> {

	private static int COMMIT_ID_INDEX = 0;
	private static int PALETTE_ID_INDEX = 1;
	private static int UPDATE_TIME_INDEX = 2;

	public CommitDTO convert(Object[] source) {
		return new CommitDTO(
				((BigInteger) source[COMMIT_ID_INDEX]).longValue(),
				((BigInteger) source[PALETTE_ID_INDEX]).longValue(),
				(Date) source[UPDATE_TIME_INDEX]);
	}
}
