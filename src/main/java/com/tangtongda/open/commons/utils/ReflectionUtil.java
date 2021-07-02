package com.tangtongda.open.commons.utils;

import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.ClassUtils;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data Object Filed reflection util
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2021/6/10
 */
public class ReflectionUtil {

  private ReflectionUtil() {}

  private static final Map<SerializableFunction<?, ?>, Field> cache = new ConcurrentHashMap<>();

  /**
   * get field name
   *
   * @param function lambda function
   * @param <T> Object
   * @param <R> return
   * @return name
   */
  public static <T, R> String getFieldName(SerializableFunction<T, R> function) {
    Field field = getField(function);
    return field.getName();
  }

  /**
   * Find field
   *
   * @param function lambda function
   * @return Field
   */
  public static Field getField(SerializableFunction<?, ?> function) {
    return cache.computeIfAbsent(function, ReflectionUtil::findField);
  }

  /**
   * Find filed by entity:property lambda
   *
   * @param function lambda function
   * @return Field
   */
  private static Field findField(SerializableFunction<?, ?> function) {
    Field field = null;
    String fieldName = null;
    try {
      Method method = function.getClass().getDeclaredMethod("writeReplace");
      method.setAccessible(Boolean.TRUE);
      SerializedLambda serializedLambda = (SerializedLambda) method.invoke(function);
      String implMethodName = serializedLambda.getImplMethodName();
      if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
        fieldName = Introspector.decapitalize(implMethodName.substring(3));

      } else if (implMethodName.startsWith("is") && implMethodName.length() > 2) {
        fieldName = Introspector.decapitalize(implMethodName.substring(2));
      } else if (implMethodName.startsWith("lambda$")) {
        throw new IllegalArgumentException("SerializableFunction cannot transform lambda");

      } else {
        throw new IllegalArgumentException(implMethodName + "is not getter method");
      }
      String declaredClass = serializedLambda.getImplClass().replace("/", ".");
      Class<?> aClass = Class.forName(declaredClass, false, ClassUtils.getDefaultClassLoader());

      field = ReflectionUtils.findRequiredField(aClass, fieldName);

    } catch (Exception e) {
      e.printStackTrace();
    }
    if (field != null) {
      return field;
    }
    throw new NoSuchFieldError(fieldName);
  }
}
