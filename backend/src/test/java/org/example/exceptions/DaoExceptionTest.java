package org.example.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DaoExceptionTest {
    @Test
    void preservesMessage() {
        DaoException exception =
                new DaoException("problem");

        assertEquals(
                "problem",
                exception.getMessage());
    }
}
