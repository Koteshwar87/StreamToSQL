package org.streamtosql.consumer.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "order_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4476528771660529763L;

    @EmbeddedId
    private OrderItemId id; // composite key: correlationId + orderId

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}