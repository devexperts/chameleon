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

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * Class for product. Additional tag information for palette
 * Not currently used
 */
@Entity
@Table(name = "product")
public class ProductEntity extends BaseEntity {

	private static final long serialVersionUID = 935753275783228164L;

	@Size(max = 255)
	@NotNull
	private String name;

	@ManyToMany(fetch = FetchType.EAGER)
	@OrderBy("name")
	private Set<UiComponentEntity> uiComponentEntities;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<UiComponentEntity> getUiComponentEntities() {
		return uiComponentEntities;
	}

	public void setUiComponentEntities(Set<UiComponentEntity> uiComponentEntities) {
		this.uiComponentEntities = uiComponentEntities;
	}

	@Override
	public String toString() {
		return "Product{" +
				"name='" + name + '\'' +
				", uiComponentEntities=" + uiComponentEntities +
				'}';
	}
}
