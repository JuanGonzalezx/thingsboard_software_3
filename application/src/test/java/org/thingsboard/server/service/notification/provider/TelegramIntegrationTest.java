/**
 * Copyright © 2016-2025 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.notification.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.web.client.RestTemplate;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.targets.telegram.TelegramChat;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PRUEBAS DE INTEGRACIÓN REAL - ESTAS SÍ ENVÍAN MENSAJES A TELEGRAM
 * 
 * Para ejecutar estas pruebas:
 * 1. Configura las variables de entorno:
 *    - TELEGRAM_BOT_TOKEN=tu_bot_token_real
 *    - TELEGRAM_CHAT_ID=tu_chat_id_real
 * 2. Ejecuta: mvn test -Dtest=TelegramIntegrationTest
 * 
 * NOTA: Estas pruebas envían mensajes REALES a Telegram
 */
@EnabledIfEnvironmentVariable(named = "TELEGRAM_BOT_TOKEN", matches = ".*")
class TelegramIntegrationTest {

    private DefaultTelegramService telegramService;
    private static final TenantId TENANT_ID = TenantId.fromUUID(java.util.UUID.randomUUID());
    
    // Estas se obtienen de variables de entorno para seguridad
    private String botToken;
    private String chatId;

    @BeforeEach
    void setUp() {
        // Configuración REAL sin mocks
        telegramService = new DefaultTelegramService();
        
        // Inyectar dependencias REALES
        setPrivateField(telegramService, "restTemplate", new RestTemplate());
        setPrivateField(telegramService, "objectMapper", new ObjectMapper());
        
        // Obtener configuración de variables de entorno
        botToken = System.getenv("TELEGRAM_BOT_TOKEN");
        chatId = System.getenv("TELEGRAM_CHAT_ID");
        
        System.out.println("\n🔥 === PRUEBAS DE INTEGRACIÓN REAL - TELEGRAM ===");
        System.out.println("⚠️  ESTAS PRUEBAS ENVÍAN MENSAJES REALES A TELEGRAM");
        System.out.println("Bot Token: " + (botToken != null ? botToken.substring(0, 10) + "..." : "NO CONFIGURADO"));
        System.out.println("Chat ID: " + (chatId != null ? chatId : "NO CONFIGURADO"));
        
        if (botToken == null || chatId == null) {
            System.out.println("❌ ERROR: Variables de entorno no configuradas");
            System.out.println("Configura:");
            System.out.println("  export TELEGRAM_BOT_TOKEN=tu_bot_token");
            System.out.println("  export TELEGRAM_CHAT_ID=tu_chat_id");
            fail("Variables de entorno no configuradas para pruebas de integración");
        }
        
        System.out.println("✅ Configuración lista para pruebas REALES");
        System.out.println("==============================================\n");
    }

    @Test
    void testSendRealMessage() {
        System.out.println("🚀 === ENVIANDO MENSAJE REAL A TELEGRAM ===");
        
        String testMessage = "🧪 Test desde ThingsBoard - " + System.currentTimeMillis();
        
        System.out.println("Enviando mensaje real:");
        System.out.println("  📝 Mensaje: " + testMessage);
        System.out.println("  🤖 Bot: " + botToken.substring(0, 10) + "...");
        System.out.println("  💬 Chat: " + chatId);
        System.out.println("  ⏰ Tiempo: " + new java.util.Date());
        
        try {
            // ENVÍO REAL - Sin mocks
            telegramService.sendMessage(TENANT_ID, botToken, chatId, testMessage);
            
            System.out.println("✅ ¡MENSAJE ENVIADO EXITOSAMENTE!");
            System.out.println("📱 Revisa tu chat de Telegram para ver el mensaje");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar mensaje:");
            System.out.println("  Tipo: " + e.getClass().getSimpleName());
            System.out.println("  Mensaje: " + e.getMessage());
            
            if (e.getMessage().contains("chat not found")) {
                System.out.println("💡 SOLUCIÓN: Verifica que el chat ID sea correcto");
            } else if (e.getMessage().contains("Unauthorized")) {
                System.out.println("💡 SOLUCIÓN: Verifica que el bot token sea válido");
            } else if (e.getMessage().contains("Forbidden")) {
                System.out.println("💡 SOLUCIÓN: Agrega el bot al grupo y dale permisos");
            }
            
            throw e; // Re-lanzar para que la prueba falle
        }
        
        System.out.println("========================================\n");
    }

