package com.ddang.global.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class WebSocketMessageEvent extends ApplicationEvent {
    private final String destination;
    private final Object payload;

    public WebSocketMessageEvent(Object source, String destination, Object payload) {
        super(source);
        this.destination = destination;
        this.payload = payload;
    }
}
