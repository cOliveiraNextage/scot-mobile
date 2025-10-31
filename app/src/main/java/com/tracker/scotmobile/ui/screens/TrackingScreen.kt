package com.tracker.scotmobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.tracker.scotmobile.ui.theme.ScotMobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ordens de Serviço") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .verticalScroll(rememberScrollState())
        ) {
            // Header informativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Rastreamento",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Lista de Ordens de Serviço",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Realise a instalação, revisão e desistalação de equipamentos",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Lista de veículos (exemplo)
            VehicleList()
        }
    }
}

@Composable
private fun VehicleList() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Veículos Ativos",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Veículo 1
        VehicleCard(
            plate = "ABC-1234",
            model = "Mercedes-Benz Sprinter",
            status = "Em movimento",
            location = "São Paulo, SP",
            lastUpdate = "2 min atrás",
            isOnline = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Veículo 2
        VehicleCard(
            plate = "XYZ-5678",
            model = "Ford Transit",
            status = "Parado",
            location = "Rio de Janeiro, RJ",
            lastUpdate = "5 min atrás",
            isOnline = true
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Veículo 3
        VehicleCard(
            plate = "DEF-9012",
            model = "Volkswagen Crafter",
            status = "Offline",
            location = "Belo Horizonte, MG",
            lastUpdate = "1 hora atrás",
            isOnline = false
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VehicleCard(
    plate: String,
    model: String,
    status: String,
    location: String,
    lastUpdate: String,
    isOnline: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { /* TODO: Navegar para detalhes do veículo */ }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status indicator
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Status",
                    modifier = Modifier.size(12.dp),
                    tint = if (isOnline) MaterialTheme.colorScheme.primary 
                           else MaterialTheme.colorScheme.error
                )
            }
            
            // Informações do veículo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plate,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                
                Text(
                    text = model,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Text(
                    text = status,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = if (isOnline) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.error
                    )
                )
                
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
                
                Text(
                    text = "Última atualização: $lastUpdate",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                )
            }
            
            // Botão de ação
            IconButton(
                onClick = { /* TODO: Abrir mapa */ }
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Ver no mapa"
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TrackingScreenPreview() {
    ScotMobileTheme {
        TrackingScreen(
            onNavigateBack = {}
        )
    }
}
