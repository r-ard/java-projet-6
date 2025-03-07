package com.paymybuddy.service;

import com.paymybuddy.dto.UserTransactionDTO;
import com.paymybuddy.entity.Contact;
import com.paymybuddy.entity.Transaction;
import com.paymybuddy.entity.User;
import com.paymybuddy.exception.contact.NotContactOfUserException;
import com.paymybuddy.exception.transaction.InsufficientBalanceException;
import com.paymybuddy.repository.TransactionRepository;
import com.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class TransactionServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ContactService contactService;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(transactionService, "transactionFee", "0.5");
    }

    @Test
    public void testGetUserTransactions() {
        User user = new User();
        user.setId(1);

        Transaction t1 = new Transaction();
        Transaction t2 = new Transaction();
        List<Transaction> transactions = new ArrayList<>();

        transactions.add(t1);
        transactions.add(t2);

        when(transactionRepository.findTransactionsOfUser(user)).thenReturn(transactions);

        List<Transaction> result = transactionService.getUserTransactions(user);
        assertEquals(2, result.size());
        verify(transactionRepository).findTransactionsOfUser(user);
    }

    @Test
    public void testRegisterUserTransactionInsufficientBalance() {
        User sender = new User();
        sender.setId(1);
        sender.setBalance(50.0);
        User receiver = new User();
        receiver.setId(2);
        receiver.setBalance(100.0);

        double amount = 100.0; // fee = 100 * 0.005 = 0.5, total = 100.5

        assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.registerUserTransaction(sender, receiver, amount, "Paiement test");
        });
    }

    @Test
    public void testRegisterUserTransactionSuccess() throws InsufficientBalanceException {
        User sender = new User();
        sender.setId(1);
        sender.setBalance(200.0);
        User receiver = new User();
        receiver.setId(2);
        receiver.setBalance(100.0);

        double amount = 100.0;
        String description = "Payment test";

        Transaction transaction = transactionService.registerUserTransaction(sender, receiver, amount, description);

        assertEquals(sender, transaction.getSender());
        assertEquals(receiver, transaction.getReceiver());
        assertEquals(description, transaction.getDescription());
        assertEquals(amount, transaction.getAmount());

        // fee = amount * (0.5/100) = 0.5
        assertEquals(0.5, transaction.getFee(), 0.0001);

        // Check users balances
        verify(userRepository).setUserBalance(sender.getId(), sender.getBalance() - amount - transaction.getFee());
        verify(userRepository).setUserBalance(receiver.getId(), receiver.getBalance() + amount);
    }

    @Test
    public void testGetViewUserTransactionsSentWithContact() throws Exception {
        User sender = new User();
        sender.setId(1);
        sender.setUsername("Receiver");
        sender.setEmail("receiver@example.com");

        User receiver = new User();
        receiver.setId(2);
        receiver.setUsername("Sender");
        receiver.setEmail("sender@example.com");

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription("Test payment");
        transaction.setAmount(100.0);
        transaction.setFee(0.5);

        List<Transaction> transactions = new ArrayList<>();

        transactions.add(transaction);

        when(transactionRepository.findTransactionsOfUser(sender))
                .thenReturn(transactions);

        Contact contact = new Contact();
        contact.setName("ContactName");
        when(contactService.getUserContactOfUser(sender, receiver)).thenReturn(contact);

        List<UserTransactionDTO> dtos = transactionService.getViewUserTransactions(sender, 10);
        assertEquals(1, dtos.size());

        UserTransactionDTO dto = dtos.get(0);
        assertTrue(dto.isSent());

        assertEquals("Test payment", dto.getDescription());
        assertEquals(100.0, dto.getAmount(), 0.0001);
        assertEquals(0.5, dto.getFee(), 0.0001);
    }

    @Test
    public void testGetViewUserTransactionsReceivedWithoutContact() throws Exception {
        User receiver = new User();
        receiver.setId(2);
        receiver.setUsername("Receiver");
        receiver.setEmail("receiver@example.com");

        User sender = new User();
        sender.setId(1);
        sender.setUsername("Sender");
        sender.setEmail("sender@example.com");

        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setDescription("Sent payment");
        transaction.setAmount(150.0);
        transaction.setFee(0.75);

        List<Transaction> userTransactions = new ArrayList<>();
        userTransactions.add(transaction);

        when(transactionRepository.findTransactionsOfUser(receiver))
                .thenReturn(userTransactions);

        // Simulate contact service exception
        when(contactService.getUserContactOfUser(receiver, sender))
                .thenThrow(new NotContactOfUserException(sender.getId()));

        List<UserTransactionDTO> dtos = transactionService.getViewUserTransactions(receiver, 10);
        assertEquals(1, dtos.size());
        UserTransactionDTO dto = dtos.get(0);

        assertFalse(dto.isSent());

        assertEquals("Sender", dto.getNickname());
        assertEquals(sender.getEmail(), dto.getEmail());
        assertEquals("Sent payment", dto.getDescription());
        assertEquals(150.0, dto.getAmount(), 0.0001);
        assertEquals(0.75, dto.getFee(), 0.0001);
    }

    @Test
    public void testGetViewUserTransactions_WithLimit() throws Exception {
        // Vérifier que la limite d'affichage des transactions est respectée
        User sender = new User();
        sender.setId(1);
        sender.setUsername("User");
        sender.setEmail("user@example.com");

        User receiver = new User();
        receiver.setId(2);
        receiver.setUsername("Sender");
        receiver.setEmail("sender@example.com");

        Transaction transaction1 = new Transaction();
        transaction1.setSender(sender);
        transaction1.setReceiver(receiver);
        transaction1.setDescription("Payment 1");
        transaction1.setAmount(50.0);
        transaction1.setFee(0.25);

        Transaction transaction2 = new Transaction();
        transaction2.setSender(sender);
        transaction2.setReceiver(receiver);
        transaction2.setDescription("Payment 2");
        transaction2.setAmount(75.0);
        transaction2.setFee(0.375);

        List<Transaction> userTransactions = new ArrayList<>();

        userTransactions.add(transaction1);
        userTransactions.add(transaction2);

        when(transactionRepository.findTransactionsOfUser(sender))
                .thenReturn(userTransactions);

        when(contactService.getUserContactOfUser(sender, receiver)).thenReturn(null);

        List<UserTransactionDTO> dtos = transactionService.getViewUserTransactions(sender, 1);
        assertEquals(1, dtos.size());
    }

    @Test
    public void testGetTransactionFeeInvalidValue() {
        double fee = transactionService.getTransactionFee();
        assertEquals(0.005, fee, 0.0001);
    }
}
