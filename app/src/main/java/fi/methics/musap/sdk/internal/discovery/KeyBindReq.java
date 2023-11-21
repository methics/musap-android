package fi.methics.musap.sdk.internal.discovery;

import android.app.Activity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import fi.methics.musap.sdk.internal.datatype.KeyAttribute;
import fi.methics.musap.sdk.internal.datatype.StepUpPolicy;

public class KeyBindReq {

    private String keyAlias;
    private String displayText;
    private String did;
    private String role;
    private StepUpPolicy stepUpPolicy;
    private List<KeyAttribute> attributes;

    private Activity activity;
    private View view;

    protected KeyBindReq() {

    }

    public String getKeyAlias() {
        return keyAlias;
    }

    public String getDid() {
        return did;
    }

    public String getRole() {
        return role;
    }

    public StepUpPolicy getStepUpPolicy() {
        return stepUpPolicy;
    }

    public List<KeyAttribute> getAttributes() {
        return attributes;
    }

    public Activity getActivity() {
        return activity;
    }

    public View getView() {
        return view;
    }

    /**
     * Get a key attribute value
     * @param name Attribute name
     * @return Attribute value or null if not found
     */
    public String getAttribute(String name) {
        if (name == null) return null;
        for (KeyAttribute attr : getAttributes()) {
            if (attr == null) continue;
            if (name.equals(attr.name)) {
                return attr.value;
            }
        }
        return null;
    }

    /**
     * Get the text to display to the user during the signature request
     * @return Display text (a.k.a. DTBD). Default is "Sign with MUSAP".
     */
    public String getDisplayText() {
        return this.displayText;
    }

    public static class Builder {
        private String keyAlias;
        private String displayText = "Activate MUSAP";
        private String did;
        private String role;
        private StepUpPolicy stepUpPolicy;
        private List<KeyAttribute> attributes = new ArrayList<>();
        private Activity activity;
        private View view;

        private boolean generateNewKey;


        public Builder setGenerateNewKey(boolean generateNewKey) {
            this.generateNewKey = generateNewKey;
            return this;
        }

        public Builder setDid(String did) {
            this.did = did;
            return this;
        }

        public Builder setRole(String role) {
            this.role = role;
            return this;
        }

        public Builder setKeyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
            return this;
        }

        public Builder setActivity(Activity activity) {
            this.activity = activity;
            return this;
        }

        public Builder setView(View view) {
            this.view = view;
            return this;
        }

        public Builder setStepUpPolicy(StepUpPolicy stepUpPolicy) {
            this.stepUpPolicy = stepUpPolicy;
            return this;
        }

        public Builder addAttribute(String key, String value) {
            this.attributes.add(new KeyAttribute(key, value));
            return this;
        }

        public Builder addAttribute(KeyAttribute attr) {
            this.attributes.add(attr);
            return this;
        }

        public Builder setDisplayText(String text) {
            this.displayText = text;
            return this;
        }

        public KeyBindReq createKeyBindReq() {
            KeyBindReq req = new KeyBindReq();
            req.keyAlias     = keyAlias;
            req.did          = did;
            req.attributes   = attributes;
            req.stepUpPolicy = stepUpPolicy;
            req.role         = role;
            req.view         = view;
            req.activity     = activity;
            req.displayText  = displayText;
            return req;
        }
    }

}
