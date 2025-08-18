# ScotMobile - Aplicativo Android

Um aplicativo Android moderno com tela de login elegante e integração com backend.

## Funcionalidades

- ✅ Tela de login moderna com design Material 3
- ✅ Validação de campos em tempo real
- ✅ Integração com API REST usando Retrofit
- ✅ Gerenciamento de estado com ViewModel e StateFlow
- ✅ Navegação entre telas com Navigation Compose
- ✅ Tratamento de erros e loading states
- ✅ Interface responsiva e acessível

## Estrutura do Projeto

```
app/src/main/java/com/tracker/scotmobile/
├── data/
│   ├── api/
│   │   ├── ApiConfig.kt          # Configurações da API
│   │   ├── AuthApi.kt            # Interface da API de autenticação
│   │   └── RetrofitClient.kt     # Cliente Retrofit
│   ├── model/
│   │   ├── LoginRequest.kt       # Modelo de requisição de login
│   │   └── LoginResponse.kt      # Modelo de resposta de login
│   └── repository/
│       └── AuthRepository.kt     # Repositório de autenticação
├── ui/
│   ├── screens/
│   │   ├── LoginScreen.kt        # Tela de login
│   │   └── HomeScreen.kt         # Tela principal
│   └── viewmodel/
│       └── LoginViewModel.kt     # ViewModel da tela de login
└── MainActivity.kt               # Activity principal
```

## Configuração da API

### 1. URL da API

Edite o arquivo `app/src/main/java/com/tracker/scotmobile/data/api/ApiConfig.kt` e altere a `BASE_URL`:

```kotlin
const val BASE_URL = "https://sua-api.com/"
```

### 2. Endpoint de Login

O aplicativo espera um endpoint de login em:
```
POST /auth/login
```

### 3. Formato da Requisição

```json
{
  "email": "usuario@exemplo.com",
  "password": "senha123"
}
```

### 4. Formato da Resposta

```json
{
  "success": true,
  "object": {
    "success": true,
    "message": "Login realizado com sucesso",
    "object": {
      "jwt": {
        "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
        "tokenType": "Bearer",
        "expiresIn": 1709822803,
        "userId": 41377
      },
      "scUserDTO": {
        "fnUserId": 41377,
        "fcUserNm": "Luiz Gustavo Liu dos Santos",
        "fcUserLogin": "luiz.santos",
        "fcUserDocNu": "06797092906",
        "scRole": {
          "fnRoleId": 1,
          "fcRoleDs": "Administrador do SCOT",
          "fcRoleNm": "ADMINISTRADOR"
        }
      }
    }
  }
}
```

**Nota**: O app usa um mapper para simplificar essa resposta complexa e extrair apenas os dados necessários.

## Tecnologias Utilizadas

- **Jetpack Compose**: Interface do usuário
- **Material 3**: Design system
- **Retrofit**: Cliente HTTP para APIs
- **OkHttp**: Cliente HTTP base
- **Coroutines**: Programação assíncrona
- **ViewModel**: Gerenciamento de estado
- **Navigation Compose**: Navegação entre telas
- **StateFlow**: Reatividade

## Como Executar

1. Clone o repositório
2. Abra o projeto no Android Studio
3. Configure a URL da API no `ApiConfig.kt`
4. Execute o aplicativo no emulador ou dispositivo

## Desenvolvimento Local

Para desenvolvimento com backend local:

1. **Emulador Android**: Use `http://10.0.2.2:8080/`
2. **Dispositivo Físico**: Use `http://localhost:8080/` ou o IP da sua máquina

## Próximos Passos

- [ ] Implementar registro de usuário
- [ ] Adicionar recuperação de senha
- [ ] Implementar persistência de token
- [ ] Adicionar biometria
- [ ] Implementar logout automático
- [ ] Adicionar testes unitários
- [ ] Implementar cache offline

## Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## Licença

Este projeto está sob a licença MIT.
