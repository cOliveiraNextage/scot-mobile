# Solução de Problemas - Previews do Jetpack Compose

## Problema: Não consigo visualizar os previews das telas

### Soluções:

#### 1. **Verificar Configuração do Android Studio**

1. **Abra o Android Studio**
2. **Vá em File → Settings (ou Preferences no Mac)**
3. **Navegue para: Editor → General → Appearance**
4. **Certifique-se que "Show parameter name hints" está marcado**
5. **Vá em: Editor → Code Style → Kotlin**
6. **Verifique se o Compose está configurado corretamente**

#### 2. **Verificar Dependências**

Certifique-se que estas dependências estão no `build.gradle.kts`:

```kotlin
dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
}
```

#### 3. **Reiniciar o Android Studio**

1. **Feche o Android Studio**
2. **Delete a pasta `.idea` do projeto**
3. **Reabra o projeto**
4. **Aguarde a indexação completa**

#### 4. **Invalidar Caches**

1. **File → Invalidate Caches and Restart**
2. **Selecione "Invalidate and Restart"

#### 5. **Verificar Imports**

Certifique-se que estes imports estão presentes:

```kotlin
import androidx.compose.ui.tooling.preview.Preview
import android.content.res.Configuration
```

#### 6. **Testar Preview Simples**

Use este preview simples para testar:

```kotlin
@Preview(showBackground = true)
@Composable
fun TestPreview() {
    Text("Hello World!")
}
```

#### 7. **Verificar Versão do Compose**

A versão do Compose BOM deve ser compatível:

```kotlin
composeBom = "2024.09.00"
```

#### 8. **Problemas Comuns**

**Problema**: Preview não aparece
- **Solução**: Clique com botão direito no arquivo → "Split Editor" → "Preview"

**Problema**: Preview mostra erro
- **Solução**: Verifique se todas as dependências estão resolvidas

**Problema**: Preview não atualiza
- **Solução**: Pressione Ctrl+Shift+F12 (ou Cmd+Shift+F12 no Mac)

#### 9. **Comandos Úteis**

- **Atualizar Preview**: Ctrl+Shift+F12
- **Abrir Preview**: Alt+P
- **Split Editor**: Ctrl+Alt+Shift+Right

#### 10. **Configuração Avançada**

Se ainda não funcionar, tente:

1. **Clean Project**: Build → Clean Project
2. **Rebuild Project**: Build → Rebuild Project
3. **Sync Project**: File → Sync Project with Gradle Files

### Previews Configurados no Projeto:

1. **LoginScreenPreview** - Preview da tela de login
2. **LoginScreenDarkPreview** - Preview modo escuro
3. **HomeScreenPreview** - Preview da tela home
4. **SimpleLoginPreview** - Preview simples para teste

### Como Usar:

1. **Abra o arquivo** `LoginScreen.kt` ou `HomeScreen.kt`
2. **Clique no ícone de preview** no canto superior direito
3. **Ou use Alt+P** para abrir o preview
4. **Use Ctrl+Shift+F12** para atualizar o preview

### Dicas:

- Os previews podem demorar alguns segundos para carregar
- Se houver erros de compilação, o preview não funcionará
- Previews com ViewModels podem ser mais lentos
- Use previews simples para testar componentes básicos

