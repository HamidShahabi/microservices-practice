package org.example.notification.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = "order-topic", groupId = "notification-group")
    public void consume(String message) {
      log.info("Notification Service received: " + message);
        // Simulate sending a notification
        sendNotification(message);
    }

    private void sendNotification(String message) {
        log.info("Sending notification: " + message);
    }
}

