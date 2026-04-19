package org.example.saadtechstore.Service;


import org.example.saadtechstore.Domain.Order;

import org.springframework.stereotype.Service;



@Service

    public interface EmailService {
        void sendOrderConfirmation(Order order);
        void sendOrderStatusUpdate(Order order);
    }

