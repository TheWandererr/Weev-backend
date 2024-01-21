package com.pivo.weev.backend.utils;

import static java.lang.System.arraycopy;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;


/**
 * @author Artyom Konashchenko
 * @since 05.04.2020
 */
@UtilityClass
public class ArrayUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[] copy(T[] array, Class<T> of) {
        if (isEmpty(array)) {
            return array;
        }
        T[] out = (T[]) Array.newInstance(of, array.length);
        arraycopy(array, 0, out, 0, out.length);
        return out;
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] copy(T[][] array, Class<T> of) {
        if (isEmpty(array)) {
            return array;
        }
        int rows = array.length;
        int columns = (array[0]).length;
        T[][] out = (T[][]) Array.newInstance(of, rows, columns);
        for (int i = 0; i < rows; i++) {
            arraycopy(array[i], 0, out[i], 0, columns);
        }
        return out;
    }

    public static double[] copy(double[] arr) {
        double[] dest = new double[arr.length];
        arraycopy(arr, 0, dest, 0, dest.length);
        return dest;
    }

    public static int[] copy(int[] arr) {
        int[] dest = new int[arr.length];
        arraycopy(arr, 0, dest, 0, dest.length);
        return dest;
    }

    public static char[] copy(char[] arr) {
        char[] dest = new char[arr.length];
        arraycopy(arr, 0, dest, 0, dest.length);
        return dest;
    }

    public static <T> boolean isEmpty(T[][] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    public static <T> boolean isEmpty(T[] array) {
        return Objects.isNull(array) || array.length == 0;
    }

    public static char[] toPrimitive(Character[] arr) {
        if (isEmpty(arr)) {
            return new char[0];
        }
        char[] dest = new char[arr.length];
        arraycopy(arr, 0, dest, 0, dest.length);
        return dest;
    }

    public static int[] toPrimitive(Integer[] arr) {
        return StreamUtils.stream(arr).mapToInt(Integer::intValue).toArray();
    }

    public static long[] toPrimitive(Long[] arr) {
        return StreamUtils.stream(arr).mapToLong(Long::longValue).toArray();
    }

    public static double[] toPrimitive(Double[] arr) {
        return StreamUtils.stream(arr).mapToDouble(Double::doubleValue).toArray();
    }

    public static char[][] toPrimitive(Character[][] arr) {
        if (isEmpty(arr)) {
            return new char[0][0];
        }
        char[][] dest = new char[arr.length][(arr[0]).length];
        for (int i = 0; i < dest.length; i++) {
            dest[i] = toPrimitive(arr[i]);
        }
        return dest;
    }

    public static double[][] toPrimitive(Double[][] arr) {
        if (isEmpty(arr)) {
            return new double[0][0];
        }
        double[][] dest = new double[arr.length][(arr[0]).length];
        for (int i = 0; i < dest.length; i++) {
            dest[i] = toPrimitive(arr[i]);
        }
        return dest;
    }

    public static int[][] toPrimitive(Integer[][] arr) {
        if (isEmpty(arr)) {
            return new int[0][0];
        }
        int[][] dest = new int[arr.length][(arr[0]).length];
        for (int i = 0; i < dest.length; i++) {
            dest[i] = toPrimitive(arr[i]);
        }
        return dest;
    }

    public static long[][] toPrimitive(Long[][] arr) {
        if (isEmpty(arr)) {
            return new long[0][0];
        }
        long[][] dest = new long[arr.length][(arr[0]).length];
        for (int i = 0; i < dest.length; i++) {
            dest[i] = toPrimitive(arr[i]);
        }
        return dest;
    }

    @SafeVarargs
    public static <T> List<T> toList(T... values) {
        return StreamUtils.nullableStream(values).toList();
    }

    @SafeVarargs
    public static <T> Set<T> toSet(T... values) {
        return StreamUtils.nullableStream(values).collect(Collectors.toSet());
    }

    @SafeVarargs
    public static <T> T[] toArray(T... items) {
        return items;
    }

    public static <T, S> S[] map(T[] values, Function<T, S> mapper, IntFunction<S[]> gen) {
        return StreamUtils.map(values, mapper).toArray(gen);
    }

    public static <T, S> List<S> mapToList(T[] values, Function<T, S> mapper) {
        return StreamUtils.map(values, mapper).toList();
    }

    public static <T> List<T> selectToList(T[] values, Predicate<T> condition) {
        return StreamUtils.select(values, condition).collect(Collectors.toList());
    }

    public static <T> boolean isPresent(T[] values, Predicate<T> condition) {
        return findFirst(values, condition).isPresent();
    }

    public static <T> Optional<T> first(T[] values) {
        return isEmpty(values) ? Optional.empty() : Optional.of(values[0]);
    }

    public static <T> Optional<T> last(T[] values) {
        return isEmpty(values) ? Optional.empty() : Optional.of(values[values.length - 1]);
    }

    public static <T> Optional<T> findFirst(T[] values, Predicate<T> condition) {
        return StreamUtils.select(values, condition).findFirst();
    }
}
