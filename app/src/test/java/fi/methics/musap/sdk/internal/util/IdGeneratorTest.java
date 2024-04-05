package fi.methics.musap.sdk.internal.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class IdGeneratorTest {

    @Test
    public void generateKeyId() {
        String id = IdGenerator.generateKeyId();
        assertNotNull(id);
    }

    @Test
    public void generatesDifferentIds() {
        String id1 = IdGenerator.generateKeyId();
        String id2 = IdGenerator.generateKeyId();
        assertNotEquals(id1, id2);
    }
}