import java.util.concurrent.TimeUnit;
import java.util.Random;

public class DespachoPedido extends ProcesoPedido {
    private final Casillero[] casilleros;
    private final Random rand = new Random();


    public DespachoPedido(Casillero[] casilleros, RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, tiempoEspera, totalPedidos);
        this.casilleros = casilleros;
    }

    @Override
    public void run() {
        int pedidosDespachados = 0;
        while (!Thread.currentThread().isInterrupted()) {
            if (pedidosDespachados >= totalPedidos) break;

            Pedido pedido = null;

            synchronized (repo.enPreparacion) {
                if (!repo.enPreparacion.isEmpty()) {
                    int index = rand.nextInt(repo.enPreparacion.size());
                    pedido = repo.enPreparacion.remove(index);
                }
            }

            if (pedido == null) {
                esperar();
                continue;
            }

            synchronized (pedido) {
                Casillero casillero = casilleros[pedido.getIdCasillero()];
                boolean datosCorrectos = rand.nextDouble() < 0.85;

                if (datosCorrectos) {
                    casillero.liberar();
                    pedido.setEstado(EstadoPedido.EN_TRANSITO);
                    synchronized (repo.enTransito) {
                        repo.enTransito.add(pedido);
                    }
                    System.out.println("[DESPACHO] Pedido #" + pedido.getId() + " despachado con éxito.");
                    pedidosDespachados++;
                } else {
                    casillero.ponerFueraDeServicio();
                    pedido.setEstado(EstadoPedido.FALLIDO);
                    synchronized (repo.fallidos) {
                        repo.fallidos.add(pedido);
                    }
                    System.out.println("[DESPACHO] Pedido #" + pedido.getId() + " falló verificación y casillero marcado FDS.");
                    pedidosDespachados++;
                }
            }
            if(repo.enPreparacion.isEmpty()) {}
            esperar();
        }

    }
}
