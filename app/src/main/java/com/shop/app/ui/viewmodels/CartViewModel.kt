package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.Product
import com.shop.app.data.repository.AuthRepository
import com.shop.app.data.repository.CartRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: CartRepository, private val authRepository: AuthRepository) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems

    val totalPrice: StateFlow<Double> = _cartItems.map { list ->
        list.sumOf { cartItem ->
            cartItem.product.price * cartItem.quantity
        }
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    private val isLoggedInFlow = flow { emit(authRepository.getAuthToken() != null) }

    init {
        viewModelScope.launch {
            isLoggedInFlow.collect { isLoggedIn ->
                if (isLoggedIn) {
                    fetchCartFromServer()
                } else {
                    _cartItems.value = emptyList()
                }
            }
        }
    }

    private fun fetchCartFromServer() {
        viewModelScope.launch {
            try {
                _cartItems.value = cartRepository.getCart()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshForLanguageChange() {
        viewModelScope.launch {
            if (isLoggedInFlow.first()) {
                fetchCartFromServer()
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            if (isLoggedInFlow.first()) {
                try {
                    cartRepository.addToCart(product.id, quantity)
                    fetchCartFromServer()
                } catch (e: Exception) { e.printStackTrace() }
            } else {
                _cartItems.update { currentList ->
                    val existingItem = currentList.find { it.product.id == product.id }
                    if (existingItem != null) {
                        currentList.map {
                            if (it.product.id == product.id) it.copy(quantity = it.quantity + quantity) else it
                        }
                    } else {
                        currentList + CartItem(product = product, quantity = quantity)
                    }
                }
            }
        }
    }

    fun decrementQuantity(productId: String) {
        viewModelScope.launch {
            if (isLoggedInFlow.first()) {
                val currentItem = _cartItems.value.find { it.product.id == productId }
                if (currentItem != null && currentItem.quantity > 1) {
                    cartRepository.updateCartItemQuantity(productId, currentItem.quantity - 1)
                    fetchCartFromServer()
                } else {
                    removeFromCart(productId) // If there's only one item, just remove it
                }
            } else {
                _cartItems.update { currentList ->
                    val existingItem = currentList.find { it.product.id == productId }
                    if (existingItem != null && existingItem.quantity > 1) {
                        currentList.map {
                            if (it.product.id == productId) it.copy(quantity = it.quantity - 1) else it
                        }
                    } else {
                        currentList.filterNot { it.product.id == productId }
                    }
                }
            }
        }
    }

    fun incrementQuantity(productId: String) {
        viewModelScope.launch {
            if (isLoggedInFlow.first()) {
                val currentItem = _cartItems.value.find { it.product.id == productId }
                if (currentItem != null) {
                    cartRepository.updateCartItemQuantity(productId, currentItem.quantity + 1)
                    fetchCartFromServer()
                }
            } else {
                _cartItems.update { currentList ->
                    currentList.map {
                        if (it.product.id == productId) it.copy(quantity = it.quantity + 1) else it
                    }
                }
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            if (isLoggedInFlow.first()) {
                cartRepository.removeCartItem(productId)
                fetchCartFromServer()
            } else {
                _cartItems.update { currentList ->
                    currentList.filterNot { it.product.id == productId }
                }
            }
        }
    }

    companion object {
        fun provideFactory(cartRepository: CartRepository, authRepository: AuthRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(CartViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return CartViewModel(cartRepository, authRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}