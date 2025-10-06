package com.shop.app.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.shop.app.data.model.AuthRequest
import com.shop.app.data.model.AuthResponse
import com.shop.app.data.model.ProfileResponse
import com.shop.app.data.model.ProfileUpdateRequest
import com.shop.app.data.repository.AuthRepository
import com.shop.app.data.repository.UserRepository
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

class AuthViewModel(private val userRepository: UserRepository, private val authRepository: AuthRepository) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState

    private val _userProfile = MutableStateFlow<ProfileResponse?>(null)
    val userProfile: StateFlow<ProfileResponse?> = _userProfile

    private val _profileUpdateState = MutableStateFlow<ProfileUpdateState>(ProfileUpdateState.Idle)
    val profileUpdateState: StateFlow<ProfileUpdateState> = _profileUpdateState

    val isLoggedIn: StateFlow<Boolean> = flow {
        emit(authRepository.getAuthToken() != null)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

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
                val profile = userRepository.getProfile()
                _userProfile.value = profile
            } catch (e: Exception) {
                e.printStackTrace()
                logout()
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            try {
                userRepository.registerUser(AuthRequest(email, password))
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
                val response = userRepository.loginUser(AuthRequest(email, password))
                authRepository.saveAuthToken(response.token)
                _authUiState.value = AuthUiState.Success(response)
            } catch (e: Exception) {
                _authUiState.value = AuthUiState.Error(e.message ?: "Login error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.saveAuthToken("")
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
                val updatedProfile = userRepository.updateProfile(request)
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

    companion object {
        fun provideFactory(userRepository: UserRepository, authRepository: AuthRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                        @Suppress("UNCHECKED_CAST")
                        return AuthViewModel(userRepository, authRepository) as T
                    }
                    throw IllegalArgumentException("Unknown ViewModel class")
                }
            }
        }
    }
}