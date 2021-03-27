package br.com.likwi.socketIO;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Requisicoes implements Runnable {

    private final ExecutorService threads;
    private Socket socket;
    private ServerSocketIO serverSocketIO;

    private static final Logger logger = Logger.getLogger(Requisicoes.class.toString());

    public Requisicoes(ExecutorService threads, Socket socket, ServerSocketIO serverSocketIO) {
        this.threads = threads;
        this.socket = socket;
        this.serverSocketIO = serverSocketIO;
    }

    @Override
    public void run() {

        logger.info(MessageFormat.format("Requisição INICIADA na porta {0}",
                String.valueOf(this.socket.getPort())));

        try (final Scanner requisicao = new Scanner(this.socket.getInputStream())) {
            final PrintStream responseParaCliente = new PrintStream(this.socket.getOutputStream());
            while (requisicao.hasNextLine()) {
                final String comandoVindoDoCliente = requisicao.nextLine();
                logger.info(MessageFormat.format("Comando {0}", comandoVindoDoCliente));

                switch (comandoVindoDoCliente) {

                    case "http":{
                        //reaproveitando o pool de threads
                        final RequisicaoHTTP requisicaoHTTP = new RequisicaoHTTP(responseParaCliente);
                        this.threads.submit(requisicaoHTTP);
                        break;
                    }
                    case "sql":{
                        //reaproveitando o pool de threads
                        final RequisicaoSQL requisicaoSQL = new RequisicaoSQL(responseParaCliente);
                        this.threads.submit(requisicaoSQL);
                        break;
                    }
                    case "http-sql":{
                        //reaproveitando o pool de threads
                        final RequisicaoHTTP requisicaoHTTP = new RequisicaoHTTP(responseParaCliente);
                        final RequisicaoSQL requisicaoSQL = new RequisicaoSQL(responseParaCliente);
                        final Future<String> futureHTTP = this.threads.submit(requisicaoHTTP);
                        final Future<String> futureSQL = this.threads.submit(requisicaoSQL);

                        this.threads.submit(new JoinThreadsHandler(responseParaCliente,futureHTTP,futureSQL));

                        break;
                    }
                    case "fim":{
                    responseParaCliente.println(MessageFormat.format("** {0} servidor será desligado em 5 segundos", comandoVindoDoCliente));
                        this.serverSocketIO.pararServidor();
                    }
                }
                logger.info(MessageFormat.format("Comando recebido {0}", comandoVindoDoCliente));
            }
            responseParaCliente.close();
            requisicao.close();

            Thread.sleep(1000); //simulate a long process
        } catch (InterruptedException | IOException e) {
            logger.log(Level.SEVERE, MessageFormat.format("Erro grave {0}", e.getLocalizedMessage()));
            throw new RuntimeException(e);
        }

        logger.info("Requisição FINALIZADA\n\n");
    }
}
