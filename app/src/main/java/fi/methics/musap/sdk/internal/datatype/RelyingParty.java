package fi.methics.musap.sdk.internal.datatype;

import com.google.gson.Gson;

public class RelyingParty {

    private static final Gson GSON = new Gson();

    private String name;
    private String linkid;

    public RelyingParty(CouplingResponsePayload payload) {
        this.linkid = payload.linkid;
        this.name   = payload.name;
    }

    public RelyingParty(String name, String linkid) {
        this.name   = name;
        this.linkid = linkid;
    }

    public String getName() {
        return name;
    }

    public String getLinkID() {
        return this.linkid;
    }

}
