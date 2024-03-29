package fi.methics.musap.sdk.internal.datatype;

/**
 * Level of Assurance
 */
public class MusapLoA implements Comparable<MusapLoA> {

    public static final String LOA_SCHEME_EIDAS   = "EIDAS-2014";
    public static final String LOA_SCHEME_ISO     = "ISO-29115";
    public static final String LOA_SCHEME_NIST    = "NIST-SP-800";
    public static final String LOA_SCHEME_UNKNOWN = "UNKNOWN";

    public static final MusapLoA EIDAS_LOW         = new MusapLoA("low", 1, LOA_SCHEME_EIDAS);
    public static final MusapLoA EIDAS_SUBSTANTIAL = new MusapLoA("substantial", 3, LOA_SCHEME_EIDAS);
    public static final MusapLoA EIDAS_HIGH        = new MusapLoA("high", 4, LOA_SCHEME_EIDAS);
    public static final MusapLoA ISO_LOA1          = new MusapLoA("loa1", 1, LOA_SCHEME_ISO);
    public static final MusapLoA ISO_LOA2          = new MusapLoA("loa2", 2, LOA_SCHEME_ISO);
    public static final MusapLoA ISO_LOA3          = new MusapLoA("loa3", 3, LOA_SCHEME_ISO);
    public static final MusapLoA ISO_LOA4          = new MusapLoA("loa4", 4, LOA_SCHEME_ISO);

    public static final MusapLoA NIST_IAL1         = new MusapLoA("ial1", 1, LOA_SCHEME_NIST);
    public static final MusapLoA NIST_IAL2         = new MusapLoA("ial2", 2, LOA_SCHEME_NIST);
    public static final MusapLoA NIST_IAL3         = new MusapLoA("ial3", 3, LOA_SCHEME_NIST);

    public static final MusapLoA NIST_AAL1         = new MusapLoA("aal1", 1, LOA_SCHEME_NIST);
    public static final MusapLoA NIST_AAL2         = new MusapLoA("aal2", 2, LOA_SCHEME_NIST);
    public static final MusapLoA NIST_AAL3         = new MusapLoA("aal3", 3, LOA_SCHEME_NIST);


    private String loa;
    private String scheme;
    private int    number; // internal number used for comparison

    public MusapLoA(String loa, int number, String scheme) {
        this.loa    = loa;
        this.number = number;
        this.scheme = scheme;
    }

    public String getLoa() {
        return this.loa;
    }

    public String getScheme() {
        return this.scheme;
    }

    /**
     * Check if this LoA is higher or equal to given other LoA
     * @param other Other LoA
     * @return true if higher or equal
     */
    public boolean compareLoA(MusapLoA other) {
        if (other == null) return true;
        return this.number >= other.number;
    }

    public static boolean compareLoA(MusapLoA first, MusapLoA second) {
        if (first == null) {
            return false;
        }
        return first.compareLoA(second);
    }

    @Override
    public int compareTo(MusapLoA other) {
        if (other == null) return 1;
        return Integer.valueOf(this.number).compareTo(Integer.valueOf(other.number));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String scheme = this.scheme;
        if (scheme != null) {
            switch (scheme) {
                case LOA_SCHEME_EIDAS: sb.append("eidas-"); break;
                case LOA_SCHEME_NIST:  sb.append("nist-"); break;
                case LOA_SCHEME_ISO:   sb.append("iso-"); break;
            }
        }
        sb.append(this.loa);
        return sb.toString();
    }

}
