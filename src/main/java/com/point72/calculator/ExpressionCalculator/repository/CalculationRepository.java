package com.point72.calculator.ExpressionCalculator.repository;

import com.point72.calculator.ExpressionCalculator.model.CalculationRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface CalculationRepository extends MongoRepository<CalculationRecord, String> {
    List<CalculationRecord> findByResult(double result);
    Optional<CalculationRecord> findByExpression(String expression);
}
