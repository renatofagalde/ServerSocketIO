package br.com.likwi.socketIO;

import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CaughtThreadException implements Thread.UncaughtExceptionHandler {

    private static final Logger logger = Logger.getLogger(CaughtThreadException.class.toString());

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.log(Level.SEVERE, MessageFormat.format("Erro ao executar {0}, motivo {1}", t.getName(), e.getLocalizedMessage()));
    }
}
