package com.devexperts.chameleon.repository;

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

import com.devexperts.chameleon.dto.CommitDTO;
import com.devexperts.chameleon.entity.CommitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository for the {@link CommitEntity}.
 *
 */
@org.springframework.stereotype.Repository
public interface CommitRepository extends JpaRepository<CommitEntity, Long> {

	/**
	 * Find last commit by palette id
	 *
	 * @param paletteId
	 * @return {@link CommitEntity}
	 */
	CommitEntity findFirstByPaletteEntityIdOrderByUpdateTimeDesc(Long paletteId);

	/**
	 * Find all commits by palette id
	 *
	 * @param paletteId
	 * @return list of {@link CommitEntity}
	 */
	List<CommitEntity> findAllByPaletteEntityId(Long paletteId);

	/**
	 *  Find last commits by palette ids
	 *
	 * @param paletteIds
	 * @return list of native query results. Will be converted by {@link com.devexperts.chameleon.converter.CommitConverter}
	 */
	@Query(value =
			"SELECT " +
			"      (SELECT " +
			"       	    ID" +
			"	    FROM" +
			"           COMMIT AS C" +
			"   	WHERE " +
			"           C.PALETTE_ID = P.PALETTE_ID AND C.UPDATE_TIME = P.LAST_UPDATE_TIME) AS COMMIT_ID, " +
			"   	PALETTE_ID, " +
			"   	LAST_UPDATE_TIME " +
			"FROM " +
			"      (SELECT " +
			"             PALETTE_ID, " +
			"             MAX(UPDATE_TIME) as LAST_UPDATE_TIME " +
			"       FROM " +
			"             COMMIT "+
			"       WHERE " +
			"             PALETTE_ID IN ?1 "+
			"       GROUP BY  " +
			"              PALETTE_ID) AS P ", nativeQuery = true)
	List<Object[]> getLastCommitsByPaletteIds(List<Long> paletteIds);
}
