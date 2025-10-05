package com.shop.app.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.AuthRequest
import com.shop.app.data.model.AuthResponse
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.ProfileUpdateRequest
import com.shop.app.data.repository.AuthRepository
import com.shop.app.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class Error(val message: String) : AuthUiState
    data class Success(val response: AuthResponse? = null) : AuthUiState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    private val _userProfile = MutableStateFlow<ProfileResponse?>(null)
    val userProfile: StateFlow<ProfileResponse?> = _userProfile

    val isLoggedIn: StateFlow<Boolean> = userPreferencesRepository.authTokenFlow
        .map { token -> token != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            isLoggedIn.collect { loggedIn ->
                if (loggedIn) {
                    fetchProfile()
                } else {
                    _userProfile.value = null
                }
            }
        }
    }

    private fun fetchProfile() {
        viewModelScope.launch {
            try {
                // `first()` is a suspend function that waits for the first value from Flow
                val token = userPreferencesRepository.authTokenFlow.first()
                // Token null check is not needed here because we call `fetchProfile`
                // only when `isLoggedIn` becomes true, which already guarantees token presence.
                val profile = authRepository.getProfile(token!!) // Use `!!` because we're sure token is not null
                _userProfile.value = profile
            } catch (e: Exception) {
                e.printStackTrace()
                // If token exists but profile couldn't be loaded (e.g., token expired),
                // need to log out the user.
                logout()
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                authRepository.registerUser(AuthRequest(email, password))
                // Pass Success without data
                _authUiState.value = AuthUiState.Success(null)
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Registration error")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                val response = authRepository.loginUser(AuthRequest(email, password))
                userPreferencesRepository.saveAuthToken(response.token)
                _authUiState.value = AuthUiState.Success(response)
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Login error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearAuthToken()
        }
    }

    fun updateProfile(request: ProfileUpdateRequest, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authTokenFlow.first()
                if (token == null) {
                    onResult(false)
                    logout()
                    return@launch
                }

                val updatedProfile = authRepository.updateProfile(token, request)
                _userProfile.value = updatedProfile
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    fun resetUiState() {
        _authUiState.value = AuthUiState.Idle
    }
}