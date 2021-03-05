package es.studium.tiendalibrosJ2EE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

@WebServlet("/controlador")
public class controlador extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	//Pool de conexiones
	private static DataSource pool = null;

    //Iniciar pool
    public void init(ServletConfig config) throws ServletException {
    	super.init(config);
    	try
    	{
	    	// Crea un contexto para poder luego buscar el recurso DataSource
	    	InitialContext ctx = new InitialContext();
	    	// Busca el recurso DataSource en el contexto
	    	pool = (DataSource)ctx.lookup("java:comp/env/jdbc/mysql_tiendadelibros");
	    	if(pool == null)
	    	{
	    		throw new ServletException("DataSource desconocida 'mysql_tiendadelibros'");
	    	}
    	}
    	catch(NamingException ex){
    		ex.printStackTrace();
    	}
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession(true);
		//Boton que se ha pulsado
		String btn = request.getParameter("btn");
		//Obtiene el carrito
		@SuppressWarnings("unchecked")
		ArrayList<ElementoPedido> elCarrito = (ArrayList<ElementoPedido>)session.getAttribute("carrito");
		//Ruta a la que se redijirá la aplicación
		String ruta = "log.html";
		//Si está en TRUE se utilizará otro método de redirección
		boolean utilizarRequest = false;
		
		//Dependiendo del botón que se pulse
		if(btn.equals("Registrarse")) 
		{
			ruta = "registro.html";
		} 
		else if(btn.equals("Acceder")) 
		{
			//Recogemos los input
			String usuario = request.getParameter("LogNombreUsuario");
			String clave = request.getParameter("LogClave");
			/*
			 * Función que comprueba que los datos de los input son correctos y
			 * que coinciden con la base de datos.
			 */
			int resultadoInicio = Modelo.iniciarSesion(usuario, clave, pool);
			//Si los campos están vacios
			if(resultadoInicio==1) {
				ruta = "log.html?empty=true";
			} 
			//Si el usuario y la contraseña no están en la BD
			else if(resultadoInicio==2) {
				ruta = "log.html?log=false";
			} 
			//Si los datos coinciden se inicia sesión
			else if(resultadoInicio==3) {
				if(session != null)
				{
					session.invalidate();
				}
				session = request.getSession(true);
				synchronized(session)
				{
					session.setAttribute("usuario", usuario);
				}
				/*
				 * Cuando se pasa por parámetro TRUE se comprueba si el usuario tiene permisos
				 * de administrador o no, dependiendo de los permisos se dirige a la página correspondiente. 
				 */
				boolean permiso = Modelo.comprobarUsuario(usuario, pool, true);
				if(permiso) {
					session.setAttribute("tipo","admin");
					ruta = "gestion.jsp";
				} else {
					session.setAttribute("tipo","user");
					ruta = "carrito.jsp";
				}
			}
		} 
		else if(btn.equals("Crear"))
		{
			String usuario = request.getParameter("RegNombreUsuario");
			String clave1 = request.getParameter("RegClave");
			String clave2 = request.getParameter("RegClave2");
			
			Boolean existe = Modelo.comprobarUsuario(usuario, pool, false);
			if(existe==true) {
				ruta = "registro.html?exist=true";
			} else if(!(clave1.equals(clave2))) {
				ruta = "registro.html?clavedif=true";
			} else {
				if(usuario==""||clave1=="") {
					ruta = "registro.html?empty=true";
				} else {
					Boolean insertOK = Modelo.registrarUsuario(usuario, clave1, pool);
					if(insertOK) {
						ruta = "log.html?insertOK=true";
					} else {
						ruta = "registro.html?insertOK=false";
					}
				}
			}
		} 
		else if(btn.equals("Agregar"))
		{
			int idLibro = Integer.parseInt(request.getParameter("idLibro"));
			int cantidad = Integer.parseInt(request.getParameter("cantidad"));
			int stock = Modelo.getStock(Modelo.getTableID(idLibro));
			
			ruta = "carrito.jsp";
			
			if(cantidad>stock) {
				ruta = "carrito.jsp?stock=true";
			} else {
				ElementoPedido nuevoElementoPedido = new ElementoPedido(idLibro,cantidad);
				if(elCarrito==null) {
					elCarrito = new ArrayList<>();
					elCarrito.add(nuevoElementoPedido);
					// Enlazar el carrito con la sesión
					session.setAttribute("carrito", elCarrito);
				} else {
					// Comprueba si el libro está ya en el carrito
					// Si lo está, actualizamos la cantidad
					// Si no está, lo añadimos
					boolean encontrado = false;
					Iterator<ElementoPedido> iter = elCarrito.iterator();
					while(!encontrado&&iter.hasNext())
					{
						ElementoPedido unElementoPedido = (ElementoPedido)iter.next();
						if(unElementoPedido.getIdLibro() == nuevoElementoPedido.getIdLibro())
						{
							int cantidadSuma = unElementoPedido.getCantidad() + nuevoElementoPedido.getCantidad();
							if(cantidadSuma<stock) {
								unElementoPedido.setCantidad(unElementoPedido.getCantidad() + nuevoElementoPedido.getCantidad());
							} else {
								ruta = "carrito.jsp?stock=true";
							}
							encontrado = true;
						}
					}
					if(!encontrado)
					{
						// Lo añade al carrito
						elCarrito.add(nuevoElementoPedido);
					}
				}
			}
		} 
		else if(btn.equals("borrarLibro")) 
		{
			int indiceCarrito = Integer.parseInt(request.getParameter("indiceElemento"));
			elCarrito.remove(indiceCarrito);
			ruta = "carrito.jsp";
		} 
		else if(btn.equals("Comprar"))
		{
			// Enviado por carrito.jsp
			// Calcula el precio total de todos los elementos del carrito
			double precioTotal = 0;
			int cantidadTotalOrdenada = 0;
			for(ElementoPedido item: elCarrito)
			{
				double precio = item.getPrecio();
				int cantidadOrdenada = item.getCantidad();
				precioTotal += precio * cantidadOrdenada;
				cantidadTotalOrdenada += cantidadOrdenada;
			}
			String pTtal = Modelo.formatoDecimal(precioTotal);
			// Coloca el precioTotal y la cantidadtotal en el request
			request.setAttribute("precioTotal", pTtal);
			request.setAttribute("cantidadTotal", cantidadTotalOrdenada+"");
			
			String nombreUsuario = (String)session.getAttribute("usuario");
			//Si se ha expirado la sesión al pulsar la confirmación de compra no realiza el pedido
			if(nombreUsuario==null) {
				ruta = "log.html";
			} else {
				int idUsuario = Modelo.idUsuario(pool, nombreUsuario);
				int idPedido = Modelo.crearPedido(pool, idUsuario, precioTotal);
				for(ElementoPedido item: elCarrito) {
					Modelo.crearSubPedido(pool, idPedido, item.getIdLibro(), item.getCantidad());
				}
			}
			
			// Redirige a confirmacion.jsp
			ruta = "/confirmacion.jsp";
			utilizarRequest = true;
		}
		else if(btn.equals("altaLibro")) {
			String nombreLibro = request.getParameter("nombreLibro");
			String precioLibro = request.getParameter("precioLibro");
			String stockLibro = request.getParameter("stockLibro");
			String idAutor = request.getParameter("idAutor");
			String idEditorial = request.getParameter("idEditorial");
			
			int filasAfectadas = Modelo.altaLibro(pool, nombreLibro, precioLibro, stockLibro, idAutor, idEditorial, "alta", "0");
			if(filasAfectadas>0) {
				ruta = "gestion.jsp?ilibro=true";
			} else {
				ruta = "gestion.jsp?ilibro=false";
			}
		}
		else if(btn.equals("modificarLibro")) {
			String nombreLibro = request.getParameter("nombreLibro");
			String precioLibro = request.getParameter("precioLibro");
			String stockLibro = request.getParameter("stockLibro");
			String idAutor = request.getParameter("idAutor");
			String idEditorial = request.getParameter("idEditorial");
			String idLibro = request.getParameter("idLibro");
			
			int filasAfectadas = Modelo.altaLibro(pool, nombreLibro, precioLibro, stockLibro, idAutor, idEditorial, "modificar", idLibro);
			if(filasAfectadas>0) {
				ruta = "gestion.jsp?mlibro=true";
			} else {
				ruta = "gestion.jsp?mlibro=false";
			}
		}
		//Ver detalles de un pedido
		else if(btn.equals("detalles")) {
			String idPedido = request.getParameter("indicePedido");
			session.setAttribute("detalles", "true");
			session.setAttribute("pedidoSeleccionado", Modelo.cargarSubPedidos(pool, idPedido));
			session.setAttribute("idPedido", idPedido);
			ruta = "pedidos.jsp";
		}
		//Marca como enviado un pedido
		else if(btn.equals("Enviado")) {
			String idPedido = request.getParameter("indicePedido");
			Modelo.cambiarEstadoPedido(pool, idPedido, 1);
			ruta = "pedidos.jsp";
		}
		//Marca como pendiente un pedido
		else if(btn.equals("Pendiente")) {
			String idPedido = request.getParameter("indicePedido");
			Modelo.cambiarEstadoPedido(pool, idPedido, 0);
			ruta = "pedidos.jsp";
		}
		else if(btn.equals("logout")) {
			if(session != null)
			{
				session.invalidate();
			}
			ruta = "log.html";
		}
		
		//Cargamos libros, autores y editoriales para poder seleccionarlos.
		Modelo.cargarLibros(pool);
		Modelo.cargarAutores(pool);
		Modelo.cargarEditoriales(pool);
		Modelo.cargarPedidos(pool);
		
		
		//Obtenemos la ruta que le hemos dado y hacemos la redirección
		//Se hace de esta manera para poder mandar parámetros GET
		if(!utilizarRequest) {
			response.sendRedirect(ruta);
		}
		//Utilizamos este método de redirección para enviar parámetros por request
		else {
			utilizarRequest = false;
			ServletContext servletContext = getServletContext();
			RequestDispatcher requestDispatcher = servletContext.getRequestDispatcher(ruta);
			requestDispatcher.forward(request, response);
		}
	}
}