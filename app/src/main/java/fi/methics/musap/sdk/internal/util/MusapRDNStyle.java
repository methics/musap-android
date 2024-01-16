package fi.methics.musap.sdk.internal.util;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x500.style.RFC4519Style;

import java.util.Hashtable;

import javax.security.auth.x500.X500Principal;

/**
 * Extended version of BouncyCastle RFC4519Style definitions with
 * additional aliases.
 */
public class MusapRDNStyle extends RFC4519Style {

    public static final ASN1ObjectIdentifier eidSmartCardSerialNumber       = new ASN1ObjectIdentifier("1.2.752.34.2.1").intern();
    public static final ASN1ObjectIdentifier identificationpathlength       = new ASN1ObjectIdentifier("1.2.246.277.1.5.4.106").intern();
    public static final ASN1ObjectIdentifier initialidentificationauthority = new ASN1ObjectIdentifier("1.3.6.1.4.1.16722.101.1").intern();

    /**
     * Singleton instance.
     */
    public static final MusapRDNStyle INSTANCE = new MusapRDNStyle();

    protected final Hashtable<String,ASN1ObjectIdentifier> kiuruLookUp;
    protected final Hashtable<ASN1ObjectIdentifier,String> kiuruSymbols;

    @SuppressWarnings("unchecked")
    protected MusapRDNStyle() {
        // Construct supertype base
        super();
        // Make own copy sets of maps
        this.kiuruSymbols = copyHashTable(super.defaultSymbols);
        this.kiuruLookUp  = copyHashTable(super.defaultLookUp);

        this.kiuruLookUp.put("organizationidentifier", BCStyle.ORGANIZATION_IDENTIFIER);
        this.kiuruSymbols.put(BCStyle.ORGANIZATION_IDENTIFIER, "organizationIdentifier");

        // Uppercasify all output names
        for (final ASN1ObjectIdentifier oid : this.kiuruSymbols.keySet()) {
            this.kiuruSymbols.put(oid, this.kiuruSymbols.get(oid).toUpperCase());
        }

        // Add new parsed aliases, and modified outputs.

        // Match JRE outputs
        this.kiuruSymbols.put(RFC4519Style.sn,                  "SURNAME");
        this.kiuruSymbols.put(RFC4519Style.dnQualifier,         "DNQ");
        this.kiuruSymbols.put(RFC4519Style.generationQualifier, "GENERATION");
        this.kiuruSymbols.put(RFC4519Style.title,               "T");
        this.kiuruSymbols.put(PKCSObjectIdentifiers.pkcs_9_at_emailAddress, "EMAILADDRESS");
        this.kiuruSymbols.put(BCStyle.PSEUDONYM,                "PSEUDONYM");
        this.kiuruSymbols.put(eidSmartCardSerialNumber,         "ICCID");
        this.kiuruSymbols.put(RFC4519Style.telephoneNumber,     "MSISDN");

        // Add new renditions.
        this.kiuruSymbols.put(BCStyle.PSEUDONYM,                "PSEUDONYM");

        // All the lookup keys are in LOWER CASE form.

        // RFC4519 Style accepts SN, accept also SURNAME
        this.kiuruLookUp.put("surname",   RFC4519Style.sn);

        // Finnish VRK accepts G as alias of GIVENNAME.
        this.kiuruLookUp.put("g",         RFC4519Style.givenName);

        // LDAP alias name for GivenName
        this.kiuruLookUp.put("gn",        RFC4519Style.givenName);

        // Match JRE mappings
        this.kiuruLookUp.put("generation",RFC4519Style.generationQualifier);
        this.kiuruLookUp.put("s",         RFC4519Style.st);
        this.kiuruLookUp.put("t",         RFC4519Style.title);
        this.kiuruLookUp.put("dnq",       RFC4519Style.dnQualifier);
        // SUN proprietary ipAddress oid =  { 1, 3, 6, 1, 4, 1, 42, 2, 11, 2, 1 }
        // this.kiuruLookUp.put("ip",        xxxx.ipAddress_oid);
        // this.kiuruLookUp.put("ipaddress", xxxx.ipAddress_oid);
        this.kiuruLookUp.put("email",        PKCSObjectIdentifiers.pkcs_9_at_emailAddress);
        this.kiuruLookUp.put("emailaddress", PKCSObjectIdentifiers.pkcs_9_at_emailAddress);

        // Other LDAP alias names
        this.kiuruLookUp.put("countryname",          RFC4519Style.c);
        this.kiuruLookUp.put("country",              RFC4519Style.c);
        this.kiuruLookUp.put("commonname",           RFC4519Style.cn);
        this.kiuruLookUp.put("localityname",         RFC4519Style.l);
        this.kiuruLookUp.put("stateorprovincename",  RFC4519Style.st);
        this.kiuruLookUp.put("streetaddress",        RFC4519Style.street);
        this.kiuruLookUp.put("organizationname",     RFC4519Style.o);
        this.kiuruLookUp.put("organizationunitname", RFC4519Style.ou);
        this.kiuruLookUp.put("fax",                  RFC4519Style.facsimileTelephoneNumber);
        this.kiuruLookUp.put("domaincomponent",      RFC4519Style.dc);
        this.kiuruLookUp.put("userid",               RFC4519Style.uid);
        this.kiuruLookUp.put("serialnumber",         RFC4519Style.serialNumber);
        this.kiuruLookUp.put("msisdn",               RFC4519Style.telephoneNumber);
        this.kiuruLookUp.put("eidsmartcardserialnumber",       eidSmartCardSerialNumber);
        this.kiuruLookUp.put("cardnumber",                     eidSmartCardSerialNumber);
        this.kiuruLookUp.put("iccid",                          eidSmartCardSerialNumber);
        this.kiuruLookUp.put("pseudonym",                      BCStyle.PSEUDONYM);
        this.kiuruLookUp.put("pn",                             BCStyle.PSEUDONYM);
        this.kiuruLookUp.put("identificationpathlength",       identificationpathlength);
        this.kiuruLookUp.put("initialidentificationauthority", initialidentificationauthority);

    }

