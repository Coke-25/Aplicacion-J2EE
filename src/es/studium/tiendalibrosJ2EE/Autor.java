package es.studium.tiendalibrosJ2EE;

public class Autor {
	private int id;
	private String nombre;
	private String apellidos;
	
	public Autor() {
		setId(0);
		nombre = "";
		apellidos = "";
	}
	public Autor(int i, String n, String a) {
		setId(i);
		nombre = n;
		apellidos = a;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
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
