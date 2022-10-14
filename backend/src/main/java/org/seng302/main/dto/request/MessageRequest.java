package org.seng302.main.dto.request;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class MessageRequest {
    private long recipientId;
    private long cardId;
    private String message;
}
