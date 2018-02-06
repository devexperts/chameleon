package com.devexperts.chameleon.exception;

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

import org.springframework.validation.FieldError;

import java.util.ArrayList;
import java.util.List;

import static com.devexperts.chameleon.exception.ErrorCode.ARGUMENT_NOT_VALID;

public class ArgumentNotValidCustomException extends BaseCustomException {
	public ArgumentNotValidCustomException(FieldError fieldError) {
		super(ARGUMENT_NOT_VALID, fieldError.getDefaultMessage());

		List<Object> parameters = new ArrayList<>();
		parameters.add(fieldError.getField());
		parameters.add(fieldError.getRejectedValue());
		setParameters(parameters);
	}
}
