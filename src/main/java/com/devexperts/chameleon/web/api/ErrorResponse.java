package com.devexperts.chameleon.web.api;

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

import com.devexperts.chameleon.exception.BaseCustomException;
import com.devexperts.chameleon.exception.ErrorCode;

import java.util.List;

/*
 * A sample class for sending error information to frontend
 */
public class ErrorResponse {
	private final String message;

	private final ErrorCode errorCode;

	private final List<?> parameters;

	public ErrorResponse(BaseCustomException ex) {
		this.message = ex.getMessage();
		this.errorCode = ex.getErrorCode();
		this.parameters = ex.getParameters();
	}

	public String getMessage() {
		return message;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public List<?> getParameters() {
		return parameters;
	}
}
