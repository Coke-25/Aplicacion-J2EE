package es.studium.tiendalibrosJ2EE;

public class ElementoPedido
{
	private int idLibro;
	private int cantidad;
	
	public ElementoPedido(int idLibro, int cantidad)
	{
		this.idLibro = idLibro;
		this.cantidad = cantidad;
	}
	public int getIdLibro()
	{
		return idLibro;
	}
	public void setIdLibro(int idLibro)
	{
		this.idLibro = idLibro;
	}
	public int getCantidad()
	{
		return cantidad;
	}
	public void setCantidad(int cantidad)
	{
		this.cantidad = cantidad;
	}
	public String getAutor()
	{
		return Modelo.getAutor(Modelo.getTableID(idLibro));
	}
	public String getTitulo()
	{
		return Modelo.getTitulo(Modelo.getTableID(idLibro));
	}
	public double getPrecio()
	{
		return Modelo.getPrecio(Modelo.getTableID(idLibro));
	}
}