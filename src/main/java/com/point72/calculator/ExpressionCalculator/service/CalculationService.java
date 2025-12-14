package com.point72.calculator.ExpressionCalculator.service;

import com.point72.calculator.ExpressionCalculator.model.CalculationRecord;
import com.point72.calculator.ExpressionCalculator.repository.CalculationRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

@Service
public class CalculationService {

    private final CalculationRepository repository;

    public CalculationService(CalculationRepository repository) {
        this.repository = repository;
    }

    public double calculate(String expressionString) {
        String expression = expressionString.replaceAll("\\s", "");
        if (expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty.");
        }

        Optional<CalculationRecord> existing = repository.findByExpression(expressionString);
        if (existing.isPresent()) {
            return existing.get().getResult();  // return cached
        }

        try {
            double result = evaluate(expression);

            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new IllegalArgumentException("Invalid calculation result (NaN or Infinity). Check for division by zero.");
            }

            saveHistoryAsync(expressionString, result);
            return result;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (EmptyStackException e) {
            throw new IllegalArgumentException("Invalid expression format. Check for unbalanced parentheses or missing operands.");
        } catch (Exception e) {
            throw new IllegalArgumentException("An unknown error occurred during calculation: " + e.getMessage());
        }
    }

    private double evaluate(String expression) {
        Stack<Double> values = new Stack<>();
        Stack<Character> ops = new Stack<>();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                values.push(Double.parseDouble(sb.toString()));
                i--;

            } else if (c == '(') {
                ops.push(c);

            } else if (c == ')') {
                while (ops.peek() != '(') {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.pop();

            } else if (isOperator(c)) {
                while (!ops.isEmpty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.isEmpty()) {
            if (ops.peek() == '(') {
                throw new IllegalArgumentException("Invalid expression: Unbalanced parentheses.");
            }
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid expression structure: Calculation finished with multiple results or no result.");
        }

        return values.pop();
    }

    // Checks if op2 has higher or same precedence as op1
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        if ((op1 == '*' || op1 == '/') && (op2 == '+' || op2 == '-')) return false;
        return true; // op2 has higher or equal precedence
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private double applyOp(char op, double b, double a) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }
                yield a / b;
            }
            default -> throw new IllegalArgumentException("Unknown operator: " + op);
        };
    }

    @Async
    public void saveHistoryAsync(String expression, double result) {
        repository.save(new CalculationRecord(expression, result));
    }

    public List<CalculationRecord> getHistoryByResult(double result) {
        return repository.findByResult(result);
    }
}