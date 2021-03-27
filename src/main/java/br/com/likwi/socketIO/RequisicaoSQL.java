package br.com.likwi.socketIO;

import java.io.PrintStream;
import java.util.Random;
import java.util.concurrent.Callable;

public class RequisicaoSQL implements Callable<String> {
    private final PrintStream responseParaCliente;

    public RequisicaoSQL(PrintStream responseParaCliente) {

        this.responseParaCliente = responseParaCliente;
    }

    @Override
    public String call() throws Exception {
        responseParaCliente.println("sql processado");
        Thread.sleep(new Random().nextInt(6000));
        return "[sql-sucesso] ".concat(String.valueOf(new Random().nextInt(100)+1));
        //throw new RuntimeException("exception no erro no sql");
    }
}
