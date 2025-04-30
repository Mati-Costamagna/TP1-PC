import java.util.Random;

public class EntregaCliente extends ProcesoPedido {
    private final Random rand = new Random();

    public EntregaCliente(RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
    }

    @Override
    public void run() {
        while (repo.pedidosEntregados.get() + repo.pedidosFallidos.get() < totalPedidos) {
            Pedido pedido = null;

            synchronized (repo.enTransito) {
                if (repo.enTransito.isEmpty()
                        && repo.pedidosEntregados.get() + repo.pedidosFallidos.get() < totalPedidos) {
                    try {
                        System.out.println("Esperando en transito");
                        repo.enTransito.wait();
                        System.out.println("Toy en transito");
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                if (repo.enTransito.isEmpty())
                    continue;

                int index = rand.nextInt(repo.enTransito.size());
                pedido = repo.enTransito.remove(index);

            }

            boolean entregado = rand.nextDouble() < 0.90;

            if (entregado) {
                pedido.setEstado(EstadoPedido.ENTREGADO);
                synchronized (repo.entregados) {
                    repo.entregados.add(pedido);
                    repo.pedidosEntregados.incrementAndGet();
                    repo.entregados.notifyAll();
                    System.out.println("[ENTREGA] Pedido #" + pedido.getId() + " entregado correctamente.");
                }
            } else {
                pedido.setEstado(EstadoPedido.FALLIDO);
                synchronized (repo.fallidos) {
                    repo.fallidos.add(pedido);
                    repo.pedidosFallidos.incrementAndGet();
                    System.out.println("[ENTREGA] Pedido #" + pedido.getId() + " falló en la entrega.");
                }
            }
            esperar();
        }
    }
}
