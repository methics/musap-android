package fi.methics.musap.sdk.internal.datatype;


import java.util.Objects;

public class SignatureFormat {

    public static final SignatureFormat CMS = new SignatureFormat("CMS");
    public static final SignatureFormat RAW = new SignatureFormat("RAW"); // a.k.a. PKCS1

    private String format;

    public SignatureFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return this.format;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignatureFormat that = (SignatureFormat) o;
        return Objects.equals(format, that.format);
    }

    @Override
    public int hashCode() {
        return format != null ? format.hashCode() : 0;
    }
}
