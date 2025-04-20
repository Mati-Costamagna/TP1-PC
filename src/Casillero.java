public class Casillero {
    private EstadoCasillero estado;
    private int contador;

    public Casillero() {
        this.estado = EstadoCasillero.VACIO;
    }

    public synchronized void ocupar() {
        if (estaDisponible()) {
            this.estado = EstadoCasillero.OCUPADO;
            this.contador++;
            this.notifyAll();
        }
    }

    public synchronized void liberar() {
        this.estado = EstadoCasillero.VACIO;
        this.notifyAll();
    }

    public synchronized void ponerFueraDeServicio() {
        this.estado = EstadoCasillero.FUERA_DE_SERVICIO;
        this.notifyAll();
    }

    public synchronized boolean estaDisponible() {
        return this.estado == EstadoCasillero.VACIO;
    }

    public EstadoCasillero getEstado() {
        return this.estado;
    }

    public int getContador() {
        return contador;
    }
}
