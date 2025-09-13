# 📱 Implementación de Notificaciones por Telegram en ThingsBoard

## 🚀 Resumen de la Funcionalidad

La nueva funcionalidad de **Notificaciones por Telegram** permite al sistema ThingsBoard enviar alertas y notificaciones automáticas a través de Telegram Bot API, proporcionando un canal de comunicación instantáneo y ubicuo para administradores y usuarios del sistema.

### 🎯 Características Principales

- **✅ Notificaciones Automáticas del Sistema**: Alertas de conexión/desconexión de dispositivos, errores de conectividad, actualizaciones de firmware, etc.
- **✅ Configuración Simplificada**: Credenciales por defecto configurables mediante variables de entorno
- **✅ Integración Transparente**: Funciona junto con los canales existentes (Email, SMS, Web, Slack, etc.)
- **✅ Soporte para Templates**: Plantillas personalizables con formato HTML
- **✅ Gestión Centralizada**: Administración desde la interfaz web de ThingsBoard

---

## 💡 Beneficios para el Proyecto

### 1. **📢 Alcance Inmediato y Universal**
- **Ubicuidad**: Telegram está disponible en todas las plataformas (móvil, web, desktop)
- **Entrega Instantánea**: Las notificaciones llegan inmediatamente al dispositivo del usuario
- **Sin Dependencias Externas**: No requiere configuración de servidores SMTP o SMS

### 2. **💰 Eficiencia Operacional**
- **Costo Cero**: Telegram Bot API es gratuito, reduciendo costos operacionales
- **Escalabilidad**: Maneja volúmenes altos de mensajes sin limitaciones de cuota
- **Confiabilidad**: Infraestructura robusta de Telegram garantiza alta disponibilidad

### 3. **🛠️ Facilidad de Gestión**
- **Configuración Automática**: Credenciales por defecto facilitan la adopción
- **Monitoreo en Tiempo Real**: Notificaciones inmediatas de eventos críticos del sistema
- **Trazabilidad Completa**: Historial de notificaciones en la interfaz de ThingsBoard

### 4. **🎨 Flexibilidad de Formato**
- **Soporte HTML**: Mensajes con formato enriquecido (negrita, cursiva, código)
- **Templates Personalizables**: Adaptación del contenido según el tipo de notificación
- **Contextualización**: Información específica del tenant, dispositivo y evento

---

## 🏗️ Adherencia a la Arquitectura ThingsBoard

### 1. **🎭 Patrón de Canales de Notificación**

La implementación sigue el **patrón Strategy** ya establecido en ThingsBoard:

```java
// Interfaz común para todos los canales
public interface NotificationChannel<R extends NotificationRecipient> {
    void sendNotification(R recipient, DeliveryMethodNotificationTemplate template, 
                         NotificationProcessingContext ctx);
    void check(TenantId tenantId);
    NotificationDeliveryMethod getDeliveryMethod();
}

// Implementación específica para Telegram
@Component
public class TelegramNotificationChannel implements NotificationChannel<NotificationRecipient>
```

**✅ Beneficios Arquitectónicos:**
- **Polimorfismo**: Todos los canales implementan la misma interfaz
- **Extensibilidad**: Fácil adición de nuevos canales sin modificar código existente
- **Mantenibilidad**: Lógica encapsulada en clases especializadas

### 2. **⚙️ Inyección de Dependencias y Configuración**

Sigue el patrón de **Service Layer** con inyección de dependencias de Spring:

```java
@Component
public class TelegramNotificationChannel {
    private final TelegramService telegramService;
    private final NotificationSettingsService notificationSettingsService;
    
    // Constructor injection siguiendo las buenas prácticas de Spring
    public TelegramNotificationChannel(TelegramService telegramService, 
                                     NotificationSettingsService notificationSettingsService) {
        this.telegramService = telegramService;
        this.notificationSettingsService = notificationSettingsService;
    }
}
```

**✅ Beneficios:**
- **Testabilidad**: Fácil creación de mocks para pruebas unitarias
- **Acoplamiento Débil**: Dependencias inyectadas, no instanciadas directamente
- **Configuración Centralizada**: Gestión a través del contenedor IoC de Spring

### 3. **🎯 Separación de Responsabilidades (SoC)**

La implementación mantiene una clara separación:

```
📦 Estructura de Capas
├── 🌐 Presentation Layer (UI-NGX)
│   ├── SendNotificationButtonComponent     → Interfaz de usuario
│   └── SentNotificationDialogComponent     → Formularios y validación
│
├── 🔧 Service Layer (Application)
│   ├── TelegramNotificationChannel         → Lógica de canal específico
│   ├── DefaultTelegramService              → Comunicación con Telegram API
│   └── DefaultNotificationCenter           → Orchestación de notificaciones
│
├── 💾 Data Layer (Common/DAO)
│   ├── TelegramNotificationDeliveryMethodConfig → Configuración de delivery
│   ├── TelegramDeliveryMethodNotificationTemplate → Templates de mensajes
│   └── NotificationSettingsService         → Persistencia de configuración
│
└── 🔌 Integration Layer
    └── Telegram Bot API                    → Servicio externo
```

### 4. **🎨 Patrón Template Method**

El sistema utiliza el patrón Template Method para el procesamiento de notificaciones:

