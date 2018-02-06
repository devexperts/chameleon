package com.devexperts.chameleon.util;

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

import com.devexperts.chameleon.exception.EntityNotFoundCustomException;

import java.util.Arrays;
import java.util.List;

import static com.devexperts.chameleon.exception.BaseCustomException.buildIllegalArgument;

/**
 * This class is used for checking preconditions during getting entities from database
 * and chek for null or empty in case of fail checking custom exception will be thrown
 *
 */
public class PreconditionUtils {

	/**
	 * Check not null object
	 *
	 * @param entity
	 */
	public static void checkNotNull(Object entity) {
		checkNotFound(entity, 0L);
	}

	/**
	 * Chech not found object with define id
	 *
	 * @param entity
	 * @param id
	 */
	public static void checkNotFound(Object entity, Long id) {
		if (entity == null) {
			throw new EntityNotFoundCustomException(id);
		}
	}

	/**
	 * Check not found object with define field
	 *
	 * @param entity
	 * @param field
	 */
	public static void checkNotFound(Object entity, String field) {
		if (entity == null) {
			throw new EntityNotFoundCustomException("Not found by field: " + field);
		}
	}

	/**
	 * Check is any not null from list
	 *
	 * @param enities
	 */
	public static void checkIsAnyNotNull(List<?> enities) {
		enities.forEach(PreconditionUtils::checkNotNull);
	}

	/**
	 * Check is any string empty
	 *
	 * @param elements
	 */
	public static void checkIsAnyEmpty(String... elements) {
		Arrays.stream(elements)
				.filter(element -> element == null || element.isEmpty())
				.findAny()
				.ifPresent(element -> {
					throw buildIllegalArgument(element);
				});
	}
}
