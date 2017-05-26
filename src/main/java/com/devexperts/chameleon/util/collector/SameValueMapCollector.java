package com.devexperts.chameleon.util.collector;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SameValueMapCollector<K, T> implements Collector<T, Map<K, T>, Map<K, T>> {

	Function<? super T, ? extends K> keyMapper;

	public SameValueMapCollector(Function<? super T, ? extends K> keyMapper) {
		this.keyMapper = keyMapper;
	}

	@Override
	public Supplier<Map<K, T>> supplier() {
		return HashMap::new;
	}

	@Override
	public BiConsumer<Map<K, T>, T> accumulator() {
		return (map, element) -> map.merge(keyMapper.apply(element), element, throwingMerger());
	}

	@Override
	public BinaryOperator<Map<K, T>> combiner() {
		return (m1, m2) -> {
			for (Map.Entry<K,T> e : m2.entrySet())
				m1.merge(e.getKey(), e.getValue(), throwingMerger());
			return m1;
		};
	}

	@Override
	public Function<Map<K, T>, Map<K, T>> finisher() {
		return i -> (Map<K, T>) i;
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Collections.singleton(Characteristics.IDENTITY_FINISH);
	}

	private static <T> BinaryOperator<T> throwingMerger() {
		return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
	}
}
