import java.util.*;

public class Pedido {
    private final String id;
    private final int idCasillero;
    private EstadoPedido estado;
    private static final Map<EstadoPedido, Set<EstadoPedido>> transicionesEstado = new HashMap<>();

    static {
        transicionesEstado.put(EstadoPedido.EN_PREPARACION, Set.of(EstadoPedido.EN_TRANSITO, EstadoPedido.FALLIDO));
        transicionesEstado.put(EstadoPedido.EN_TRANSITO, Set.of(EstadoPedido.ENTREGADO, EstadoPedido.FALLIDO));
        transicionesEstado.put(EstadoPedido.ENTREGADO, Set.of(EstadoPedido.VERIFICADO, EstadoPedido.FALLIDO));

    }

    public Pedido(String id, int idCasillero) {
        this.id = id;
        this.idCasillero = idCasillero;
        this.estado = EstadoPedido.EN_PREPARACION;  // Estado inicial
    }

    public String getId() {
        return id;
    }

    public int getIdCasillero() {
        return idCasillero;
    }

    // Control de estado
    public synchronized void setEstado(EstadoPedido nuevoEstado) {
        Set<EstadoPedido> posibles = transicionesEstado.getOrDefault(this.estado, Set.of());

        if (posibles.contains(nuevoEstado)) {
            this.estado = nuevoEstado;
        } else {
            System.out.println("[ERROR] Transición de estado no válida para el pedido #" + this.id);
        }
    }
}