    @Test
    void testSendMessageWithHtml() {
        System.out.println("🚀 === ENVIANDO MENSAJE CON HTML ===");
        
        String htmlMessage = "🧪 <b>Prueba con HTML</b>\n" +
                           "<i>Mensaje en cursiva</i>\n" +
                           "<code>Código: " + System.currentTimeMillis() + "</code>";
        
        System.out.println("Enviando mensaje con formato HTML:");
        System.out.println("  📝 Mensaje: " + htmlMessage);
        
        try {
            telegramService.sendMessage(TENANT_ID, botToken, chatId, htmlMessage, true);
            
            System.out.println("✅ ¡MENSAJE HTML ENVIADO EXITOSAMENTE!");
            System.out.println("📱 Revisa que el formato HTML se vea correctamente");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar mensaje HTML: " + e.getMessage());
            throw e;
        }
        
        System.out.println("=====================================\n");
    }

    @Test
    void testGetUpdates() {
        System.out.println("🚀 === OBTENIENDO ACTUALIZACIONES REALES ===");
        
        try {
            List<TelegramChat> chats = telegramService.getUpdates(TENANT_ID, botToken);
            
            System.out.println("✅ Actualizaciones obtenidas:");
            System.out.println("  📊 Chats encontrados: " + chats.size());
            
            for (int i = 0; i < chats.size() && i < 3; i++) { // Mostrar máximo 3
                TelegramChat chat = chats.get(i);
                System.out.println("  💬 Chat " + (i+1) + ":");
                System.out.println("    ID: " + chat.getChatId());
                System.out.println("    Tipo: " + chat.getType());
                System.out.println("    Nombre: " + chat.getName());
            }
            
            if (chats.size() > 3) {
                System.out.println("  ... y " + (chats.size() - 3) + " más");
            }
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al obtener actualizaciones: " + e.getMessage());
            throw e;
        }
        
        System.out.println("==========================================\n");
    }

    @Test
    void testDeviceAlarmNotification() {
        System.out.println("🚀 === ENVIANDO NOTIFICACIÓN DE ALARMA DE DISPOSITIVO ===");
        
        String deviceName = "Sensor Temperatura Oficina";
        String alarmType = "HIGH_TEMPERATURE";
        String currentValue = "78.5°C";
        String threshold = "75°C";
        long timestamp = System.currentTimeMillis();
        
        String alarmMessage = String.format(
            "🚨 <b>ALARMA ACTIVADA</b>\n\n" +
            "📱 <b>Dispositivo:</b> %s\n" +
            "⚠️ <b>Tipo de Alarma:</b> %s\n" +
            "🌡️ <b>Valor Actual:</b> %s\n" +
            "📊 <b>Umbral:</b> %s\n" +
            "🕒 <b>Hora:</b> %s\n\n" +
            "💡 <i>Revisa inmediatamente el dispositivo para evitar daños</i>",
            deviceName, alarmType, currentValue, threshold, 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date(timestamp))
        );
        
        System.out.println("Enviando notificación de alarma:");
        System.out.println("  🏷️ Dispositivo: " + deviceName);
        System.out.println("  ⚠️ Alarma: " + alarmType);
        System.out.println("  📈 Valor: " + currentValue + " > " + threshold);
        
        try {
            telegramService.sendMessage(TENANT_ID, botToken, chatId, alarmMessage, true);
            System.out.println("✅ ¡NOTIFICACIÓN DE ALARMA ENVIADA!");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar alarma: " + e.getMessage());
            throw e;
        }
        
