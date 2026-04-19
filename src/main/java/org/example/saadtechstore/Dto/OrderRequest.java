package org.example.saadtechstore.Dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {

    @NotEmpty(message = "La commande doit contenir au moins un article")
    private List<ItemDto> items;

    @NotBlank(message = "L'adresse est obligatoire")
    private String shippingAddress;

    @NotBlank(message = "La ville est obligatoire")
    private String city;
    private String paymentMethod;
    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^(\\+212|0)[5-7][0-9]{8}$",
            message = "Numéro de téléphone marocain invalide")
    private String phone;
    private String customerEmail;

    private String customerName;
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemDto {

        @NotBlank(message = "L'ID produit est obligatoire")
        private String productId;

        @NotNull
        @Min(value = 1, message = "La quantité doit être au moins 1")
        private Integer quantity;
    }
}
