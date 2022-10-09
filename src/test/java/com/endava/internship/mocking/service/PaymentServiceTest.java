package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Payment;
import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import com.endava.internship.mocking.repository.PaymentRepository;
import com.endava.internship.mocking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ValidationService validationService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    void createPaymentWhenUserIdNotContained() {
        int userId = 10;
        double amount = 2D;

        assertThrows(NoSuchElementException.class,
                () -> paymentService.createPayment(userId, amount));
        verify(validationService).validateUserId(userId);
        verify(validationService).validateAmount(amount);
        verify(userRepository).findById(userId);
    }

    @Test
    void createPaymentWhenUserIsContained() {
        //TODO
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);

        User user = new User(1, "John", Status.ACTIVE);
        double amount = 200D;

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        paymentService.createPayment(user.getId(), amount);

        verify(validationService).validateUserId(user.getId());
        verify(validationService).validateAmount(amount);
        verify(userRepository).findById(user.getId());
        verify(validationService).validateUser(user);
        verify(paymentRepository).save(paymentCaptor.capture());

        Payment createdPayment = paymentCaptor.getValue();
        String message = "Payment from user " + user.getName();
        assertAll(
                () -> assertEquals(user.getId(), createdPayment.getUserId()),
                () -> assertEquals(amount, createdPayment.getAmount()),
                () -> assertEquals(message, createdPayment.getMessage())
        );
    }

    @Test
    void editMessageWithValidParameters() {
        UUID paymentId = UUID.randomUUID();
        String newMessage = "New message";

        ArgumentCaptor<UUID> paymentIdCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        paymentService.editPaymentMessage(paymentId, newMessage);

        verify(paymentRepository).editMessage(paymentIdCaptor.capture(), messageCaptor.capture());

        UUID capturedPaymentId = paymentIdCaptor.getValue();
        String capturedMessage = messageCaptor.getValue();

        assertAll(
                () -> assertEquals(paymentId, capturedPaymentId),
                () -> assertEquals(newMessage, capturedMessage)
        );
    }

    @Test
    void getAllByAmountExceedingWithExistingGreaterAmountsInList() {
        List<Payment> paymentList = createPaymentList();

        List<Payment> expectedList = new ArrayList<>(Arrays.
                asList(paymentList.get(3), paymentList.get(4)));

        when(paymentRepository.findAll()).thenReturn(paymentList);
        when(paymentService.getAllByAmountExceeding(102D)).thenReturn(expectedList);
        List<Payment> resultList = paymentService.getAllByAmountExceeding(10D);

        assertEquals(expectedList, resultList);
    }

    @Test
    void getAllByAmountExceedingWithNoGreaterAmountsInList() {
        List<Payment> paymentList = createPaymentList();

        List<Payment> expectedList = new ArrayList<>();

        when(paymentRepository.findAll()).thenReturn(paymentList);
        when(paymentService.getAllByAmountExceeding(104D)).thenReturn(expectedList);
        List<Payment> resultList = paymentService.getAllByAmountExceeding(104D);

        assertEquals(expectedList, resultList);
    }

    private List<Payment> createPaymentList() {
        List<Payment> paymentList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            paymentList.add(new Payment(i, i + 100D, "Payment " + i));
        }

        return paymentList;
    }
}
