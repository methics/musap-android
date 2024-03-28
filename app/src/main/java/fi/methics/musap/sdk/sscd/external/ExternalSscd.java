package fi.methics.musap.sdk.sscd.external;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.attestation.KeyAttestation;
import fi.methics.musap.sdk.attestation.UiccKeyAttestation;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.CmsSignature;
import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.KeyAttribute;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.SignatureAttribute;
import fi.methics.musap.sdk.internal.datatype.SignatureFormat;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.datatype.coupling.ExternalSignaturePayload;
import fi.methics.musap.sdk.internal.datatype.coupling.ExternalSignatureResponsePayload;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;
import fi.methics.musap.sdk.internal.util.IdGenerator;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musapsdk.R;

/**
 * SSCD that uses MUSAP Link to request signatures with the "externalsign" Coupling API call
 */
public class ExternalSscd implements MusapSscdInterface<ExternalSscdSettings> {

    public static final String SSCD_TYPE         = "External Signature";
    public static final String ATTRIBUTE_MSISDN  = "msisdn";

    private Context              context;
    private ExternalSscdSettings settings;
    private MusapLink            musapLink;

    private String clientid;

    public ExternalSscd(Context context, ExternalSscdSettings settings) {
        this.context   = context;
        this.settings  = settings;
        this.musapLink = settings.getMusapLink();
        this.clientid  = settings.getClientId();
    }

    @Override
    public MusapKey bindKey(KeyBindReq req) throws Exception {

        MLog.d("Binding ExternalSscd");

        ExternalSignaturePayload request = new ExternalSignaturePayload(this.clientid);
        CompletableFuture<String> future = new CompletableFuture<>();

        String msisdn = req.getAttribute(ATTRIBUTE_MSISDN);
        if (msisdn == null) {
            this.showEnterMsisdnDialog(req.getActivity(), future);
            msisdn = future.get();
        }

        MLog.d("MSISDN=" + msisdn);

        String keyid = IdGenerator.generateKeyId();

        request.data = Base64.encodeToString("Bind Key".getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        request.attributes.put(ATTRIBUTE_MSISDN, msisdn);
        for (KeyAttribute attr : req.getAttributes()) {
            request.attributes.put(attr.name, attr.value);
        }
        request.clientid = this.clientid;
        request.display  = req.getDisplayText();
        request.format   = "CMS";
        request.keyid    = keyid;
        request.keyusages= req.getKeyUsages();

        MLog.d("Created bind request");
        // If MUSAP Link is null (because this class was initialized too early)
        // try to refetch the link
        if (this.musapLink == null) {
            this.musapLink = this.settings.getMusapLink();
        }

        MLog.d("Sending sign request to MUSAP Link");
        ExternalSignatureResponsePayload response = this.musapLink.sign(request);
        CmsSignature signature = new CmsSignature(response.getRawSignature());

        return new MusapKey.Builder()
                .setCertificate(signature.getSignerCertificate())
                .setCertificateChain(signature.getCertificateChain())
                .setKeyAlias(req.getKeyAlias())
                .setSscd(this.getSscdInfo())
                .setKeyId(keyid)
                .setAlgorithm(KeyAlgorithm.RSA_2K) // TODO: Make this configurable or resolve it
                .addAttribute(new KeyAttribute(ATTRIBUTE_MSISDN, msisdn))
                .build();
    }

    @Override
    public MusapKey generateKey(KeyGenReq req) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MusapSignature sign(SignatureReq req) throws Exception {

        ExternalSignaturePayload request = new ExternalSignaturePayload(this.clientid);
        CompletableFuture<String> future = new CompletableFuture<>();

        String msisdn = req.getAttribute(ATTRIBUTE_MSISDN);
        if (msisdn == null) {
            this.showEnterMsisdnDialog(req.getActivity(), future);
            msisdn = future.get();
        }

        MusapKey key = req.getKey();

        request.attributes.put(ATTRIBUTE_MSISDN, msisdn);
        for (SignatureAttribute attr : req.getAttributes()) {
            request.attributes.put(attr.name, attr.value);
        }
        request.clientid = this.clientid;
        request.display  = req.getDisplayText();
        request.format   = req.getFormat().getFormat();
        request.data     = Base64.encodeToString(req.getData(), Base64.NO_WRAP);
        request.keyid    = key.getKeyId();
        request.keyusages= key.getKeyUsages();

        // If MUSAP Link is null (because this class was initialized too early)
        // try to refetch the link
        if (this.musapLink == null) {
            this.musapLink = this.settings.getMusapLink();
        }

        ExternalSignatureResponsePayload response = this.musapLink.sign(request);

        MusapSignature sig = new MusapSignature(response.getRawSignature());
        sig.setKey(req.getKey());
        return sig;
    }

    @Override
    public SscdInfo getSscdInfo() {
        return new SscdInfo.Builder()
                .setSscdName(this.settings.getSscdName())
                .setSscdType(SSCD_TYPE)
                .setKeygenSupported(false)
                .setProvider(this.settings.getProvider())
                .setSupportedAlgorithms(Arrays.asList(KeyAlgorithm.RSA_2K))
                .setSupportedFormats(Arrays.asList(SignatureFormat.RAW, SignatureFormat.CMS))
                .build();
    }

    @Override
    public ExternalSscdSettings getSettings() {
        return settings;
    }

    @Override
    public KeyAttestation getKeyAttestation() {
        return new UiccKeyAttestation();
    }

    /**
     * Show a dialog asking for the MSISDN
     * @param activity Activity to inflate with the dialog
     * @param future Future used to deliver the response
     * @throws MusapException
     */
    public void showEnterMsisdnDialog(Activity activity, CompletableFuture<String> future) throws MusapException {
        if (activity == null) {
            throw new MusapException("Cannot show MSISDN dialog");
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_msisdn, null);

        activity.runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle("Enter your Phone Number")
                    .setView(view)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String msisdn = ((TextView) view.findViewById(R.id.dialog_msisdn_edittext)).getText().toString();
                        MLog.d("MSISDN=" + msisdn);
                        future.complete(msisdn);
                    })
                    .setNeutralButton("Cancel", (dialogInterface, i) ->  {
                        dialogInterface.cancel();
                        future.completeExceptionally(new MusapException("User canceled"));
                    })
                    .create();
            dialog.show();
        });
    }

}
