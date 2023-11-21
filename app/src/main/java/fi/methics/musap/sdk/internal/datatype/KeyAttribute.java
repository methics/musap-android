package fi.methics.musap.sdk.internal.datatype;

/**
 * Key binding, key generation, or key specific attributes
 */
public class KeyAttribute {

    public String name;
    public String value;

    public KeyAttribute(String name, String value) {
        this.name  = name;
        this.value = value;
    }

}
