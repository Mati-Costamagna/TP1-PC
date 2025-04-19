
import java.util.Random;

public class VerificacionFinal extends ProcesoPedido{
    private final Random rand = new Random();

    public VerificacionFinal(RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
    }

    @Override
    public void run() {
        int pedidosVerificados = 0;
        while (!Thread.interrupted()) {
            if (pedidosVerificados >= totalPedidos) break;
            synchronized (repo.entregados) {
                if (!repo.entregados.isEmpty()) {
                    Pedido pedido = repo.entregados.remove(rand.nextInt(repo.entregados.size()));
                    boolean verificado = rand.nextDouble() < 0.95;

                    if (verificado) {
                        pedido.setEstado(EstadoPedido.VERIFICADO);
                        synchronized (repo.verificados) {
                            repo.verificados.add(pedido);
                            pedidosVerificados++;
                        }
                    } else {
                        pedido.setEstado(EstadoPedido.FALLIDO);
                        synchronized (repo.fallidos) {
                            repo.fallidos.add(pedido);
                            pedidosVerificados++;
                        }
                    }
                }
            }
            esperar();
        }
    }
}