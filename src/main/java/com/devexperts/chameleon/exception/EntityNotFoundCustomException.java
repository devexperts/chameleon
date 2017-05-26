package com.devexperts.chameleon.exception;

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

import java.util.Collections;

import static com.devexperts.chameleon.exception.ErrorCode.NOT_FOUND;

public class EntityNotFoundCustomException extends BaseCustomException {

	public EntityNotFoundCustomException() {
		super(NOT_FOUND);
	}

	public EntityNotFoundCustomException(String message) {
		super(NOT_FOUND, message);
	}

	public EntityNotFoundCustomException(Long id) {
		super(NOT_FOUND, "Entity not found");
		setParameters(Collections.singletonList(id));
	}
}
