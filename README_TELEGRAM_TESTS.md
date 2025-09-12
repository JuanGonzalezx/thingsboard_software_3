# 🧪 README - Pruebas de Telegram para ThingsBoard

## 📋 Índice
- [Descripción General](#descripción-general)
- [Configuración Inicial](#configuración-inicial)
- [Tipos de Pruebas](#tipos-de-pruebas)
- [Comandos de Ejecución](#comandos-de-ejecución)
- [Solución de Problemas](#solución-de-problemas)
- [Archivos Importantes](#archivos-importantes)

---

## 🎯 Descripción General

Este proyecto incluye dos tipos de pruebas para verificar la funcionalidad de notificaciones de Telegram en ThingsBoard:

1. **Pruebas Unitarias** - Simuladas con mocks (NO envían mensajes reales)
2. **Pruebas de Integración** - Reales (SÍ envían mensajes a Telegram)

---

## ⚙️ Configuración Inicial

### Requisitos Previos

- Java 17+
- Maven 3.6+
- Bot de Telegram configurado
- Chat/Grupo de Telegram para pruebas

### Variables de Entorno

Para las pruebas de integración, configura estas variables:

```bash
export TELEGRAM_BOT_TOKEN="tu_bot_token_aqui"
export TELEGRAM_CHAT_ID="tu_chat_id_aqui"
```


export TELEGRAM_BOT_TOKEN="8267413521:AAHqfznze9NpU-wRbQ4-IOkr7YigbFvnoqE"
export TELEGRAM_CHAT_ID="-4965225979"

### Verificar Configuración

```bash
# Verificar bot token
curl -X GET "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/getMe"

# Obtener chat IDs disponibles
curl -X GET "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/getUpdates"
```

---

## 🧪 Tipos de Pruebas

### 1. Pruebas Unitarias (DefaultTelegramServiceTest)

**Características:**
- ✅ Rápidas (2-3 segundos)
- ✅ No requieren conectividad
- ✅ Usan mocks para simular respuestas
- ❌ NO envían mensajes reales

**Qué verifican:**
- Lógica de envío de mensajes
- Manejo de errores de API
- Parsing de respuestas
- Validación de parámetros

### 2. Pruebas de Integración (TelegramIntegrationTest)

**Características:**
- ✅ Verifican integración real
- ✅ Envían mensajes reales a Telegram
- ❌ Requieren configuración de bot
- ❌ Más lentas (5-10 segundos)

**Qué verifican:**
- Conectividad con API de Telegram
- Envío real de mensajes
- Formato HTML en mensajes
- Obtención de actualizaciones

---

## 🚀 Comandos de Ejecución

### Compilar el Proyecto

```bash
# Cambiar al directorio del proyecto
cd /ruta/a/thingsboard_software_3

# Compilar módulos necesarios
mvn compile -pl application,common,dao,rule-engine,transport
```

### Ejecutar Pruebas Unitarias

```bash
# Ejecutar solo pruebas unitarias (rápido, sin mensajes reales)
mvn test -pl application -Dtest=DefaultTelegramServiceTest
```

**Resultado esperado:**
```
Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Ejecutar Pruebas de Integración

```bash
# Método 1: Con variables de entorno configuradas previamente
mvn test -pl application -Dtest=TelegramIntegrationTest

# Método 2: Configurar variables en línea
TELEGRAM_BOT_TOKEN="tu_token" TELEGRAM_CHAT_ID="tu_chat_id" mvn test -pl application -Dtest=TelegramIntegrationTest
```

**Resultado esperado:**
```
🔥 === PRUEBAS DE INTEGRACIÓN REAL - TELEGRAM ===
⚠️  ESTAS PRUEBAS ENVÍAN MENSAJES REALES A TELEGRAM

✅ ¡MENSAJE ENVIADO EXITOSAMENTE!
📱 Revisa tu chat de Telegram para ver el mensaje

Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Ejecutar Todas las Pruebas

```bash
# Primero las unitarias (rápidas)
mvn test -pl application -Dtest=DefaultTelegramServiceTest

# Luego las de integración (reales)
TELEGRAM_BOT_TOKEN="tu_token" TELEGRAM_CHAT_ID="tu_chat_id" mvn test -pl application -Dtest=TelegramIntegrationTest
```

### Script Automatizado

```bash
# Dar permisos de ejecución
chmod +x run_telegram_real_tests.sh

# Ejecutar script que configura y ejecuta pruebas
./run_telegram_real_tests.sh
```

---

## 🛠️ Solución de Problemas

### Error: "chat not found"

**Causa:** Chat ID incorrecto o bot sin acceso

**Solución:**
```bash
# Obtener chat IDs correctos
curl -X GET "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/getUpdates"

# Enviar mensaje al bot para generar update
# Luego ejecutar el comando anterior
```

### Error: "Unauthorized"

**Causa:** Bot token inválido

**Solución:**
```bash
# Verificar token
curl -X GET "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/getMe"

# Si falla, revisar token en @BotFather
```

### Error: "Forbidden"

**Causa:** Bot sin permisos en el grupo

**Solución:**
1. Agregar bot al grupo
2. Dar permisos de envío de mensajes
3. Asegurarse de que el grupo no esté restringido

### Error: Variables de entorno no configuradas

**Error:**
```
❌ TELEGRAM_BOT_TOKEN no está configurado
❌ TELEGRAM_CHAT_ID no está configurado
```

**Solución:**
```bash
export TELEGRAM_BOT_TOKEN="tu_bot_token"
export TELEGRAM_CHAT_ID="tu_chat_id"
```

### Error: Compilación fallida

```bash
# Verificar errores
mvn clean compile -pl application

# Instalar dependencias faltantes
mvn install -pl rule-engine/rule-engine-api
```

---

## 📁 Archivos Importantes

### Archivos de Pruebas

| Archivo | Ubicación | Propósito |
|---------|-----------|-----------|
| `DefaultTelegramServiceTest.java` | `application/src/test/java/.../provider/` | Pruebas unitarias con mocks |
| `TelegramIntegrationTest.java` | `application/src/test/java/.../provider/` | Pruebas de integración reales |
| `application-test.yml` | `application/src/test/resources/` | Configuración de pruebas |

### Archivos de Implementación

| Archivo | Ubicación | Propósito |
|---------|-----------|-----------|
| `DefaultTelegramService.java` | `application/src/main/java/.../provider/` | Servicio principal de Telegram |
| `TelegramNotificationChannel.java` | `application/src/main/java/.../channels/` | Canal de notificaciones |
| `TelegramService.java` | `rule-engine/rule-engine-api/src/main/java/.../notification/` | Interfaz del servicio |

### Scripts

| Archivo | Ubicación | Propósito |
|---------|-----------|-----------|
| `run_telegram_real_tests.sh` | Raíz del proyecto | Script automatizado para pruebas reales |

---

## 🔍 Interpretación de Logs

### Logs Normales (Esperados)

```
INFO - TelegramService initialized
WARN - getToken method not implemented for tenant: xxx
```

### Logs de Pruebas de Error (Esperados)

```
ERROR - Failed to send Telegram message
java.lang.RuntimeException: Failed to send Telegram message: {"ok":false,"error_code":400,"description":"Bad Request: chat not found"}
```

**Nota:** Estos errores en las pruebas son **ESPERADOS** y demuestran que el manejo de errores funciona correctamente.

### Logs de Problemas Reales

```
ERROR - Connection refused
ERROR - Network error
ERROR - Timeout
```

---

## 📊 Ejemplos de Salida

### Pruebas Unitarias Exitosas

```
🚀 === CONFIGURACIÓN DE PRUEBAS DE TELEGRAM ===
Bot Token configurado: 8216106650...
Chat ID de prueba: -1002919021008

=== TEST: testSendMessage_Success ===
Verificando llamada a API:
  - URL llamada: https://api.telegram.org/bot8216106650:.../sendMessage
  - Cuerpo de la petición: {text=Test notification message, chat_id=-1002919021008}
✅ Prueba exitosa - Mensaje enviado correctamente

Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Pruebas de Integración Exitosas

```
🔥 === PRUEBAS DE INTEGRACIÓN REAL - TELEGRAM ===
⚠️  ESTAS PRUEBAS ENVÍAN MENSAJES REALES A TELEGRAM

🚀 === ENVIANDO MENSAJE REAL A TELEGRAM ===
  📝 Mensaje: 🧪 Test desde ThingsBoard - 1757654052071
  🤖 Bot: 8216106650...
  💬 Chat: -1002919021008

✅ ¡MENSAJE ENVIADO EXITOSAMENTE!
📱 Revisa tu chat de Telegram para ver el mensaje

Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## 📞 Soporte y Contacto

### Información del Proyecto

- **Proyecto:** ThingsBoard con integración Telegram
- **Versión:** 4.3.0-SNAPSHOT
- **Java:** 17+
- **Framework:** Spring Boot

### Recursos Adicionales

- `TELEGRAM_NOTIFICATIONS_GUIDE.md` - Guía completa de configuración
- `QUICK_START_TELEGRAM.md` - Guía rápida de inicio
- Logs de aplicación en `docker-compose logs -f thingsboard-ce`

---

## ⚠️ Notas Importantes

- **🚨 Las pruebas de integración envían mensajes REALES a Telegram**
- **🔒 Nunca hagas commit de tokens reales en el repositorio**
- **🧪 Usa pruebas unitarias para desarrollo continuo**
- **🚀 Usa pruebas de integración para validación final**
- **📱 Siempre verifica los mensajes en tu chat de Telegram después de las pruebas**
- **⏱️ Las pruebas de integración pueden tomar varios segundos**

---

*Generado para ThingsBoard 4.3.0-SNAPSHOT - Integración Telegram*