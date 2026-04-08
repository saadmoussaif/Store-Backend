package org.example.saadtechstore.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format(
                "Stock insuffisant pour '%s' : demandé %d, disponible %d",
                productName, requested, available));
    }
}