package com.paymybuddy.service;

import com.paymybuddy.dto.UserTransactionDTO;
import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.contact.NotContactOfUserException;
import com.paymybuddy.exception.transaction.InsufficientBalanceException;
import com.paymybuddy.exception.transaction.TransactionUserNotFoundException;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import com.paymybuddy.utils.FormatUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Component
public class TransactionService {
    @Value("${app.transactionfee}")
    private String transactionFee;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ContactService contactService;

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findTransactionsOfUser(user);
    }

    public List<Transaction> getNumbersUserTransactions(User user, int limit) {
        return transactionRepository.findNumbersTransactionsOfUser(user, limit);
    }

    public List<Transaction> getPagedUserTransactions(User user, int limit, int page) {
        return transactionRepository.findPagedTransactionsOfUser(user, limit, (page - 1) * limit);
    }

    @Transactional(rollbackOn = Exception.class)
    public Transaction registerUserTransaction(
            User sender,
            User receiver,
            double amount,
            String description
    ) throws InsufficientBalanceException, TransactionUserNotFoundException {
        double senderBalance = sender.getBalance();

        // Clean amount value
        amount = FormatUtils.roundDecimal(amount, 2);

        double fee = FormatUtils.roundDecimal(amount * this.getTransactionFee(), 2); // Round ex : 0.133 -> 0.14

        // Check if sender's has suffisant balance for transaction's amount + fee
        if(senderBalance < (amount + fee)) {
            throw new InsufficientBalanceException(sender.getBalance());
        }

        String transactionDescription = (description == null || description.trim().isEmpty()) ? null : description;

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription(transactionDescription);
        transaction.setAmount(amount);
        transaction.setFee(fee);
        transactionRepository.save(transaction);

        int senderCount = userRepository.setUserBalance(sender.getId(), FormatUtils.roundDecimal(senderBalance - amount - fee, 2));
        if(senderCount == 0) {
            throw new TransactionUserNotFoundException();
        }

        int receiverCount = userRepository.setUserBalance(receiver.getId(), FormatUtils.roundDecimal(receiver.getBalance() + amount, 2));
        if(receiverCount == 0) {
            throw new TransactionUserNotFoundException();
        }

        return transaction;
    }

    public long getUserTransactionsAmount(User user) {
        return transactionRepository.countTransactionsOfUser(user);
    }

    public List<UserTransactionDTO> getViewUserTransactions(User user, int limit, int page) {
        List<UserTransactionDTO> out = new ArrayList<>();

        List<Transaction> userTransactions = null;

        if(limit == -1) {
            userTransactions = this.getUserTransactions(user);
        }
        else if(page <= 0) {
            userTransactions = this.getNumbersUserTransactions(user, limit);
        }
        else {
            userTransactions = this.getPagedUserTransactions(user, limit, page);
        }

        for(Transaction transaction : userTransactions) {
            User receiver = transaction.getReceiver();
            User sender = transaction.getSender();

            boolean isSent = sender.getId().equals(user.getId());

            String contactEmail = isSent ? receiver.getEmail() : sender.getEmail();
            String contactName = isSent ? receiver.getUsername() : sender.getUsername();

            try {
                Contact contact = contactService.getUserContactOfUser(
                        isSent ? sender : receiver,
                        isSent ? receiver : sender
                );

                if(contact != null) {
                    contactName = contact.getName();
                }
            }
            catch(NotContactOfUserException ex) {

            }

            LocalDateTime createdAt = transaction.getCreatedAt();

            UserTransactionDTO item = new UserTransactionDTO(
                    isSent,
                    contactName,
                    contactEmail,
                    transaction.getDescription(),
                    transaction.getAmount(),
                    transaction.getFee(),
                    createdAt == null ? "" : createdAt.toString().replace('T', ' ')
            );

            out.add(item);
        }

        return out;
    }

    protected double getTransactionFee() {
        try {
            double parsedValue = Double.parseDouble(this.transactionFee);
            if(Double.isNaN(parsedValue)) throw new Exception();

            // If fee is not 0, then convert it to on 1 factor
            return parsedValue > 0 ? (parsedValue / 100.0D) : parsedValue;
        }
        catch(Exception e) {
            return 0.005; // Default fee value is 0.005 -> 0.5%
        }
    }
}
