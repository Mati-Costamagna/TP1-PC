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
    private long finPreparacion = -1;
    private long finDespacho = -1;
    private long finEntrega = -1;
    private long finVerificacion = -1;

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
    public void setFinPreparacion() {
        this.finPreparacion = System.currentTimeMillis();
    }
    public void setFinDespacho() {
        this.finDespacho = System.currentTimeMillis();
    }
    public void setFinEntrega() {
        this.finEntrega = System.currentTimeMillis();
    }
    public void setFinVerificacion() {
        this.finVerificacion = System.currentTimeMillis();
    }
    public void finalizar() {
        finalizar.set(true);
    }

    @Override
    public void run() {
        try {
            // Log periódico
            while (!finalizar.get()) {
                writer.write("Pedidos fallidos: " + repo.fallidos.size() + ", verificados: " + repo.pedidosVerificados.get() + "\n");
                writer.flush();
                Thread.sleep(200);
            }

            // Estadísticas finales
            writer.write("\n--- ESTADÍSTICAS FINALES ---\n");
            writer.write("Tiempo total: " + (System.currentTimeMillis() - inicio) + " ms\n");
            writer.write("Pedidos fallidos: " + repo.fallidos.size() + ", verificados: " + repo.pedidosVerificados.get() + "\n");

            writer.write("--- ESTADO DE CASILLEROS ---\n");
            for (int i = 0; i < this.casilleros.length; i++) {
                Casillero c = this.casilleros[i];
                String estado;
                synchronized (c) {
                    if (c.getEstado()==EstadoCasillero.FUERA_DE_SERVICIO) estado = "FUERA DE SERVICIO";
                    else if (c.estaDisponible()) estado = "VACÍO";
                    else estado = "OCUPADO";
                }
                writer.write("Casillero #" + i + " | Estado: " + estado + " | Usado: " + c.getContador() + " veces\n");
            }
            writer.write("\n--- TIEMPOS POR ETAPA (duraciones reales) ---\n");

            if (finPreparacion != -1)
                writer.write("Duración de preparación: " + (finPreparacion - inicio) + " ms\n");
            else
                writer.write("Preparación no completada\n");

            if (finDespacho != -1 && finPreparacion != -1)
                writer.write("Duración de despacho: " + (finDespacho - finPreparacion) + " ms\n");
            else
                writer.write("Despacho no completado o preparación faltante\n");

            if (finEntrega != -1 && finDespacho != -1)
                writer.write("Duración de entrega: " + (finEntrega - finDespacho) + " ms\n");
            else
                writer.write("Entrega no completada o despacho faltante\n");

            if (finVerificacion != -1 && finEntrega != -1)
                writer.write("Duración de verificación: " + (finVerificacion - finEntrega) + " ms\n");
            else
                writer.write("Verificación no completada o entrega faltante\n");

            writer.write("Tiempo total (inicio → fin de verificación): " + (finVerificacion - inicio) + " ms\n");

            writer.close();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}