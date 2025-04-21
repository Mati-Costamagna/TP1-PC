public class Main {
    public static void main(String[] args) throws InterruptedException {
        int cantidadPedidos = 500;
        int numeroCasilleros = 200;

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
        try {
            for (Thread t : preparacionThreads) t.join();
            System.out.println("Pedidos preparados");

            for (Thread t : despachoThreads) t.join();
            System.out.println("Pedidos despachos");

            for (Thread t : entregaThreads) t.join();
            System.out.println("Pedidos entregados");
            for (Thread t : verificacionThreads) t.join();
            System.out.println("Pedidos verificados");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Estadísticas finales:");
        System.out.println("Pedidos en preparación: " + repo.enPreparacion.size());
        System.out.println("Pedidos en tránsito: " + repo.enTransito.size());
        System.out.println("Pedidos entregados: " + repo.entregados.size());
        System.out.println("Pedidos fallidos: " + repo.fallidos.size());
        System.out.println("Pedidos verificados: " + repo.verificados.size());
    }
}
