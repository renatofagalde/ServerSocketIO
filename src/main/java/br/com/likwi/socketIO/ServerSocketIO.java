package br.com.likwi.socketIO;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerSocketIO {
    //export JAVA_HOME="/Library/Java/JavaVirtualMachines/jdk1.8.0_45.jdk/Contents/Home"
    //-Dexec.mainClass="br.com.likwi.socketIO.ServerSocketIO" or use mojo plugin

    private static final Logger logger = Logger.getLogger(ServerSocketIO.class.toString());
    private final ServerSocket servidor;
    private final ExecutorService threads;
    //volatile -> evita o cache neste caso de threads
    //wrapr para está opcao: AtomicBoolean
//    private volatile boolean manterServidorNoAr = Boolean.TRUE;
    private AtomicBoolean manterServidorNoAr = new AtomicBoolean(true);

    public ServerSocketIO() throws IOException {
        this.servidor = new ServerSocket(3300);
        this.threads = Executors.newCachedThreadPool(new FabricaThread());
    }

    public static void main(String[] args) throws Exception {

        logger.info("---- Iniciando Servidor ----");
        final ServerSocketIO serverSocketIO = new ServerSocketIO();
        serverSocketIO.subirServidor();
        serverSocketIO.pararServidor();
    }

    public void pararServidor() throws IOException {
        this.manterServidorNoAr.set(false); // this.manterServidorNoAr = Boolean.FALSE;
        this.threads.shutdown();
        this.servidor.close();
        logger.warning("** SERVIDOR FINALIZADO **");
    }

    public void subirServidor() throws IOException {
        while (this.manterServidorNoAr.get()) {
            try {
                Socket socket = servidor.accept();
                logger.info(MessageFormat.format("Nova requisição na porta ", "a"));
                Requisicoes requisicao = new Requisicoes(threads, socket, this);
                threads.submit(requisicao);
            } catch (SocketException e) {
                logger.log(Level.WARNING, "O servidor está rodando? {0}: " + (this.manterServidorNoAr.get() ? "SIM" : "NÃO"));
            }
        }
    }
}
