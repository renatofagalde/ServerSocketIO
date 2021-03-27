package br.com.likwi.socketIO;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class JoinThreadsHandler implements Callable<Void> {

    private static final Logger logger = Logger.getLogger(JoinThreadsHandler.class.toString());

    private final PrintStream responseParaCliente;
    private final Future<String> futureHTTP;
    private final Future<String> futureSQL;

    public JoinThreadsHandler(PrintStream responseParaCliente, Future<String> futureHTTP, Future<String> futureSQL) {
        this.responseParaCliente = responseParaCliente;
        this.futureHTTP = futureHTTP;
        this.futureSQL = futureSQL;
    }

    @Override
    public Void call() { // umas das vantagens de usar o call(sobre o run) é poder lancar exceção, como quero tratar o
        //timeout, fiz o try-catch

        String idHandler = "Handler-".concat(String.valueOf(new Random().nextInt(1000) + 1));

        logger.info(MessageFormat.format("{0} iniciado", idHandler));
        try {

            final String retornoHttp = this.futureHTTP.get(5, TimeUnit.SECONDS);
            final String retornoSQL = this.futureSQL.get(5, TimeUnit.SECONDS);

            this.responseParaCliente.println(MessageFormat.format("Sucesso ao processar requisicoes, http({0}) e sql({1})", retornoHttp, retornoSQL));

        } catch (InterruptedException | ExecutionException | TimeoutException exception) {
            responseParaCliente.println("[TIMEOUT] ao executar HTTP e SQL");
            logger.info(MessageFormat.format("{0}} cancelado", idHandler));

            //garatir que as threas serão canceladas
            this.futureHTTP.cancel(true);
            this.futureSQL.cancel(true);
        }
        logger.info(MessageFormat.format("{0}} finalizado", idHandler));

        return null;
    }
}
