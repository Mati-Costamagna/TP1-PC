
import java.util.Random;

public class VerificacionFinal extends ProcesoPedido{
    private final Random rand = new Random();

    public VerificacionFinal(RepositorioPedidos repo, int tiempoEspera) {
        super(repo, tiempoEspera);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Pedido pedido = null;

            synchronized (repo.entregados) {
                while (repo.entregados.isEmpty()) {
                    if (EstadoGlobal.verificacionTerminada) {
                        return; // Terminar el hilo si todo está verificado
                    }

                   try {
                        repo.entregados.wait();  // Esperar hasta que haya elementos
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return; // Salir si el hilo es interrumpido
                    }
                }
                // Extraer un pedido de la lista
                pedido = repo.entregados.remove(rand.nextInt(repo.entregados.size()));
            }

            //if (pedido != null) {
                boolean verificado = rand.nextDouble() < 0.95;

                if (verificado) {
                    pedido.setEstado(EstadoPedido.VERIFICADO);
                    synchronized (repo.verificados) {
                            repo.verificados.add(pedido);
                            System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " verificado correctamente.");
                    }
                }
                else {
                    pedido.setEstado(EstadoPedido.FALLIDO);
                    synchronized (repo.fallidos) {
                            repo.fallidos.add(pedido);
                            System.out.println("[VERIFICACION] Pedido #" + pedido.getId() + " falló la verificación.");
                    }
                }
           //}

            esperar(); // Esperar el tiempo configurado
        }
    }
}
