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
import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.notification.settings.NotificationSettings;
import org.thingsboard.server.common.data.notification.settings.TelegramNotificationDeliveryMethodConfig;
import org.thingsboard.server.common.data.notification.targets.NotificationRecipient;
import org.thingsboard.server.common.data.notification.targets.telegram.TelegramChat;
import org.thingsboard.server.common.data.notification.template.TelegramDeliveryMethodNotificationTemplate;
import org.thingsboard.server.dao.notification.NotificationSettingsService;
import org.thingsboard.server.service.notification.NotificationProcessingContext;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramNotificationChannel implements NotificationChannel<NotificationRecipient, TelegramDeliveryMethodNotificationTemplate> {

    private final TelegramService telegramService;
    private final NotificationSettingsService notificationSettingsService;

    @Override
    public void sendNotification(NotificationRecipient recipient, TelegramDeliveryMethodNotificationTemplate processedTemplate, NotificationProcessingContext ctx) throws Exception {
        TelegramNotificationDeliveryMethodConfig config = ctx.getDeliveryMethodConfig(NotificationDeliveryMethod.TELEGRAM);
        
        // Para Telegram, el CHAT_ID viene del template, no del recipient
        String chatId;
        if (recipient instanceof TelegramChat) {
            chatId = ((TelegramChat) recipient).getChatId();
        } else {
            // Usar el CHAT_ID del template (configurado por defecto)
            chatId = processedTemplate.getChatId();
        }
        
        if (chatId == null || chatId.isEmpty()) {
            throw new RuntimeException("Telegram CHAT_ID not configured");
        }
        
        telegramService.sendMessage(ctx.getTenantId(), config.getBotToken(), chatId, processedTemplate.getBody());
        log.debug("Telegram notification sent successfully to chat: {}", chatId);
    }

    @Override
    public void check(TenantId tenantId) throws Exception {
        NotificationSettings notificationSettings = notificationSettingsService.findNotificationSettings(tenantId);
        if (!notificationSettings.getDeliveryMethodsConfigs().containsKey(NotificationDeliveryMethod.TELEGRAM)) {
            throw new RuntimeException("Telegram bot token is not configured");
        }
    }

    @Override
    public NotificationDeliveryMethod getDeliveryMethod() {
        return NotificationDeliveryMethod.TELEGRAM;
    }
}
