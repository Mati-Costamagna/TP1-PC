public class Main {
    public static void main(String[] args) throws InterruptedException {
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

        for (Thread t : preparacionThreads) {
            try{
                t.join();

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Pedidos preparados");
        for (Thread t : despachoThreads) {
            try{
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Pedidos despachados");
        for (Thread t : entregaThreads) {
            try{
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Pedidos entregados");
        for (Thread t : verificacionThreads) {
            try{
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Pedidos verificados");

        System.out.println("Estadísticas finales:");
        System.out.println("Pedidos preparados: " + repo.pedidosDespachados.get());
        System.out.println("Pedidos entregados: " + repo.pedidosEntregados.get());
        System.out.println("Pedidos verificados: " + repo.pedidosVerificados.get());
        System.out.println("Pedidos fallidos: " + repo.pedidosFallidos.get());

        logger.finalizar();
        try{
            logger.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long fin = System.currentTimeMillis();
        System.out.println("Tiempo total: " + (fin - inicio) + " ms");

    }
}
