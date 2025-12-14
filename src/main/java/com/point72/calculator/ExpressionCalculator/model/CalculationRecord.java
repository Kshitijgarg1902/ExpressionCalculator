package com.point72.calculator.ExpressionCalculator.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "calculations")
public class CalculationRecord {
    @Id
    private String id;
    private String expression;
    @Indexed
    private double result;
    private LocalDateTime createdAt;

    public CalculationRecord(String expression, double result) {
        this.expression = expression;
        this.result = result;
        this.createdAt = LocalDateTime.now();
    }

}

