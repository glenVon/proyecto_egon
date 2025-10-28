package com.egon.my2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.egon.my2.ui.theme.MyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // Estados simples
                    val userState = remember { UserState() }
                    val cartState = remember { CartState() }

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(navController, userState)
                        }
                        composable("register") {
                            RegisterScreen(navController, userState)
                        }
                        composable("main") {
                            MainScreen(navController, userState, cartState)
                        }
                        composable("products") {
                            ProductListScreen(navController, cartState)
                        }
                        composable("cart") {
                            CartScreen(navController, cartState)
                        }
                        composable("admin") {
                            AdminScreen(navController, userState)
                        }
                        composable("editUser/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")?.toIntOrNull()
                            EditUserScreen(navController, userState, userId)
                        }
                    }
                }
            }
        }
    }
}

// CLASES DE ESTADO
class UserState {
    var currentUser: User? by mutableStateOf(null)
    var loginState: String by mutableStateOf("")
    var errorMessage: String by mutableStateOf("")
    var allUsers: List<User> by mutableStateOf(emptyList())

    init {
        // Usuarios iniciales
        allUsers = listOf(
            User(1, "Administrador", "admin@admin.com", "admin123", true),
            User(2, "Juan Pérez", "juan@test.com", "password", false),
            User(3, "María García", "maria@test.com", "password", false)
        )
    }

    fun login(email: String, password: String) {
        loginState = "loading"

        val user = allUsers.find { it.email == email && it.password == password }

        if (user != null) {
            currentUser = user
            loginState = "success"
        } else {
            errorMessage = "Credenciales inválidas"
            loginState = "error"
        }
    }

    fun register(name: String, email: String, password: String) {
        loginState = "loading"

        if (allUsers.any { it.email == email }) {
            errorMessage = "El usuario ya existe"
            loginState = "error"
            return
        }

        if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
            val newUser = User(
                id = (allUsers.maxOfOrNull { it.id } ?: 0) + 1,
                name = name,
                email = email,
                password = password,
                isAdmin = false
            )
            allUsers = allUsers + newUser
            currentUser = newUser
            loginState = "success"
        } else {
            errorMessage = "Complete todos los campos"
            loginState = "error"
        }
    }

    fun logout() {
        currentUser = null
        loginState = ""
        errorMessage = ""
    }

    // CRUD operations
    fun addUser(name: String, email: String, password: String, isAdmin: Boolean = false) {
        val newUser = User(
            id = (allUsers.maxOfOrNull { it.id } ?: 0) + 1,
            name = name,
            email = email,
            password = password,
            isAdmin = isAdmin
        )
        allUsers = allUsers + newUser
    }

    fun updateUser(userId: Int, name: String, email: String, password: String, isAdmin: Boolean) {
        allUsers = allUsers.map { user ->
            if (user.id == userId) {
                user.copy(name = name, email = email, password = password, isAdmin = isAdmin)
            } else {
                user
            }
        }
    }

    fun deleteUser(userId: Int) {
        allUsers = allUsers.filter { it.id != userId }
        // Si el usuario eliminado es el actual, hacer logout
        if (currentUser?.id == userId) {
            logout()
        }
    }

    fun getUserById(userId: Int): User? {
        return allUsers.find { it.id == userId }
    }
}

class CartState {
    var cartItems: List<CartItem> by mutableStateOf(emptyList())

    val cartItemCount: Int get() = cartItems.sumOf { it.quantity }
    val cartTotal: Double get() = cartItems.sumOf { it.price * it.quantity }

    fun addToCart(product: Product) {
        val currentItems = cartItems.toMutableList()
        val existingItem = currentItems.find { it.productId == product.id }

        if (existingItem != null) {
            val updatedItems = currentItems.map { item ->
                if (item.productId == product.id) {
                    item.copy(quantity = item.quantity + 1)
                } else {
                    item
                }
            }
            cartItems = updatedItems
        } else {
            currentItems.add(CartItem(
                productId = product.id,
                name = product.name,
                price = product.price,
                quantity = 1
            ))
            cartItems = currentItems
        }
    }

    fun removeFromCart(productId: Int) {
        cartItems = cartItems.filter { it.productId != productId }
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
        } else {
            cartItems = cartItems.map { item ->
                if (item.productId == productId) {
                    item.copy(quantity = quantity)
                } else {
                    item
                }
            }
        }
    }

    fun clearCart() {
        cartItems = emptyList()
    }
}

// MODELOS DE DATOS
data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean = false
)

data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val description: String
)

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val quantity: Int = 1
)

// PRODUCTOS DE EJEMPLO
fun getSampleProducts(): List<Product> {
    return listOf(
        Product(1, "Laptop Gaming", 999.99, "Laptop potente para gaming"),
        Product(2, "Smartphone", 499.99, "Teléfono inteligente"),
        Product(3, "Tablet", 299.99, "Tablet para trabajo"),
        Product(4, "Auriculares", 79.99, "Auriculares inalámbricos"),
        Product(5, "Smart Watch", 199.99, "Reloj inteligente")
    )
}

// PANTALLAS COMPOSABLE

