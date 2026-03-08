package com.escrow.escrowbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.escrow.escrowbackend.entity.Escrow;

@Service
@RequiredArgsConstructor
public class AdminLiveService {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendEscrowUpdate(Escrow escrow) {

        messagingTemplate.convertAndSend(
                "/topic/admin/escrows",
                escrow
        );
    }
}