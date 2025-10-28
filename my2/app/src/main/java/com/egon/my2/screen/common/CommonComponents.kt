package com.egon.my2.screen.common

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.egon.my2.model.getSampleProducts
import com.egon.my2.viewmodel.CartViewModel
import com.egon.my2.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavHostController,
    userViewModel: UserViewModel,
    cartViewModel: CartViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val products = remember { getSampleProducts() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tienda Online") },
                actions = {
                    IconButton(onClick = { navController.navigate("cart") }) {
                        Icon(Icons.Default.ShoppingCart, "Carrito")
                    }
                    if (currentUser?.isAdmin == true) {
                        IconButton(onClick = { navController.navigate("admin") }) {
                            Icon(Icons.Default.AdminPanelSettings, "Admin")
                        }
                    }
                    IconButton(onClick = { userViewModel.logout() }) {
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
        ) {
            // User info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bienvenido, ${currentUser?.name ?: "Usuario"}",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = currentUser?.email ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    if (currentUser?.isAdmin == true) {
                        Text(
                            text = "Administrador",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Products list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    ProductCard(
                        product = product,
                        onAddToCart = {
                            cartViewModel.addToCart(
                                product.id,
                                product.name,
                                product.price,
                                product.imageUrl
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductCard(product: com.egon.my2.model.Product, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                Text(
                    text = "$${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onAddToCart) {
                Icon(Icons.Default.AddShoppingCart, "Agregar al carrito")
            }
        }
    }
}