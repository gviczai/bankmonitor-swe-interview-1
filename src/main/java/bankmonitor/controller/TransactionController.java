package bankmonitor.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import bankmonitor.api.TransactionApi;
import bankmonitor.api.model.TransactionDto;
import bankmonitor.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;

@Controller
@RequestMapping("/")
public class TransactionController implements TransactionApi {

    private TransactionService transactionService;

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    public ResponseEntity<List<TransactionDto>> _readTransactions() {
        return ResponseEntity.ok(transactionService.read());
    }

    public ResponseEntity<TransactionDto> _createTransaction(String jsonData) {
        return ResponseEntity.ok(transactionService.create(jsonData));
    }

    public ResponseEntity<TransactionDto> _updateTransaction(Long id, String jsonData) {

        try { 
            return ResponseEntity.ok(transactionService.update(id, jsonData));
        }
        catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}