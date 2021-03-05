let params = new URLSearchParams(location.search);
var existe = params.get('exist');
var clavediferente = params.get('clavedif');
var insertOK = params.get('insertOK');
var acceso = params.get('log');
var camposvacios = params.get('empty');
var stock = params.get('stock');
var insertLibro = params.get('ilibro');
var sesionKO = params.get('sesionKO');
var modificacionLibro = params.get('mlibro');

if(existe=="true"){
	alertify.warning("Ya existe un usuario con ese nombre");
}
if(clavediferente=="true"){
	alertify.warning("Las contraseñas no coinciden");
}
if(insertOK=="true"){
	alertify.success("Te has registrado correctamente");
} else if (insertOK=="false"){
	alertify.error("No se ha podido registrar tu cuenta");
}
if(acceso=="false"){
	alertify.error("Usuario o contraseña incorrectos");
}
if(camposvacios=="true"){
	alertify.warning("Rellena los campos");
}
if(stock=="true"){
	alertify.warning("No tenemos tantos libros como has pedido");
}
if(insertLibro=="true"){
	alertify.success("Libro dado de alta correctamente");
} else if(insertLibro=="false"){
	alertify.error("El libro no se ha podido dar de alta");
}
if(sesionKO=="true"){
	alertify.warning("Tu sesión ha caducado");
}
if(modificacionLibro=="true"){
	alertify.success("Libro modificado correctamente");
} else if(modificacionLibro=="false") {
	alertify.error("No se ha podido modificar el libro");
}