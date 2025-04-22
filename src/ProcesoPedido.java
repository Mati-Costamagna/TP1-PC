import java.util.concurrent.TimeUnit;

public abstract class ProcesoPedido implements Runnable {
    protected final RepositorioPedidos repo;
    protected final int tiempoEspera;

    public ProcesoPedido(RepositorioPedidos repo, int tiempoEspera) {
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

    public abstract void run();
}

