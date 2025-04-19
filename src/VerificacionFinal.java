import java.util.Random;

public class VerificacionFinal extends ProcesoPedido {
    private final Random rand = new Random();

    public VerificacionFinal(RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
    }

    @Override
    public void run() {
        int pedidosVerificados = 0;
        while (true) {

            Pedido pedido = null;

            // Sincronización sobre repo.entregados para acceder a los pedidos entregados
            synchronized (repo.entregados) {
                // Si ya no hay más pedidos entregados, y se han entregado todos, terminamos
                if (repo.entregados.isEmpty()) {
                    break; // Terminamos porque ya no hay pedidos por verificar
                }

                // Si hay pedidos entregados, seguimos con la verificación
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
                }
                System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " verificado correctamente.");
            } else {
                pedido.setEstado(EstadoPedido.FALLIDO);
                synchronized (repo.fallidos) {
                    repo.fallidos.add(pedido);
                }
                System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " falló la verificación final.");
            }

            // Pausa después de cada verificación
            pedidosVerificados++;
            esperar();
        }
    }
}
