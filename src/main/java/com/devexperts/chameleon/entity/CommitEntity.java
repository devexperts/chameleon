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

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Class for palette's commit. Each commit stores list of {@link VariableSnapshotEntity} for define time point.
 * It created on each palette save.
 */
@Entity
@Table(name = "commit", indexes = {@Index(columnList = "update_time", name = "update_time_idx")})
public class CommitEntity extends BaseEntity {

    @Size(min = 1, max = 255)
    private String message;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "palette_id")
    private PaletteEntity paletteEntity;

    @CreationTimestamp
    @Column(name = "update_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateTime;

    public CommitEntity() {
    }

    public CommitEntity(PaletteEntity paletteEntity) {
        this.paletteEntity = paletteEntity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public PaletteEntity getPaletteEntity() {
        return paletteEntity;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    @Override
    public String toString() {
        return "CommitEntity{" +
                "id='" + getId() + '\'' +
                "message='" + message + '\'' +
                '}';
    }
}
