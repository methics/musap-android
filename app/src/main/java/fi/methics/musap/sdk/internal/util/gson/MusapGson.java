package fi.methics.musap.sdk.internal.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.time.Instant;

public class MusapGson {

    public static final Gson GSON =
            new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .disableHtmlEscaping()
                    .create();

}
