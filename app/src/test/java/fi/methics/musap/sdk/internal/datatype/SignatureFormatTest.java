package fi.methics.musap.sdk.internal.datatype;

import static org.junit.Assert.*;

import org.junit.Test;

public class SignatureFormatTest {

    @Test
    public void getFormat() {
        SignatureFormat format = SignatureFormat.CMS;
        assertEquals("CMS", format.getFormat());
    }

    @Test
    public void fromString() {
        SignatureFormat format = SignatureFormat.fromString("RAW");
        assertEquals(SignatureFormat.RAW, format);
    }
}