package com.stocks.aggregator.api.revolut;

import com.stocks.aggregator.revolut.RevolutStatementService;
import com.stocks.aggregator.revolut.model.RevolutStatement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/statements")
public class RevolutStatementController {

    private final RevolutStatementService statementService;

    @Autowired
    public RevolutStatementController(RevolutStatementService statementService) {
        this.statementService = statementService;
    }

    // Get all statements mapped by description
    @GetMapping("/by-description")
    public ResponseEntity<Map<String, List<RevolutStatement>>> getStatementsMappedByDescription(@RequestParam String type) {
        return ResponseEntity.ok(statementService.getStatementsMappedByDescription(type));
    }

    // Get statements by week of month
    @GetMapping("/by-week")
    public ResponseEntity<Map<String, List<RevolutStatement>>> getStatementsByWeekOfMonth(@RequestParam String type, @RequestParam int weekOfMonth) {
        return ResponseEntity.ok(statementService.getStatementsByWeekOfMonth(type, weekOfMonth));
    }

    // Get statements by month
    @GetMapping("/by-month")
    public ResponseEntity<Map<String, List<RevolutStatement>>> getStatementsByMonth(@RequestParam String type, @RequestParam int month) {
        return ResponseEntity.ok(statementService.getStatementsByMonth(type, month));
    }

    // Calculate expenditure
    @GetMapping("/expenditure")
    public ResponseEntity<Map<String, Double>> calculateExpenditure(@RequestParam String type) {
        Map<String, List<RevolutStatement>> map = statementService.getStatementsMappedByDescription(type);
        return ResponseEntity.ok(statementService.calculateExpenditure(map));
    }

    // Print all statements
//    @GetMapping("/print-all")
//    public ResponseEntity<String> printAllStatements(@RequestParam String type) {
//        statementService.printAllStatements(type);
//        return ResponseEntity.ok("All statements printed successfully.");
//    }
//
//    // Print expenditure
//    @GetMapping("/print-expenditure")
//    public ResponseEntity<String> printExpenditure(@RequestParam String type) {
//        Map<String, List<RevolutStatement>> map = statementService.getStatementsMappedByDescription(type);
//        statementService.printExpenditure(map);
//        return ResponseEntity.ok("Expenditure printed successfully.");
//    }
//
//    // Print all service methods
//    @GetMapping("/print-methods")
//    public ResponseEntity<String> printAllServiceMethods() {
//        statementService.printAllServiceMethods();
//        return ResponseEntity.ok("Service methods printed successfully.");
//    }
}
