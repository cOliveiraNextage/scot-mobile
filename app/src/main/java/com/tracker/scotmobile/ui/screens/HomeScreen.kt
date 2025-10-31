package com.tracker.scotmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration

import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Sync
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tracker.scotmobile.ui.theme.ScotMobileTheme
import com.tracker.scotmobile.ui.viewmodel.SyncViewModel
import com.tracker.scotmobile.ui.viewmodel.SyncViewModelFactory
import com.tracker.scotmobile.data.local.AppDatabase
import com.tracker.scotmobile.data.local.repository.ResultCodeLocalRepository
import com.tracker.scotmobile.data.local.repository.OrderServiceLocalRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onNavigateToTracking: () -> Unit,
    onNavigateToServices: () -> Unit,
    user: com.tracker.scotmobile.data.model.User? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val resultCodeLocalRepository = ResultCodeLocalRepository(
        database.resultCodeDao(),
        database.resultCodeWarehouseTypeDao()
    )
    val orderServiceLocalRepository = OrderServiceLocalRepository(
        database.orderServiceDao(),
        database.ownerDao(),
        database.addressDao(),
        database.phoneDao(),
        database.vehicleDao(),
        database.productDao(),
        database.equipmentDao(),
        database.compatibilityEquipmentDao(),
        database.accessoryDao(),
        database.taskDao(),
        database.vehicleColorDao(),
        database.vehicleTypeDao(),
        database.checklistTypeDao(),
        database.checklistItemDao(),
        database.checklistTypeItemDao()
    )
    val syncViewModel: SyncViewModel = viewModel(
        factory = SyncViewModelFactory(resultCodeLocalRepository, orderServiceLocalRepository)
    )
    val syncState by syncViewModel.uiState.collectAsState()
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarColor by remember { mutableStateOf(Color.Green) }

    // Observar mudanças no estado de sincronização
    LaunchedEffect(syncState.syncSuccess, syncState.errorMessage) {
        when {
            syncState.syncSuccess -> {
                snackbarMessage =
                    "Sincronização realizada com sucesso! ${syncState.services.size} serviços, ${syncState.serviceOrders.size} ordens, ${syncState.resultCodes.size} códigos de resultado"
                snackbarColor = Color.Green
                showSnackbar = true
                syncViewModel.resetSyncSuccess()
            }

            syncState.errorMessage != null -> {
                snackbarMessage = syncState.errorMessage!!
                snackbarColor = Color.Red
                showSnackbar = true
                syncViewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ScotMobile") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                    IconButton(
                        onClick = {
                            user?.token?.let { token ->
                                syncViewModel.loadResultCodes(token)
                                syncViewModel.loadServiceOrders(token)
                            }
                        },
                        enabled = !syncState.isSyncing
                    ) {
                        if (syncState.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = "Sincronizar"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = if (snackbarColor == Color.Green)
                        Color(0xFF2E7D32) // Verde escuro para sucesso
                    else
                        Color(0xFFD32F2F), // Vermelho escuro para erro
                    action = {
                        TextButton(
                            onClick = { showSnackbar = false }
                        ) {
                            Text(
                                "OK",
                                color = Color.White
                            )
                        }
                    }
                ) {
                    Text(
                        text = snackbarMessage,
                        color = Color.White
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Header com informações do usuário
            UserHeader(user = user)

            Spacer(modifier = Modifier.height(24.dp))

            // Menu principal com opções
            MenuOptions(
                onNavigateToTracking = onNavigateToTracking,
                onNavigateToServices = onNavigateToServices
            )
        }
    }
}

@Composable
private fun UserHeader(user: com.tracker.scotmobile.data.model.User?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bem-vindo!",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = user?.name ?: "Usuário",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                textAlign = TextAlign.Center
            )

            user?.role?.let { role ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = role.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun MenuOptions(
    onNavigateToTracking: () -> Unit,
    onNavigateToServices: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Primeira linha de opções
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuCard(
                icon = Icons.AutoMirrored.Filled.List,
                title = "Ordem de Serviço",
                description = "Ver lista de ordens de serviços",
                modifier = Modifier.weight(1f),
                onClick = onNavigateToTracking
            )

            MenuCard(
                icon = Icons.Default.DriveEta,
                title = "Drive Thru",
                description = "Agedamento imediato de veículo em ponto fixo",
                modifier = Modifier.weight(1f),
                onClick = onNavigateToServices
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Segunda linha de opções
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuCard(
                icon = Icons.Default.Dashboard,
                title = "Frotas",
                description = "Agendamento de veículo de frota",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Implementar navegação */ }
            )

            MenuCard(
                icon = Icons.Default.ViewAgenda,
                title = "Frotas Agendadas",
                description = "Lista de frotas agendadas",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Implementar navegação */ }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Terceira linha de opções
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuCard(
                icon = Icons.Default.DateRange,
                title = "Aceite de Equipamentos",
                description = "Gestão e transferência de equipamentos",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Implementar navegação */ }
            )

            MenuCard(
                icon = Icons.AutoMirrored.Filled.ListAlt,
                title = "Lista de Finalização",
                description = "Lista de serviços finalizados",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Implementar navegação */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quarta linha de opções
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MenuCard(
                icon = Icons.Default.LocationOn,
                title = "Teste de GPS Bancada",
                description = "Realiza o teste de GPS",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Implementar navegação */ }
            )

            MenuCard(
                icon = Icons.Default.SettingsInputAntenna,
                title = "Teste de RF Bancada",
                description = "Realiza o teste de RF",
                modifier = Modifier.weight(1f),
                onClick = { /* TODO: Implementar navegação */ }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    ScotMobileTheme {
        HomeScreen(
            onLogout = {},
            onNavigateToTracking = {},
            onNavigateToServices = {},
            user = com.tracker.scotmobile.data.model.User(
                id = 41377,
                name = "Teste Teste ",
                login = "teste.teste",
                document = "123123123",
                role = com.tracker.scotmobile.data.model.Role(
                    id = 1,
                    description = "Administrador do SCOT",
                    name = "ADMINISTRADOR"
                )
            )
        )
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenDarkPreview() {
    ScotMobileTheme {
        HomeScreen(
            onLogout = {},
            onNavigateToTracking = {},
            onNavigateToServices = {},
            user = com.tracker.scotmobile.data.model.User(
                id = 41377,
                name = "Teste Teste ",
                login = "teste.teste",
                document = "123123123",
                role = com.tracker.scotmobile.data.model.Role(
                    id = 1,
                    description = "Administrador do SCOT",
                    name = "ADMINISTRADOR"
                )
            )
        )
    }
}
