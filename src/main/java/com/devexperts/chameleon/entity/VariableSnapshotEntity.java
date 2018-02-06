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

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Class for variable snapshot.
 * It stores color information for define {@link VariableEntity},
 * {@link PaletteEntity} and {@link CommitEntity}
 */
@Entity
@Table(name = "variable_snapshot", indexes = {@Index(columnList = "commit_id", name = "commit_id_idx")})
public class VariableSnapshotEntity extends ColorEntity {

	private static final long serialVersionUID = -5539490373306190892L;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "palette_id")
	private PaletteEntity paletteEntity;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "variable_id")
	private VariableEntity variableEntity;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "commit_id")
	private CommitEntity commitEntity;

	private Float opacity;

	public VariableSnapshotEntity() {
	}

	public VariableSnapshotEntity(String color,
								  Float opacity,
								  PaletteEntity paletteEntity,
								  VariableEntity variableEntity,
								  CommitEntity commitEntity) {
		super(color);
		this.opacity = opacity;
		this.paletteEntity = paletteEntity;
		this.variableEntity = variableEntity;
		this.commitEntity = commitEntity;
	}

	public PaletteEntity getPaletteEntity() {
		return paletteEntity;
	}

	public void setPaletteEntity(PaletteEntity paletteEntity) {
		this.paletteEntity = paletteEntity;
	}

	public VariableEntity getVariableEntity() {
		return variableEntity;
	}

	public void setVariableEntity(VariableEntity variableEntity) {
		this.variableEntity = variableEntity;
	}

	public Float getOpacity() {
		return opacity;
	}

	public void setOpacity(Float opacity) {
		this.opacity = opacity;
	}

	public CommitEntity getCommitEntity() {
		return commitEntity;
	}

	public void setCommitEntity(CommitEntity commitEntity) {
		this.commitEntity = commitEntity;
	}

	@Override
	public String toString() {
		return super.toString() +
				"VariableSnapshotEntity{" +
				"paletteEntity=" + paletteEntity +
				", variableEntity=" + variableEntity +
				", opacity=" + opacity +
				'}';
	}
}
