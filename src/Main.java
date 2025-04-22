public class Main {
    public static void main(String[] args) throws InterruptedException {
        int cantidadPedidos = 50;
        int numeroCasilleros = 20;

        Casillero[] casilleros = new Casillero[numeroCasilleros]; //Inicializa el array de casilleros todos en vacío
        for (int i = 0; i < numeroCasilleros; i++) {
            casilleros[i] = new Casillero();
        }

        RepositorioPedidos repo = new RepositorioPedidos();

        Thread[] preparacionThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            preparacionThreads[i] = new Thread(new PreparacionPedido(casilleros, repo, cantidadPedidos, 50));
            preparacionThreads[i].start();
        }

        Thread[] despachoThreads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            despachoThreads[i] = new Thread(new DespachoPedido(casilleros, repo, 100));
            despachoThreads[i].start();
        }

        Thread[] entregaThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            entregaThreads[i] = new Thread(new EntregaCliente(repo, 200));
            entregaThreads[i].start();
        }

        Thread[] verificacionThreads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            verificacionThreads[i] = new Thread(new VerificacionFinal(repo, 300));
            verificacionThreads[i].start();
        }

        // Esperar a que los hilos de preparación terminen
        for (Thread t : preparacionThreads) {
            t.join();
        }
        System.out.println("Pedidos preparados");
        EstadoGlobal.preparacionTerminada = true; // Indicar que la preparación ha terminado

        for (Thread t : despachoThreads) {
            t.join();
        }
        System.out.println("Pedidos despachados");
        EstadoGlobal.transitoTerminada = true;

        for (Thread t : entregaThreads) {
            t.join();
        }
        System.out.println("Pedidos entregados");
        EstadoGlobal.entregaTerminada = true;

        for (Thread t : verificacionThreads) {
            t.join();
        }
        System.out.println("Pedidos verificados");
        EstadoGlobal.verificacionTerminada = true;

        // Mostrar estadísticas finales
        System.out.println("Estadísticas finales:");
        System.out.println("Pedidos en preparación: " + repo.enPreparacion.size());
        System.out.println("Pedidos en tránsito: " + repo.enTransito.size());
        System.out.println("Pedidos entregados: " + repo.entregados.size());
        System.out.println("Pedidos fallidos: " + repo.fallidos.size());
        System.out.println("Pedidos verificados: " + repo.verificados.size());
    }
}
