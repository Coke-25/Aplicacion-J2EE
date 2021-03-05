<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="true" import="java.util.*, es.studium.tiendalibrosJ2EE.*" %>
<%! 
	String usuario; 
	String detalles;
%>
<%
	usuario = (String)session.getAttribute("usuario");
	//Si hay sesion se redirige al log
	if(usuario==null){
		response.sendRedirect("log.html?sesionKO=true");
	} else {
		//Si hay sesion pero no tiene permisos de administrador se va al log
		String permiso = (String)session.getAttribute("tipo");
		if(permiso.equals("user")){
			response.sendRedirect("log.html");
		}
	}

	detalles = (String)session.getAttribute("detalles");

	@SuppressWarnings("unchecked")
	ArrayList<ElementoPedido> pedidoSeleccionado = (ArrayList<ElementoPedido>)session.getAttribute("pedidoSeleccionado");
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<title>Casa del Libro</title>
		<!-- Bootstrap CSS -->
    	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta2/dist/css/bootstrap.min.css" rel="stylesheet">
		<!-- Css Alertify -->
		<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/css/alertify.min.css"/>
		<!-- Default theme -->
		<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/css/themes/default.min.css"/>
		<!-- Semantic UI theme --> 
		<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/css/themes/semantic.min.css"/>
		<!-- Bootstrap theme -->
		<link rel="stylesheet" href="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/css/themes/bootstrap.min.css"/>
		<!-- Css propio -->
		<link href="css/vip.css" rel="stylesheet">
	</head>
	<body>
		<div class="container-fluid">
			<div class="row cabecera">
				<div class="col-3">
					<h1 id="titulo">Casa del Libro</h1>
				</div>
				<div class="col-4">
					<nav class="navbar navbar-expand-lg">
						<ul class="navbar-nav">
    						<li class="nav-item"><a class="nav-link" href="gestion.jsp">Libros</a></li>
    						<li class="nav-item"><a class="nav-link" href="autores.jsp">Autores</a></li>
    						<li class="nav-item"><a class="nav-link" href="editoriales.jsp">Editoriales</a></li>
    						<li class="nav-item"><a class="nav-link" href="pedidos.jsp">Pedidos</a></li>
  						</ul>
					</nav>
				</div>
				<div class="col-4">
					<h2 id="usuarioActual"><span id="usuarioActualTt">Usuario:</span> <%=usuario%></h2>
				</div>
				<div class="col-1 colLogout">
					<form action="controlador" method="POST" title="Cerrar Sesión">
						<button type="submit" class="btn btn-warning">
							<svg id="logoutIcon" xmlns="http://www.w3.org/2000/svg" fill="currentColor" class="bi bi-box-arrow-right" viewBox="0 0 16 16">
	  							<path fill-rule="evenodd" d="M10 12.5a.5.5 0 0 1-.5.5h-8a.5.5 0 0 1-.5-.5v-9a.5.5 0 0 1 .5-.5h8a.5.5 0 0 1 .5.5v2a.5.5 0 0 0 1 0v-2A1.5 1.5 0 0 0 9.5 2h-8A1.5 1.5 0 0 0 0 3.5v9A1.5 1.5 0 0 0 1.5 14h8a1.5 1.5 0 0 0 1.5-1.5v-2a.5.5 0 0 0-1 0v2z"/>
	  							<path fill-rule="evenodd" d="M15.854 8.354a.5.5 0 0 0 0-.708l-3-3a.5.5 0 0 0-.708.708L14.293 7.5H5.5a.5.5 0 0 0 0 1h8.793l-2.147 2.146a.5.5 0 0 0 .708.708l3-3z"/>
							</svg>
						</button>
						<input type="hidden" name="btn" value="logout">
					</form>
				</div>
			</div>
			<%
				if(detalles=="true"){
				session.setAttribute("detalles", "false");
			%>		
				<div class="row">
					<div class="col-12">
						<h2 class="ttlPedido">Pedido Nº <%=session.getAttribute("idPedido")%></h2>
					</div>
				</div>
				<div class="row rowTabla">
					<div class="col-8 colTabla">
						<table class="table table-hover table-striped">
							<thead class="table-dark">
								<tr>
									<th scope="col">#</th>
									<th scope="col">Nombre</th>
									<th scope="col">Precio</th>
									<th scope="col">Cantidad</th>
									<th scope="col">Autor</th>
								</tr>
							</thead>
							<tbody class="table-light">
							<%
							int num=0;
							for(ElementoPedido item: pedidoSeleccionado){
							num++;
							
							//Cambiar formato del precio
							String precio = Modelo.formatoDecimal(item.getPrecio());
							%>
								<tr>
									<th><%=num%></th>
									<td><%=item.getTitulo()%></td>
									<td><%=precio%> €</td>
									<td><%=item.getCantidad()%></td>
									<td><%=item.getAutor()%></td>
								</tr>
							<%
							}
							%>
							</tbody>
						</table>
					</div>
				</div>
			<%
				}
			%>
			<div class="row">
					<div class="col-12">
						<h2 class="ttlPedido">Todos los pedidos</h2>
					</div>
				</div>
			<div class="row rowTabla">
				<div class="col-8 colTabla">
					<table class="table table-striped table-hover">
						<thead class="table-dark">
							<tr>
								<th scope="col">#</th>
								<th scope="col">Fecha</th>
								<th scope="col">Estado</th>
								<th scope="col">Precio</th>
								<th scope="col">Usuario</th>
								<th scope="col" colspan="2">Opciones</th>
							</tr>
						</thead>
						<tbody class="table-light">
							<%
							for(int i=0;i<Modelo.tamanoPedidos();i++){
								//Cambiar mensaje del estado
								boolean estado = Modelo.getEnviadoPedido(i);
								String msgEstado = "";
								if(estado){
									msgEstado = "Enviado";
								} else {
									msgEstado = "Pendiente";
								}
								//Cambiar formato del precio
								String precioPedido = Modelo.formatoDecimal(Modelo.getPrecioPedido(i));
								//Cambiar formato de la fecha
								String fecha = Modelo.getFechaPedido(i);
								String tablaFecha[] = fecha.split(" ");
								String parteFecha[] = tablaFecha[0].split("-");
								String parteHora[] = tablaFecha[1].split(":");
								fecha = parteFecha[2]+"/"+parteFecha[1]+"/"+parteFecha[0]+" "+parteHora[0]+":"+parteHora[1];
							%>
							<tr>
								<form action="controlador" method="POST">
									<th><%= i+1 %></th>
									<td><%= fecha %></td>
									<td><%= msgEstado %></td>
									<td><%= precioPedido %> €</td>
									<td><%= Modelo.getUsuarioPedido(i) %></td>
									<td><input type="submit" value="Ver detalles" class="btn btn-warning"></td>
									<input type="hidden" name="btn" value="detalles">
									<input type="hidden" name="indicePedido" value="<%=Modelo.getIdPedido(i)%>">
								</form>
							<%
							if(!estado){
							%>
								<form action="controlador" method="POST">
									<td><input type="submit" class="btn btn-success" name="btn" value="Enviado"></td>
									<input type="hidden" name="indicePedido" value="<%=Modelo.getIdPedido(i)%>">
								</form>
							<%
							} else {
							%>
								<form action="controlador" method="POST">
									<td><input type="submit" class="btn btn-danger" name="btn" value="Pendiente"></td>
									<input type="hidden" name="indicePedido" value="<%=Modelo.getIdPedido(i)%>">
								</form>
							<% } %>
							</tr>
							<% } %>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</body>
</html>