import java.util.Random;

public class VerificacionFinal extends ProcesoPedido {
    private final Random rand = new Random();

    public VerificacionFinal(RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
    }

    @Override
    public void run() {

        while ((repo.pedidosVerificados.get()+repo.pedidosFallidos.get()) <  totalPedidos) {

            Pedido pedido = null;

            // Sincronización sobre repo.entregados para acceder a los pedidos entregados
            synchronized (repo.entregados) {
                if (!repo.entregados.isEmpty()) {
                    pedido = repo.entregados.remove(rand.nextInt(repo.entregados.size()));
                }
            }

            // Si no hay pedido, esperamos y seguimos
            if (pedido == null) {
                esperar();
                System.out.println("Esperando...");
                continue;
            }

            // Simulación de verificación
            boolean verificado = rand.nextDouble() < 0.95;

            if (verificado) {
                pedido.setEstado(EstadoPedido.VERIFICADO);
                synchronized (repo.verificados) {
                    repo.verificados.add(pedido);
                    repo.pedidosVerificados.incrementAndGet();
                }
                System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " verificado correctamente.");
            } else {
                pedido.setEstado(EstadoPedido.FALLIDO);
                synchronized (repo.fallidos) {
                    repo.fallidos.add(pedido);
                    repo.pedidosFallidos.incrementAndGet();
                }
                System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " falló la verificación final.");
            }

            esperar();
        }
    }
}
