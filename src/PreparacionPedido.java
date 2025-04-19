import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.UUID;

public class PreparacionPedido extends ProcesoPedido {
    private final Casillero[] casilleros;
    private final Random rand = new Random();

    public PreparacionPedido(Casillero[] casilleros, RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, totalPedidos, tiempoEspera);
        this.casilleros = casilleros;
    }

    @Override
    public void run() {
        while (repo.contadorGlobalPedidos.get() < totalPedidos) {
            int idGenerado = repo.contadorGlobalPedidos.getAndIncrement(); // obtiene y luego incrementa

            if (idGenerado >= totalPedidos) {
                break; // Detenemos el hilo si ya se generaron suficientes pedidos
            }

            boolean pedidoGenerado = false;

            while (!pedidoGenerado) {
                int idCasillero = rand.nextInt(this.casilleros.length);
                Casillero casillero = this.casilleros[idCasillero];

                if (casillero.estaDisponible()) {
                    synchronized (casillero) {
                        if (casillero.estaDisponible()) { // Verificamos dentro del lock
                            casillero.ocupar();
                            String idUnico = UUID.randomUUID().toString().substring(0, 4);
                            Pedido pedido = new Pedido(idUnico, idCasillero);

                            synchronized (repo.enPreparacion) {
                                repo.enPreparacion.add(pedido);
                            }

                            System.out.println("[PREPARACION] Pedido #" + pedido.getId() + " asignado a casillero #" + idCasillero);
                            pedidoGenerado = true;
                        }
                    }
                }
            }

            esperar(); // Espera entre cada intento de generación
        }
    }
}
