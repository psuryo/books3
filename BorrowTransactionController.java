package com.example.perpus;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/transactions")
public class BorrowTransactionController {

    private final UserService userService;
    

    public BorrowTransactionController(UserService userService) {
        this.userService = userService;
    }

    // Serve the HTML page
    @GetMapping
   public String showTransactionsPage(Model model) {
    try {
            model.addAttribute("activeTransactions", userService.getActiveTransactions());
            return "transactions";
        } catch (Exception e) {
            model.addAttribute("error", "Error fetching transactions: " + e.getMessage());
            return "error"; // Create an error.html template
        }
}

    @PostMapping("/borrow")
    public ResponseEntity<String> borrowBook(@RequestParam Long userId, @RequestParam Long bookId) {
        try {
            userService.borrowBook(userId, bookId);
            return ResponseEntity.ok("Book borrowed successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestParam Long transactionId) {
        try {
            userService.returnBook(transactionId);
            return ResponseEntity.ok("Book returned successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("error", e.getMessage());
        return "error";
    }
}
