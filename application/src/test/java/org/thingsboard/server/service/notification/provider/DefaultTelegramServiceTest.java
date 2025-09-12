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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.targets.telegram.TelegramChat;
import org.thingsboard.server.common.data.notification.targets.telegram.TelegramChatType;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DefaultTelegramServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private DefaultTelegramService telegramService;

    private static final TenantId TENANT_ID = TenantId.fromUUID(java.util.UUID.randomUUID());
    private static final String BOT_TOKEN = "8216106650:AAGnWPkSVkRjh0x9wpPAGzko8R8hYQOVYEg";
    private static final String CHAT_ID = "-1002919021008";
    private static final String MESSAGE_TEXT = "Test notification message";
    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    @BeforeEach
    void setUp() {
        telegramService = new DefaultTelegramService();
        // Usar reflexión para inyectar los mocks
        setPrivateField(telegramService, "restTemplate", restTemplate);
        setPrivateField(telegramService, "objectMapper", objectMapper);
        
        System.out.println("\n🚀 === CONFIGURACIÓN DE PRUEBAS DE TELEGRAM ===");
        System.out.println("Bot Token configurado: " + BOT_TOKEN);
        System.out.println("Chat ID de prueba: " + CHAT_ID);
        System.out.println("Mensaje de prueba: " + MESSAGE_TEXT);
        System.out.println("URL base de API: " + TELEGRAM_API_URL);
        System.out.println("Tenant ID: " + TENANT_ID);
        System.out.println("===============================================");
        System.out.println("💡 PARA DEPURAR TU APLICACIÓN:");
        System.out.println("1. Verifica que tu bot token sea válido");
        System.out.println("2. Confirma que el chat ID sea correcto");
        System.out.println("3. Asegúrate de que el bot tenga permisos en el chat");
        System.out.println("4. Revisa que la URL de API sea accesible");
        System.out.println("===============================================\n");
    }

    @Test
    void testSendMessage_Success() {
        // Given
        ResponseEntity<String> successResponse = new ResponseEntity<>(
            "{\"ok\":true,\"result\":{\"message_id\":123}}", 
            HttpStatus.OK
        );
        
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(successResponse);

        System.out.println("=== TEST: testSendMessage_Success ===");
        System.out.println("Enviando mensaje a Telegram:");
        System.out.println("  - Bot Token: " + BOT_TOKEN);
        System.out.println("  - Chat ID: " + CHAT_ID);
        System.out.println("  - Mensaje: " + MESSAGE_TEXT);
        System.out.println("  - Tenant ID: " + TENANT_ID);

        // When
        assertDoesNotThrow(() -> {
            telegramService.sendMessage(TENANT_ID, BOT_TOKEN, CHAT_ID, MESSAGE_TEXT);
        });

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        
        verify(restTemplate).postForEntity(urlCaptor.capture(), entityCaptor.capture(), eq(String.class));
        
        String capturedUrl = urlCaptor.getValue();
        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        
        System.out.println("Verificando llamada a API:");
        System.out.println("  - URL llamada: " + capturedUrl);
        System.out.println("  - Cuerpo de la petición: " + capturedEntity.getBody());
        System.out.println("  - Headers: " + capturedEntity.getHeaders());
        
        assertEquals(TELEGRAM_API_URL + BOT_TOKEN + "/sendMessage", capturedUrl);
        assertNotNull(capturedEntity.getBody());
        
        System.out.println("✅ Prueba exitosa - Mensaje enviado correctamente");
        System.out.println("==========================================\n");
    }

    @Test
    void testSendMessage_WithParseMode() {
        // Given
        ResponseEntity<String> successResponse = new ResponseEntity<>(
            "{\"ok\":true,\"result\":{\"message_id\":123}}", 
            HttpStatus.OK
        );
        
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(successResponse);

        System.out.println("=== TEST: testSendMessage_WithParseMode ===");
        System.out.println("Enviando mensaje con parse mode HTML:");
        System.out.println("  - Bot Token: " + BOT_TOKEN);
        System.out.println("  - Chat ID: " + CHAT_ID);
        System.out.println("  - Mensaje: " + MESSAGE_TEXT);
        System.out.println("  - Parse Mode: HTML (habilitado)");

        // When
        assertDoesNotThrow(() -> {
            telegramService.sendMessage(TENANT_ID, BOT_TOKEN, CHAT_ID, MESSAGE_TEXT, true);
        });

        // Then
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        
        verify(restTemplate).postForEntity(urlCaptor.capture(), entityCaptor.capture(), eq(String.class));
        
        String capturedUrl = urlCaptor.getValue();
        HttpEntity<?> capturedEntity = entityCaptor.getValue();
        
        System.out.println("Verificando llamada a API con parse mode:");
        System.out.println("  - URL llamada: " + capturedUrl);
        System.out.println("  - Cuerpo de la petición: " + capturedEntity.getBody());
        System.out.println("  - Headers: " + capturedEntity.getHeaders());
        
        System.out.println("✅ Prueba exitosa - Mensaje con parse mode enviado correctamente");
        System.out.println("================================================\n");
    }

    @Test
    void testSendMessage_ApiError() {
        // Given
        ResponseEntity<String> errorResponse = new ResponseEntity<>(
            "{\"ok\":false,\"error_code\":400,\"description\":\"Bad Request: chat not found\"}", 
            HttpStatus.BAD_REQUEST
        );
        
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenReturn(errorResponse);

        System.out.println("=== TEST: testSendMessage_ApiError ===");
        System.out.println("Simulando error de API de Telegram:");
        System.out.println("  - Bot Token: " + BOT_TOKEN);
        System.out.println("  - Chat ID: " + CHAT_ID);
        System.out.println("  - Error simulado: Bad Request: chat not found");
        System.out.println("  - Código de error: 400");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            telegramService.sendMessage(TENANT_ID, BOT_TOKEN, CHAT_ID, MESSAGE_TEXT);
        });
        
        System.out.println("Error capturado correctamente:");
        System.out.println("  - Tipo: " + exception.getClass().getSimpleName());
        System.out.println("  - Mensaje: " + exception.getMessage());
        
        assertTrue(exception.getMessage().contains("Failed to send Telegram message"));
        
        System.out.println("✅ Prueba exitosa - Error de API manejado correctamente");
        System.out.println("NOTA: Si ves este error en tu app, verifica que:");
        System.out.println("  1. El bot token sea válido");
        System.out.println("  2. El chat ID sea correcto");
        System.out.println("  3. El bot tenga permisos en el chat");
        System.out.println("===========================================\n");
    }

    @Test
    void testSendMessage_NetworkException() {
        // Given
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
            .thenThrow(new RestClientException("Network error"));

        System.out.println("=== TEST: testSendMessage_NetworkException ===");
        System.out.println("Simulando error de red:");
        System.out.println("  - Bot Token: " + BOT_TOKEN);
        System.out.println("  - Chat ID: " + CHAT_ID);
        System.out.println("  - Error simulado: Network error");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            telegramService.sendMessage(TENANT_ID, BOT_TOKEN, CHAT_ID, MESSAGE_TEXT);
        });
        
        System.out.println("Error de red capturado correctamente:");
        System.out.println("  - Tipo: " + exception.getClass().getSimpleName());
        System.out.println("  - Mensaje: " + exception.getMessage());
        System.out.println("  - Causa raíz: " + exception.getCause().getClass().getSimpleName());
        System.out.println("  - Mensaje de causa: " + exception.getCause().getMessage());
        
        assertTrue(exception.getMessage().contains("Failed to send Telegram message"));
        assertTrue(exception.getCause() instanceof RestClientException);
        
        System.out.println("✅ Prueba exitosa - Error de red manejado correctamente");
        System.out.println("NOTA: Si ves este error en tu app, verifica la conectividad a internet");
        System.out.println("===============================================\n");
    }

    @Test
    void testGetUpdates_Success() throws Exception {
        // Given
        String updatesResponse = """
            {
                "ok": true,
                "result": [
                    {
                        "update_id": 1,
                        "message": {
                            "message_id": 1,
                            "chat": {
                                "id": 123456789,
                                "type": "private",
                                "first_name": "Test",
                                "last_name": "User"
                            },
                            "text": "/start"
                        }
                    }
                ]
            }
            """;
        
        ResponseEntity<String> successResponse = new ResponseEntity<>(updatesResponse, HttpStatus.OK);
        
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
            .thenReturn(successResponse);
        
        // Mock ObjectMapper response
        Map<String, Object> parsedResponse = Map.of(
            "ok", true,
            "result", List.of(
                Map.of(
                    "update_id", 1,
                    "message", Map.of(
                        "message_id", 1,
                        "chat", Map.of(
                            "id", 123456789L,
                            "type", "private",
                            "first_name", "Test",
                            "last_name", "User"
                        ),
                        "text", "/start"
                    )
                )
            )
        );
        
        when(objectMapper.readValue(eq(updatesResponse), eq(Map.class)))
            .thenReturn(parsedResponse);

        System.out.println("=== TEST: testGetUpdates_Success ===");
        System.out.println("Obteniendo actualizaciones de Telegram:");
        System.out.println("  - Bot Token: " + BOT_TOKEN);
        System.out.println("  - URL esperada: " + TELEGRAM_API_URL + BOT_TOKEN + "/getUpdates");

        // When
        List<TelegramChat> chats = telegramService.getUpdates(TENANT_ID, BOT_TOKEN);

        // Then
        System.out.println("Respuesta de API procesada:");
        System.out.println("  - Respuesta raw: " + updatesResponse.replaceAll("\\s+", " "));
        System.out.println("  - Chats encontrados: " + chats.size());
        
        assertNotNull(chats);
        assertEquals(1, chats.size());
        
        TelegramChat chat = chats.get(0);
        System.out.println("Chat procesado:");
        System.out.println("  - Chat ID: " + chat.getChatId());
        System.out.println("  - Tipo: " + chat.getType());
        System.out.println("  - Nombre: " + chat.getName());
        
        assertEquals("123456789", chat.getChatId());
        assertEquals(TelegramChatType.PRIVATE, chat.getType());
        assertEquals("Test User", chat.getName());
        
        verify(restTemplate).getForEntity(
            eq(TELEGRAM_API_URL + BOT_TOKEN + "/getUpdates"), 
            eq(String.class)
        );
        
        System.out.println("✅ Prueba exitosa - Actualizaciones obtenidas correctamente");
        System.out.println("==========================================\n");
    }

    @Test
    void testGetToken() {
        System.out.println("=== TEST: testGetToken ===");
        System.out.println("Verificando método getToken:");
        System.out.println("  - Tenant ID: " + TENANT_ID);
        
        // When
        String token = telegramService.getToken(TENANT_ID);

        System.out.println("Resultado:");
        System.out.println("  - Token obtenido: " + (token != null ? token : "null (no implementado)"));
        
        // Then
        assertNull(token); // Por ahora retorna null ya que no está implementado
        
        System.out.println("✅ Prueba exitosa - getToken funciona como esperado (no implementado)");
        System.out.println("NOTA: Este método debería implementarse para obtener tokens de configuración");
        System.out.println("===============================\n");
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