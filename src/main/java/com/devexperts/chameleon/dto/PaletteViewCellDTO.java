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
import com.google.common.base.Objects;

public class PaletteViewCellDTO {
    private Long variableId;
    private String variableName;
    private String variableUsage;
    private Long paletteId;
    private String paletteName;
    private Long variableSnapshotId;
    private String color;
    private Float opacity;

    public PaletteViewCellDTO(Long variableId,
                              String variableName,
                              String variableUsage,
                              Long paletteId,
                              String paletteName,
                              Long variableSnapshotId,
                              String color,
                              Float opacity) {
        this.variableId = variableId;
        this.variableName = variableName;
        this.variableUsage = variableUsage;
        this.paletteId = paletteId;
        this.paletteName = paletteName;
        this.variableSnapshotId = variableSnapshotId;
        this.color = color;
        this.opacity = opacity;
    }

    private PaletteViewCellDTO() {
    }

    private void setVariableId(Long variableId) {
        this.variableId = variableId;
    }

    private void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    private void setVariableUsage(String variableUsage) {
        this.variableUsage = variableUsage;
    }

    private void setPaletteId(Long paletteId) {
        this.paletteId = paletteId;
    }

    private void setPaletteName(String paletteName) {
        this.paletteName = paletteName;
    }

    private void setVariableSnapshotId(Long variableSnapshotId) {
        this.variableSnapshotId = variableSnapshotId;
    }

    private void setColor(String color) {
        this.color = color;
    }

    private void setOpacity(Float opacity) {
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

    public Long getPaletteId() {
        return paletteId;
    }

    public String getPaletteName() {
        return paletteName;
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Long variableId;
        private String variableName;
        private String variableUsage;
        private Long paletteId;
        private String paletteName;
        private Long variableSnapshotId;
        private String color;
        private Float opacity;

        public Builder variableId(Long variableId) {
            this.variableId = variableId;
            return this;
        }

        public Builder paletteId(Long paletteId) {
            this.paletteId = paletteId;
            return this;
        }

        public Builder variableName(String variableName) {
            this.variableName = variableName;
            return this;
        }

        public Builder variableUsage(String variableUsage) {
            this.variableUsage = variableUsage;
            return this;
        }

        public Builder paletteName(String paletteName) {
            this.paletteName = paletteName;
            return this;
        }

        public Builder variableSnapshotId(Long variableSnapshotId) {
            this.variableSnapshotId = variableSnapshotId;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public Builder opacity(Float opacity) {
            this.opacity = opacity;
            return this;
        }

        public PaletteViewCellDTO build() {
            PaletteViewCellDTO paletteViewCellDTO = new PaletteViewCellDTO();
            paletteViewCellDTO.setVariableId(this.variableId);
            paletteViewCellDTO.setPaletteId(this.paletteId);
            paletteViewCellDTO.setPaletteName(this.paletteName);
            paletteViewCellDTO.setVariableName(this.variableName);
            paletteViewCellDTO.setVariableUsage(this.variableUsage);
            paletteViewCellDTO.setVariableSnapshotId(this.variableSnapshotId);
            paletteViewCellDTO.setColor(this.color);
            paletteViewCellDTO.setOpacity(this.opacity);
            return paletteViewCellDTO;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaletteViewCellDTO that = (PaletteViewCellDTO) o;
        return Objects.equal(variableId, that.variableId) &&
                Objects.equal(variableName, that.variableName) &&
                Objects.equal(variableUsage, that.variableUsage) &&
                Objects.equal(paletteId, that.paletteId) &&
                Objects.equal(paletteName, that.paletteName) &&
                Objects.equal(variableSnapshotId, that.variableSnapshotId) &&
                Objects.equal(color, that.color) &&
                Objects.equal(opacity, that.opacity);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(variableId, variableName, variableUsage, paletteId, paletteName, variableSnapshotId, color, opacity);
    }
}
