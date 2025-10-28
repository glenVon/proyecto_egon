package com.egon.my2.viewmodel

import androidx.lifecycle.ViewModel
import com.egon.my2.repository.CartRepository
import kotlinx.coroutines.flow.Flow

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {

    val cartItems: Flow<List<com.egon.my2.model.CartItem>> = cartRepository.cartItems

    fun addToCart(productId: Int, name: String, price: Double, imageUrl: String) {
        cartRepository.addToCart(productId, name, price, imageUrl)
    }

    fun removeFromCart(productId: Int) {
        cartRepository.removeFromCart(productId)
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        cartRepository.updateQuantity(productId, quantity)
    }

    fun clearCart() {
        cartRepository.clearCart()
    }

    fun getCartTotal(): Double = cartRepository.getCartTotal()

    fun getCartItemCount(): Int = cartRepository.getCartItemCount()
}