        System.out.println("================================================\n");
    }

    @Test
    void testNewDeviceNotification() {
        System.out.println("🚀 === ENVIANDO NOTIFICACIÓN DE NUEVO DISPOSITIVO ===");
        
        String deviceName = "Smart Thermostat Living Room";
        String deviceType = "Thermostat";
        String customerName = "Empresa ABC Corp";
        String assignedUser = "Juan González";
        
        String newDeviceMessage = String.format(
            "📱 <b>NUEVO DISPOSITIVO REGISTRADO</b>\n\n" +
            "🏷️ <b>Nombre:</b> %s\n" +
            "🔧 <b>Tipo:</b> %s\n" +
            "🏢 <b>Cliente:</b> %s\n" +
            "👤 <b>Asignado a:</b> %s\n" +
            "🕒 <b>Fecha de registro:</b> %s\n\n" +
            "✨ <i>El dispositivo está listo para configuración</i>",
            deviceName, deviceType, customerName, assignedUser,
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
        
        System.out.println("Enviando notificación de nuevo dispositivo:");
        System.out.println("  📱 Dispositivo: " + deviceName);
        System.out.println("  🏢 Cliente: " + customerName);
        System.out.println("  👤 Usuario: " + assignedUser);
        
        try {
            telegramService.sendMessage(TENANT_ID, botToken, chatId, newDeviceMessage, true);
            System.out.println("✅ ¡NOTIFICACIÓN DE NUEVO DISPOSITIVO ENVIADA!");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar notificación: " + e.getMessage());
            throw e;
        }
        
        System.out.println("=====================================================\n");
    }

    @Test
    void testDeviceInactivityNotification() {
        System.out.println("🚀 === ENVIANDO NOTIFICACIÓN DE INACTIVIDAD ===");
        
        String deviceName = "Gateway Principal Planta 1";
        int hoursInactive = 4;
        String lastSeen = "2025-09-12 11:15:32";
        
        String inactivityMessage = String.format(
            "😴 <b>DISPOSITIVO INACTIVO</b>\n\n" +
            "📱 <b>Dispositivo:</b> %s\n" +
            "⏰ <b>Tiempo inactivo:</b> %d horas\n" +
            "👁️ <b>Última actividad:</b> %s\n" +
            "📍 <b>Estado:</b> Sin conexión\n\n" +
            "🔧 <i>Verifica la conectividad del dispositivo</i>",
            deviceName, hoursInactive, lastSeen
        );
        
        System.out.println("Enviando notificación de inactividad:");
        System.out.println("  📱 Dispositivo: " + deviceName);
        System.out.println("  ⏰ Inactivo por: " + hoursInactive + " horas");
        System.out.println("  👁️ Última vez visto: " + lastSeen);
        
        try {
            telegramService.sendMessage(TENANT_ID, botToken, chatId, inactivityMessage, true);
            System.out.println("✅ ¡NOTIFICACIÓN DE INACTIVIDAD ENVIADA!");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar notificación: " + e.getMessage());
            throw e;
        }
        
        System.out.println("===============================================\n");
    }

    @Test
    void testRuleChainErrorNotification() {
        System.out.println("🚀 === ENVIANDO NOTIFICACIÓN DE ERROR EN RULE CHAIN ===");
        
        String ruleChainName = "Procesamiento de Sensores IoT";
        String errorNode = "Transform Message";
        String errorMessage = "JSON parsing failed: Unexpected token at position 42";
        String deviceOrigin = "Sensor-001-Temperature";
        
        String ruleChainErrorMessage = String.format(
            "⚙️ <b>ERROR EN RULE CHAIN</b>\n\n" +
            "🔗 <b>Rule Chain:</b> %s\n" +
            "❌ <b>Nodo con error:</b> %s\n" +
            "📱 <b>Dispositivo origen:</b> %s\n" +
            "💬 <b>Error:</b> <code>%s</code>\n" +
            "🕒 <b>Hora:</b> %s\n\n" +
            "🛠️ <i>Revisa la configuración del nodo y los datos de entrada</i>",
            ruleChainName, errorNode, deviceOrigin, errorMessage,
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
        );
        
        System.out.println("Enviando notificación de error en Rule Chain:");
        System.out.println("  🔗 Rule Chain: " + ruleChainName);
        System.out.println("  ❌ Nodo: " + errorNode);
        System.out.println("  📱 Dispositivo: " + deviceOrigin);
        
        try {
            telegramService.sendMessage(TENANT_ID, botToken, chatId, ruleChainErrorMessage, true);
            System.out.println("✅ ¡NOTIFICACIÓN DE ERROR ENVIADA!");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar notificación: " + e.getMessage());
            throw e;
        }
        
        System.out.println("====================================================\n");
    }

    @Test
    void testSystemMaintenanceNotification() {
        System.out.println("🚀 === ENVIANDO NOTIFICACIÓN DE MANTENIMIENTO ===");
        
        String maintenanceType = "Actualización de Sistema";
        String startTime = "2025-09-12 23:00:00";
        String endTime = "2025-09-13 01:00:00";
        String affectedServices = "API REST, WebSocket, Dashboard";
        
        String maintenanceMessage = String.format(
            "🔧 <b>MANTENIMIENTO PROGRAMADO</b>\n\n" +
            "🛠️ <b>Tipo:</b> %s\n" +
            "🕐 <b>Inicio:</b> %s\n" +
            "🕑 <b>Fin estimado:</b> %s\n" +
            "⚡ <b>Servicios afectados:</b> %s\n\n" +
            "ℹ️ <i>Durante este periodo algunos servicios podrían no estar disponibles</i>\n" +
            "📧 <i>Te notificaremos cuando el mantenimiento haya terminado</i>",
            maintenanceType, startTime, endTime, affectedServices
        );
        
        System.out.println("Enviando notificación de mantenimiento:");
        System.out.println("  🛠️ Tipo: " + maintenanceType);
        System.out.println("  🕐 Ventana: " + startTime + " - " + endTime);
        System.out.println("  ⚡ Servicios: " + affectedServices);
        
        try {
            telegramService.sendMessage(TENANT_ID, botToken, chatId, maintenanceMessage, true);
            System.out.println("✅ ¡NOTIFICACIÓN DE MANTENIMIENTO ENVIADA!");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar notificación: " + e.getMessage());
            throw e;
        }
        
        System.out.println("================================================\n");
    }

    @Test
    void testResourceUsageAlertNotification() {
        System.out.println("🚀 === ENVIANDO ALERTA DE USO DE RECURSOS ===");
        
        String resourceType = "API Calls";
        int currentUsage = 8750;
        int limit = 10000;
        double percentage = (currentUsage * 100.0) / limit;
        String timeFrame = "último mes";
        
        String resourceAlertMessage = String.format(
            "📊 <b>ALERTA DE USO DE RECURSOS</b>\n\n" +
            "📈 <b>Recurso:</b> %s\n" +
            "🔢 <b>Uso actual:</b> %,d de %,d\n" +
            "📊 <b>Porcentaje:</b> %.1f%%\n" +
            "📅 <b>Período:</b> %s\n\n" +
            "%s <i>%s</i>",
            resourceType, currentUsage, limit, percentage, timeFrame,
            percentage > 90 ? "🚨" : percentage > 75 ? "⚠️" : "ℹ️",
            percentage > 90 ? "Límite casi alcanzado - considera actualizar tu plan" :
            percentage > 75 ? "Uso elevado - monitorea tu consumo" :
            "Uso normal del recurso"
        );
        
        System.out.println("Enviando alerta de recursos:");
        System.out.println("  📈 Recurso: " + resourceType);
        System.out.println("  📊 Uso: " + String.format("%.1f%% (%d/%d)", percentage, currentUsage, limit));
        System.out.println("  📅 Período: " + timeFrame);
        
        try {
            telegramService.sendMessage(TENANT_ID, botToken, chatId, resourceAlertMessage, true);
            System.out.println("✅ ¡ALERTA DE RECURSOS ENVIADA!");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar alerta: " + e.getMessage());
            throw e;
        }
        
        System.out.println("============================================\n");
    }

    @Test
    void testAutomaticNotificationFlow() throws InterruptedException {
        System.out.println("🚀 === SIMULANDO FLUJO AUTOMÁTICO DE NOTIFICACIONES ===");
        
        System.out.println("📋 Simulando diferentes eventos del sistema...\n");
        
        // Simular secuencia de eventos reales
        String[] events = {
            "🔐 Usuario admin inició sesión desde 192.168.1.100",
            "📱 Dispositivo 'Sensor Humedad-001' conectado exitosamente",
            "📊 Dashboard 'Control Industrial' fue modificado por admin",
            "⚠️ Alarma 'Temperatura Alta' se activó en Sensor-TH-05",
            "🔄 Rule Chain 'Procesamiento MQTT' procesó 1,247 mensajes",
            "🌐 API alcanzó 5,000 requests en la última hora"
        };
        
        try {
            for (int i = 0; i < events.length; i++) {
                String event = events[i];
                
                // Mensaje de notificación estilizado
                String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
                String notificationMessage = String.format(
                    "📢 <b>EVENTO DEL SISTEMA</b> <code>#%03d</code>\n\n" +
                    "%s\n\n" +
                    "🕒 <i>%s</i> | 🏢 <i>ThingsBoard IoT Platform</i>",
                    (i + 1), event, timestamp
                );
                
                System.out.println("📤 Enviando evento " + (i + 1) + "/" + events.length + ":");
                System.out.println("   " + event);
                
                // Enviar notificación
                telegramService.sendMessage(TENANT_ID, botToken, chatId, notificationMessage, true);
                
                System.out.println("   ✅ Enviado a las " + timestamp);
                
                // Esperar un poco entre mensajes para simular eventos reales
                Thread.sleep(2000);
            }
            
            // Mensaje de resumen
            String summaryMessage = String.format(
                "📈 <b>RESUMEN DE ACTIVIDAD</b>\n\n" +
                "✅ <b>Eventos procesados:</b> %d\n" +
                "🕒 <b>Período:</b> Últimos %d minutos\n" +
                "🎯 <b>Estado del sistema:</b> Operativo\n\n" +
                "🔔 <i>Todas las notificaciones han sido enviadas correctamente</i>",
                events.length, (events.length * 2) / 60 + 1
            );
            
            Thread.sleep(1000);
            telegramService.sendMessage(TENANT_ID, botToken, chatId, summaryMessage, true);
            
            System.out.println("\n🎯 ¡FLUJO AUTOMÁTICO COMPLETADO!");
            System.out.println("📊 Total de notificaciones enviadas: " + (events.length + 1));
            
        } catch (Exception e) {
            System.out.println("❌ ERROR en el flujo automático: " + e.getMessage());
            throw e;
        }
        
        System.out.println("=====================================================\n");
    }

    @Test
    void testDashboardAndDataVisualizationNotifications() throws InterruptedException {
        System.out.println("🚀 === ENVIANDO NOTIFICACIONES DE DASHBOARD Y DATOS ===");
        
        try {
            // Notificación de dashboard actualizado
            String dashboardUpdateMessage = 
                "📊 <b>DASHBOARD ACTUALIZADO</b>\n\n" +
                "🎨 <b>Dashboard:</b> Control de Producción\n" +
                "👤 <b>Modificado por:</b> admin@thingsboard.com\n" +
                "📝 <b>Cambios:</b> Añadido widget de eficiencia energética\n" +
                "🕒 <b>Hora:</b> " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n\n" +
                "👁️ <i>Los usuarios verán los cambios en su próximo acceso</i>";
            
            telegramService.sendMessage(TENANT_ID, botToken, chatId, dashboardUpdateMessage, true);
            System.out.println("✅ Notificación de dashboard actualizado enviada");
            
            Thread.sleep(2000);
            
            // Notificación de reporte de datos
            String dataReportMessage = 
                "📈 <b>REPORTE DIARIO DE DATOS</b>\n\n" +
                "📅 <b>Fecha:</b> " + new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()) + "\n" +
                "📡 <b>Dispositivos activos:</b> 247 de 250\n" +
                "📊 <b>Mensajes procesados:</b> 1,247,893\n" +
                "⚡ <b>Alarmas generadas:</b> 12\n" +
                "🔧 <b>Eventos de Rule Engine:</b> 89,234\n" +
                "💾 <b>Datos almacenados:</b> 145.7 MB\n\n" +
                "📋 <i>Reporte completo disponible en el dashboard principal</i>";
            
            telegramService.sendMessage(TENANT_ID, botToken, chatId, dataReportMessage, true);
            System.out.println("✅ Reporte diario de datos enviado");
            
            Thread.sleep(2000);
            
            // Notificación de backup completado
            String backupMessage = 
                "💾 <b>BACKUP COMPLETADO</b>\n\n" +
                "🗄️ <b>Tipo:</b> Backup automático diario\n" +
                "📦 <b>Tamaño:</b> 2.3 GB\n" +
                "⏱️ <b>Duración:</b> 47 minutos\n" +
                "✅ <b>Estado:</b> Exitoso\n" +
                "📍 <b>Ubicación:</b> AWS S3 Bucket\n" +
                "🕒 <b>Completado:</b> " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) + "\n\n" +
                "🔒 <i>Todos los datos están seguros y respaldados</i>";
            
            telegramService.sendMessage(TENANT_ID, botToken, chatId, backupMessage, true);
            System.out.println("✅ Notificación de backup completado enviada");
            
        } catch (Exception e) {
            System.out.println("❌ ERROR al enviar notificaciones de datos: " + e.getMessage());
            throw e;
        }
        
        System.out.println("=======================================================\n");
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set private field: " + fieldName, e);
        }
    }
}