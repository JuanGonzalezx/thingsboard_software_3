# Guía de Configuración y Prueba: Notificaciones por Telegram

## 🔧 **CONFIGURACIÓN INICIAL**

### 1. Crear Bot de Telegram

1. **Abrir Telegram y buscar @BotFather**
2. **Crear nuevo bot:**
   ```
   /newbot
   ```
3. **Seguir las instrucciones:**
   - Nombre del bot: `ThingsBoard Notifications Bot`
   - Username: `thingsboard_notifications_bot` (debe terminar en 'bot')
4. **Guardar el token** que proporciona BotFather (formato: `123456789:ABCdefGhiJklMnoPqrsTuvWxyz`)

### 2. Configurar ThingsBoard

1. **Acceder como System Administrator o Tenant Administrator**
2. **Ir a Settings → Notifications → Delivery Methods**
3. **Configurar Telegram:**
   ```json
   {
     "method": "TELEGRAM",
     "botToken": "123456789:ABCdefGhiJklMnoPqrsTuvWxyz"
   }
   ```

### 3. Obtener Chat IDs

Para enviar notificaciones, necesitas los Chat IDs de los usuarios/grupos:

1. **Para usuarios individuales:**
   - El usuario debe enviar `/start` al bot
   - Usar la API: `https://api.telegram.org/bot{TOKEN}/getUpdates`
   - Buscar el `chat.id` en la respuesta

2. **Para grupos:**
   - Agregar el bot al grupo
   - Enviar un mensaje en el grupo
   - Usar la misma API para obtener el chat ID del grupo

## 🧪 **COMO PROBAR LA FUNCIONALIDAD**

### Prueba 1: Configuración Básica

1. **Compilar el proyecto:**
   ```bash
   cd /path/to/thingsboard
   mvn clean install -DskipTests
   ```

2. **Iniciar ThingsBoard:**
   ```bash
   java -jar application/target/thingsboard-*.jar
   ```

3. **Verificar en logs:**
   ```
   INFO - TelegramNotificationChannel bean created successfully
   INFO - TelegramService initialized
   ```

### Prueba 2: Configurar Notificación de Prueba

1. **Crear Notification Template:**
   - Ir a `Settings → Notifications → Templates`
   - Crear nuevo template:
     ```
     Name: "Telegram Test Template"
     Type: GENERAL
     Delivery Methods: 
       - Telegram: "🔔 Mensaje de prueba desde ThingsBoard: ${message}"
     ```

2. **Crear Notification Target:**
   - Ir a `Settings → Notifications → Targets`
   - Crear nuevo target:
     ```
     Name: "Telegram Test Chat"
     Configuration:
       - Chat Type: PRIVATE
       - Chat ID: "tu_chat_id_aqui"
       - Name: "Usuario Test"
     ```

3. **Crear Notification Rule:**
   - Ir a `Settings → Notifications → Rules`
   - Crear nueva regla:
     ```
     Name: "Telegram Test Rule"
     Template: "Telegram Test Template"
     Targets: ["Telegram Test Chat"]
     Trigger: Manual (para pruebas)
     ```

### Prueba 3: Envío Manual de Notificación

1. **Usar la API REST para enviar notificación de prueba:**
   ```bash
   curl -X POST http://localhost:8080/api/notifications \
     -H "Content-Type: application/json" \
     -H "X-Authorization: Bearer {JWT_TOKEN}" \
     -d '{
       "subject": "Prueba Telegram",
       "text": "Este es un mensaje de prueba",
       "type": "GENERAL",
       "targets": ["{TARGET_ID}"],
       "deliveryMethods": ["TELEGRAM"]
     }'
   ```

### Prueba 4: Verificar Notificación Automática

1. **Usar notificación de mantenimiento predefinida:**
   - La notificación `maintenanceWork` ya está configurada
   - Se puede activar mediante:
     ```bash
     curl -X POST http://localhost:8080/api/notifications/send \
       -H "Content-Type: application/json" \
       -H "X-Authorization: Bearer {JWT_TOKEN}" \
       -d '{
         "notificationType": "GENERAL",
         "templateName": "maintenanceWork",
         "deliveryMethods": ["TELEGRAM"],
         "targets": ["{TARGET_ID}"]
       }'
     ```

## 🐛 **TROUBLESHOOTING**

### Error: "Telegram Bot token is not configured"
- **Solución:** Verificar que el token está configurado correctamente en Settings → Notifications

### Error: "Bad Request: chat not found"
- **Solución:** 
  1. Verificar que el Chat ID es correcto
  2. Asegurarse de que el usuario ha iniciado conversación con el bot (`/start`)
  3. Para grupos, verificar que el bot está añadido y tiene permisos

### Error: "Unauthorized"
- **Solución:** 
  1. Verificar que el token del bot es válido
  2. Revisar que no hay espacios extra en el token
  3. Generar nuevo token con BotFather si es necesario

### No se reciben mensajes
- **Verificar logs de ThingsBoard:**
  ```bash
  tail -f logs/thingsboard.log | grep -i telegram
  ```
- **Verificar status del bot:**
  ```bash
  curl https://api.telegram.org/bot{TOKEN}/getMe
  ```

## 📝 **VALIDACIÓN DE FUNCIONALIDAD**

### Checklist de Pruebas

- [ ] ✅ Bot creado en Telegram
- [ ] ✅ Token configurado en ThingsBoard
- [ ] ✅ Chat ID obtenido
- [ ] ✅ Template de notificación creado
- [ ] ✅ Target configurado
- [ ] ✅ Regla de notificación creada
- [ ] ✅ Mensaje de prueba enviado
- [ ] ✅ Mensaje recibido en Telegram
- [ ] ✅ Logs sin errores

### Casos de Uso Típicos

1. **Notificaciones de mantenimiento:** Avisar a administradores sobre mantenimiento programado
2. **Alertas de sistema:** Notificar fallos o problemas críticos
3. **Actualizaciones de estado:** Informar cambios importantes en la plataforma
4. **Recordatorios:** Notificaciones programadas para tareas importantes

## 🔧 **COMANDOS ÚTILES PARA DESARROLLO**

### Compilar solo el módulo de notificaciones:
```bash
mvn clean compile -pl application -am
```

### Reiniciar solo el servicio:
```bash
sudo systemctl restart thingsboard
```

### Ver logs en tiempo real:
```bash
journalctl -u thingsboard -f | grep -i telegram
```

### Verificar configuración de notificaciones:
```bash
curl -H "X-Authorization: Bearer {TOKEN}" \
  http://localhost:8080/api/notification/settings
```

¡Con esta configuración ya tienes el módulo de Telegram funcionando en ThingsBoard! 🚀