```java
// Template común para todas las notificaciones
public abstract class TemplateConfiguration<T, R> {
    protected NotificationTemplate getNotificationTemplateValue();
    protected void updateDeliveryMethodsDisableState();
    // ... métodos template comunes
}

// Implementación específica mantiene el flujo común
public class SentNotificationDialogComponent extends TemplateConfiguration<...>
```

### 5. **🔄 Patrón Observer para Configuración**

La configuración sigue el patrón de observación de cambios:

```java
// Reactividad en la configuración
this.notificationRequestForm.get('useTemplate').valueChanges.pipe(
    takeUntil(this.destroy$)
).subscribe(value => {
    // Actualización reactiva de la UI
});
```

---

## 🏆 Buenas Prácticas Implementadas

### 1. **🛡️ Manejo de Errores Robusto**

```java
public void sendNotification(NotificationRecipient recipient, 
                           DeliveryMethodNotificationTemplate template, 
                           NotificationProcessingContext ctx) {
    try {
        // Validación de entrada
        if (recipient instanceof User) {
            // Manejo especial para User recipients
        }
        
        telegramService.sendMessage(ctx.getTenantId(), botToken, chatId, message, useHtml);
        
    } catch (Exception e) {
        log.error("Failed to send Telegram notification", e);
        throw new RuntimeException("Telegram notification delivery failed: " + e.getMessage());
    }
}
```

### 2. **🧪 Testing Integral**

```java
// Tests unitarios con mocks
@ExtendWith(MockitoExtension.class)
class DefaultTelegramServiceTest {
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;
}

// Tests de integración reales
@EnabledIfEnvironmentVariable(named = "TELEGRAM_BOT_TOKEN", matches = ".*")
class TelegramIntegrationTest {
    // Tests que envían mensajes reales a Telegram
}
```

### 3. **⚙️ Configuración Externa**

```java
// Variables de entorno para configuración
String botToken = System.getProperty("TELEGRAM_BOT_TOKEN", 
                  System.getenv("TELEGRAM_BOT_TOKEN"));
String chatId = System.getProperty("TELEGRAM_CHAT_ID", 
                System.getenv("TELEGRAM_CHAT_ID"));
```

### 4. **🔍 Logging y Observabilidad**

```java
@Slf4j
public class TelegramNotificationChannel {
    public void sendNotification(...) {
        log.debug("Sending Telegram notification to chat: {}", chatId);
        // ... lógica
        log.info("Telegram notification sent successfully to chat: {}", chatId);
    }
}
```

### 5. **🎭 Polimorfismo y Flexibilidad**

```java
// Interface común permite tratamiento uniforme
Map<NotificationDeliveryMethod, NotificationChannel<NotificationRecipient>> channels;

// Registro automático de canales
channels.put(NotificationDeliveryMethod.TELEGRAM, telegramNotificationChannel);
```

### 6. **🔧 Validación Específica por Canal**

```java
// Validación especial para Telegram que no requiere recipients tradicionales
if (deliveryMethod != NotificationDeliveryMethod.TELEGRAM && 
    targets.stream().noneMatch(target -> 
        target.getConfiguration().getType().getSupportedDeliveryMethods()
              .contains(deliveryMethod))) {
    throw new IllegalArgumentException("Recipients for " + 
        deliveryMethod.getName() + " delivery method not chosen");
}
```

---

## 📋 Componentes Implementados

### 🔧 **Backend Components**
- **`TelegramNotificationChannel`**: Canal principal de notificaciones
- **`DefaultTelegramService`**: Servicio de comunicación con Telegram API
- **`TelegramNotificationDeliveryMethodConfig`**: Configuración de delivery method
- **`TelegramDeliveryMethodNotificationTemplate`**: Templates de mensajes
- **`DefaultNotificationSettingsService`**: Configuración automática

### 🌐 **Frontend Components**
- **`SendNotificationButtonComponent`**: Botón de envío de notificaciones
- **`SentNotificationDialogComponent`**: Diálogo de creación paso a paso
- **`NotificationService`**: Servicio HTTP para API calls

### 🧪 **Testing Components**
- **`DefaultTelegramServiceTest`**: Tests unitarios con mocks
- **`TelegramIntegrationTest`**: Tests de integración real
- **Simulación de mensajes reales del sistema**

---

## 🎯 Conclusión

La implementación de **Notificaciones por Telegram** en ThingsBoard demuestra una **perfecta adherencia a los principios arquitectónicos** establecidos en el proyecto:

### ✅ **Arquitectura Mantenida**
- **Patrón Strategy** para canales de notificación
- **Inyección de dependencias** con Spring Framework  
- **Separación clara de responsabilidades** en capas
- **Interfaces bien definidas** para extensibilidad

### ✅ **Buenas Prácticas Aplicadas**
- **Testing integral** (unitario + integración)
- **Manejo robusto de errores**
- **Configuración externa** flexible
- **Logging comprehensivo** para observabilidad
- **Validación específica** por tipo de canal

### 🚀 **Valor Agregado**
- **Canal de comunicación gratuito y confiable**
- **Notificaciones instantáneas y ubicuas**
- **Configuración simplificada** para adopción rápida
- **Extensibilidad mantenida** para futuros canales

Esta implementación no solo añade funcionalidad valiosa al sistema, sino que lo hace de manera que **fortalece y ejemplifica la arquitectura existente**, sirviendo como **referencia para futuras extensiones** del sistema de notificaciones.

---

*📝 Documento generado para ThingsBoard 4.3.0-SNAPSHOT - Implementación de Notificaciones por Telegram*