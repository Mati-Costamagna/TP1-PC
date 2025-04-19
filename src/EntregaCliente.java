import java.util.Random;

public class EntregaCliente extends ProcesoPedido {
    private final Random rand = new Random();

    public EntregaCliente(RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
    }

    @Override
    public void run() {
        while (true) {
            Pedido pedido = null;

            synchronized (repo.enTransito) {
                if (!repo.enTransito.isEmpty()) {
                    int index = rand.nextInt(repo.enTransito.size());
                    pedido = repo.enTransito.remove(index);
                } else if (repo.pedidosDespachados.get() == totalPedidos) {
                    // No hay más pedidos que puedan llegar a esta etapa
                    break;
                }
            }

            if (pedido == null) {
                esperar();
                continue;
            }

            boolean entregado = rand.nextDouble() < 0.90;

            if (entregado) {
                pedido.setEstado(EstadoPedido.ENTREGADO);
                synchronized (repo.entregados) {
                    repo.entregados.add(pedido);
                    repo.pedidosEntregados.incrementAndGet();
                }
                System.out.println("[ENTREGA] Pedido #" + pedido.getId() + " entregado correctamente.");
            } else {
                pedido.setEstado(EstadoPedido.FALLIDO);
                synchronized (repo.fallidos) {
                    repo.fallidos.add(pedido);
                }
                System.out.println("[ENTREGA] Pedido #" + pedido.getId() + " falló en la entrega.");
            }

            esperar();
        }
    }

}
