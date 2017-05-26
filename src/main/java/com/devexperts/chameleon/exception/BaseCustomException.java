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

import java.util.List;

/**
 * Base class for custom exceptions, which are throwed when business error occured
 */
public class BaseCustomException extends RuntimeException {

	private final ErrorCode errorCode;

	private List<?> parameters;

	public BaseCustomException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public BaseCustomException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public BaseCustomException(ErrorCode errorCode, String message, List<?> parameters) {
		super(message);
		this.errorCode = errorCode;
		this.parameters = parameters;
	}

	public static BaseCustomException buildUnexpected(Throwable cause) {
		return new BaseCustomException(ErrorCode.UNEXPECTED_ERROR, cause.getMessage());
	}

	public static BaseCustomException buildIllegalArgument(String field) {
		return new BaseCustomException(ErrorCode.ARGUMENT_NOT_VALID, field);
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public List<?> getParameters() {
		return parameters;
	}

	public void setParameters(List<?> parameters) {
		this.parameters = parameters;
	}
}
