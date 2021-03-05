package es.studium.tiendalibrosJ2EE;

public class Pedido {

	private int id;
	private String fecha;
	private boolean enviado;
	private double precio;
	private String usuario;
	
	public Pedido() {
		id = 0;
		fecha = "";
		enviado = false;
		precio = 0;
		usuario = "";
	}
	
	public Pedido(int i, String f, boolean e, double p, String u) {
		this.id = i;
		this.fecha = f;
		this.enviado = e;
		this.precio = p;
		this.usuario = u;
	}
	
	public int getId() {
		return id;
	}
	public String getFecha() {
		return fecha;
	}
	public boolean getEnviado() {
		return enviado;
	}
	public double getPrecio() {
		return precio;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
}
