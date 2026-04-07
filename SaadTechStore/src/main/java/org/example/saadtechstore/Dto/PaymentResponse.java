package org.example.saadtechstore.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String orderId;
    private String cmiPaymentUrl;   // URL redirect vers page CMI
    private String cmiOrderId;
    private BigDecimal amount;
    private String status;
}
