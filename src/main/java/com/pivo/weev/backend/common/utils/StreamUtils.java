package com.pivo.weev.backend.common.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class StreamUtils {

  /*
   *
   *  return stream of nullable elements
   *
   * */
  public static <T> Stream<T> nullableStream(Collection<T> collection) {
    return Stream.ofNullable(collection).flatMap(Collection::stream);
  }

  /*
   *
   *  return parallel stream of nullable elements
   *
   * */
  public static <T> Stream<T> nullableParallelStream(Collection<T> collection) {
    return Stream.ofNullable(collection).flatMap(Collection::parallelStream);
  }

  /*
   *
   *  return stream of nullable elements
   *
   * */
  @SafeVarargs
  public static <T> Stream<T> nullableStream(T... elements) {
    return Stream.ofNullable(elements).flatMap(Arrays::stream);
  }

  /*
   *
   *  return stream without null elements
   *
   * */
  public static <T> Stream<T> stream(Collection<T> collection) {
    return nullableStream(collection).filter(Objects::nonNull);
  }

  /*
   *
   *  return parallel stream without null elements
   *
   * */
  public static <T> Stream<T> parallelStream(Collection<T> collection) {
    return nullableParallelStream(collection).filter(Objects::nonNull);
  }

  /*
   *
   *  return stream without null elements
   *
   * */
  @SafeVarargs
  public static <T> Stream<T> stream(T... elements) {
    return nullableStream(elements).filter(Objects::nonNull);
  }

  /*
   *
   *  return stream of presented collections
   *
   * */
  @SafeVarargs
  public static <T> Stream<T> flatStream(Collection<T>... collections) {
    return stream(collections).flatMap(Collection::stream);
  }

  /*
   *
   *  return mapped stream by mapper
   *
   * */
  public static <S, T> Stream<S> map(Collection<T> collection, Function<T, S> mapper) {
    return stream(collection).map(mapper);
  }

  /*
   *
   *  return flat mapped stream by flat mapper
   *
   * */
  public static <S, T, R> Stream<R> flatMap(Collection<T> collection, Function<? super T, ? extends Stream<? extends R>> mapper) {
    return stream(collection).flatMap(mapper);
  }

  /*
   *
   *  return mapped stream by mapper
   *
   * */
  public static <S, T> Stream<S> map(T[] elements, Function<T, S> mapper) {
    return stream(elements).map(mapper);
  }


  /*
   *
   *  return filtered stream by condition
   *
   * */
  public static <T> Stream<T> select(Collection<T> collection, Predicate<? super T> condition) {
    return stream(collection).filter(condition);
  }

  /*
   *
   *  return filtered stream by condition
   *
   * */
  public static <T> Stream<T> select(T[] elements, Predicate<? super T> condition) {
    return stream(elements).filter(condition);
  }

  /*
   *
   *  return filtered parallel stream by condition
   *
   * */
  public static <T> Stream<T> selectParallel(Collection<T> collection, Predicate<? super T> condition) {
    return parallelStream(collection).filter(condition);
  }
}
