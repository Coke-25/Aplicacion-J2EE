package es.studium.tiendalibrosJ2EE;
public class Libro
{
	private int id;
	private String titulo;
	private String autor;
	private double precio;
	private int stock;
	public Libro()
	{
		setId(0);
		titulo = "";
		autor = "";
		precio = 0.0;
		stock = 0;
	}
	public Libro(int i, String t, String a, double p, int s)
	{
		setId(i);
		titulo = t;
		autor = a;
		precio = p;
		stock = s;
	}
	public String getTitulo()
	{
		return titulo;
	}
	public String getAutor()
	{
		return autor;
	}
	public void setAutor(String autor) {
		this.autor = autor;
	}
	public double getPrecio()
	{
		return precio;
	}
	public int getStock() {
		return stock;
	}
	public int getId()
	{
		return id;
	}
	public void setId(int id)
	{
		this.id = id;
	}
}