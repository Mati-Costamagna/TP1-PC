import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class LoggerEstadistico extends Thread {
    private final RepositorioPedidos repo;
    private final AtomicBoolean finalizar = new AtomicBoolean(false);
    private final long inicio;
    private BufferedWriter writer;
    private final Casillero[] casilleros;

    public LoggerEstadistico(RepositorioPedidos repo, long inicio, Casillero[] casillero) {
        this.repo = repo;
        this.inicio = inicio;
        this.casilleros = casillero;
        try {
            this.writer = new BufferedWriter(new FileWriter("log_estadisticas.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void finalizar() {
        finalizar.set(true);
    }

    @Override
    public void run() {
        try {
            while (!finalizar.get()) {
                writer.write("Pedidos fallidos: " + repo.pedidosFallidos.get() +
                        ", verificados: " + repo.pedidosVerificados.get() + "\n");
                writer.flush();
                Thread.sleep(200);
            }

            // Finalizar log con estadísticas finales
            writer.write("\n--- Estadísticas Finales ---\n");
            writer.write("Tiempo total: " + (System.currentTimeMillis() - inicio) + " ms\n");

            for (int i = 0; i < this.casilleros.length; i++) {
                Casillero c = this.casilleros[i];
                writer.write("Casillero #" + i + " - Estado final: " + c.getEstado().toString() + "\n");
            }

            writer.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
