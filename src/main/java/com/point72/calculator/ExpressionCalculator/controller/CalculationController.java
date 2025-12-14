package com.point72.calculator.ExpressionCalculator.controller;

import com.point72.calculator.ExpressionCalculator.service.CalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/calculator")
@RequiredArgsConstructor
public class CalculationController {

    private final CalculationService service;

    private static final Pattern VALID_EXPRESSION_PATTERN = Pattern.compile("^[0-9().+\\-*/\\s]+$");

    @GetMapping("/calculate")
    public ResponseEntity<?> calculate(@RequestBody Map<String, String> body) {
        String expression = body.get("expression");
        ResponseEntity<?> validationResponse = validateExpression(expression);

        // If validationResponse is not null, an error occurred (i.e., it's a Bad Request)
        if (validationResponse != null) {
            return validationResponse;
        }

        try {
            double result = service.calculate(expression);

            return ResponseEntity.ok(Map.of(
                    "expression", expression,
                    "result", result
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Endpoint 2: Search by result (Remains structured)
    @GetMapping("/search")
    public ResponseEntity<?> searchByResult(@RequestParam double result) {
        return ResponseEntity.ok(service.getHistoryByResult(result));
    }

    private ResponseEntity<?> validateExpression(String expression) {

        Matcher matcher = VALID_EXPRESSION_PATTERN.matcher(expression);

        // Check if the expression contains ONLY valid characters
        if (!matcher.matches()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Input Validation Failed",
                    "message", "Expression contains invalid characters. Only numbers, operators (+, -, *, /), and parentheses are allowed."
            ));
        }

        return null;
    }
}
