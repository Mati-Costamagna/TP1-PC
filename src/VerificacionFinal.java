import java.util.Random;

public class VerificacionFinal extends ProcesoPedido {
    private final Random rand = new Random();

    public VerificacionFinal(RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
    }

    @Override
    public void run() {
        while ((repo.pedidosVerificados.get() + repo.fallidos.size()) < totalPedidos) {
            Pedido pedido = null;

            synchronized (repo.entregados) {
                // **Verificamos la condición *DENTRO* del bloque synchronized**
                if (repo.entregados.isEmpty()) {
                    // **Revisamos *otra vez* si ya terminaron antes de esperar**
                    if ((repo.pedidosVerificados.get() + repo.fallidos.size()) >= totalPedidos) {
                        System.out.println("Ya se procesaron todos los pedidos. Sale " + Thread.currentThread().getName());
                        return;
                    }
                    try {
                        System.out.println("esperando " + Thread.currentThread().getName());
                        repo.entregados.wait();
                        System.out.println("despertado " + Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    // **¡¡¡VOLVEMOS A VERIFICAR DESPUÉS DEL WAIT!!!**
                    if (repo.entregados.isEmpty()) {
                        return; // Puede que otro hilo haya tomado el último pedido
                    }
                }

                try {
                    System.out.println(repo.entregados.size() + " " + Thread.currentThread().getName());
                    pedido = repo.entregados.remove(rand.nextInt(repo.entregados.size()));
                    System.out.println(repo.entregados.size() + " " + Thread.currentThread().getName());
                } catch (IllegalArgumentException e) {
                    continue;
                }
            }

            // Simular verificación
            boolean verificado = rand.nextDouble() < 0.95;

            if (verificado) {
                pedido.setEstado(EstadoPedido.VERIFICADO);
                synchronized (repo.verificados) {
                    repo.verificados.add(pedido);
                    repo.pedidosVerificados.incrementAndGet();
                    System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " verificado correctamente.");
                }
            } else {
                pedido.setEstado(EstadoPedido.FALLIDO);
                synchronized (repo.fallidos) {
                    repo.fallidos.add(pedido);
                    System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " falló la verificación final.");
                }
            }

            esperar();
        }
    }
}