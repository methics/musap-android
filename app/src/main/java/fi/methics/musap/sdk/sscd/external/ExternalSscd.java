package fi.methics.musap.sdk.sscd.external;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.CmsSignature;
import fi.methics.musap.sdk.internal.datatype.ExternalSignaturePayload;
import fi.methics.musap.sdk.internal.datatype.ExternalSignatureResponsePayload;
import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.KeyAttribute;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;
import fi.methics.musap.sdk.internal.datatype.SignatureFormat;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.sscd.methicsdemo.MethicsDemoSscd;
import fi.methics.musapsdk.R;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * SSCD that uses MUSAP Link to request signatures with the "externalsign" Coupling API call
 */
public class ExternalSscd implements MusapSscdInterface<ExternalSscdSettings> {

    public static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    public static final String SSCD_TYPE         = "External Signature";
    public static final String ATTRIBUTE_MSISDN  = "msisdn";
    public static final String SIGN_MSG_TYPE     = "externalsignature";

    private static final int POLL_AMOUNT = 10;

    private static final Gson GSON = new Gson();

    private Context              context;
    private ExternalSscdSettings settings;
    private OkHttpClient         client;
    private MusapLink            musapLink;

    private String clientid;

    public ExternalSscd(Context context, ExternalSscdSettings settings) {
        this.context   = context;
        this.settings  = settings;
        this.musapLink = settings.getMusapLink();
        this.clientid  = settings.getClientId();
        this.client    = new OkHttpClient.Builder().readTimeout(settings.getTimeout()).build();
    }

    @Override
    public MusapKey bindKey(KeyBindReq req) throws Exception {

        ExternalSignaturePayload request = new ExternalSignaturePayload(this.clientid);
        CompletableFuture<String> future = new CompletableFuture<>();

        String msisdn = req.getAttribute(ATTRIBUTE_MSISDN);
        if (msisdn == null) {
            this.showEnterMsisdnDialog(req, future);
            msisdn = future.get();
        }

        request.data = Base64.encodeToString("Bind Key".getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP);
        request.attributes.put(ATTRIBUTE_MSISDN, msisdn);
        request.clientid = this.clientid;
        request.display  = req.getDisplayText();
        request.format   = "CMS";

        ExternalSignatureResponsePayload response = this.musapLink.sign(request);
        CmsSignature signature = new CmsSignature(response.getRawSignature());

        return new MusapKey.Builder()
                .setCertificate(signature.getSignerCertificate())
                .setKeyAlias(req.getKeyAlias())
                .setSscd(this.getSscdInfo())
                .addAttribute(new KeyAttribute(ATTRIBUTE_MSISDN, msisdn))
                .build();
    }

    @Override
    public MusapKey generateKey(KeyGenReq req) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MusapSignature sign(SignatureReq req) throws Exception {

        String msisdn = "35847001001"; // TODO

        ExternalSignaturePayload request = new ExternalSignaturePayload(this.clientid);
        request.attributes.put(ATTRIBUTE_MSISDN, msisdn);
        request.clientid = this.clientid;
        request.display  = req.getDisplayText();
        request.format   = req.getFormat().getFormat();
        request.data     = Base64.encodeToString(req.getData(), Base64.NO_WRAP);

        ExternalSignatureResponsePayload response = this.musapLink.sign(request);

        return new MusapSignature(response.getRawSignature());
    }

    @Override
    public MusapSscd getSscdInfo() {
        return new MusapSscd.Builder()
                .setSscdName(this.settings.getSscdName())
                .setSscdType(SSCD_TYPE)
                .setKeygenSupported(false)
                .setSupportedAlgorithms(Arrays.asList(KeyAlgorithm.RSA_2K))
                .setSupportedFormats(Arrays.asList(SignatureFormat.RAW, SignatureFormat.CMS))
                .build();
    }

    @Override
    public String generateSscdId(MusapKey key) {
        return SSCD_TYPE + "/" + key.getAttributeValue(ATTRIBUTE_MSISDN);
    }

    @Override
    public ExternalSscdSettings getSettings() {
        return settings;
    }

    /**
     * Show a dialog asking for the MSISDN
     * @param req KeyBind request
     * @throws MusapException
     */
    public void showEnterMsisdnDialog(KeyBindReq req, CompletableFuture<String> future) throws MusapException {
        if (req.getActivity() == null) {
            throw new MusapException("Cannot show MSISDN dialog");
        }
        Activity activity = req.getActivity();
        View view = LayoutInflater.from(req.getActivity()).inflate(R.layout.dialog_msisdn, null);

        req.getActivity().runOnUiThread(() -> {
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
