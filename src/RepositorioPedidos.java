import java.util.*;

public class RepositorioPedidos {
    public final List<Pedido> enPreparacion = new ArrayList<>();
    public final List<Pedido> enTransito = new ArrayList<>();
    public final List<Pedido> entregados = new ArrayList<>();
    public final List<Pedido> fallidos = new ArrayList<>();
    public final List<Pedido> verificados = new ArrayList<>();
}