    @Override
    public String[] oidToAttrNames(final ASN1ObjectIdentifier oid)
    {
        return IETFUtils.findAttrNamesForOID(oid, this.kiuruSymbols);
    }

    @Override
    public ASN1ObjectIdentifier attrNameToOID(final String attrName)
    {
        return IETFUtils.decodeAttrName(attrName, this.kiuruLookUp);
    }

    // parse backwards -- matches that of how JRE outputs X500Principals !
    @Override
    public RDN[] fromString(final String dirName)
    {
        final RDN[] tmp = IETFUtils.rDNsFromString(dirName, this);
        final RDN[] res = new RDN[tmp.length];

        for (int i = 0; i != tmp.length; i++)
        {
            res[res.length - i - 1] = tmp[i];
        }

        return res;
    }

    // Convert in reverse -- matches that of how JRE outputs X500Principals !
    @Override
    public String toString(final X500Name n)
    {
        final StringBuffer buf = new StringBuffer();
        boolean first = true;

        final RDN[] rdns = n.getRDNs();

        for (int i = rdns.length - 1; i >= 0; i--) {
            if (first) {
                first = false;
            } else {
                buf.append(',');
            }
            IETFUtils.appendRDN(buf, rdns[i], this.kiuruSymbols);
        }
        return buf.toString();
    }

    /**
     * Convert an {@link X500Principal} to a DN String
     * @param principal X500Principal
     * @return Subject DN string
     */
    public String toString(final X500Principal principal) {
        return toString(X500Name.getInstance(principal.getEncoded()));
    }

    /**
     * Convert existing Subject DN String back to String
     * using Kiuru RDN style
     * @param n Subject DN
     * @return Subject DN
     */
    public String toString(final String n) {
        return this.toString(new X500Name(this, n));
    }

    /**
     * Get a value of a single attribute in a Subject
     * @param x500     X500Name to search from
     * @param attrName Attribute name to search
     * @return value of the attribute (without XX= part). May be null.
     */
    public String getAttribute(final X500Name x500,
                               final String   attrName) {

        try {
            ASN1ObjectIdentifier oid = this.attrNameToOID(attrName);
            RDN[] rdns = x500.getRDNs(oid);
            if (rdns == null || rdns.length < 1) return null;

            final StringBuffer buf = new StringBuffer();
            IETFUtils.appendRDN(buf, rdns[0], this.kiuruSymbols);
            return buf.toString().replaceFirst(".*=", "");
        } catch (Exception e) {
            // Unknown attr name, etc
            return null;
        }
    }

}
