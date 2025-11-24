package com.MarketPlace.Model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Method method;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String transactionId; // in simulated online, fill a random tx id

    private Double amount;

    private Instant paidAt;


    public enum Method { COD, UPI, CARD, NET_BANKING }
    public enum Status { PENDING, SUCCESS, FAILED }
}
