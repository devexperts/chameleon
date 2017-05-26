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

import com.devexperts.chameleon.util.collector.SameValueMapCollector;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * This class is used for converting purposes
 *
 */
public class ConverterUtils {

	/**
	 * Converts percent to opacity in color information
	 * It needs to be convertded because backend stores opacity
	 * as float and frontend use percent representation
	 *
	 * @param percent percent
	 * @return opacity value
	 */
	public static Optional<Float> percentToOpacity(Integer percent) {
		if(percent == null) {
			return Optional.empty();
		}
		return Optional.of(percent / 100f);
	}

	/**
	 * Converts opacity in color information to percent
	 * It needs to be convertded because backend stores opacity
	 * as float and frontend use percent representation
	 *
	 * @param opacity opacity value
	 * @return percent
	 */
	public static Optional<Integer> opacityToPercent(Float opacity) {
		if(opacity == null) {
			return Optional.empty();
		}
		return Optional.of(Math.round(opacity * 100));
	}

	/**
	 * Create map from list. Result contains list's element as a value and key is a result of keyMapper invoke
	 * @param keyMapper key mapper
	 * @param values list of elements
	 * @param <K> key type
	 * @param <V> value type
	 * @return map
	 */
	public static <K, V> Map<K,V>  toMap(Function<V, K> keyMapper, List<V> values) {
		return values.stream().collect(Collectors.toMap(keyMapper, Function.identity()));
	}

	/**
	 * Collector for create map from list.
	 * Result contains list's element as a value and key is a result of keyMapper invoke
	 *
	 * @param keyMapper key mapper
	 * @param <T>
	 * @param <R>
	 * @param <K>
	 * @return
	 */
	public static <T, R, K> Collector<T, Map<K, T>, Map<K, T>> toSameValueMap(Function<T, R> keyMapper) {
		return new SameValueMapCollector(keyMapper);
	}
}
