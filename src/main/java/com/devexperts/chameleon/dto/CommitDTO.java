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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CommitDTO {

	private final Long id;
	private final Long paletteId;
	private final String updateTime;

	private static final DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

	@JsonCreator
	public CommitDTO(
			@JsonProperty("id") Long id,
			@JsonProperty("paletteId") Long paletteId,
			@JsonProperty("updateTime") Date updateTime) {
		this.id = id;
		this.paletteId = paletteId;
		this.updateTime = LocalDateTime.ofInstant(updateTime.toInstant(), ZoneId.systemDefault()).format(formater);
	}

	public Long getId() {
		return id;
	}

	public Long getPaletteId() {
		return paletteId;
	}

	public String getUpdateTime() {
		return updateTime;
	}
}
