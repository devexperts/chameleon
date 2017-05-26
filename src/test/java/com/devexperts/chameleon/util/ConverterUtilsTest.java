package com.devexperts.chameleon.util;

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

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class ConverterUtilsTest {

	@Test
	public void testToSameValueMap() {
		List<String> list = ImmutableList.of("aaa", "bbb", "ccc");
		Map<String, String> stringMap = list.stream().collect(ConverterUtils.toSameValueMap(entry -> entry.substring(0, 1)));
		Assert.assertTrue(stringMap.keySet().containsAll(ImmutableList.of("a", "b", "c")));
		Assert.assertTrue(stringMap.values().containsAll(ImmutableList.of("aaa", "bbb", "ccc")));
	}
}
