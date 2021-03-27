package br.com.likwi.socketIO;

import java.io.PrintStream;
import java.util.Random;
import java.util.concurrent.Callable;

public class RequisicaoHTTP implements Callable<String> {
    private final PrintStream responseParaCliente;

    public RequisicaoHTTP(PrintStream responseParaCliente) {

        this.responseParaCliente = responseParaCliente;
    }

    @Override
    public String call() throws Exception {
        responseParaCliente.println("http feito");
        Thread.sleep(new Random().nextInt(6000));
        return "[http-sucesso] ".concat(String.valueOf(new Random().nextInt(100)+1));
    }
}
