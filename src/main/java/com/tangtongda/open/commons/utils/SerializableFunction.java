package com.tangtongda.open.commons.utils;

import java.io.Serializable;
import java.util.function.Function;

/**
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2021/6/10
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {}
