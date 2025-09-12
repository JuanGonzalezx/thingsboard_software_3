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