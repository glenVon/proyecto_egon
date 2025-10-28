package com.egon.my2.repository

import com.egon.my2.model.CartItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: Flow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(productId: Int, name: String, price: Double, imageUrl: String) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.productId == productId }

        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + 1)
            val index = currentItems.indexOf(existingItem)
            currentItems[index] = updatedItem
        } else {
            currentItems.add(CartItem(
                productId = productId,
                name = name,
                price = price,
                imageUrl = imageUrl,
                quantity = 1
            ))
        }
        _cartItems.value = currentItems
    }

    fun removeFromCart(productId: Int) {
        val currentItems = _cartItems.value.toMutableList()
        currentItems.removeAll { it.productId == productId }
        _cartItems.value = currentItems
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        val currentItems = _cartItems.value.toMutableList()
        val existingItem = currentItems.find { it.productId == productId }

        if (existingItem != null) {
            if (quantity > 0) {
                val updatedItem = existingItem.copy(quantity = quantity)
                val index = currentItems.indexOf(existingItem)
                currentItems[index] = updatedItem
            } else {
                currentItems.remove(existingItem)
            }
            _cartItems.value = currentItems
        }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getCartTotal(): Double {
        return _cartItems.value.sumOf { it.price * it.quantity }
    }

    fun getCartItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
}