package com.endava.internship.mocking.repository;

import com.endava.internship.mocking.model.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class InMemPaymentRepositoryTest {

    private InMemPaymentRepository inMemPaymentRepository;

    @BeforeEach
    void setUp() {
        inMemPaymentRepository = new InMemPaymentRepository();
    }

    @Test
    void findByIdWhenParameterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> inMemPaymentRepository.findById(null),
                "Payment id must not be null");
    }

    @Test
    void findByIdWhenParameterIsContained() {
        Payment paymentToFind = new Payment(1, 1D, "Old message");
        inMemPaymentRepository.save(paymentToFind);
        savingMocks(2);

        Optional<Payment> result = inMemPaymentRepository.findById(paymentToFind.getPaymentId());

        assertAll(
                () -> assertTrue(result.isPresent()),
                () -> assertEquals(Optional.of(paymentToFind), result)
        );
    }

    @Test
    void findByIdWhenParameterIsNotContained() {
        savingMocks(3);

        Optional<Payment> result = inMemPaymentRepository.findById(UUID.randomUUID());

        assertFalse(result.isPresent());
    }

    @Test
    void findAllWhenMapIsEmpty() {
        List<Payment> paymentList = inMemPaymentRepository.findAll();

        assertEquals(0, paymentList.size());
    }

    @Test
    void findAllWhenMapContainsOnePayment() {
        Payment payment = createPayment();
        inMemPaymentRepository.save(payment);

        List<Payment> paymentList = inMemPaymentRepository.findAll();

        assertAll(
                () -> assertEquals(1, paymentList.size()),
                () -> assertTrue(paymentList.contains(payment))
        );
    }

    @Test
    void saveWhenPaymentIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> inMemPaymentRepository.save(null),
                "Payment must not be null");
    }

    @Test
    void saveWhenPaymentIsNotContained() {
        Payment payment = mock(Payment.class);
        Payment result = inMemPaymentRepository.save(payment);

        assertEquals(payment, result);
    }

    @Test
    void saveWhenPaymentIsContained() {
        Payment payment = createPayment();
        Payment result = inMemPaymentRepository.save(payment);

        assertAll(
                () -> assertEquals(payment, result),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> inMemPaymentRepository.save(payment),
                        "Payment with id " + payment.getPaymentId() + "already saved")
        );
    }

    @Test
    void editMessageWhenPaymentIdIsNull() {
        assertThrows(NoSuchElementException.class,
                () -> inMemPaymentRepository.editMessage(null, "Some message"));
    }

    @Test
    void editMessageWhenPaymentIdIsNotContained() {
        savingMocks(3);
        UUID paymentId = UUID.randomUUID();

        assertThrows(NoSuchElementException.class,
                () -> inMemPaymentRepository.editMessage(paymentId, "Some message"),
                "Payment with id " + paymentId + " not found");
    }

    @Test
    void editMessageWhenPaymentIdIsContained() {
        Payment payment = createPayment();
        inMemPaymentRepository.save(payment);
        savingMocks(2);

        Payment result = inMemPaymentRepository.editMessage(payment.getPaymentId(), "New message");

        assertAll(
                () -> assertEquals(payment, result),
                () -> assertEquals("New message", result.getMessage())
        );
    }

    private Payment createPayment() {
        return new Payment(1, 1D, "Old message");
    }

    private void savingMocks(int count) {
        for (int i = 0; i < count; i++) {
            inMemPaymentRepository.save(mock(Payment.class));
        }
    }
}