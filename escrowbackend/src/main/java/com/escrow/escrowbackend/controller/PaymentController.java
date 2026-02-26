package com.escrow.escrowbackend.controller;
import com.escrow.escrowbackend.entity.ShipmentStatus;
import com.escrow.escrowbackend.entity.Escrow;
import com.escrow.escrowbackend.entity.EscrowStatus;
import com.escrow.escrowbackend.repository.EscrowRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private EscrowRepository escrowRepository;

    @Value("${razorpay.key.secret}")
    private String razorpayKeySecret;

    // ===============================
    // CREATE ORDER
    // ===============================
    @PostMapping("/create-order")
    public String createOrder(@RequestParam int amount) throws Exception {

        JSONObject options = new JSONObject();
        options.put("amount", amount * 100); // paise
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        Order order = razorpayClient.orders.create(options);

        return order.toString();
    }

    // ===============================
    // VERIFY PAYMENT + CREATE ESCROW
    // ===============================
    @PostMapping("/verify-payment")
    public String verifyPayment(@RequestBody Map<String, String> data) throws Exception {

        String orderId = data.get("razorpay_order_id");
        String paymentId = data.get("razorpay_payment_id");
        String signature = data.get("razorpay_signature");

        // Generate signature (HEX)
        String generatedSignature = hmacSHA256(
                orderId + "|" + paymentId,
                razorpayKeySecret
        );

        // Debug (optional)
        System.out.println("Generated Signature: " + generatedSignature);
        System.out.println("Razorpay Signature: " + signature);

        if (!generatedSignature.equals(signature)) {
            throw new RuntimeException("Invalid Payment Signature ❌");
        }

        // ===============================
        // PAYMENT VERIFIED → CREATE ESCROW
        // ===============================

        Escrow escrow = Escrow.builder()
                .productName(data.get("productName"))
                .sellerEmail(data.get("sellerEmail"))
                .buyerEmail(data.get("buyerEmail")) // ✅ ADD THIS
                .amount(Double.parseDouble(data.get("amount")))
                .escrowStatus(EscrowStatus.FUNDED)
                .shipmentStatus(ShipmentStatus.PENDING) // ✅ VERY IMPORTANT
                .createdAt(LocalDateTime.now())
                .build();

        escrowRepository.save(escrow);

        return "PAYMENT VERIFIED & ESCROW CREATED ✅";
    }

    // ===============================
    // HMAC SHA256 → HEX FORMAT
    // ===============================
    private String hmacSHA256(String data, String secret) throws Exception {

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey =
                new SecretKeySpec(secret.getBytes(), "HmacSHA256");

        mac.init(secretKey);

        byte[] hash = mac.doFinal(data.getBytes());

        // Convert to HEX (IMPORTANT)
        StringBuilder hex = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) hex.append('0');
            hex.append(s);
        }

        return hex.toString();
    }
}