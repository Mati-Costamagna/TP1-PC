
import java.util.Random;

public class VerificacionFinal extends ProcesoPedido{
    private final Random rand = new Random();

    public VerificacionFinal(RepositorioPedidos repo, int tiempoEspera) {
        super(repo, tiempoEspera);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (repo.entregados) {
                if (repo.entregados.isEmpty()) {
                    break;
                }
                else {
                    Pedido pedido = repo.entregados.remove(rand.nextInt(repo.entregados.size()));
                    boolean verificado = rand.nextDouble() < 0.95;

                    if (verificado) {
                        pedido.setEstado(EstadoPedido.VERIFICADO);
                        synchronized (repo.verificados) {
                            repo.verificados.add(pedido);
                        }
                    } else {
                        pedido.setEstado(EstadoPedido.FALLIDO);
                        synchronized (repo.fallidos) {
                            repo.fallidos.add(pedido);
                        }
                    }
                }
            }
            esperar();
        }
    }
}