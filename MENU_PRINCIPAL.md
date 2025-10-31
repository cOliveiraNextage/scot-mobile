# Menu Principal - ScotMobile

## Funcionalidades Implementadas

### 1. Tela de Menu Principal (HomeScreen)
Após o login bem-sucedido, o usuário é direcionado para uma tela de menu principal com as seguintes características:

#### Layout
- **Header do Usuário**: Card com informações do usuário logado
  - Nome do usuário
  - Cargo/função
  - Ícone de perfil

#### Menu de Opções
O menu principal contém 6 opções organizadas em 3 linhas:

**Primeira Linha:**
- 🚗 **Rastreamento** - Acompanhar veículos (funcional)
- 📋 **Histórico** - Ver histórico de atividades

**Segunda Linha:**
- ⚙️ **Configurações** - Ajustes do aplicativo
- 🔔 **Notificações** - Alertas e avisos

**Terceira Linha:**
- 📊 **Relatórios** - Gerar relatórios
- ❓ **Ajuda** - Suporte e FAQ

#### Botão de Logout
- Botão vermelho "Sair do Sistema" na parte inferior
- Funcionalidade de logout implementada

### 2. Tela de Rastreamento (TrackingScreen)
Implementada como exemplo de navegação do menu principal:

#### Funcionalidades
- Lista de veículos ativos
- Status de cada veículo (online/offline)
- Informações detalhadas:
  - Placa do veículo
  - Modelo
  - Status atual
  - Localização
  - Última atualização
- Botão para visualizar no mapa

#### Navegação
- Botão de voltar na top bar
- Navegação fluida entre telas

### 3. Sistema de Navegação
Implementado usando Jetpack Compose Navigation:

- **Login** → **Menu Principal** (após login bem-sucedido)
- **Menu Principal** → **Rastreamento** (ao clicar no card)
- **Rastreamento** → **Menu Principal** (botão voltar)

## Estrutura de Arquivos

```
app/src/main/java/com/tracker/scotmobile/
├── MainActivity.kt                    # Navegação principal
├── ui/screens/
│   ├── HomeScreen.kt                  # Menu principal
│   ├── LoginScreen.kt                 # Tela de login
│   └── TrackingScreen.kt              # Tela de rastreamento
└── data/model/
    └── LoginResponse.kt               # Modelos de dados
```

## Como Usar

1. **Login**: Digite suas credenciais na tela de login
2. **Menu Principal**: Após login, você verá o menu com suas opções
3. **Navegação**: Clique em qualquer card para navegar (Rastreamento está funcional)
4. **Logout**: Use o botão "Sair do Sistema" para fazer logout

## Próximos Passos

Para implementar as outras telas do menu:

1. Criar novas telas (ex: `HistoryScreen.kt`, `SettingsScreen.kt`)
2. Adicionar rotas no `MainActivity.kt`
3. Implementar navegação nos cards correspondentes
4. Adicionar funcionalidades específicas de cada tela

## Tecnologias Utilizadas

- **Jetpack Compose**: UI moderna e declarativa
- **Material Design 3**: Design system atualizado
- **Navigation Compose**: Navegação entre telas
- **Kotlin**: Linguagem de programação
- **Android Studio**: IDE de desenvolvimento
