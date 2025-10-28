package com.egon.my2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.egon.my2.database.entity.UserEntity
import com.egon.my2.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    val allUsers: Flow<List<UserEntity>> = userRepository.getAllUsers()

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser

    private val _uiState = MutableStateFlow<AuthState>(AuthState.Idle)
    val uiState: StateFlow<AuthState> = _uiState

    init {
        // Crear usuario admin por defecto al inicializar
        viewModelScope.launch {
            createDefaultAdmin()
        }
    }

    private suspend fun createDefaultAdmin() {
        try {
            val existingAdmin = userRepository.getUserByEmail("admin@admin.com")
            if (existingAdmin == null) {
                val adminUser = UserEntity(
                    name = "Administrador",
                    email = "admin@admin.com",
                    password = "admin123",
                    isAdmin = true
                )
                userRepository.register(adminUser)
            }
        } catch (e: Exception) {
            // Ignorar errores silenciosamente
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = userRepository.login(email, password)
                if (user != null) {
                    _currentUser.value = user
                    _uiState.value = AuthState.Success("Login exitoso")
                } else {
                    _uiState.value = AuthState.Error("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _uiState.value = AuthState.Error("Error en el login")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthState.Loading
            try {
                val user = UserEntity(
                    name = name,
                    email = email,
                    password = password,
                    isAdmin = false
                )
                val success = userRepository.register(user)
                if (success) {
                    _uiState.value = AuthState.Success("Registro exitoso")
                } else {
                    _uiState.value = AuthState.Error("El usuario ya existe")
                }
            } catch (e: Exception) {
                _uiState.value = AuthState.Error("Error en el registro")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _uiState.value = AuthState.Idle
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(userId)
        }
    }

    fun resetState() {
        _uiState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}