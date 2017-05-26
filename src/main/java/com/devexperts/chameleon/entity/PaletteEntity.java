package com.devexperts.chameleon.entity;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Class for pallete.
 * Palette contains list of {@link VariableEntity} with color information stored in {@link VariableSnapshotEntity} of different
 * versions referenced by commit {@link CommitEntity}
 */
@Entity
@Table(name = "palette")
public class PaletteEntity extends BaseEntity {

    @Size(min = 1, max = 255)
    @NotNull
    @Column(unique = true)
    private String name;

    public PaletteEntity() {
    }

    public PaletteEntity(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "PaletteEntity{" +
                "name='" + name + '\'' +
                '}';
    }
}
