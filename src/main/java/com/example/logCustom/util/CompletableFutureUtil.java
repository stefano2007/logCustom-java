package com.example.logCustom.util;

import org.slf4j.MDC;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class CompletableFutureUtil {
    public static CompletableFuture supplyCustomizado(Supplier funcao){
        var mdcContext = MDC.getCopyOfContextMap();
        return CompletableFuture.supplyAsync(() -> {
            if (mdcContext != null) {
                MDC.setContextMap(mdcContext);
            }
            return funcao.get();
        });
    }
}
