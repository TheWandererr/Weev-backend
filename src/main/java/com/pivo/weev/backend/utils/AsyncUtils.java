package com.pivo.weev.backend.utils;

import static java.util.concurrent.CompletableFuture.supplyAsync;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class AsyncUtils {

    public static <S, R> List<R> supplyAll(List<S> payload, Function<S, R> execution, Executor executor) {
        return StreamUtils.map(payload, object -> supplyAsync(() -> execution.apply(object), executor))
                          .map(CompletableFuture::join)
                          .toList();
    }
}
