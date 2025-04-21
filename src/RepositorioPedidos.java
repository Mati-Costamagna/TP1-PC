import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RepositorioPedidos {
    public final AtomicInteger contadorGlobalPedidos = new AtomicInteger(0);
    public final AtomicInteger pedidosDespachados = new AtomicInteger(0);
    public final AtomicInteger pedidosEntregados = new AtomicInteger(0);
    public final AtomicInteger pedidosFallidos = new AtomicInteger(0);
    public final AtomicInteger pedidosVerificados = new AtomicInteger(0);
    public final List<Pedido> enPreparacion = new ArrayList<>();
    public final List<Pedido> enTransito = new ArrayList<>();
    public final List<Pedido> entregados = new ArrayList<>();
    public final List<Pedido> fallidos = new ArrayList<>();
    public final List<Pedido> verificados = new ArrayList<>();
}
