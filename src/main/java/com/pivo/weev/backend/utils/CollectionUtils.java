package com.pivo.weev.backend.utils;

import static com.pivo.weev.backend.utils.StreamUtils.flatMap;
import static com.pivo.weev.backend.utils.StreamUtils.flatStream;
import static com.pivo.weev.backend.utils.StreamUtils.map;
import static com.pivo.weev.backend.utils.StreamUtils.nullableStream;
import static com.pivo.weev.backend.utils.StreamUtils.parallelStream;
import static com.pivo.weev.backend.utils.StreamUtils.select;
import static com.pivo.weev.backend.utils.StreamUtils.stream;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CollectionUtils {

    public static <T> T[] toArray(Collection<T> collection, IntFunction<T[]> generator) {
        return stream(collection).toArray(generator);
    }

    public static <T> T[] selectToArray(Collection<T> collection, Predicate<T> condition, IntFunction<T[]> generator) {
        return select(collection, condition).toArray(generator);
    }

    public static <T, V> V[] mapToArray(Collection<T> collection, Function<T, V> mapper, IntFunction<V[]> generator) {
        return map(collection, mapper).toArray(generator);
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <T> boolean isSingle(Collection<T> collection) {
        return !isEmpty(collection) && collection.size() == 1;
    }

    public static <T> List<T> selectToList(Collection<T> collection, Predicate<? super T> condition) {
        return select(collection, condition).collect(toList());
    }

    public static <S, T> List<S> selectToList(Collection<T> collection, Predicate<? super T> condition, Function<T, S> mapper) {
        return select(collection, condition).map(mapper).collect(toList());
    }

    public static <S, T> List<T> mapToList(Collection<S> collection, Function<S, T> mapper) {
        return map(collection, mapper).collect(toList());
    }

    public static <S, T> List<T> mapToList(Collection<S> collection, Function<S, T> mapper, Predicate<? super T> condition) {
        return map(collection, mapper).filter(condition).collect(toList());
    }

    public static <S, T> Set<T> mapToSet(Collection<S> collection, Function<S, T> mapper) {
        return map(collection, mapper).collect(Collectors.toSet());
    }

    public static <S, T> Set<T> mapToSet(Collection<S> collection, Function<S, T> mapper, Predicate<? super T> condition) {
        return map(collection, mapper).filter(condition).collect(Collectors.toSet());
    }

    public static <S, T> List<T> flatMapToList(Collection<S> collection, Function<? super S, ? extends Stream<? extends T>> mapper) {
        return flatMap(collection, mapper).collect(toList());
    }

    public static <S, T> Set<T> flatMapToSet(Collection<S> collection, Function<? super S, ? extends Stream<? extends T>> mapper) {
        return flatMap(collection, mapper).collect(Collectors.toSet());
    }

    public static <T> List<T> list(Collection<T> collection) {
        return nullableStream(collection).collect(toList());
    }

    public static <T> Set<T> set(Collection<T> collection) {
        return nullableStream(collection).collect(Collectors.toSet());
    }

    @SafeVarargs
    public static <T> List<T> union(Collection<T>... collections) {
        return flatStream(collections).collect(toList());
    }

    public static <T> Optional<T> findFirst(Collection<T> collection, Predicate<? super T> condition) {
        return select(collection, condition).findFirst();
    }

    public static <T> Optional<T> firstOptional(Collection<T> collection) {
        return nullableStream(collection).findFirst();
    }

    public static <T> T first(Collection<T> collection) {
        return firstOptional(collection).orElse(null);
    }

    public static <T> Optional<T> findLast(Collection<T> collection, Predicate<? super T> condition) {
        return select(collection, condition).reduce((prev, next) -> next);
    }

    public static <T> Optional<T> lastOptional(Collection<T> collection) {
        return nullableStream(collection).reduce((prev, next) -> next);
    }

    public static <T> T last(Collection<T> collection) {
        return lastOptional(collection).orElse(null);
    }

    public static <T> boolean isPresent(Collection<T> collection, Predicate<? super T> condition) {
        return stream(collection).anyMatch(condition);
    }

    public static <T> T remove(List<T> list, int index) {
        return list.remove(index);
    }

    public static <T> T pop(List<T> list) {
        return remove(list, 0);
    }

    public static <T> boolean containsAny(Collection<T> source, Collection<T> samples) {
        return isPresent(samples, source::contains);
    }

    public static <R, S, A, T> R collect(Collection<T> source, Function<T, S> mapper, Collector<? super S, A, R> collector) {
        return stream(source).map(mapper).collect(collector);
    }

    public static <R, A, T> R collect(Collection<T> source, Predicate<T> filter, Collector<? super T, A, R> collector) {
        return stream(source).filter(filter).collect(collector);
    }

    public static <R, A, T> R collect(Collection<T> source, Collector<? super T, A, R> collector) {
        return stream(source).collect(collector);
    }

    public static <R, A, T> R collectAsync(Collection<T> source, Collector<? super T, A, R> collector) {
        return parallelStream(source).collect(collector);
    }

    public static <T> Set<T> newHashSet(Collection<T> collection) {
        return set(collection);
    }

    public static <T> List<T> newArrayList(Collection<T> collection) {
        return list(collection);
    }

    public static <T> boolean allMatch(Collection<T> collection, Predicate<? super T> condition) {
        if (isEmpty(collection)) {
            return false;
        }
        return stream(collection).allMatch(condition);
    }

    public static <T> List<T> subList(Collection<T> collection, long offset, long limit) {
        return stream(collection)
                .skip(offset)
                .limit(limit)
                .collect(toList());
    }
}
