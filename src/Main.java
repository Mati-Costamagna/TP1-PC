public class Main {
    public static void main(String[] args) {//throws InterruptedException {
        int cantidadPedidos = 500;
        int numeroCasilleros = 200;
        long inicio = System.currentTimeMillis();

        Casillero[] casilleros = new Casillero[numeroCasilleros];
        for (int i = 0; i < numeroCasilleros; i++) {
            casilleros[i] = new Casillero();
        }

        RepositorioPedidos repo = new RepositorioPedidos();

        LoggerEstadistico logger = new LoggerEstadistico(repo, inicio, casilleros);
        logger.start();

        Thread[] preparacionThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            preparacionThreads[i] = new Thread(new PreparacionPedido(casilleros, repo, cantidadPedidos, 20));
            preparacionThreads[i].start();
        }

        Thread[] despachoThreads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            despachoThreads[i] = new Thread(new DespachoPedido(casilleros, repo, cantidadPedidos, 50));
            despachoThreads[i].start();
        }

        Thread[] entregaThreads = new Thread[3];
        for (int i = 0; i < 3; i++) {
            entregaThreads[i] = new Thread(new EntregaCliente(repo, cantidadPedidos, 90));
            entregaThreads[i].start();
        }

        Thread[] verificacionThreads = new Thread[2];
        for (int i = 0; i < 2; i++) {
            verificacionThreads[i] = new Thread(new VerificacionFinal(repo, cantidadPedidos, 120));
            verificacionThreads[i].start();
        }

        try {
            for (Thread t : preparacionThreads) t.join();
            System.out.println("Pedidos preparados OK");
            logger.setFinPreparacion();
            for (Thread t : despachoThreads) t.join();
            System.out.println("Pedidos despachados OK");
            logger.setFinDespacho();
            for (Thread t : entregaThreads) t.join();
            System.out.println("Pedidos entregados OK");
            logger.setFinEntrega();
            for (Thread t : verificacionThreads) t.join();
            System.out.println("Pedidos verificados OK");
            logger.setFinVerificacion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Estadisticas finales:");
        System.out.println("Pedidos preparados: " + repo.contadorGlobalPedidos.get());
        System.out.println("Pedidos entregados: " + repo.pedidosEntregados.get());
        System.out.println("Pedidos verificados: " + repo.pedidosVerificados.get());
        System.out.println("Pedidos fallidos: " + repo.fallidos.size());

        logger.finalizar();
        try{
            logger.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
