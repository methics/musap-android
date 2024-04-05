package fi.methics.musap.sdk.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class MusapExceptionTest {

    @Test
    public void getErrorName() {
        MusapException exception = new MusapException(new IllegalArgumentException());
        assertEquals("internal_error", exception.getErrorName());
    }

    @Test
    public void getErrorCode() {
        MusapException exception = new MusapException(new IllegalArgumentException());
        assertEquals(900, exception.getErrorCode());
    }

    @Test
    public void getMessage() {
        String msg = "Missing data";
        MusapException exception = new MusapException(msg);
        assertEquals(msg, exception.getMessage());
    }

    @Test
    public void testToString() {
        String msg = "Missing data";
        MusapException exception = new MusapException(msg);

        assertEquals("Missing data (internal_error)", exception.toString());
    }
}