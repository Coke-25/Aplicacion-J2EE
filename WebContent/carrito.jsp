<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page session="true" import="java.util.*, es.studium.tiendalibrosJ2EE.*" %>
<%! 
	String usuario;
%>
<%
	usuario = (String)session.getAttribute("usuario");
	if(usuario==null){
		response.sendRedirect("log.html?sesionKO=true");
	}
	
	@SuppressWarnings("unchecked")
	ArrayList<ElementoPedido> cesta = (ArrayList<ElementoPedido>)session.getAttribute("carrito");
%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<title>Carrito - Casa del Libro</title>
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
		<link href="css/index.css" rel="stylesheet">
	</head>
	<body class="bodyCarrito">
		<div class="container-fluid">
			<div class="row cabeceraCarrito">
				<div class="col-7">
					<h1 id="titulo">Casa del Libro</h1>
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
			<div class="row">
				<div class="col-12">
					<h2 id="h2Libro">Elige un libro:</h2>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
					<form action="controlador" method="POST">
						<label class="lbLibro" for="selectLibro">Libro:</label>
						<select id="selectLibro" class="form-select" name="idLibro">
							<%for(int i=0;i<Modelo.tamano();i++){%>
								<option value="<%=i+1%>">
									<%=Modelo.getTitulo(i)+" | "+Modelo.getAutor(i)+" | "+Modelo.getPrecio(i)+"€"%>
								</option>
							<%}%>
						</select>
						<label class="lbLibro" for="cantLibro">Cantidad:</label>
						<input id="cantLibro" name="cantidad" type="number" class="form-control" min="1" required>
						<input type="submit" name="btn" id="btnAgregarLibro" class="btn btn-primary btnPrincipal" value="Agregar">
					</form>
				</div>
			</div>
			<%
			if(cesta != null && cesta.size() > 0)
			{
			%>
			<div class="row">
				<div class="col-12">
					<h2>Tu cesta contiene:</h2>
				</div>
			</div>
			<div class="row rowTabla">
				<div class="col-9 divTabla">
					<table class="table table-hover">
						<thead class="table-dark">
							<tr>
								<th scope="col">Título</th>
								<th scope="col">Autor</th>
								<th scope="col">Precio</th>
								<th scope="col" colspan="2">Cantidad</th>
							</tr>
						</thead>
						<tbody class="table-light">
							<% for(int i = 0; i<cesta.size(); i++)
							{
								ElementoPedido elementoPedido = cesta.get(i);%>
							<tr>
								<form action="controlador" method="POST">
									<input type="hidden" name="btn" value="borrarLibro"> 
									<input type="hidden" name="indiceElemento" value="<%=i%>">
									<td><%=elementoPedido.getTitulo()%></td>
									<td><%=elementoPedido.getAutor()%></td>
									<td><%=elementoPedido.getPrecio()%> €</td>
									<td><%=elementoPedido.getCantidad()%></td>
									<td align="center"><input type="submit" class="btn btn-danger" value="Eliminar de la cesta"></td>
								</form>
							</tr>
							<% } %>
						</tbody>
					</table>
					<form action="controlador" method="POST">
						<input type="hidden" name="btn" value="Comprar"> 
						<input type="submit" class="btn btn-success" value="Confirmar compra">
					</form>
				</div>
			</div>
			<% } %>
		</div>
		
		<!-- Script Alertify -->
		<script src="//cdn.jsdelivr.net/npm/alertifyjs@1.13.1/build/alertify.min.js"></script>
		<!-- Script propio -->
		<script src="js/index.js"></script>
	</body>
</html>