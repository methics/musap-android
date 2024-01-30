package fi.methics.musap.sdk.sscd.yubikey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yubico.yubikit.android.YubiKitManager;
import com.yubico.yubikit.android.transport.nfc.NfcConfiguration;
import com.yubico.yubikit.android.transport.nfc.NfcNotAvailable;
import com.yubico.yubikit.android.transport.nfc.NfcYubiKeyDevice;
import com.yubico.yubikit.core.smartcard.SmartCardConnection;
import com.yubico.yubikit.openpgp.KeyRef;
import com.yubico.yubikit.openpgp.OpenPgpCurve;
import com.yubico.yubikit.openpgp.OpenPgpSession;
import com.yubico.yubikit.piv.ManagementKeyType;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

import fi.methics.musap.sdk.api.MusapException;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.datatype.KeyAlgorithm;
import fi.methics.musap.sdk.internal.datatype.MusapCertificate;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapLoA;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.datatype.SignatureFormat;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;
import fi.methics.musap.sdk.internal.util.IdGenerator;
import fi.methics.musap.sdk.internal.util.KeyGenerationResult;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.SigningResult;
import fi.methics.musapsdk.R;

public class YubiKeyOpenPgpSscd implements MusapSscdInterface<YubiKeySettings> {
    private static final byte[] MANAGEMENT_KEY = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final ManagementKeyType TYPE = ManagementKeyType.TDES;


    public static final char[] DEFAULT_USER_PIN = "123456".toCharArray();
    public static final char[] DEFAULT_ADMIN_PIN = "12345678".toCharArray();

    private static final String SSCD_TYPE        = "YubikeyEddsa";
    private static final String ATTRIBUTE_SERIAL = "SerialNumber";

    private YubiKeySettings settings = new YubiKeySettings();

    private AlertDialog currentPrompt;
    private CompletableFuture<KeyGenerationResult> keygenFuture;
    private CompletableFuture<SigningResult> signFuture;

    private final ManagementKeyType type;

    private final byte[] managementKey;

    private final YubiKitManager yubiKitManager;

    private KeyGenReq keyGenReq;
    private SignatureReq sigReq;

    // Yubikey API supports signing only with the signature key slot.
    private KeyRef slot = KeyRef.SIG;

    private final Context c;

    public YubiKeyOpenPgpSscd(Context context) {
        this.managementKey = MANAGEMENT_KEY;
        this.type = TYPE;
        this.c = context;
        this.yubiKitManager = new YubiKitManager(this.c);
    }

    @Override
    public MusapKey bindKey(KeyBindReq req) throws Exception {
        // Bind an existing YubiKey keypair to MUSAP by signing with it
        // Get the public key, and verify that it matches
        return null;
    }

    @Override
    public MusapKey generateKey(KeyGenReq req) throws Exception {
        // Save request type
        this.keyGenReq = req;
        this.sigReq = null;
        this.keygenFuture = new CompletableFuture<>();

        showInsertPinDialog();

        KeyGenerationResult result = keygenFuture.get();
        if (result.key       != null) return result.key;
        if (result.exception != null) throw  result.exception;

        throw new MusapException("Keygen failed");
    }

    @Override
    public MusapSignature sign(SignatureReq req) throws Exception {

        this.signFuture = new CompletableFuture<>();
        this.sigReq = req;
        this.keyGenReq = null;
        this.showInsertPinDialog();

        SigningResult result = signFuture.get();
        if (result.signature != null) return result.signature;
        if (result.exception != null) throw  result.exception;

        throw new MusapException("Signing failed");
    }

    private Activity getActivity() {
        if (this.sigReq != null) return this.sigReq.getActivity();
        if (this.keyGenReq != null) return this.keyGenReq.getActivity();
        return null;
    }

    private void showInsertYubiKeyDialog(String pin, Activity activity) {

        // Dismiss old dialog if it it showing
        activity.runOnUiThread(() -> {
            if (currentPrompt != null) {
                currentPrompt.dismiss();
                currentPrompt.cancel();
            }
        });

        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_insert_yubikey, null);

        activity.runOnUiThread(() -> {
            currentPrompt = new AlertDialog.Builder(activity)
                    .setTitle("Insert YubiKey")
                    .setView(v)
                    .create();
            currentPrompt.show();
        });

