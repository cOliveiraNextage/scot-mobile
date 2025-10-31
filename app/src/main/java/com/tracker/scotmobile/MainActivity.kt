package com.tracker.scotmobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tracker.scotmobile.ui.screens.HomeScreen
import com.tracker.scotmobile.ui.screens.LoginScreen
import com.tracker.scotmobile.ui.screens.OrderServiceListScreen
import com.tracker.scotmobile.ui.screens.ServicesScreen
import com.tracker.scotmobile.ui.theme.ScotMobileTheme
import com.tracker.scotmobile.ui.viewmodel.LoginViewModel
import com.tracker.scotmobile.data.model.User
import com.tracker.scotmobile.data.api.TokenExpiredEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.DisposableEffect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScotMobileTheme {
                ScotMobileApp()
            }
        }
    }
}

@Composable
fun ScotMobileApp() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    var currentUser by remember { mutableStateOf<User?>(null) }
    
    // Observar mudanças no usuário do ViewModel
    LaunchedEffect(Unit) {
        loginViewModel.repository.getCurrentUser().collect { user ->
            currentUser = user
        }
    }
    
    // Observar evento de token expirado
    DisposableEffect(Unit) {
        val mainScope = CoroutineScope(Dispatchers.Main)
        val tokenExpiredListener: () -> Unit = {
            // Token expirado: fazer logout automático
            mainScope.launch {
                loginViewModel.logout()
                currentUser = null
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
        
        // Adicionar listener ao evento de token expirado
        TokenExpiredEvent.addListener(tokenExpiredListener)
        
        // Limpar listener quando o composable for removido
        onDispose {
            TokenExpiredEvent.removeListener(tokenExpiredListener)
        }
    }
    
    // Verificar se há usuário logado ao iniciar o app
    LaunchedEffect(Unit) {
        val hasUser = loginViewModel.repository.hasLoggedInUser()
        if (hasUser) {
            navController.navigate("home") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { user ->
                    currentUser = user
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onLogout = {
                    loginViewModel.logout()
                    currentUser = null
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                onNavigateToTracking = {
                    navController.navigate("tracking")
                },
                onNavigateToServices = {
                    navController.navigate("services")
                },
                user = currentUser
            )
        }
        
        composable("tracking") {
            OrderServiceListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("services") {
            ServicesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                userToken = currentUser?.token
            )
        }
    }
}