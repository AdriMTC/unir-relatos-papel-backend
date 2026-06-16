package com.unir.comms.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {

    public enum MessageType { MESSAGE, RESPONSE, ERROR }

    private MessageType type;
    private String sender;
    private String content;
}
