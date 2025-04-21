import java.util.concurrent.TimeUnit;

public abstract class ProcesoPedido implements Runnable {
    protected final RepositorioPedidos repo;
    protected final int tiempoEspera;
    protected final int totalPedidos;


    public ProcesoPedido(RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        this.totalPedidos = totalPedidos;
        this.repo = repo;
        this.tiempoEspera = tiempoEspera;
    }

    protected void esperar() {
        try {
            TimeUnit.MILLISECONDS.sleep(tiempoEspera);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}