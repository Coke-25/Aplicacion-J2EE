package es.studium.tiendalibrosJ2EE;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Formatter;
import javax.sql.DataSource;

import java.io.UnsupportedEncodingException;

public class Modelo{
	//Parametros para la BD
	static Connection conexion = null;
	static Statement stm = null;
	//Listas con los datos cargados para utilizarlos
	static ArrayList<Libro> tabla = new ArrayList<Libro>();
	static ArrayList<Autor> autores = new ArrayList<Autor>();
	static ArrayList<Editorial> editoriales = new ArrayList<Editorial>();
	static ArrayList<Pedido> pedidos = new ArrayList<Pedido>();
	
	// ----- Funciones -----
	
	public static int iniciarSesion(String usuario, String clave, DataSource pool) {
		int devolver = 0;
		if(usuario.length()==0||clave.length()==0) {
			return 1;
		}
		try {
			// Obtener una conexión del pool
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("SELECT * FROM usuarios WHERE ");
			sqlStr.append("STRCMP(usuarios.nombreUsuario,'").append(usuario).append("') = 0");
			sqlStr.append(" AND STRCMP(usuarios.claveUsuario,SHA1('").append(clave).append("')) = 0");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			if(!rs.next())
			{
				devolver = 2;
			} else {
				devolver = 3;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return devolver;
	}
	
	public static boolean comprobarUsuario(String user, DataSource pool, boolean tipo) {
		boolean devolver = false;
		try {
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("SELECT nombreUsuario, tipoUsuario from usuarios");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			while(rs.next()) {
				//Al encontrar el usuario que queremos comprobar
				if(user.equals(rs.getString("nombreUsuario"))) {
					//Al ser tipo TRUE queremos ver si tiene permisos de administrador
					if(tipo) {
						//Devuelve TRUE si tiene permisos
						int permiso = rs.getInt("tipoUsuario");
						if(permiso==1) {
							devolver = true;
						}
					} else {
						//Si el tipo es FALSE queremos comprobar si el usuario está en la BD
						//Devulve TRUE porque si esta
						devolver = true;
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return devolver;
	}
	
	public static boolean registrarUsuario(String usuario, String clave, DataSource pool) {
		try {
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			String claveCifrada = cifrarSHA1(clave);
			StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("INSERT into usuarios (nombreUsuario, claveUsuario, tipoUsuario) VALUES ('"+usuario+"', '"+claveCifrada+"', 0);");
			stm.executeUpdate(sqlStr.toString());
			Boolean respuesta = comprobarUsuario(usuario,pool,false);
			//Si existe es que se ha hecho el insert correctamente
			if(respuesta==true) {
				return true;
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return false;
	}
	
	public static void cargarLibros(DataSource pool) {
		try {
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			StringBuilder sqlStr = new StringBuilder();
			//Sacamos todos los libros
			sqlStr.append("SELECT * from libros;");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			
			Libro libro;
			//Limpiamos la tabla para que no se duplique
			tabla.clear();
			while(rs.next()) {
				/*
				 * Creamos objetos libro para añadirlos a la tabla
				 * En el campo autor se guarda el idAutor para luego pedirlo y reemplazarlo
				 */
				libro = new Libro(rs.getInt("idLibro"), rs.getString("nombreLibro"), rs.getString("idAutorFK"), rs.getDouble("precioLibro"), rs.getInt("stockLibro"));
				tabla.add(libro);
			}
			/*
			 * Por cada libro en la tabla cogemos el valor del autor que por ahora es su id y hacemos select
			 * para seleccionarlo de la base de datos y reemplazar lo que antes era el id por el nombre y apellido.
			 */
			for(int i=0;i<tamano();i++) {
				String idAutor = tabla.get(i).getAutor();
				sqlStr = new StringBuilder();
				sqlStr.append("SELECT * from autores where idAutor='"+idAutor+"';");
				rs = stm.executeQuery(sqlStr.toString());
				if(rs.next()) {
					tabla.get(i).setAutor(rs.getString("nombreAutor")+" "+rs.getString("apellidosAutor"));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	public static void cargarAutores(DataSource pool) {
		try {
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("SELECT * from autores ORDER BY apellidosAutor, nombreAutor;");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			
			Autor autor;
			autores.clear();
			while(rs.next()) {
				autor = new Autor(rs.getInt("idAutor"), rs.getString("nombreAutor"), rs.getString("apellidosAutor"));
				autores.add(autor);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	public static void cargarEditoriales(DataSource pool) {
		try {
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("SELECT * from editoriales ORDER BY nombreEditorial;");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			
			Editorial editorial;
			editoriales.clear();
			while(rs.next()) {
				editorial = new Editorial(rs.getInt("idEditorial"), rs.getString("nombreEditorial"));
				editoriales.add(editorial);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	public static void cargarPedidos(DataSource pool) {
		try {
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("SELECT * from pedidos;");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			
			Pedido pedido;
			pedidos.clear();
			while(rs.next()) {
				pedido = new Pedido(rs.getInt("idPedido"), rs.getString("fechaPedido"), rs.getBoolean("enviadoPedido"), rs.getDouble("precioPedido"), rs.getString("idUsuarioFK"));
				pedidos.add(pedido);
			}
			for(int i=0;i<tamanoPedidos();i++) {
				String idUsuario = pedidos.get(i).getUsuario();
				sqlStr = new StringBuilder();
				sqlStr.append("SELECT * from usuarios WHERE idUsuario="+idUsuario+";");
				rs = stm.executeQuery(sqlStr.toString());
				if(rs.next()) {
					pedidos.get(i).setUsuario(rs.getString("nombreUsuario"));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	public static ArrayList<ElementoPedido> cargarSubPedidos(DataSource pool, String idPedido) {
		ArrayList<ElementoPedido> subpedidos = new ArrayList<ElementoPedido>();
		try {
			conexion = pool.getConnection();
			stm = conexion.createStatement();
			StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("SELECT * from subpedidos WHERE idPedidoFK="+idPedido+";");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			
			ElementoPedido subpedido;
			
			while(rs.next()) {
				subpedido = new ElementoPedido(rs.getInt("idLibroFK"), rs.getInt("cantidadSubpedido"));
				subpedidos.add(subpedido);
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return subpedidos;
	}
	
	public static int crearPedido(DataSource pool, int idUsuario, double precioTotal) {
		 DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	     String fecha = dtf.format(LocalDateTime.now());
	     int idPedido = 0;
	     try {
	    	 conexion = pool.getConnection();
	    	 stm = conexion.createStatement();
	    	 StringBuilder sqlStr = new StringBuilder();
			 sqlStr.append("INSERT into pedidos (fechaPedido, enviadoPedido, precioPedido, idUsuarioFK) VALUES ('"+fecha+"', 0, "+precioTotal+", "+idUsuario+");");
			 stm.executeUpdate(sqlStr.toString());
			 
			 sqlStr = new StringBuilder();
			 sqlStr.append("SELECT idPedido FROM pedidos ORDER BY idPedido DESC LIMIT 1");
			 ResultSet rs = stm.executeQuery(sqlStr.toString());
			 if(rs.next()) {
				 idPedido = rs.getInt("idPedido");
			 }
	     } catch (Exception e) {
	    	 e.printStackTrace();
	     } finally {
	    	 try
				{
					if(stm != null)
					{
						stm.close();
					}
					if(conexion != null)
					{
						conexion.close();
					}
				} catch (SQLException sqle) {
					sqle.printStackTrace();
				}
	     }
	     return idPedido;
	}
	
	public static void crearSubPedido(DataSource pool, int idPedido, int idLibro, int cantidad) {
		try {
			conexion = pool.getConnection();
	    	stm = conexion.createStatement();
	    	StringBuilder sqlStr = new StringBuilder();
	    	sqlStr.append("INSERT into subpedidos (idPedidoFK, idLibroFK, cantidadSubpedido) VALUES ("+idPedido+", "+idLibro+", "+cantidad+");");
	    	stm.executeUpdate(sqlStr.toString());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	//Devuelve el id del usuario según el nombre
	public static int idUsuario(DataSource pool, String nombreUsuario){
		int idUsuario = 0;
		try {
			conexion = pool.getConnection();
	    	stm = conexion.createStatement();
	    	StringBuilder sqlStr = new StringBuilder();
			sqlStr.append("SELECT idUsuario from usuarios where nombreUsuario='"+nombreUsuario+"';");
			ResultSet rs = stm.executeQuery(sqlStr.toString());
			if(rs.next()) {
				idUsuario = rs.getInt("idUsuario");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return idUsuario;
	}
	
	//Da de alta o modifica un libro según se le indique por parámetro
	public static int altaLibro(DataSource pool, String nombre, String precio, String stock, String idAutor, String idEditorial, String operacion, String idLibro) {
		int resultado = 0;
		try {
			conexion = pool.getConnection();
	    	stm = conexion.createStatement();
	    	StringBuilder sqlStr = new StringBuilder();
	    	//Dependiendo de lo indicado se da de alta o se modifica uno existente
	    	if(operacion=="alta") {
	    		sqlStr.append("INSERT into libros (nombreLibro, precioLibro, stockLibro, idAutorFK, idEditorialFK) VALUES('"+nombre+"', "+precio+", "+stock+", "+idAutor+", "+idEditorial+");");
	    	} else {
	    		sqlStr.append("UPDATE libros SET nombreLibro='"+nombre+"', precioLibro="+precio+", stockLibro="+stock+", idAutorFK="+idAutor+", idEditorialFK="+idEditorial+" WHERE idLibro="+idLibro+";");
	    	}
			resultado = stm.executeUpdate(sqlStr.toString());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
		return resultado;
	}
	
	public static void cambiarEstadoPedido(DataSource pool, String idPedido, int estado) {
		try {
			conexion = pool.getConnection();
	    	stm = conexion.createStatement();
	    	StringBuilder sqlStr = new StringBuilder();
	    	sqlStr.append("UPDATE pedidos SET enviadoPedido="+estado+" WHERE idPedido="+idPedido+";");
	    	stm.executeUpdate(sqlStr.toString());
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			try
			{
				if(stm != null)
				{
					stm.close();
				}
				if(conexion != null)
				{
					conexion.close();
				}
			} catch (SQLException sqle) {
				sqle.printStackTrace();
			}
		}
	}
	
	public static String formatoDecimal(double decimal) {
		StringBuilder sb = new StringBuilder();
		Formatter formatter = new Formatter(sb);
		formatter.format("%.2f", decimal);
		formatter.close();
		
		return sb.toString();
	}
	
	
	//Funciones para cifrar en SHA-1
	private static String cifrarSHA1(String clave)
	{
	    String sha1 = "";
	    try
	    {
	        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	        crypt.reset();
	        crypt.update(clave.getBytes("UTF-8"));
	        sha1 = byteToHex(crypt.digest());
	    }
	    catch(NoSuchAlgorithmException e)
	    {
	        e.printStackTrace();
	    }
	    catch(UnsupportedEncodingException e)
	    {
	        e.printStackTrace();
	    }
	    return sha1;
	}

	private static String byteToHex(final byte[] hash)
	{
	    Formatter formatter = new Formatter();
	    for (byte b : hash)
	    {
	        formatter.format("%02x", b);
	    }
	    String result = formatter.toString();
	    formatter.close();
	    return result;
	}
	
	//Devuelve el id de la tabla que tenga el id de la Base de Datos indicado
	public static int getTableID(int idBD) {
		for(int i=0; i<tamano(); i++) {
			if(tabla.get(i).getId()==idBD) {
				return i;
			}
		}
		return 0;
	}
	
	
	//Funciones para coger información de la tabla que contiene los libros
	public static int getIdLibro(int id) {
		return tabla.get(id).getId();
	}
	public static String getTitulo(int id){
		return tabla.get(id).getTitulo();
	}
	public static String getAutor(int id){
		return tabla.get(id).getAutor();
	}
	public static double getPrecio(int id){
		return tabla.get(id).getPrecio();
	}
	public static int getStock(int id) {
		return tabla.get(id).getStock();
	}
	public static int tamano(){
		return tabla.size();
	}
	
	//Funciones para coger información de autores
	public static int tamanoAutores() {
		return autores.size();
	}
	public static int getIdAutor(int id) {
		return autores.get(id).getId();
	}
	public static String getNombreAutor(int id) {
		return autores.get(id).getNombre();
	}
	public static String getApellidosAutor(int id) {
		return autores.get(id).getApellidos();
	}
	
	//Funciones para coger información de editoriales
	public static int tamanoEditoriales() {
		return editoriales.size();
	}
	public static int getIdEditorial(int id) {
		return editoriales.get(id).getId();
	}
	public static String getNombreEditorial(int id) {
		return editoriales.get(id).getNombre();
	}
	
	//Funciones para coger información de pedidos
	public static int tamanoPedidos() {
		return pedidos.size();
	}
	public static int getIdPedido(int id) {
		return pedidos.get(id).getId();
	}
	public static String getFechaPedido(int id) {
		return pedidos.get(id).getFecha();
	}
	public static boolean getEnviadoPedido(int id) {
		return pedidos.get(id).getEnviado();
	}
	public static double getPrecioPedido(int id) {
		return pedidos.get(id).getPrecio();
	}
	public static String getUsuarioPedido(int id) {
		return pedidos.get(id).getUsuario();
	}
}