package fi.methics.musap.sdk.internal.datatype;

public class MusapLink {

    private String url;
    private String id;

    private String aesKey;
    private String macKey;

    public MusapLink(String url, String id) {
        this.url = url;
        this.id  = id;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public void setMacKey(String macKey) {
        this.macKey = macKey;
    }

    public void encrypt(MusapMessage msg) {
        // TODO
    }

    public void decrypt(MusapMessage msg) {
        // TODO
    }

}
