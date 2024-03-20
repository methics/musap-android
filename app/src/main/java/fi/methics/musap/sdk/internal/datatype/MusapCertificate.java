package fi.methics.musap.sdk.internal.datatype;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.X509Principal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.security.auth.x500.X500Principal;

import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapRDNStyle;

/**
 * MUSAP Certificate wrapper
 */
public class MusapCertificate {

    private String subject;
    private byte[] certificate;
    private PublicKey publicKey;

    /**
     * Crate a new MUSAP certificate
     * @param subject Subject DN String
     * @param cert Raw certificate bytes
     * @param publicKey Public Key associated with the certificate
     */
    public MusapCertificate(String subject, byte[] cert, PublicKey publicKey) {
        this.subject     = new X500Name(MusapRDNStyle.INSTANCE, subject).toString();
        this.certificate = cert;
        this.publicKey   = publicKey;
    }

    /**
     * Create a new MUSAP certificate from an {@link X509CertificateHolder}
     * @param cert CertificateHolder
     * @throws IOException if the certificate could not be parsed
     */
    public MusapCertificate(X509CertificateHolder cert) throws IOException {
        this.subject     = MusapRDNStyle.INSTANCE.toString(cert.getSubject());
        this.certificate = cert.getEncoded();
        this.publicKey   = new PublicKey(cert.getSubjectPublicKeyInfo().getEncoded());
    }

    /**
     * Create a new MUSAP certificate from a {@link X509Certificate}
     * @param cert Certificate
     * @throws CertificateEncodingException if the certificate could not be parsed
     */
    public MusapCertificate(X509Certificate cert) throws CertificateEncodingException {
        this.subject     = MusapRDNStyle.INSTANCE.toString(cert.getSubjectX500Principal());
        this.certificate = cert.getEncoded();
        this.publicKey   = new PublicKey(cert.getPublicKey().getEncoded());
    }

    /**
     * Create a new MUSAP certificate from a raw byte[] certificate (e.g. {@link X509Certificate#getEncoded()}
     * @param cert Certificate
     * @throws CertificateEncodingException if the certificate could not be parsed
     */
    public MusapCertificate(byte[] cert) throws CertificateEncodingException {
        this(bytesToCert(cert));
    }

    /**
     * Get GIVENNAME from the certificate subject
     * @return GIVENNAME
     */
    public String getGivenName() {
        return getSubjectAttribute("GIVENNAME");
    }

    /**
     * Get SURNAME from the certificate subject
     * @return SURNAME
     */
    public String getSurname() {
        return getSubjectAttribute("SURNAME");
    }

    /**
     * Get SERIALNUMBER from the certificate subject
     * @return SERIALNUMBER
     */
    public String getSerialNumber() {
        return getSubjectAttribute("SERIALNUMBER");
    }

    /**
     * Get EMAIL from the certificate subject
     * @return EMAIL
     */
    public String getEmail() {
        return getSubjectAttribute("EMAILADDRESS");
    }

    /**
     * Get any attribute from the certifcate subject
     * @param attrName Attribute name (e.g. "COUNTRY")
     * @return Attribute value or null if not found in subject
     */
    public String getSubjectAttribute(String attrName) {
        return MusapRDNStyle.INSTANCE.getAttribute(new X500Name(MusapRDNStyle.INSTANCE, this.subject), attrName);
    }

    /**
     * Get the certificate subject as a String
     * @return Subject
     */
    public String getSubject() {
        return MusapRDNStyle.INSTANCE.toString(this.subject);
    }

    /**
     * Get the raw certificate bytes
     * @return certificate
     */
    public byte[] getCertificate() {
        return this.certificate;
    }

    /**
     * Get the public key associated with the certificate
     * @return certificate
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }

    /**
     * Convert a byte[] to an {@link X509Certificate}
     * @param cert Certificate as byte[]
     * @return X509Certificate
     * @throws CertificateEncodingException if conversion fails
     */
    private static X509Certificate bytesToCert(byte[] cert) throws CertificateEncodingException {
        try (InputStream in = new ByteArrayInputStream(cert)) {
            CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(in);
        } catch (Exception e) {
            MLog.d("Failed to parse byte[] certificate", e);
            throw new CertificateEncodingException(e);
        }
    }

}
