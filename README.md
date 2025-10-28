# proyecto_egon
# aplicacion_movil
carrito de compras
Diseñado por Egon Von Furstenberg

Impementaciones añadidas al proyecto
-Espacio para Login y si no se tiene un apartado de registro 
-Un admin automatico para pruebas mas fluidas
-Manejo de estados con saludo personalizado 
-logotipos descriptivos para el carrito, edicion y salir 
-botones para ver productos, ver carrito y panel de administrador con un CRUD implementado solo para administradores
-en el apartado de ver productos muestra el nombre, una descripcion, el precio y un boton para agregar al carrito
-en ver carrito muestra los productos seleccionados para aumentar la cantidad, un boton para realizar los pagos y uno para vaciar el carro
-en el panel de admin se muestran 3 usuarios como ejemplo para editarlos, añadir otro ususario y eliminar

FLUJO DE LA APP
-Inicio: Login/Registro
-Autenticacion: Verificacion de Rom en database
-MainScreen: Menu segun el usuario
-Navegacion: Productos, Carrito, Administracion
-Gestion de estados: Viewmodel + Repository + Room
