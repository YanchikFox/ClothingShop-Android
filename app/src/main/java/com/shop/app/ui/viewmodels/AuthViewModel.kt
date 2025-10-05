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

sealed interface ProfileUpdateState {
    data object Idle : ProfileUpdateState
    data object Loading : ProfileUpdateState
    data object Success : ProfileUpdateState
    data class Error(val message: String) : ProfileUpdateState
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository()
    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    private val _userProfile = MutableStateFlow<ProfileResponse?>(null)
    val userProfile: StateFlow<ProfileResponse?> = _userProfile

    private val _profileUpdateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState

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
            _profileUpdateState.value = ProfileUpdateState.Idle
        }
    }

    fun resetUiState() {
        _authUiState.value = AuthUiState.Idle
    }

    fun updateProfile(request: ProfileUpdateRequest) {
        viewModelScope.launch {
            _profileUpdateState.value = ProfileUpdateState.Loading
            try {
                val token = userPreferencesRepository.authTokenFlow.first()
                if (token == null) {
                    _profileUpdateState.value = ProfileUpdateState.Error("Authentication required")
                    return@launch
                }

                val updatedProfile = authRepository.updateProfile(token, request)
                _userProfile.value = updatedProfile
                _profileUpdateState.value = ProfileUpdateState.Success
            } catch (e: Exception) {
                _profileUpdateState.value =
                    ProfileUpdateState.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun resetProfileUpdateState() {
        _profileUpdateState.value = ProfileUpdateState.Idle
    }
}