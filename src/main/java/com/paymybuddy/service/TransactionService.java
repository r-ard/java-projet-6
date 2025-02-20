package com.paymybuddy.service;

import com.paymybuddy.dto.UserTransactionDTO;
import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.contact.NotContactOfUserException;
import com.paymybuddy.exception.transaction.InsufficientBalanceException;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ContactService contactService;

    private static final double TRANSACTION_FEE = 0.05; // 0.5%

    public List<Transaction> getUserTransactions(User user) {
        return transactionRepository.findTransactionsOfUser(user);
    }

    @Transactional
    public void registerUserTransaction(
            User sender,
            User receiver,
            double amount,
            String description
    ) throws InsufficientBalanceException {
        double senderBalance = sender.getBalance();

        double fee = amount * TRANSACTION_FEE;

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

        userRepository.setUserBalance(sender.getId(), senderBalance - amount - fee);
        userRepository.setUserBalance(receiver.getId(), receiver.getBalance() + amount);
    }

    public List<UserTransactionDTO> getViewUserTransactions(User user, int limit) {
        List<UserTransactionDTO> out = new ArrayList<>();

        List<Transaction> userTransactions = this.getUserTransactions(user);

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

            UserTransactionDTO item = new UserTransactionDTO(
                    isSent,
                    contactName,
                    contactEmail,
                    transaction.getDescription(),
                    transaction.getAmount(),
                    transaction.getFee(),
                    transaction.getCreatedAt().toString().replace('T', ' ')
            );

            out.add(item);

            if(limit > 0 && out.size() >= limit) {
                break;
            }
        }

        return out;
    }
}
