package com.tracker.scotmobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tracker.scotmobile.data.model.OrderStatus
import com.tracker.scotmobile.data.model.Priority
import com.tracker.scotmobile.data.model.Service
import com.tracker.scotmobile.data.model.ServiceOrder
import com.tracker.scotmobile.data.model.ServiceStatus
import com.tracker.scotmobile.data.model.ResultCode
import com.tracker.scotmobile.data.model.ResultCodeWarehouseType
import com.tracker.scotmobile.ui.theme.ScotMobileTheme
import com.tracker.scotmobile.ui.viewmodel.SyncViewModel
import com.tracker.scotmobile.ui.viewmodel.SyncViewModelFactory
import com.tracker.scotmobile.data.local.AppDatabase
import com.tracker.scotmobile.data.local.repository.ResultCodeLocalRepository
import com.tracker.scotmobile.data.local.repository.OrderServiceLocalRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesScreen(
    onNavigateBack: () -> Unit, userToken: String? = null
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

    // Carregar dados ao entrar na tela
    LaunchedEffect(Unit) {
        userToken?.let { token ->
            syncViewModel.loadServiceOrders(token)
            syncViewModel.loadResultCodes(token)
            syncViewModel.loadServices(token)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Serviços e Ordens") }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar"
                    )
                }
            }, actions = {
                IconButton(
                    onClick = {
                        userToken?.let { token ->
                            syncViewModel.syncServices(token)
                        }
                    }, enabled = !syncState.isSyncing
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
            })
        }) { paddingValues ->
        if (syncState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Resumo
                item {
                    SummaryCard(
                        servicesCount = syncState.services.size,
                        ordersCount = syncState.serviceOrders.size,
                        lastSync = syncState.lastSync
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Seção de Serviços
                item {
                    Text(
                        text = "Serviços Disponíveis",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(syncState.services) { service ->
                    ServiceCard(service = service)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Seção de Ordens de Serviço
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Ordens de Serviço",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(syncState.serviceOrders) { order ->
                    ServiceOrderCard(order = order)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                // Seção de Códigos de Resultado
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Códigos de Resultado",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }
                
                items(syncState.resultCodes) { resultCode ->
                    ResultCodeCard(resultCode = resultCode)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(
    servicesCount: Int, ordersCount: Int, lastSync: Long?
) {
    Card(
        modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Resumo", style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "$servicesCount",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Serviços", style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column {
                    Text(
                        text = "$ordersCount", style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Text(
                        text = "Ordens", style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            lastSync?.let { sync ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Última sincronização: ${formatDate(sync)}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceCard(service: Service) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Navegar para detalhes do serviço */ }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = service.name, style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                PriorityChip(priority = service.priority)
            }

            service.description?.let { description ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description, style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatusChip(status = service.status)

                service.assignedTo?.let { assigned ->
                    Text(
                        text = "Responsável: $assigned", style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceOrderCard(order: ServiceOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Navegar para detalhes da ordem */ }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OS #${order.orderNumber}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = order.serviceName, style = MaterialTheme.typography.bodyMedium
                    )
                }

                PriorityChip(priority = order.priority)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OrderStatusChip(status = order.status)

                order.clientName?.let { client ->
                    Text(
                        text = "Cliente: $client", style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            order.location?.let { location ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = location, style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun PriorityChip(priority: Priority) {
    AssistChip(
        onClick = { },
        label = { Text(priority.description) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(android.graphics.Color.parseColor(priority.color))
        )
    )
}

@Composable
private fun StatusChip(status: ServiceStatus) {
    AssistChip(
        onClick = { },
        label = { Text(status.description) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    )
}

@Composable
private fun OrderStatusChip(status: OrderStatus) {
    AssistChip(
        onClick = { },
        label = { Text(status.description) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = Color(android.graphics.Color.parseColor(status.color))
        )
    )
}

private fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"))
    return formatter.format(date)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResultCodeCard(resultCode: ResultCode) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Selecionar código de resultado */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Código #${resultCode.fnResultCodeId}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                AssistChip(
                    onClick = { },
                    label = { 
                        Text(
                            if (resultCode.fnResultCodeSt) "Ativo" else "Inativo"
                        ) 
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (resultCode.fnResultCodeSt) 
                            Color.Green.copy(alpha = 0.2f) 
                        else 
                            Color.Red.copy(alpha = 0.2f)
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = resultCode.fcResultCodeNm,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
            
            resultCode.fcResultCodeDs?.let { description ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            }
            
            resultCode.fcWarehouseTypeNameList?.let { warehouseTypes ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tipos de Armazém: $warehouseTypes",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ServicesScreenPreview() {
    ScotMobileTheme {
        ServicesScreen(
            onNavigateBack = {}, userToken = "test-token"
        )
    }
}
