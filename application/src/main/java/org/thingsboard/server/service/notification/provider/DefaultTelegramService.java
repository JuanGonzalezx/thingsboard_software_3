package org.thingsboard.server.service.notification.provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thingsboard.rule.engine.api.notification.TelegramService;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.targets.telegram.TelegramChat;
import org.thingsboard.server.common.data.notification.targets.telegram.TelegramChatType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DefaultTelegramService implements TelegramService {

    private static final String TELEGRAM_API_URL = "https://api.telegram.org/bot";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void sendMessage(TenantId tenantId, String token, String chatId, String message) {
        sendMessage(tenantId, token, chatId, message, false);
    }

    @Override
    public void sendMessage(TenantId tenantId, String token, String chatId, String message, boolean parseMode) {
        try {
            String url = TELEGRAM_API_URL + token + "/sendMessage";
            
            Map<String, Object> payload = new HashMap<>();
            payload.put("chat_id", chatId);
            payload.put("text", message);
            if (parseMode) {
                payload.put("parse_mode", "HTML");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to send Telegram message: " + response.getBody());
            }

            log.debug("Telegram message sent successfully to chat: {}", chatId);
        } catch (Exception e) {
            log.error("Failed to send Telegram message", e);
            throw new RuntimeException("Failed to send Telegram message", e);
        }
    }

    @Override
    public List<TelegramChat> getUpdates(TenantId tenantId, String token) {
        try {
            String url = TELEGRAM_API_URL + token + "/getUpdates";
            
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get Telegram updates: " + response.getBody());
            }

            Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
            Boolean ok = (Boolean) responseBody.get("ok");
            
            if (!ok) {
                throw new RuntimeException("Telegram API returned error: " + responseBody.get("description"));
            }

            List<Map<String, Object>> updates = (List<Map<String, Object>>) responseBody.get("result");
            List<TelegramChat> chats = new ArrayList<>();

            for (Map<String, Object> update : updates) {
                Map<String, Object> message = (Map<String, Object>) update.get("message");
                if (message != null) {
                    Map<String, Object> chat = (Map<String, Object>) message.get("chat");
                    if (chat != null) {
                        TelegramChat telegramChat = parseTelegramChat(chat);
                        if (telegramChat != null && !chats.contains(telegramChat)) {
                            chats.add(telegramChat);
                        }
                    }
                }
            }

            return chats;
        } catch (Exception e) {
            log.error("Error getting Telegram updates", e);
            throw new RuntimeException("Error getting Telegram updates", e);
        }
    }

    @Override
    public String getToken(TenantId tenantId) {
        // Implementar lógica para obtener el token del tenant
        // Esto dependerá de cómo esté configurado en tu sistema
        // Por ahora retornamos null y se debe implementar según la lógica de negocio
        log.warn("getToken method not implemented for tenant: {}", tenantId);
        return null;
    }

    private TelegramChat parseTelegramChat(Map<String, Object> chatData) {
        try {
            Object idObj = chatData.get("id");
            String chatId = null;
            if (idObj instanceof Number) {
                chatId = String.valueOf(((Number) idObj).longValue());
            } else if (idObj instanceof String) {
                chatId = (String) idObj;
            }

            String type = (String) chatData.get("type");
            TelegramChatType chatType = TelegramChatType.PRIVATE; // Default
            if (type != null) {
                try {
                    chatType = TelegramChatType.valueOf(type.toUpperCase());
                } catch (IllegalArgumentException e) {
                    log.warn("Unknown chat type: {}", type);
                }
            }

            String title = (String) chatData.get("title");
            String firstName = (String) chatData.get("first_name");
            String lastName = (String) chatData.get("last_name");
            String username = (String) chatData.get("username");
            
            String name = title;
            if (name == null) {
                if (firstName != null && lastName != null) {
                    name = firstName + " " + lastName;
                } else if (firstName != null) {
                    name = firstName;
                } else if (lastName != null) {
                    name = lastName;
                } else if (username != null) {
                    name = "@" + username;
                } else {
                    name = "Unknown";
                }
            }

            return TelegramChat.builder()
                    .type(chatType)
                    .chatId(chatId)
                    .name(name)
                    .firstName(firstName)
                    .lastName(lastName)
                    .username(username)
                    .build();

        } catch (Exception e) {
            log.warn("Failed to parse Telegram chat data", e);
            return null;
        }
    }
}