package com.MarketPlace.DTOs;

import com.MarketPlace.Model.PaymentMethod;
import lombok.Data;

@Data
public class PaymentDto {

    private PaymentMethod method;
    private Double amount; // optional - will be validated against computed total
    // for simulated online payments you can optionally include card details etc (ignored)
}
