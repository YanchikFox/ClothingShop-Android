package com.shop.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.CartItem
import com.shop.app.data.model.Product
import com.shop.app.data.repository.CartRepository
import com.shop.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)
    private val cartRepository = CartRepository()

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

    private val isLoggedInFlow = userPreferencesRepository.authTokenFlow.map { it != null }
    private val authTokenFlow = userPreferencesRepository.authTokenFlow

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
            val token = authTokenFlow.first()
            if (token != null) {
                try {
                    _cartItems.value = cartRepository.getCart(token)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
                val token = authTokenFlow.first()!!
                try {
                    cartRepository.addToCart(token, product.id, quantity)
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
            val token = authTokenFlow.first()
            if (token != null) {
                val currentItem = _cartItems.value.find { it.product.id == productId }
                if (currentItem != null && currentItem.quantity > 1) {
                    cartRepository.updateCartItemQuantity(token, productId, currentItem.quantity - 1)
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
            val token = authTokenFlow.first()
            if (token != null) {
                val currentItem = _cartItems.value.find { it.product.id == productId }
                if (currentItem != null) {
                    cartRepository.updateCartItemQuantity(token, productId, currentItem.quantity + 1)
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
            val token = authTokenFlow.first()
            if (token != null) {
                cartRepository.removeCartItem(token, productId)
                fetchCartFromServer()
            } else {
                _cartItems.update { currentList ->
                    currentList.filterNot { it.product.id == productId }
                }
            }
        }
    }
}