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

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class VariableViewDTO {

    @NotNull(message = "{exception.variable.notnull}")
    private final VariableDTO variable;

    private final List<PaletteDTO> palettes;

    @Valid
    private final List<VariableSnapshotDTO> snapshots;

    @JsonCreator
    public VariableViewDTO(
            @JsonProperty("variable") VariableDTO variable,
            @JsonProperty("palettes") List<PaletteDTO> palettes,
            @JsonProperty("snapshots") List<VariableSnapshotDTO> snapshots) {
        this.variable = variable;
        this.palettes = palettes;
        this.snapshots = snapshots;
    }

    public VariableDTO getVariable() {
        return variable;
    }

    public List<PaletteDTO> getPalettes() {
        return palettes;
    }

    public List<VariableSnapshotDTO> getSnapshots() {
        return snapshots;
    }
}
