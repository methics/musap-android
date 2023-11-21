package fi.methics.musap.sdk.internal.discovery;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;

public class MusapImportData {

    private static final Gson GSON = new Gson();

    @SerializedName("sscds")
    public List<MusapSscd> sscds;

    @SerializedName("keys")
    public List<MusapKey> keys;

    public MusapImportData() {

    }

    /**
     * Convert this import data to JSON
     * @return JSON
     */
    public String toJson() {
        return GSON.toJson(this);
    }

    /**
     * Parse JSON import data
     * @param json JSON import data to parse
     * @return {@link MusapImportData} object
     * @throws JsonSyntaxException if data is not parseable
     */
    public static MusapImportData fromJson(String json) throws JsonSyntaxException {
        return GSON.fromJson(json, MusapImportData.class);
    }

}
