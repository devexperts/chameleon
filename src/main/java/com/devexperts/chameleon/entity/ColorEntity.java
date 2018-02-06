package com.devexperts.chameleon.entity;

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

import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Pattern;

/**
 * Base class for colored entities
 */
@MappedSuperclass
public abstract class ColorEntity extends BaseEntity {

	private static final long serialVersionUID = 511270403795819316L;

	@Pattern(regexp = "^$|^[0-9A-Fa-f]{6}$")
	private String color;

	public ColorEntity() {
	}

	public ColorEntity(String color) {
		this.color = color;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Override
	public String toString() {
		return "ColorEntity{" +
				"color='" + color + '\'' +
				'}';
	}
}
