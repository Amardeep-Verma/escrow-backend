package com.escrow.escrowbackend.service;

import com.escrow.escrowbackend.entity.Escrow;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 🔥 Send update only to specific user
    public void sendEscrowUpdate(Escrow escrow) {

        String buyer = escrow.getBuyerEmail();
        String seller = escrow.getSellerEmail();

        // Send to buyer
        messagingTemplate.convertAndSend(
                "/topic/user/" + buyer,
                escrow
        );

        // Send to seller
        messagingTemplate.convertAndSend(
                "/topic/user/" + seller,
                escrow
        );
    }
}