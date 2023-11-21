package fi.methics.musap.sdk.internal.keygeneration;

import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.internal.datatype.KeyAttribute;

public class UpdateKeyReqBuilder {
    private String alias;
    private String did;
    private List<KeyAttribute> attributes = new ArrayList<>();
    private String role;
    private String state;

    public UpdateKeyReqBuilder setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public UpdateKeyReqBuilder setDid(String did) {
        this.did = did;
        return this;
    }

    public UpdateKeyReqBuilder addAttribute(String attrName, String attrValue) {
        this.attributes.add(new KeyAttribute(attrName, attrValue));
        return this;
    }

    public UpdateKeyReqBuilder setRole(String role) {
        this.role = role;
        return this;
    }

    public UpdateKeyReqBuilder setState(String state) {
        this.state = state;
        return this;
    }

    public UpdateKeyReq createUpdateKeyReq() {
        return new UpdateKeyReq(alias, did, attributes, role, state);
    }
}