package com.devexperts.chameleon.dto;

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

import com.devexperts.chameleon.validator.ColorOpacity;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class VariableSnapshotDTO {

	private final Long id;

	private final Long paletteId;

	private final Long variableId;

	@ColorOpacity(message = "{exception.variable.snapshot.opacity.wrong}")
	private final Integer opacity;

	@Pattern(regexp = "^$|^[0-9A-Fa-f]{6}$", message = "{exception.variable.snapshot.color.wrong}")
	@NotNull(message = "{exception.variable.snapshot.color.notnull}")
	private final String color;

	@JsonCreator
	public VariableSnapshotDTO(
			@JsonProperty("id") Long id,
			@JsonProperty("paletteId") Long paletteId,
			@JsonProperty("variableId") Long variableId,
			@JsonProperty("opacity") Integer opacity,
			@JsonProperty("color") String color) {
		this.id = id;
		this.paletteId = paletteId;
		this.variableId = variableId;
		this.opacity = opacity;
		this.color = color;
	}

	public Long getId() {
		return id;
	}

	public Integer getOpacity() {
		return opacity;
	}

	public String getColor() {
		return color;
	}

	public Long getPaletteId() {
		return paletteId;
	}

	public Long getVariableId() {
		return variableId;
	}
}
