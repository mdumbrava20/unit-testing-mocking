package com.endava.internship.mocking.service;

import com.endava.internship.mocking.model.Status;
import com.endava.internship.mocking.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BasicValidationServiceTest {

    private BasicValidationService basicValidationService;

    @BeforeEach
    void setUp() {
        basicValidationService = new BasicValidationService();
    }

    @Test
    void validateAmountWhenParameterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateAmount(null),
                "Amount must not be null");
    }

    @Test
    void validateAmountWhenParameterIsLessThanZero() {
        Double amount = -1D;

        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateAmount(amount),
                "Amount must be greater than 0");
    }

    @Test
    void validatePaymentIdWhenParameterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validatePaymentId(null),
                "Payment id must not be null");
    }

    @Test
    void validateUserIdWhenParameterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUserId(null),
                "User id must not be null");
    }

    @Test
    void validateUserWhenStatusIsInactive() {
        User user = Mockito.mock(User.class);

        when(user.getStatus()).thenReturn(Status.INACTIVE);

        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateUser(user),
                "User with id " + user.getId() + " not in ACTIVE status");
    }

    @Test
    void validateMessageWhenParameterIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> basicValidationService.validateMessage(null),
                "Payment message must not be null");
    }
}