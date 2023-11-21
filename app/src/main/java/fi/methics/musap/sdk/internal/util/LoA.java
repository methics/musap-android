package fi.methics.musap.sdk.internal.util;

// TODO: This is duplicated by fi.methics.musap.keyuri.MUSAPLoa
public class LoA {

    public static final String ANY = "ANY";
    public static final String HIGH = "HIGH";
    public static final String SUBSTANTIAL = "SUBSTANTIAL";
    public static final String LOW = "LOW";


    /**
     * Compare
     * @param keystoreLoa
     * @param wantedLoa
     * @return
     */
    public static boolean compareLoA(String keystoreLoa, String wantedLoa) {
        int keystoreLoaLvl = mapToNumber(keystoreLoa);
        int wantedLoaLvl = mapToNumber(wantedLoa);

        return keystoreLoaLvl >= wantedLoaLvl;
    }

    /**
     * Convert String LoA to a numerical value for easier comparison.
     * @param loa
     * @return
     */
    private static int mapToNumber(String loa) {
        if (loa == null || loa.equalsIgnoreCase("any")) {
            return 0;
        }
        switch (loa.toLowerCase()) {
            case "high":
                return 3;
            case "substantial":
                return 2;
            case "low":
                return 1;
        }
        return 0;
    }
}
