package com.egon.my2.screen.main

import androidx.compose.foundation.clickable
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
    val featuredProducts = remember { getSampleProducts().take(3) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Tienda") },
                actions = {
                    // Cart icon with badge
                    var cartItemCount by remember { mutableStateOf(0) }

                    LaunchedEffect(key1 = Unit) {
                        cartViewModel.cartItems.collect { items ->
                            cartItemCount = items.sumOf { it.quantity }
                        }
                    }

                    Box(
                        contentAlignment = Alignment.TopEnd
                    ) {
                        IconButton(onClick = { navController.navigate("cart") }) {
                            Icon(Icons.Default.ShoppingCart, "Carrito")
                        }
                        if (cartItemCount > 0) {
                            Badge(
                                modifier = Modifier.offset(x = (-8).dp, y = 8.dp)
                            ) {
                                Text(
                                    text = cartItemCount.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }

                    if (currentUser?.isAdmin == true) {
                        IconButton(onClick = { navController.navigate("admin") }) {
                            Icon(Icons.Default.AdminPanelSettings, "Admin")
                        }
                    }

                    IconButton(onClick = {
                        userViewModel.logout()
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
        ) {
            // Welcome section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "¡Hola, ${currentUser?.name ?: "Usuario"}!",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Bienvenido a nuestra tienda online",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    if (currentUser?.isAdmin == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Modo Administrador",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Quick actions
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Acciones Rápidas",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            title = "Ver Productos",
                            icon = Icons.Default.ShoppingBag,
                            onClick = { navController.navigate("productList") }
                        )

                        ActionCard(
                            title = "Mi Carrito",
                            icon = Icons.Default.ShoppingCart,
                            onClick = { navController.navigate("cart") }
                        )

                        if (currentUser?.isAdmin == true) {
                            ActionCard(
                                title = "Administrar",
                                icon = Icons.Default.AdminPanelSettings,
                                onClick = { navController.navigate("admin") }
                            )
                        }
                    }
                }
            }

            // Featured products
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Productos Destacados",
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(
                            onClick = { navController.navigate("productList") }
                        ) {
                            Text("Ver todos")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(featuredProducts) { product ->
                            FeaturedProductItem(
                                product = product,
                                onAddToCart = {
                                    cartViewModel.addToCart(
                                        productId = product.id,
                                        name = product.name,
                                        price = product.price,
                                        imageUrl = product.imageUrl
                                    )
                                },
                                onProductClick = {
                                    // Navigate to product detail
                                    // navController.navigate("productDetail/${product.id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            //.weight(1f)
            .aspectRatio(1f),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun FeaturedProductItem(
    product: com.egon.my2.model.Product,
    onAddToCart: () -> Unit,
    onProductClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = "$${"%.2f".format(product.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onAddToCart,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    Icons.Default.AddShoppingCart,
                    contentDescription = "Agregar al carrito"
                )
            }
        }
    }
}