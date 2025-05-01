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
                // if (repo.entregados.isEmpty()
                // && (repo.pedidosVerificados.get() + repo.fallidos.size()) < totalPedidos) {
                // try {
                // System.out.println("Esperando entregados" +
                // Thread.currentThread().getName());
                // repo.entregados.wait();
                // System.out.println("Toy entregando");
                // } catch (InterruptedException e) {
                // Thread.currentThread().interrupt();
                // return;
                // }
                // }

                if (repo.entregados.isEmpty()) {
                    System.out.println("Esperando entregas");
                    continue;
                }
                pedido = repo.entregados.remove(rand.nextInt(repo.entregados.size()));
            }

            boolean verificado = rand.nextDouble() < 0.95;

            if (verificado) {
                pedido.setEstado(EstadoPedido.VERIFICADO);
                synchronized (repo.verificados) {
                    repo.verificados.add(pedido);
                    repo.pedidosVerificados.incrementAndGet();
                    System.out.println("[VERIFICACION " + Thread.currentThread().getName() + "] Pedido #"
                            + pedido.getId() + " verificado correctamente.");
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