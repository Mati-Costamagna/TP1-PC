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



            boolean pedidoGenerado = false;

            while (!pedidoGenerado) {
                int idCasillero = rand.nextInt(this.casilleros.length);
                Casillero casillero = this.casilleros[idCasillero];

                synchronized (casillero) {
                    if (casillero.estaDisponible()) {
                        casillero.ocupar();
                        String idUnico = UUID.randomUUID().toString().substring(0, 4);
                        Pedido pedido = new Pedido(idUnico, idCasillero);

                        synchronized (repo.enPreparacion) {
                            repo.enPreparacion.add(pedido);
                            repo.contadorGlobalPedidos.getAndIncrement();
                            repo.enPreparacion.notifyAll();
                        }

                        System.out.println("[PREPARACION] Pedido #" + pedido.getId() + " asignado a casillero #" + idCasillero);
                        pedidoGenerado = true;
                    }
                }

            }
            esperar();
        }
    }
}