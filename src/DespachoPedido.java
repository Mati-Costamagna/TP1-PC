import java.util.Random;

public class DespachoPedido extends ProcesoPedido {
    private final Casillero[] casilleros;
    private final Random rand = new Random();

    public DespachoPedido(Casillero[] casilleros, RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
        this.casilleros = casilleros;
    }

    @Override
    public void run() {
        while (repo.pedidosDespachados.get() < totalPedidos) {
            Pedido pedido = null;

            synchronized (repo.enPreparacion) {
                if (repo.enPreparacion.isEmpty()) {
                    try {
                        System.out.println("esperando despacho " + Thread.currentThread().getName());
                        repo.enPreparacion.wait();
                        System.out.println("toy despachado " + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                try {
                    int index = rand.nextInt(repo.enPreparacion.size());
                    pedido = repo.enPreparacion.remove(index);
                } catch (IllegalArgumentException e){
                    continue;
                }
            }

            Casillero casillero = casilleros[pedido.getIdCasillero()];
            boolean datosCorrectos = rand.nextDouble() < 0.85;

            if (datosCorrectos) {
                casillero.liberar();
                pedido.setEstado(EstadoPedido.EN_TRANSITO);
                synchronized (repo.enTransito) {
                    repo.enTransito.add(pedido);
                    repo.pedidosDespachados.incrementAndGet();
                    repo.enTransito.notifyAll();
                    System.out.println("[DESPACHO] Pedido #" + pedido.getId() + " despachado con éxito.");
                }
            } else {
                casillero.ponerFueraDeServicio();
                pedido.setEstado(EstadoPedido.FALLIDO);
                synchronized (repo.fallidos) {
                    repo.fallidos.add(pedido);
                    repo.pedidosDespachados.incrementAndGet();
                    repo.pedidosFallidos.incrementAndGet();
                    System.out.println("[DESPACHO] Pedido #" + pedido.getId() + " falló verificación y casillero marcado FDS.");
                }
            }
            esperar();
        }
    }
}
