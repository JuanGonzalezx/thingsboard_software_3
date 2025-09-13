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
package org.thingsboard.server.common.data.notification.template;

import java.util.List;

import org.thingsboard.server.common.data.notification.NotificationDeliveryMethod;
import org.thingsboard.server.common.data.validation.NoXss;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class TelegramDeliveryMethodNotificationTemplate extends DeliveryMethodNotificationTemplate {

    @NoXss(fieldName = "Telegram chat ID")
    @NotEmpty
    private String chatId;

    private final List<TemplatableValue> templatableValues = List.of(
            TemplatableValue.of(this::getBody, this::setBody),
            TemplatableValue.of(this::getChatId, this::setChatId)
    );

    public TelegramDeliveryMethodNotificationTemplate(DeliveryMethodNotificationTemplate other) {
        super(other);
        if (other instanceof TelegramDeliveryMethodNotificationTemplate telegramTemplate) {
            this.chatId = telegramTemplate.chatId;
        }
    }

    @NoXss(fieldName = "Telegram message")
    @Override
    public String getBody() {
        return super.getBody();
    }

    @Override
    public NotificationDeliveryMethod getMethod() {
        return NotificationDeliveryMethod.TELEGRAM;
    }

    @Override
    public TelegramDeliveryMethodNotificationTemplate copy() {
        return new TelegramDeliveryMethodNotificationTemplate(this);
    }

}
