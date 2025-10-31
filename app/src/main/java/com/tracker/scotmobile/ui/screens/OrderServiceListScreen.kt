package com.tracker.scotmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tracker.scotmobile.data.local.AppDatabase
import com.tracker.scotmobile.data.local.entity.AddressEntity
import com.tracker.scotmobile.data.local.entity.OrderServiceEntity
import com.tracker.scotmobile.data.local.entity.ProductEntity
import com.tracker.scotmobile.data.local.entity.TaskEntity
import com.tracker.scotmobile.data.local.repository.OrderServiceLocalRepository
import com.tracker.scotmobile.data.local.repository.ResultCodeLocalRepository
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderServiceListScreen(
    onNavigateBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()

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

    // Observar ordens de serviço do banco local
    val orderServices by orderServiceLocalRepository.getAllOrderServices()
        .collectAsState(initial = emptyList())

    // Estado para o texto de busca
    var searchText by remember { mutableStateOf("") }

    // Filtrar ordens de serviço baseado no fnServiceOrderId
    val filteredOrderServices = remember(orderServices, searchText) {
        if (searchText.isBlank()) {
            orderServices
        } else {
            orderServices.filter { order ->
                order.fnServiceOrderId.toString().contains(searchText, ignoreCase = true)
            }
        }
    }

    // Mapa para armazenar produtos, tasks e endereços por ordem
    var orderDetails by remember {
        mutableStateOf<Map<Long, Triple<ProductEntity?, TaskEntity?, AddressEntity?>>>(emptyMap())
    }

    // Carregar detalhes (produto, task e endereço) para cada ordem
    LaunchedEffect(orderServices) {
        if (orderServices.isNotEmpty()) {
            scope.launch {
                val detailsMap =
                    mutableMapOf<Long, Triple<ProductEntity?, TaskEntity?, AddressEntity?>>()

                orderServices.forEach { order ->
                    val product =
                        orderServiceLocalRepository.getProductByOrderId(order.fnServiceOrderId)
                    val task = orderServiceLocalRepository.getTaskById(order.fnServiceTypeId)
                    val address =
                        orderServiceLocalRepository.getAddressByOrderId(order.fnServiceOrderId)
                    detailsMap[order.fnServiceOrderId] = Triple(product, task, address)
                }

                orderDetails = detailsMap
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ordens de Serviço") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Campo de busca
            if (orderServices.isNotEmpty()) {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Buscar por número da OS...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Buscar"
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            if (orderServices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nenhuma ordem de serviço encontrada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sincronize os dados para ver as ordens",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else if (filteredOrderServices.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Nenhuma OS encontrada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tente buscar com outro número",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredOrderServices) { order ->
                        val details = orderDetails[order.fnServiceOrderId]
                        val product = details?.first
                        val task = details?.second
                        val address = details?.third

                        OrderServiceCard(
                            orderService = order,
                            productName = product?.productName ?: "N/A",
                            serviceTypeName = getServiceTypeName(order.fnServiceTypeId, task),
                            address = address,
                            onOpenLocation = { lat, lng ->
                                openGoogleMaps(context, lat, lng)
                            }
                        )
                    }
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderServiceCard(
    orderService: OrderServiceEntity,
    productName: String,
    serviceTypeName: String,
    address: AddressEntity?,
    onOpenLocation: (String, String) -> Unit
) {
    var showLocationDialog by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Diálogo para abrir Google Maps
    if (showLocationDialog && address?.fcLatitude != null && address.fcLongitude != null) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Abrir Localização") },
            text = {
                Text("Deseja abrir este endereço no Google Maps?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLocationDialog = false
                        onOpenLocation(address.fcLatitude!!, address.fcLongitude!!)
                    }
                ) {
                    Text("Abrir no Maps")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = { /* TODO: Navegar para detalhes */ }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Número da OS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OS #${orderService.fnServiceOrderId}",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                AssistChip(
                    onClick = { },
                    label = { Text(orderService.fcWarehouseType) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tipo de Solicitação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Tipo de Serviço",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = serviceTypeName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                if (orderService.isInspection) {
                    AssistChip(
                        onClick = { },
                        label = { Text("Inspeção") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Produto
            Column {
                Text(
                    text = "Produto",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = productName,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Data e Hora
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Data e Hora",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatScheduleDate(orderService.fdSchedule),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Cliente",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = orderService.fcCostumer,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = androidx.compose.ui.text.style.TextAlign.End,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            // Ícone de localização
            if (address?.fcLatitude != null && address.fcLongitude != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = { showLocationDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Abrir localização no Google Maps",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

fun getServiceTypeName(serviceTypeId: Long, task: TaskEntity?): String {
    // Se encontrou a task, usar o nome dela
    task?.let { return it.fcTaskNm }

    // Caso contrário, mapear pelo ID comum
    return when (serviceTypeId) {
        1L -> "Instalação"
        2L -> "Revisão"
        3L -> "Desinstalação"
        else -> "Tipo $serviceTypeId"
    }
}

fun formatScheduleDate(schedule: String): String {
    return try {
        // Formato esperado: "2025/08/21 12:00" ou "2025-08-21 12:00"
        val parts = schedule.split(" ")
        if (parts.size == 2) {
            val datePart = parts[0]
            val timePart = parts[1]

            // Tentar parsear a data no formato aaaa/mm/dd ou aaaa-mm-dd
            val inputFormat = if (datePart.contains("/")) {
                SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            }

            val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = inputFormat.parse(datePart)

            if (date != null) {
                val formattedDate = outputFormat.format(date)
                "$formattedDate às $timePart"
            } else {
                "$datePart às $timePart"
            }
        } else {
            schedule
        }
    } catch (e: Exception) {
        // Se falhar, tentar apenas reordenar manualmente
        try {
            val parts = schedule.split(" ", limit = 2)
            if (parts.size == 2) {
                val datePart = parts[0]
                val timePart = parts[1]

                // Formato: aaaa/mm/dd ou aaaa-mm-dd
                val dateComponents = datePart.split(Regex("[/-]"))
                if (dateComponents.size == 3) {
                    val day = dateComponents[2].padStart(2, '0')
                    val month = dateComponents[1].padStart(2, '0')
                    val year = dateComponents[0]
                    "$day-$month-$year às $timePart"
                } else {
                    schedule
                }
            } else {
                schedule
            }
        } catch (ex: Exception) {
            schedule
        }
    }
}

/**
 * Abre o Google Maps com as coordenadas fornecidas
 */
fun openGoogleMaps(context: Context, latitude: String, longitude: String) {
    try {
        val lat = latitude.toDouble()
        val lng = longitude.toDouble()

        // Criar URI para Google Maps
        val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        // Tentar abrir Google Maps, se não estiver disponível, usar app padrão
        try {
            context.startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            // Se o Google Maps não estiver instalado, abrir em outro app de mapas
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
            )
            context.startActivity(webIntent)
        }
    } catch (e: Exception) {
        Log.e("OrderServiceListScreen", "Erro ao abrir Google Maps: ${e.message}")
    }
}