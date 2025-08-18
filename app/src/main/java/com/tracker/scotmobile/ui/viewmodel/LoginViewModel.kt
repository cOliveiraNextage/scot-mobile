package com.tracker.scotmobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tracker.scotmobile.data.model.User
import com.tracker.scotmobile.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val password: String = "",
    val userNameError: String? = null,
    val passwordError: String? = null,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null,
    val user: User? = null
)

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun updateEmail(userName: String) {
        _uiState.value = _uiState.value.copy(
            userName = userName,
            userNameError = null
        )
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            passwordError = null
        )
    }
    
    fun login() {
        val currentState = _uiState.value
        
        // Validação
        if (currentState.userName.isEmpty()) {
            _uiState.value = currentState.copy(userNameError = "Usuário é obrigatório")
            return
        }
        
        if (currentState.password.isEmpty()) {
            _uiState.value = currentState.copy(passwordError = "Senha é obrigatória")
            return
        }
        
        if (currentState.password.length < 6) {
            _uiState.value = currentState.copy(passwordError = "Senha deve ter pelo menos 6 caracteres")
            return
        }
        
        // Iniciar login
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            try {
                val result = repository.login(currentState.userName, currentState.password)
                result.fold(
                    onSuccess = { user ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            loginSuccess = true,
                            user = user,
                            errorMessage = null
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Erro desconhecido"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Erro desconhecido"
                )
            }
        }
    }
    
    fun resetState() {
        _uiState.value = LoginUiState()
    }
}
