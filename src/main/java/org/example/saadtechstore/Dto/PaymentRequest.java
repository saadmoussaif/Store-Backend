package org.example.saadtechstore.Dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PaymentRequest {
    private BigDecimal amount;
    private String currency;
    private String description;
    private List<ItemDto> items;

    @Data
    public static class ItemDto {
        private String productId;
        private Integer quantity;
    }
}