# Sincronização de Dados - ScotMobile

## ✅ Funcionalidade Implementada

O aplicativo ScotMobile agora possui um sistema completo de **sincronização de dados** que permite buscar serviços e ordens de serviço da API.

## 🚀 Funcionalidades

### 🔄 **Botão de Sincronização**
- **Localização**: Top bar da tela principal (ícone de sincronização)
- **Funcionalidade**: Busca dados atualizados da API
- **Feedback Visual**: Indicador de loading durante sincronização
- **Notificações**: Snackbar com resultado da operação

### 📊 **Tela de Serviços**
- **Acesso**: Menu principal → "Serviços"
- **Funcionalidades**:
  - Lista de serviços disponíveis
  - Lista de ordens de serviço
  - Resumo com contadores
  - Filtros por status e prioridade
  - Sincronização individual

## 🏗️ Arquitetura Implementada

### **1. Modelos de Dados**
```kotlin
// ServiceModels.kt
data class Service(
    val id: Long,
    val name: String,
    val description: String?,
    val status: ServiceStatus,
    val priority: Priority,
    val assignedTo: String?,
    // ... outros campos
)

data class ServiceOrder(
    val id: Long,
    val orderNumber: String,
    val serviceId: Long,
    val serviceName: String,
    val status: OrderStatus,
    val priority: Priority,
    // ... outros campos
)
```

### **2. API de Sincronização**
```kotlin
// SyncApi.kt
interface SyncApi {
    @GET("sync/services")
    suspend fun syncServices(
        @Header("Authorization") token: String,
        @Query("lastSync") lastSync: Long? = null
    ): SyncResponse
    
    @GET("services")
    suspend fun getServices(@Header("Authorization") token: String): SyncResponse
    
    @GET("service-orders")
    suspend fun getServiceOrders(
        @Header("Authorization") token: String,
        @Query("status") status: String? = null,
        @Query("assignedTo") assignedTo: Long? = null
    ): SyncResponse
}
```

### **3. Repositório de Sincronização**
```kotlin
// SyncRepository.kt
class SyncRepository {
    suspend fun syncServices(token: String, lastSync: Long? = null): Result<SyncResponse>
    suspend fun getServices(token: String): Result<SyncResponse>
    suspend fun getServiceOrders(token: String, status: String? = null, assignedTo: Long? = null): Result<SyncResponse>
}
```

### **4. ViewModel de Sincronização**
```kotlin
// SyncViewModel.kt
class SyncViewModel : ViewModel() {
    fun syncServices(token: String)
    fun loadServices(token: String)
    fun loadServiceOrders(token: String, status: String? = null, assignedTo: Long? = null)
}
```

## 📱 Interface do Usuário

### **Tela Principal (HomeScreen)**
- **Botão de Sincronização**: Ícone na top bar
- **Indicador de Loading**: Circular progress durante sincronização
- **Snackbar**: Feedback de sucesso/erro
- **Menu "Serviços"**: Navegação para tela de serviços

### **Tela de Serviços (ServicesScreen)**
- **Resumo**: Card com contadores e última sincronização
- **Lista de Serviços**: Cards com informações detalhadas
- **Lista de Ordens**: Cards com status e prioridade
- **Chips Coloridos**: Status e prioridade com cores
- **Botão de Sincronização**: Na top bar da tela

## 🔄 Fluxo de Sincronização

### **1. Sincronização Manual**
```
Usuário clica no botão → ViewModel.syncServices() → API → Atualiza UI → Snackbar
```

### **2. Carregamento de Dados**
```
Entra na tela → ViewModel.loadServices() + loadServiceOrders() → API → Lista atualizada
```

### **3. Tratamento de Erros**
```
Erro na API → Result.failure() → ViewModel → Snackbar com erro
```

## 🎨 Componentes Visuais

### **Status Chips**
- **Serviços**: Ativo, Inativo, Em Manutenção, Suspenso
- **Ordens**: Pendente, Em Andamento, Concluído, Cancelado, Agendado, Em Espera
- **Prioridades**: Baixa (verde), Média (amarelo), Alta (laranja), Urgente (vermelho)

### **Cards Informativos**
- **Serviços**: Nome, descrição, status, responsável
- **Ordens**: Número, serviço, status, cliente, localização
- **Resumo**: Contadores e timestamp da última sincronização

## 📊 Endpoints da API

### **Sincronização Completa**
```
GET /sync/services
Headers: Authorization: Bearer {token}
Query: lastSync (opcional)
```

### **Serviços**
```
GET /services
Headers: Authorization: Bearer {token}
```

### **Ordens de Serviço**
```
GET /service-orders
Headers: Authorization: Bearer {token}
Query: status, assignedTo (opcionais)
```

## 🔧 Configuração

### **URLs da API**
```kotlin
// ApiConfig.kt
const val BASE_URL = "http://192.168.200.144:8080/"
```

### **Timeouts**
```kotlin
const val CONNECT_TIMEOUT = 30L
const val READ_TIMEOUT = 30L
const val WRITE_TIMEOUT = 30L
```

## 📁 Estrutura de Arquivos

```
app/src/main/java/com/tracker/scotmobile/
├── data/
│   ├── api/
│   │   └── SyncApi.kt                    # Interface da API
│   ├── model/
│   │   └── ServiceModels.kt              # Modelos de dados
│   └── repository/
│       └── SyncRepository.kt             # Repositório
├── ui/
│   ├── screens/
│   │   ├── HomeScreen.kt                 # Tela principal (atualizada)
│   │   └── ServicesScreen.kt             # Nova tela de serviços
│   └── viewmodel/
│       └── SyncViewModel.kt              # ViewModel de sincronização
└── MainActivity.kt                       # Navegação atualizada
```

## 🎯 Como Usar

### **1. Sincronização Manual**
1. Faça login no aplicativo
2. Na tela principal, clique no ícone de sincronização (🔄)
3. Aguarde o indicador de loading
4. Veja o resultado no snackbar

### **2. Visualizar Serviços**
1. Na tela principal, clique em "Serviços"
2. Veja a lista de serviços e ordens
3. Use o botão de sincronização para atualizar
4. Clique nos cards para ver detalhes (futuro)

### **3. Filtros (Futuro)**
- Por status de ordem
- Por responsável
- Por data
- Por prioridade

## 🚀 Benefícios

1. **Dados Atualizados**: Sincronização em tempo real
2. **Interface Intuitiva**: Feedback visual claro
3. **Performance**: Carregamento sob demanda
4. **Flexibilidade**: Múltiplos endpoints
5. **Tratamento de Erros**: Feedback de problemas
6. **Offline Ready**: Preparado para cache local

## 🔮 Próximos Passos

- [ ] Implementar cache local dos dados
- [ ] Sincronização automática em background
- [ ] Filtros avançados
- [ ] Detalhes de serviços/ordens
- [ ] Notificações push
- [ ] Modo offline
- [ ] Histórico de sincronizações

## 🧪 Testando

1. **Sincronização**: Clique no botão 🔄 na tela principal
2. **Navegação**: Menu → Serviços
3. **Carregamento**: Veja os dados sendo carregados
4. **Erros**: Teste com API offline
5. **UI**: Verifique chips coloridos e cards

A funcionalidade está completa e pronta para uso! 🎉
