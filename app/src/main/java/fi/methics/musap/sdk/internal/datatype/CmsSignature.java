package fi.methics.musap.sdk.internal.datatype;

import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignedData;

import java.io.IOException;
import java.util.Collection;

import fi.methics.musap.sdk.api.MusapException;

public class CmsSignature extends MusapSignature {

    private CMSSignedData signedData;

    public CmsSignature(byte[] cms) throws MusapException {
        super(cms, null, null, SignatureFormat.CMS);
        try {
            final ContentInfo ci = ContentInfo.getInstance(cms);
            this.signedData = new CMSSignedData(ci);
        } catch (Exception e) {
            throw new MusapException(e);
        }
    }
    public CmsSignature(byte[] cms, MusapKey key, SignatureAlgorithm algorithm) throws MusapException {
        super(cms, key, algorithm, SignatureFormat.CMS);
        try {
            final ContentInfo ci = ContentInfo.getInstance(cms);
            this.signedData = new CMSSignedData(ci);
        } catch (Exception e) {
            throw new MusapException(e);
        }
    }

    /**
     * Get the CMS signed data object
     * @return CMS signed data
     */
    public CMSSignedData getSignedData() {
        return this.signedData;
    }

    /**
     * Get the X509 Certificates related to this CMS signature
     * @return Collection of certificates
     * @throws IOException
     */
    public Collection<X509CertificateHolder> getCertificates() throws IOException {
        return signedData.getCertificates().getMatches(null);
    }

    /**
     * Get the signer's X509 certificate
     * @return certificate
     * @throws IOException
     */
    public MusapCertificate getSignerCertificate() throws IOException {
        Collection<X509CertificateHolder> certs = this.getCertificates();
        X509CertificateHolder cert = certs.stream().findFirst().orElse(null);
        return new MusapCertificate(cert);
    }

    /**
     * Get the key related to signer's certificate
     * @return signer key
     * @throws IOException
     */
    public MusapKey getSignerKey() throws IOException {
        MusapKey.Builder builder = new MusapKey.Builder();
        builder.setCertificate(this.getSignerCertificate());
        return builder.build();
    }

}