// LoginScreen (igual que antes)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavHostController, userState: UserState) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    if (userState.loginState == "success") {
        userState.loginState = ""
        navController.navigate("main") {
            popUpTo("login") { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Iniciar Sesión") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Bienvenido")

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            if (userState.loginState == "error") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    userState.login(email, password)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotEmpty() && password.isNotEmpty() &&
                        userState.loginState != "loading"
            ) {
                Text(if (userState.loginState == "loading") "Cargando..." else "Iniciar Sesión")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("register") }
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }

            Spacer(modifier = Modifier.height(32.dp))
            TextButton(
                onClick = {
                    email = "admin@admin.com"
                    password = "admin123"
                }
            ) {
                Text("Usar credenciales de admin")
            }
        }
    }
}

// RegisterScreen (igual que antes)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavHostController, userState: UserState) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordsMatch = password == confirmPassword

    if (userState.loginState == "success") {
        userState.loginState = ""
        navController.navigate("main") {
            popUpTo("register") { inclusive = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Crear Cuenta") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crear una nueva cuenta")

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = confirmPassword.isNotEmpty() && !passwordsMatch
            )

            if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                Text(
                    text = "Las contraseñas no coinciden",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Start)
                )
            }

            if (userState.loginState == "error") {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = userState.errorMessage,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (passwordsMatch) {
                        userState.register(name, email, password)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotEmpty() && email.isNotEmpty() &&
                        password.isNotEmpty() && passwordsMatch &&
                        userState.loginState != "loading"
            ) {
                Text(if (userState.loginState == "loading") "Creando cuenta..." else "Registrarse")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

// MainScreen (con botón de admin)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    userState: UserState,
    cartState: CartState
) {
    val currentUser = userState.currentUser
    val cartItemCount = cartState.cartItemCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda Online") },
                actions = {
                    // Icono del carrito
                    Box {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Default.ShoppingCart, "Carrito")
                        }
                        if (cartItemCount > 0) {
                            Text(
                                text = cartItemCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            )
                        }
                    }

                    // Botón de admin si es administrador
                    if (currentUser?.isAdmin == true) {
                        IconButton(onClick = { navController.navigate("admin") }) {
                            Icon(Icons.Default.Edit, "Administrar")
                        }
                    }

                    // Botón de logout
                    IconButton(onClick = {
                        userState.logout()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.Logout, "Cerrar sesión")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Información del usuario
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "¡Hola, ${currentUser?.name ?: "Usuario"}!",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = currentUser?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (currentUser?.isAdmin == true) {
                        Text(
                            text = "Modo Administrador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "¡Bienvenido a la Tienda!",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("products") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Productos")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.navigate("cart") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Carrito ($cartItemCount items)")
            }

            // Botón de administración solo para admins
            if (currentUser?.isAdmin == true) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("admin") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Panel de Administración")
                }
            }
        }
    }
}

// ProductListScreen (igual que antes)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(navController: NavHostController, cartState: CartState) {
    val products = remember { getSampleProducts() }
    val cartItemCount = cartState.cartItemCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Productos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Default.ShoppingCart, "Carrito")
                        }
                        if (cartItemCount > 0) {
                            Text(
                                text = cartItemCount.toString(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(products) { product ->
                ProductCard(
                    product = product,
                    onAddToCart = { cartState.addToCart(product) }
                )
            }
        }
    }
}

@Composable
fun ProductCard(product: Product, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(onClick = onAddToCart) {
                    Icon(Icons.Default.Add, "Agregar al carrito")
                }
            }
        }
    }
}

// CartScreen (igual que antes)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(navController: NavHostController, cartState: CartState) {
    val cartItems = cartState.cartItems
    val cartTotal = cartState.cartTotal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (cartItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Carrito vacío",
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Tu carrito está vacío")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(cartItems) { item ->
                        CartItemRow(
                            item = item,
                            onRemove = { cartState.removeFromCart(item.productId) },
                            onUpdateQuantity = { quantity ->
                                cartState.updateQuantity(item.productId, quantity)
                            }
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total:")
                            Text(
                                "$${"%.2f".format(cartTotal)}",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                cartState.clearCart()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Procesar Compra")
                        }

                        Button(
                            onClick = { cartState.clearCart() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Vaciar Carrito")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    onRemove: () -> Unit,
    onUpdateQuantity: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("$${"%.2f".format(item.price)} c/u")

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Cantidad: ")
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity - 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Remove, "Disminuir")
                    }
                    Text(item.quantity.toString())
                    IconButton(
                        onClick = { onUpdateQuantity(item.quantity + 1) },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Add, "Aumentar")
                    }
                }

                Text(
                    "Subtotal: $${"%.2f".format(item.price * item.quantity)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(
                onClick = onRemove
            ) {
                Icon(Icons.Default.Delete, "Eliminar")
            }
        }
    }
}

