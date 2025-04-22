import java.util.*;

public class Pedido {
    private final String id;
    private final int idCasillero;
    private EstadoPedido estado;

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

    public synchronized void setEstado(EstadoPedido estado) {
        // Validar que la transición de estado sea permitida
        if (this.estado == EstadoPedido.EN_PREPARACION && estado == EstadoPedido.EN_TRANSITO) {
            this.estado = estado;
        } else if (this.estado == EstadoPedido.EN_PREPARACION && estado == EstadoPedido.FALLIDO) {
            this.estado = estado;
        } else if (this.estado == EstadoPedido.EN_TRANSITO && estado == EstadoPedido.ENTREGADO) {
            this.estado = estado;
        } else if (this.estado == EstadoPedido.EN_TRANSITO && estado == EstadoPedido.FALLIDO) {
            this.estado = estado;
        } else if (this.estado == EstadoPedido.ENTREGADO && estado == EstadoPedido.FALLIDO) {
            this.estado = estado;
        } else if (this.estado == EstadoPedido.ENTREGADO && estado == EstadoPedido.VERIFICADO) {
            this.estado = estado;
        } else {
            System.out.println("[ERROR] Transición de estado no válida para el pedido #" + this.id);
        }
    }
}
