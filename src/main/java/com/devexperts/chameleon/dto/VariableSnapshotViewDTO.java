package com.devexperts.chameleon.dto;

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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class VariableSnapshotViewDTO {

	private final VariableDTO variable;

	private final VariableSnapshotDTO snapshot;

	@JsonCreator
	public VariableSnapshotViewDTO(
			@JsonProperty("variable") VariableDTO variable,
			@JsonProperty("snapshot") VariableSnapshotDTO snapshot) {
		this.variable = variable;
		this.snapshot = snapshot;
	}

	public VariableDTO getVariable() {
		return variable;
	}

	public VariableSnapshotDTO getSnapshot() {
		return snapshot;
	}
}
