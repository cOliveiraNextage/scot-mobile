# Exemplo de Backend para ScotMobile

Este arquivo contém exemplos de como implementar o backend para testar o aplicativo ScotMobile.

## Endpoint de Login

### Node.js com Express

```javascript
const express = require('express');
const cors = require('cors');
const app = express();

app.use(cors());
app.use(express.json());

// Simulação de banco de dados
const users = [
  {
    id: "1",
    email: "admin@scotmobile.com",
    password: "123456",
    name: "Administrador",
    profileImage: null
  },
  {
    id: "2", 
    email: "user@scotmobile.com",
    password: "123456",
    name: "Usuário Teste",
    profileImage: "https://exemplo.com/foto.jpg"
  }
];

app.post('/auth/login', (req, res) => {
  const { email, password } = req.body;
  
  // Validação básica
  if (!email || !password) {
    return res.status(400).json({
      success: false,
      message: "Email e senha são obrigatórios"
    });
  }
  
  // Buscar usuário
  const user = users.find(u => u.email === email && u.password === password);
  
  if (!user) {
    return res.status(401).json({
      success: false,
      message: "Email ou senha inválidos"
    });
  }
  
  // Simular token JWT
  const token = `jwt_${user.id}_${Date.now()}`;
  
  res.json({
    success: true,
    message: "Login realizado com sucesso",
    token: token,
    user: {
      id: user.id,
      name: user.name,
      email: user.email,
      profileImage: user.profileImage
    }
  });
});

const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
  console.log(`Servidor rodando na porta ${PORT}`);
});
```

### Python com Flask

```python
from flask import Flask, request, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# Simulação de banco de dados
users = [
    {
        "id": "1",
        "email": "admin@scotmobile.com",
        "password": "123456",
        "name": "Administrador",
        "profileImage": None
    },
    {
        "id": "2",
        "email": "user@scotmobile.com", 
        "password": "123456",
        "name": "Usuário Teste",
        "profileImage": "https://exemplo.com/foto.jpg"
    }
]

@app.route('/auth/login', methods=['POST'])
def login():
    data = request.get_json()
    email = data.get('email')
    password = data.get('password')
    
    # Validação básica
    if not email or not password:
        return jsonify({
            "success": False,
            "message": "Email e senha são obrigatórios"
        }), 400
    
    # Buscar usuário
    user = next((u for u in users if u['email'] == email and u['password'] == password), None)
    
    if not user:
        return jsonify({
            "success": False,
            "message": "Email ou senha inválidos"
        }), 401
    
    # Simular token JWT
    import time
    token = f"jwt_{user['id']}_{int(time.time())}"
    
    return jsonify({
        "success": True,
        "message": "Login realizado com sucesso",
        "token": token,
        "user": {
            "id": user['id'],
            "name": user['name'],
            "email": user['email'],
            "profileImage": user['profileImage']
        }
    })

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True)
```

### Java com Spring Boot

```java
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    // Simulação de banco de dados
    private final List<User> users = Arrays.asList(
        new User("1", "admin@scotmobile.com", "123456", "Administrador", null),
        new User("2", "user@scotmobile.com", "123456", "Usuário Teste", "https://exemplo.com/foto.jpg")
    );

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        // Validação básica
        if (request.getEmail() == null || request.getPassword() == null) {
            return ResponseEntity.badRequest()
                .body(new LoginResponse(false, "Email e senha são obrigatórios", null, null));
        }

        // Buscar usuário
        User user = users.stream()
            .filter(u -> u.getEmail().equals(request.getEmail()) && 
                        u.getPassword().equals(request.getPassword()))
            .findFirst()
            .orElse(null);

        if (user == null) {
            return ResponseEntity.status(401)
                .body(new LoginResponse(false, "Email ou senha inválidos", null, null));
        }

        // Simular token JWT
        String token = "jwt_" + user.getId() + "_" + System.currentTimeMillis();

        return ResponseEntity.ok(new LoginResponse(
            true,
            "Login realizado com sucesso",
            token,
            user
        ));
    }
}

// Classes de modelo
class LoginRequest {
    private String email;
    private String password;
    // getters e setters
}

class LoginResponse {
    private boolean success;
    private String message;
    private String token;
    private User user;
    // construtor, getters e setters
}

class User {
    private String id;
    private String email;
    private String password;
    private String name;
    private String profileImage;
    // construtor, getters e setters
}
```

## Como Testar

1. **Configure o backend** usando um dos exemplos acima
2. **Altere a URL da API** no arquivo `ApiConfig.kt`:
   ```kotlin
   const val BASE_URL = "http://10.0.2.2:8080/" // Para emulador
   ```
3. **Execute o backend** na porta 8080
4. **Execute o aplicativo Android**
5. **Teste com as credenciais**:
   - Email: `admin@scotmobile.com` / Senha: `123456`
   - Email: `user@scotmobile.com` / Senha: `123456`

## Dicas de Desenvolvimento

- Use **Postman** ou **Insomnia** para testar os endpoints
- Configure **CORS** adequadamente no backend
- Implemente **validação de senha** mais robusta (hash)
- Use **JWT real** para autenticação
- Implemente **refresh tokens** para segurança
- Adicione **rate limiting** para prevenir ataques

