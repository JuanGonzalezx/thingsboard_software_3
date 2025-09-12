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
package org.thingsboard.server.service.notification.channels;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.thingsboard.rule.engine.api.notification.TelegramService;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.notification.targets.telegram.TelegramChat;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationChannel {

    private final TelegramService telegramService;

    public void sendNotification(TenantId tenantId, TelegramChat chat, String message, String botToken) {
        try {
            telegramService.sendMessage(tenantId, botToken, chat.getChatId(), message);
            log.debug("Telegram notification sent successfully to chat: {}", chat.getChatId());
        } catch (Exception e) {
            log.error("Failed to send Telegram notification to tenant: {}, chat: {}", tenantId, chat.getChatId(), e);
            throw new RuntimeException("Failed to send Telegram notification", e);
        }
    }

    public void sendNotificationWithParseMode(TenantId tenantId, TelegramChat chat, String message, String botToken, boolean parseMode) {
        try {
            telegramService.sendMessage(tenantId, botToken, chat.getChatId(), message, parseMode);
            log.debug("Telegram notification with parse mode sent successfully to chat: {}", chat.getChatId());
        } catch (Exception e) {
            log.error("Failed to send Telegram notification with parse mode to tenant: {}, chat: {}", tenantId, chat.getChatId(), e);
            throw new RuntimeException("Failed to send Telegram notification", e);
        }
    }
}
