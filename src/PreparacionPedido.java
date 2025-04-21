import java.util.concurrent.TimeUnit;
import java.util.Random;
import java.util.UUID;

public class PreparacionPedido extends ProcesoPedido {
    private final Casillero[] casilleros;
    private final int totalPedidos;
    private final Random rand = new Random();

    public PreparacionPedido(Casillero[] casilleros, RepositorioPedidos repo, int totalPedidos, int tiempoEspera) {
        super(repo, tiempoEspera);
        this.casilleros = casilleros;
        this.totalPedidos = totalPedidos;
    }


    @Override
    public void run() {
        int pedidosGenerados = 0;

        while (true) {
            if (pedidosGenerados >= totalPedidos) break;

            boolean generado = false;

            while (!generado) {
                int idCasillero = rand.nextInt(this.casilleros.length); //Selecciona un casillero aleatorio del arreglo casilleros
                Casillero casillero = this.casilleros[idCasillero];

                if (casillero.estaDisponible()) {
                    casillero.ocupar();
                    String idUnico = UUID.randomUUID().toString().substring(0, 4); // 4 primeros caracteres

                    Pedido pedido = new Pedido(idUnico, idCasillero);

                    synchronized (repo.enPreparacion) {
                        repo.enPreparacion.add(pedido);
                    }

                    System.out.println("[PREPARACION] Pedido #" + pedido.getId() + " asignado a casillero #" + idCasillero);
                    generado = true;
                    pedidosGenerados++;
                    esperar(); //Agrego esperar() para simular el tiempo de preparación para no esperar innecesariamente en la busqueda de otro casillero
                }
            }
        }
    }
}
