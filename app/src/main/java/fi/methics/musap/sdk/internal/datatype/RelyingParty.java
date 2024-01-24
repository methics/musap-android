package fi.methics.musap.sdk.internal.datatype;


import fi.methics.musap.sdk.internal.datatype.coupling.LinkAccountResponsePayload;

/**
 * Relying party is an operator of a MUSAP Link service.
 * Instances of MUSAP apps link to these.
 */
public class RelyingParty {

    private String name;
    private String linkid;

    public RelyingParty(LinkAccountResponsePayload payload) {
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
