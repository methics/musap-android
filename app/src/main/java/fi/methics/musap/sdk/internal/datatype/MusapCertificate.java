package fi.methics.musap.sdk.internal.datatype;

import org.bouncycastle.cert.X509CertificateHolder;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

public class MusapCertificate {

    private String subject;
    private byte[] certificate;
    private PublicKey publicKey;

    public MusapCertificate(String subject, byte[] cert, PublicKey publicKey) {
        this.subject     = subject;
        this.certificate = cert;
        this.publicKey   = publicKey;
    }

    public MusapCertificate(X509CertificateHolder cert) throws IOException {
        this.subject     = cert.getSubject().toString();
        this.certificate = cert.getEncoded();
        this.publicKey   = new PublicKey(cert.getSubjectPublicKeyInfo().getEncoded());
    }

    public MusapCertificate(X509Certificate cert) throws CertificateEncodingException {
        this.subject     = cert.getSubjectDN().toString();
        this.certificate = cert.getEncoded();
        this.publicKey   = new PublicKey(cert.getPublicKey().getEncoded());
    }

    public String getSubject() {
        return this.subject;
    }

    public byte[] getCertificate() {
        return this.certificate;
    }

    public PublicKey getPublicKey() {
        return this.publicKey;
    }

}
