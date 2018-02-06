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

import com.devexperts.chameleon.web.api.ErrorResponse;
import com.devexperts.chameleon.web.controller.UiComponentController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static com.devexperts.chameleon.exception.ErrorCode.UNEXPECTED_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Called when an exception occurs during request processing.
 */
@ControllerAdvice
@ResponseStatus(BAD_REQUEST)
@ResponseBody
public class CommonExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(UiComponentController.class);

	@ExceptionHandler(EntityNotFoundException.class)
	public ErrorResponse handleEntityNotFound(EntityNotFoundException ex) {
		logger.error(ex.getMessage());
		return new ErrorResponse(new EntityNotFoundCustomException(ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse handleArgumentNotValid(MethodArgumentNotValidException ex) {
		List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
		if (allErrors.isEmpty() || !(allErrors.get(0) instanceof FieldError)) {
			return new ErrorResponse(new BaseCustomException(UNEXPECTED_ERROR, ex.getMessage()));
		}

		FieldError fieldError = (FieldError) ex.getBindingResult().getAllErrors().get(0);
		logger.error(fieldError.getDefaultMessage());
		return new ErrorResponse(new ArgumentNotValidCustomException(fieldError));
	}

	@ExceptionHandler(Exception.class)
	public ErrorResponse handleException(Exception ex) {
		logger.error(ex.getMessage(), ex);
		return new ErrorResponse(new BaseCustomException(UNEXPECTED_ERROR, ex.getMessage()));
	}

	@ExceptionHandler(BaseCustomException.class)
	public ErrorResponse handleBaseCustomException(BaseCustomException ex) {
		logger.error(ex.getMessage(), ex);
		return new ErrorResponse(ex);
	}
}
