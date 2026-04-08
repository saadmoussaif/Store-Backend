package org.example.saadtechstore.Dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.saadtechstore.Domain.Enum.OrderStatut;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private String id;
    private String customerEmail;
    private String customerName;
    private BigDecimal totalAmount;
    private OrderStatut status;
    private String shippingAddress;
    private String city;
    private String phone;
    private String cmiTransactionId;
    private List<ItemDto> items;
    private LocalDateTime createdAt;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {
        private String productName;
        private String productImageUrl;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal subtotal;
    }
}