// NUEVAS PANTALLAS PARA CRUD DE ADMIN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(navController: NavHostController, userState: UserState) {
    val allUsers = userState.allUsers
    var showAddUserDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showAddUserDialog = true }) {
                        Icon(Icons.Default.Add, "Agregar Usuario")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Estadísticas
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Gestión de Usuarios",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        "Total de usuarios: ${allUsers.size}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Administradores: ${allUsers.count { it.isAdmin }}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Lista de usuarios
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(allUsers) { user ->
                    UserCard(
                        user = user,
                        onEdit = {
                            navController.navigate("editUser/${user.id}")
                        },
                        onDelete = {
                            userState.deleteUser(user.id)
                        }
                    )
                }
            }
        }
    }

    // Diálogo para agregar usuario
    if (showAddUserDialog) {
        AddUserDialog(
            onDismiss = { showAddUserDialog = false },
            onAddUser = { name, email, password, isAdmin ->
                userState.addUser(name, email, password, isAdmin)
                showAddUserDialog = false
            }
        )
    }
}

@Composable
fun UserCard(
    user: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(user.name, style = MaterialTheme.typography.titleMedium)
                Text(user.email, style = MaterialTheme.typography.bodyMedium)
                if (user.isAdmin) {
                    Text(
                        "Administrador",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        "Usuario Normal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Botones de acción
            Row {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, "Editar usuario")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, "Eliminar usuario")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddUserDialog(
    onDismiss: () -> Unit,
    onAddUser: (String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isAdmin by remember { mutableStateOf(false) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar Usuario") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Es administrador")
                    Spacer(modifier = Modifier.weight(1f))
                    androidx.compose.material3.Switch(
                        checked = isAdmin,
                        onCheckedChange = { isAdmin = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        onAddUser(name, email, password, isAdmin)
                    }
                },
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserScreen(
    navController: NavHostController,
    userState: UserState,
    userId: Int?
) {
    val user = remember(userId) { userId?.let { userState.getUserById(it) } }

    var name by remember { mutableStateOf(user?.name ?: "") }
    var email by remember { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf(user?.password ?: "") }
    var isAdmin by remember { mutableStateOf(user?.isAdmin ?: false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Usuario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Es administrador")
                Spacer(modifier = Modifier.weight(1f))
                androidx.compose.material3.Switch(
                    checked = isAdmin,
                    onCheckedChange = { isAdmin = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (userId != null && name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                        userState.updateUser(userId, name, email, password, isAdmin)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            ) {
                Text("Guardar Cambios")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (userId != null) {
                        userState.deleteUser(userId)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Text("Eliminar Usuario")
            }
        }
    }
}









//package com.egon.my
//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.egon.my.screen.auth.FormScreen
//import com.egon.my.screen.auth.LoginScreen
//import com.egon.my.screen.cart.CartScreen
//import com.egon.my.screen.detail.DetailScreen
//import com.egon.my.screen.main.MainScreen
//import com.egon.my.ui.theme.MyTheme
//
//@ExperimentalFoundationApi
//class MainActivity : ComponentActivity() {
//    @OptIn(ExperimentalMaterial3Api::class)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MyTheme {
//                val navController = rememberNavController()
//                NavHost(
//                    navController = navController,
//                    startDestination = "main"
//                ) {
//                    composable("main") {
//                        MainScreen(navController)
//                    }
//                    composable(
//                        "detail/{mediaId}",
//                        arguments = listOf(
//                            navArgument("mediaId") {
//                                type = NavType.IntType
//                            }
//                        )
//                    ) { backStackEntry ->
//                        val id = backStackEntry.arguments?.getInt("mediaId")
//                        requireNotNull(id) {
//                            "El ID no puede ser nulo porque el detalle siempre necesita una ID"
//                        }
//                        DetailScreen(id)
//                    }
//                    composable("login") {
//                        LoginScreen(navController)
//                    }
//                    composable("form") {
//                        FormScreen(navController)
//                    }
//                    composable("cart") {
//                        CartScreen(navController)
//                    }
//                }
//            }
//        }
//    }
//}
//
//// Componente de preview (opcional)
//@Composable
//fun ButtonText() {
//    Box(
//        modifier = Modifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        Text(
//            "Hello World",
//            fontSize = 25.sp
//        )
//    }
//}

// Código comentado para referencia futura:

// Esto va debajo de topAppBar
/*
title = {
    Row {
        Text(text = stringResource(id = R.string.app_name))
        Spacer(modifier = Modifier.width(16.dp))
        Icon(Icons.Default.Face, contentDescription = null)
    }
}
*/

// Formulario de ejemplo
/*
@Composable
fun StateSample(value: String, onValueChange: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("")}
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(64.dp),
        verticalArrangement = Arrangement.Center
    ){
        TextField(
            value = value,
            onValueChange = { onValueChange(it)},
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = value,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.DarkGray)
                .padding(8.dp)
        )
        Button(
            onClick = { onValueChange("") },
            modifier = Modifier.fillMaxWidth(),
            enabled = value.isNotEmpty()
        ) {
            Text(text = "Borrar")
        }
    }
}
*/

// Menú de ejemplo
/*
navigationIcon = {
    IconButton(onClick = {}) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = null
        )
    }
}
*/

// Ejemplo de uso de StateSample
/*
val (value, onValueChange) = rememberSaveable { mutableStateOf("") }
StateSample(
    value = value,
    onValueChange = onValueChange
)
*/