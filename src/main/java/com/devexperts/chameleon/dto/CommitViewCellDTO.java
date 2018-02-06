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

import java.util.Date;

public class CommitViewCellDTO {
    private final Long variableId;
    private final String variableName;
    private final String variableUsage;
    private final Long paletteId;
    private final Long commitId;
    private final Date updateTime;
    private final Long variableSnapshotId;
    private final String color;
    private final Float opacity;

    @JsonCreator
    public CommitViewCellDTO(@JsonProperty("variableId") Long variableId,
							 @JsonProperty("variableName") String variableName,
							 @JsonProperty("variableUsage") String variableUsage,
                             @JsonProperty("paletteId") Long paletteId,
							 @JsonProperty("commitId") Long commitId,
							 @JsonProperty("updateTime") Date updateTime,
							 @JsonProperty("variableSnapshotId") Long variableSnapshotId,
							 @JsonProperty("color") String color,
							 @JsonProperty("opacity") Float opacity) {
        this.variableId = variableId;
        this.variableName = variableName;
        this.variableUsage = variableUsage;
        this.paletteId = paletteId;
        this.commitId = commitId;
        this.updateTime = updateTime;
        this.variableSnapshotId = variableSnapshotId;
        this.color = color;
        this.opacity = opacity;
    }

    public Long getVariableId() {
        return variableId;
    }

    public String getVariableName() {
        return variableName;
    }

    public String getVariableUsage() {
        return variableUsage;
    }

    public Long getCommitId() {
        return commitId;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public Long getVariableSnapshotId() {
        return variableSnapshotId;
    }

    public String getColor() {
        return color;
    }

    public Float getOpacity() {
        return opacity;
    }

    public Long getPaletteId() {
        return paletteId;
    }
}
