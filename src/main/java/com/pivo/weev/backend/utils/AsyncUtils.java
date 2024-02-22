package com.pivo.weev.backend.utils;

import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static java.util.concurrent.CompletableFuture.allOf;
import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class AsyncUtils {

    public static <R> CompletableFuture<R> async(Executor executor, Supplier<R> supplier, Function<Throwable, R> errorHandler) {
        return supplyAsync(supplier, executor).exceptionally(errorHandler);
    }

    public static CompletableFuture<Void> async(Executor executor, Runnable runnable, Function<Throwable, Void> errorHandler) {
        return runAsync(runnable, executor).exceptionally(errorHandler);
    }

    public static <R> List<R> collectAll(Executor executor, List<Supplier<R>> suppliers, Function<Throwable, R> errorHandler) {
        List<CompletableFuture<R>> futures = mapToList(suppliers, supplier -> async(executor, supplier, errorHandler));
        return collectAll(futures);
    }

    public static <R> List<R> collectAll(List<CompletableFuture<R>> futures) {
        return allOf(futures.toArray(CompletableFuture[]::new))
                .thenApply(execution -> joinAll(futures))
                .join();
    }

    public static <R> List<R> joinAll(List<CompletableFuture<R>> futures) {
        return futures.stream()
                      .map(CompletableFuture::join)
                      .filter(Objects::nonNull)
                      .collect(toList());
    }

    public static void runAll(Executor executor, List<Runnable> runnables, Function<Throwable, Void> errorHandler) {
        List<CompletableFuture<Void>> futures = mapToList(runnables, runnable -> async(executor, runnable, errorHandler));
        allOf(futures.toArray(CompletableFuture[]::new)).join();
    }
}
