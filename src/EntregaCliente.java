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
                if (repo.enTransito.isEmpty() //Todavia me falta entregar pedidos
                        && !repo.enPreparacion.isEmpty() //Todavia tengo pedidos en preparacion que tienen que entregarse
                        && repo.contadorGlobalPedidos.get() < totalPedidos) //Todavia me falta generar pedidos
                {
                    try {
                        repo.enTransito.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
                try{
                    pedido = repo.enTransito.remove(rand.nextInt(repo.enTransito.size()));
                } catch (IllegalArgumentException e){
                    continue;
                }

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