        if (this.keyGenReq != null) {
            this.yubiKeyGen(pin, this.keyGenReq);
        } else {
            this.yubiSign(pin, this.sigReq);
        }
    }

    private void showInsertPinDialog() {
        MLog.d("Showing dialog");

        if (this.keyGenReq == null && this.sigReq == null) {
            MLog.d("Missing request");
            throw new IllegalArgumentException();
        }
        MLog.d("Building view");
        Activity activity = this.keyGenReq != null ? this.keyGenReq.getActivity() : this.sigReq.getActivity();
        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_pin, null);

        MLog.d("Running on UI thread. Activity=" + activity.getClass());
        activity.runOnUiThread(() -> {
            MLog.d("Showing prompt");
            this.currentPrompt = new AlertDialog.Builder(activity)
                    .setTitle("PIN")
                    .setView(v)
                    .setPositiveButton("OK", (dialogInterface, i) -> {
                        String pin = ((TextView) v.findViewById(R.id.dialog_pin_edittext)).getText().toString();
                        MLog.d("PIN=" + pin);

                        showInsertYubiKeyDialog(pin, activity);
                    })
                    .setNeutralButton("Cancel", (dialogInterface, i) ->  {
                        dialogInterface.cancel();
                    })
                    .create();

            currentPrompt.show();
        });

    }

    public void showRemoveYubiKeyDialog(Activity activity) {

        // Dismiss old dialog if it it showing
        activity.runOnUiThread(() -> {
            if (currentPrompt != null) {
                currentPrompt.dismiss();
                currentPrompt.cancel();
            }
        });

        View v = LayoutInflater.from(activity).inflate(R.layout.dialog_remove_yubikey, null);

        activity.runOnUiThread(() -> {
            currentPrompt = new AlertDialog.Builder(activity)
                    .setTitle("Remove YubiKey")
                    .setView(v)
                    .create();
            currentPrompt.show();
        });
    }

    public void showKeyGenFailedDualog(KeyGenReq req, Exception e) {

        // Dismiss old dialog if it it showing
        req.getActivity().runOnUiThread(() -> {
            if (currentPrompt != null) {
                currentPrompt.dismiss();
            }
        });

        Context c = req.getActivity();
        View v = LayoutInflater.from(c).inflate(R.layout.dialog_keygen_failed, null);
        TextView reason = v.findViewById(R.id.text_fail_reason);

        if (e == null) {
            reason.setVisibility(View.GONE);
        } else {
            reason.setText(e.getMessage());
        }

        req.getActivity().runOnUiThread(() -> {
            currentPrompt = new AlertDialog.Builder(c)
                    .setTitle("Key Generation Failed")
                    .setView(v)
                    .create();
            currentPrompt.show();
        });
    }

    public void showSignFailedDialog(SignatureReq req, Exception e) {

        // Dismiss old dialog if it it showing
        req.getActivity().runOnUiThread(() -> {
            if (currentPrompt != null) {
                currentPrompt.dismiss();
            }
        });

        Context c = req.getActivity();
        View v = LayoutInflater.from(c).inflate(R.layout.dialog_sign_failed, null);
        TextView reason = v.findViewById(R.id.text_sign_fail_reason);

        if (e == null) {
            reason.setVisibility(View.GONE);
        } else {
            reason.setText(e.getMessage());
        }

        req.getActivity().runOnUiThread(() -> {
            currentPrompt = new AlertDialog.Builder(c)
                    .setTitle("Signature Failed")
                    .setView(v)
                    .create();
            currentPrompt.show();
        });
    }


    private void yubiKeyGen(String pin, KeyGenReq req) {
        try {
            yubiKitManager.startNfcDiscovery(new NfcConfiguration(), req.getActivity(), device -> {
                MLog.d("Found NFC");
                connect(device, req, pin);
            });
        } catch (NfcNotAvailable e) {
            if (e.isDisabled()) {
                Toast.makeText(c, "NFC is not enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(c, "NFC is not available", Toast.LENGTH_SHORT).show();
            }
            yubiKitManager.stopNfcDiscovery(req.getActivity());
        }
    }

    private void yubiSign(String pin, SignatureReq req) {
        try {
            yubiKitManager.startNfcDiscovery(new NfcConfiguration(), req.getActivity(), device -> {
                MLog.d("Found NFC");
                connectForSign(device, req, pin);
            });
        } catch (NfcNotAvailable e) {
            if (e.isDisabled()) {
                Toast.makeText(c, "NFC is not enabled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(c, "NFC is not available", Toast.LENGTH_SHORT).show();
            }
            yubiKitManager.stopNfcDiscovery(req.getActivity());
        }
    }

    @Override
    public SscdInfo getSscdInfo() {
        return new SscdInfo.Builder()
                .setSscdName("Yubikey EdDSA")
                .setSscdType(SSCD_TYPE)
                .setCountry("FI")
                .setProvider("Yubico")
                .setKeygenSupported(true)
                .setSupportedAlgorithms(Arrays.asList(KeyAlgorithm.ECC_P256_K1, KeyAlgorithm.ECC_ED25519))
                .setSupportedFormats(Arrays.asList(SignatureFormat.RAW))
                .build();
    }

    @Override
    public boolean isKeygenSupported() {
        return true;
    }

    @Override
    public YubiKeySettings getSettings() {
        return settings;
    }

    private void connect(final NfcYubiKeyDevice device, final KeyGenReq req, String pin)  {

        device.requestConnection(SmartCardConnection.class, result -> {
            // The result is a Result<SmartCardConnection, IOException>, which represents either a successful connection, or an error.
            try {
                boolean success = result.isSuccess();
                MLog.d("Connection successful=" + success);

                // If the connection is not successful, try again
                if (!success) {
                    MLog.d("Failed to connect");
                    this.showKeyGenFailedDualog(req, null);
                } else {
                    MLog.d("PIN=" + pin);
                    keyGenOnDevice(req, pin, result.getValue());
                }
            } catch (Exception e) {
                MLog.e("Failed to connect", e);
                this.showKeyGenFailedDualog(req, e);
                yubiKitManager.stopNfcDiscovery(req.getActivity());
            }
        });
    }

    private void connectForSign(final NfcYubiKeyDevice device, final SignatureReq req, String pin)  {

        device.requestConnection(SmartCardConnection.class, result -> {
            // The result is a Result<SmartCardConnection, IOException>, which represents either a successful connection, or an error.
            try {
                boolean success = result.isSuccess();
                MLog.d("Connection successful=" + success);

                // If the connection is not successful, try again
                if (!success) {
                    MLog.d("Failed to connect");
                    this.showSignFailedDialog(req, null);
                    yubiKitManager.stopNfcDiscovery(req.getActivity());
                } else {
                    MLog.d("PIN=" + pin);
                    signOnDevice(pin, req, result.getValue());
                }
            } catch (Exception e) {
                MLog.e("Failed to connect", e);
                this.showSignFailedDialog(req, e);
                yubiKitManager.stopNfcDiscovery(req.getActivity());
            }
        });
    }

    private void keyGenOnDevice(KeyGenReq req, String pin, SmartCardConnection connection) throws Exception {
        OpenPgpSession openpgp = new OpenPgpSession(connection);
        MLog.d("Opened OpenPGP session");

        MLog.d("Device supports ECC=" + openpgp.supports(OpenPgpSession.FEATURE_EC_KEYS));

        openpgp.verifyAdminPin(DEFAULT_ADMIN_PIN);

        MLog.d("Trying to generate a key");

        OpenPgpCurve curve;
        if (req.getAlgorithm() == null || req.getAlgorithm().equals(KeyAlgorithm.ECC_ED25519)) {
            curve = OpenPgpCurve.Ed25519;
        } else  if (req.getAlgorithm().equals(KeyAlgorithm.ECC_P256_K1)) {
            curve = OpenPgpCurve.SECP256K1;
        } else {
            MLog.e("Unsupported algoritm " + req.getAlgorithm());
            throw new IllegalArgumentException("Unsupported algoritm " + req.getAlgorithm());
        }

        PublicKey publicKey = openpgp.generateEcKey(this.slot, curve).toPublicKey();
        openpgp.verifyUserPin(pin.toCharArray(), false);
        MLog.d("Generated KeyPair");

        X500Name name = new X500Name("CN=MUSAP Test");
        X509v3CertificateBuilder builder = new X509v3CertificateBuilder(
                name,
                new BigInteger("123456789"),
                new Date(),
                this.getNotAfter(),
                name,
                SubjectPublicKeyInfo.getInstance(ASN1Sequence.getInstance(publicKey.getEncoded()))
        );
        MLog.d("Built cert");

        byte[] certBytes = builder.build(new ContentSigner() {

            private ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            @Override
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                // TODO: Return correct algoritm identifier
                return new AlgorithmIdentifier(X9ObjectIdentifiers.ecdsa_with_SHA256);
            }

            @Override
            public OutputStream getOutputStream() {
                return this.buffer;
            }

            @Override
            public byte[] getSignature() {
                try {
                    // TODO: sign the proper payload
                    byte[] payload = "test".getBytes(StandardCharsets.UTF_8);
                    return openpgp.sign(payload);
                } catch (Exception e) {
                    MLog.e("Failed to init content signer", e);
                    return null;
                }
            }
        }).getEncoded();

        MLog.d("Encoded cert");
        X509Certificate builtCert = (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(certBytes));

        openpgp.putCertificate(this.slot, builtCert);
        MLog.d("Put certificate to slot");

        MusapCertificate cert = new MusapCertificate(builtCert);

        MusapKey.Builder keyBuilder = new MusapKey.Builder();
        keyBuilder.setCertificate(cert);
        keyBuilder.setKeyAlias(req.getKeyAlias());
        keyBuilder.addAttribute(ATTRIBUTE_SERIAL, this.serialToString(openpgp.getAid().getSerial()));
        keyBuilder.setSscdType(this.getSscdInfo().getSscdType());
        keyBuilder.setSscdId(this.getSscdInfo().getSscdId());
        keyBuilder.setLoa(Arrays.asList(MusapLoA.EIDAS_SUBSTANTIAL, MusapLoA.ISO_LOA3));
        keyBuilder.setKeyId(IdGenerator.generateKeyId());
        keyBuilder.setAlgorithm(req.getAlgorithm());

        this.keygenFuture.complete(new KeyGenerationResult(keyBuilder.build()));

        MLog.d("Finished keygen");
        showRemoveYubiKeyDialog(req.getActivity());
    }

    private void signOnDevice(String pin, SignatureReq req, SmartCardConnection connection) throws Exception {

        try {
            OpenPgpSession openpgp = new OpenPgpSession(connection);
            MLog.d("Opened OpenPGP session");
            MLog.d("Device supports ECC=" + openpgp.supports(OpenPgpSession.FEATURE_EC_KEYS));

            openpgp.verifyUserPin(pin.toCharArray(), false);

            int serial = openpgp.getAid().getSerial();
            if (!isSerialCorrect(serial, sigReq.getKey())) {
                MLog.e("Wrong serial key");
                throw new MusapException(MusapException.ERROR_WRONG_PARAM, "Wrong device. Expected serial " + this.serialToString(serial));
            }

            byte[] message = sigReq.getData();
            byte[] sigResult = openpgp.sign(message);

            // Dismiss old dialog if it it showing
            getActivity().runOnUiThread(() -> {
                if (currentPrompt != null) {
                    currentPrompt.dismiss();
                    currentPrompt.cancel();
                }
            });

            signFuture.complete(new SigningResult(new MusapSignature(sigResult, req.getKey(), req.getAlgorithm(), req.getFormat())));

        } catch (Exception e) {
            signFuture.complete(new SigningResult(new MusapException(e)));
            throw new MusapException(e);
        }
    }

    /**
     * Make a "not after" date for the generated certificate.
     * Returns a date 5 years from now.
     * @return Not after date.
     */
    private Date getNotAfter() {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, 5);
        return c.getTime();
    }

    private boolean isSerialCorrect(int deviceSerial, MusapKey key) {
        String received = this.serialToString(deviceSerial);
        String stored = key.getAttributeValue(ATTRIBUTE_SERIAL);

        if (key.getAttribute(ATTRIBUTE_SERIAL) == null) {
            MLog.d("No serial saved");
            return true;
        } else {
            MLog.d("Received serial=" + received);
            MLog.d("Stored serial  =" + stored);
            return stored.equalsIgnoreCase(received);
        }
    }

    private String serialToString(int serial) {
        return Integer.toString(serial);
    }

}
