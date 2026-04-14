package org.example.saadtechstore.Dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String cmiPaymentUrl;
    private String status;
    private String sessionId;
}