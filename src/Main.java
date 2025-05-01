public class Main {
    public static void main(String[] args) {//throws InterruptedException {
        int cantidadPedidos = 150;
        int numeroCasilleros = 25;
        long inicio = System.currentTimeMillis();

        Casillero[] casilleros = new Casillero[numeroCasilleros];
        for (int i = 0; i < numeroCasilleros; i++) {
            casilleros[i] = new Casillero();
        }

        RepositorioPedidos repo = new RepositorioPedidos();

        LoggerEstadistico logger = new LoggerEstadistico(repo, inicio, casilleros);
        logger.start();

        Thread[] preparacionThreads = new Thread[3];
        System.out.println("Hilos de preparacion 3");
        for (int i = 0; i < 3; i++) {
            preparacionThreads[i] = new Thread(new PreparacionPedido(casilleros, repo, cantidadPedidos, 20));
            System.out.println(preparacionThreads[i].getName());
            preparacionThreads[i].start();
        }

        Thread[] despachoThreads = new Thread[2];
        System.out.println("Hilos de despacho 2");
        for (int i = 0; i < 2; i++) {
            despachoThreads[i] = new Thread(new DespachoPedido(casilleros, repo, cantidadPedidos, 50));
            System.out.println(despachoThreads[i].getName());
            despachoThreads[i].start();
        }

        Thread[] entregaThreads = new Thread[3];
        System.out.println("Hilos de entrega 3");
        for (int i = 0; i < 3; i++) {
            entregaThreads[i] = new Thread(new EntregaCliente(repo, cantidadPedidos, 90));
            System.out.println(entregaThreads[i].getName());
            entregaThreads[i].start();
        }

        Thread[] verificacionThreads = new Thread[2];
        System.out.println("Hilos de verificacion 2");
        for (int i = 0; i < 2; i++) {
            verificacionThreads[i] = new Thread(new VerificacionFinal(repo, cantidadPedidos, 120));
            System.out.println(verificacionThreads[i].getName());
            verificacionThreads[i].start();
        }

        try {
            for (Thread t : preparacionThreads) t.join();
            System.out.println("Pedidos preparados");
            System.out.println("Pedidos preparados: " + repo.contadorGlobalPedidos.get());
            System.out.println("Pedidos despachados: " + repo.pedidosDespachados.get());
            System.out.println("Pedidos entregados: " + repo.pedidosEntregados.get());
            System.out.println("Pedidos verificados: " + repo.pedidosVerificados.get());
            System.out.println("Pedidos fallidos: " + repo.fallidos.size());
            for (Thread t : despachoThreads) t.join();
            System.out.println("Pedidos despachados");
            System.out.println("Pedidos preparados: " + repo.contadorGlobalPedidos.get());
            System.out.println("Pedidos despachados: " + repo.pedidosDespachados.get());
            System.out.println("Pedidos entregados: " + repo.pedidosEntregados.get());
            System.out.println("Pedidos verificados: " + repo.pedidosVerificados.get());
            System.out.println("Pedidos fallidos: " + repo.fallidos.size());
            for (Thread t : entregaThreads) t.join();
            System.out.println("Pedidos entregados");
            System.out.println("Pedidos preparados: " + repo.contadorGlobalPedidos.get());
            System.out.println("Pedidos despachados: " + repo.pedidosDespachados.get());
            System.out.println("Pedidos entregados: " + repo.pedidosEntregados.get());
            System.out.println("Pedidos verificados: " + repo.pedidosVerificados.get());
            System.out.println("Pedidos fallidos: " + repo.fallidos.size());
            for (Thread t : verificacionThreads) t.join();
            System.out.println("Pedidos verificados");
            System.out.println("Pedidos preparados: " + repo.contadorGlobalPedidos.get());
            System.out.println("Pedidos despachados: " + repo.pedidosDespachados.get());
            System.out.println("Pedidos entregados: " + repo.pedidosEntregados.get());
            System.out.println("Pedidos verificados: " + repo.pedidosVerificados.get());
            System.out.println("Pedidos fallidos: " + repo.fallidos.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Estadísticas finales:");
        System.out.println("Pedidos preparados: " + repo.pedidosDespachados.get());
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
