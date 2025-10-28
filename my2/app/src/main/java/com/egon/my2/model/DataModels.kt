package com.egon.my2.model

data class CartItem(
    val productId: Int,
    val name: String,
    val price: Double,
    val imageUrl: String,
    val quantity: Int = 1
)

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val stock: Int
)

// Sample data
fun getSampleProducts(): List<Product> {
    return listOf(
        Product(
            id = 1,
            name = "Laptop Gaming Pro",
            description = "Laptop potente para gaming con tarjeta gráfica dedicada",
            price = 1299.99,
            imageUrl = "https://picsum.photos/200/300?random=1",
            category = "Tecnología",
            stock = 5
        ),
        Product(
            id = 2,
            name = "Smartphone Ultra",
            description = "Teléfono inteligente de última generación con 5G",
            price = 799.99,
            imageUrl = "https://picsum.photos/200/300?random=2",
            category = "Tecnología",
            stock = 15
        ),
        Product(
            id = 3,
            name = "Auriculares Bluetooth Premium",
            description = "Auriculares inalámbricos con cancelación de ruido",
            price = 199.99,
            imageUrl = "https://picsum.photos/200/300?random=3",
            category = "Audio",
            stock = 20
        ),
        Product(
            id = 4,
            name = "Tablet Pro 12.9",
            description = "Tablet profesional para diseño y productividad",
            price = 899.99,
            imageUrl = "https://picsum.photos/200/300?random=4",
            category = "Tecnología",
            stock = 8
        ),
        Product(
            id = 5,
            name = "Smart Watch Series 8",
            description = "Reloj inteligente con monitor de salud avanzado",
            price = 349.99,
            imageUrl = "https://picsum.photos/200/300?random=5",
            category = "Wearables",
            stock = 12
        ),
        Product(
            id = 6,
            name = "Cámara DSLR Pro",
            description = "Cámara profesional con lente intercambiable",
            price = 1499.99,
            imageUrl = "https://picsum.photos/200/300?random=6",
            category = "Fotografía",
            stock = 3
        ),
        Product(
            id = 7,
            name = "Altavoz Inteligente",
            description = "Altavoz con asistente virtual integrado",
            price = 129.99,
            imageUrl = "https://picsum.photos/200/300?random=7",
            category = "Audio",
            stock = 25
        ),
        Product(
            id = 8,
            name = "Monitor 4K 32\"",
            description = "Monitor ultra HD para trabajo y entretenimiento",
            price = 499.99,
            imageUrl = "https://picsum.photos/200/300?random=8",
            category = "Tecnología",
            stock = 7
        )
    )
}