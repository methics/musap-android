package fi.methics.musap.sdk.internal.datatype;

/**
 * Signature request or signature specific attributes
 */
public class SignatureAttribute {

    public String name;
    public String value;

    public SignatureAttribute(String name, String value) {
        this.name  = name;
        this.value = value;
    }

}
