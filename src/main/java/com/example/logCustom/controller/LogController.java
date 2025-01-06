package com.example.logCustom.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.MDC;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("api")
public class LogController {

    private static final Logger logger = Logger.getLogger(String.valueOf(LogController.class));

    @GetMapping("logMDC")
    public ResponseEntity<String > Get(@RequestParam("valor") String valor)
    {
        // Configurar o MDC fora das chamadas assíncronas
        MDC.put("correlationId", UUID.randomUUID().toString());
        MDC.put("idJornada", valor);

        // Capturar o contexto MDC
        var mdcContext = MDC.getCopyOfContextMap();

        logger.log(Level.INFO, "Log antes da chamada assincronas.");

        // Primeira chamada concorrente
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {
            if (mdcContext != null) MDC.setContextMap(mdcContext);

            // Logica da primeira operação
            logger.log(Level.INFO, "future1 log.");
            return "Resultado da Operação 1";
        });

        // Segunda chamada concorrente
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {
            if (mdcContext != null) MDC.setContextMap(mdcContext);

            // Logica da segunda operação
            logger.log(Level.INFO, "future2 log.");
            return "Resultado da Operação 2";
        });

        // Aguardar as operações completarem
        try {
            CompletableFuture.allOf(future1, future2).join();

            logger.log(Level.INFO,future1.get());
            logger.log(Level.INFO,future2.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(String.format("idJornada: %s", MDC.get("idJornada")));
    }
}
