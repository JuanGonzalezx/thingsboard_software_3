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
package org.thingsboard.server.common.data.notification.targets.telegram;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.thingsboard.server.common.data.notification.targets.NotificationRecipient;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TelegramChat implements NotificationRecipient {

    @NotNull
    private TelegramChatType type;
    @NotEmpty
    private String chatId;
    @NotEmpty
    private String name;

    private String firstName;
    private String lastName;
    private String username;

    @Override
    public Object getId() {
        return chatId;
    }

    @Override
    public String getTitle() {
        if (type == TelegramChatType.PRIVATE) {
            return StringUtils.defaultIfEmpty(getFullName(), name);
        } else {
            return name;
        }
    }

    @JsonIgnore
    @Override
    public String getFirstName() {
        return StringUtils.defaultIfEmpty(firstName, name);
    }

    @JsonIgnore
    @Override
    public String getLastName() {
        return lastName;
    }

    @JsonIgnore
    public String getFullName() {
        if (!isEmpty(firstName) && !isEmpty(lastName)) {
            return firstName + " " + lastName;
        } else if (!isEmpty(firstName)) {
            return firstName;
        } else if (!isEmpty(lastName)) {
            return lastName;
        }
        return name;
    }

    @JsonIgnore
    public String getDisplayName() {
        if (!isEmpty(username)) {
            return "@" + username;
        }
        return getFullName();
    }

}
