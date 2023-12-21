package fi.methics.musap.sdk.api;

import android.content.Context;

import com.google.gson.JsonSyntaxException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.lang.ref.WeakReference;
import java.security.Security;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import fi.methics.musap.sdk.internal.async.BindKeyTask;
import fi.methics.musap.sdk.internal.async.EnrollDataTask;
import fi.methics.musap.sdk.internal.async.GenerateKeyTask;
import fi.methics.musap.sdk.internal.async.CoupleTask;
import fi.methics.musap.sdk.internal.async.PollTask;
import fi.methics.musap.sdk.internal.async.SignTask;
import fi.methics.musap.sdk.internal.async.SignatureCallbackTask;
import fi.methics.musap.sdk.internal.datatype.coupling.PollResponsePayload;
import fi.methics.musap.sdk.internal.datatype.RelyingParty;
import fi.methics.musap.sdk.internal.discovery.KeySearchReq;
import fi.methics.musap.sdk.internal.discovery.MusapImportData;
import fi.methics.musap.sdk.internal.discovery.SscdSearchReq;
import fi.methics.musap.sdk.extension.MusapSscdInterface;
import fi.methics.musap.sdk.internal.discovery.KeyBindReq;
import fi.methics.musap.sdk.internal.discovery.KeyDiscoveryAPI;
import fi.methics.musap.sdk.internal.discovery.MetadataStorage;
import fi.methics.musap.sdk.internal.keygeneration.KeyGenReq;
import fi.methics.musap.sdk.internal.datatype.KeyURI;
import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.MusapSscd;
import fi.methics.musap.sdk.internal.datatype.MusapLink;
import fi.methics.musap.sdk.internal.datatype.MusapSignature;
import fi.methics.musap.sdk.internal.keygeneration.UpdateKeyReq;
import fi.methics.musap.sdk.internal.sign.SignatureReq;
import fi.methics.musap.sdk.internal.util.MLog;
import fi.methics.musap.sdk.internal.util.MusapStorage;

public class MusapClient {

    private static WeakReference<Context> context;
    private static KeyDiscoveryAPI keyDiscovery;
    private static MetadataStorage storage;
    private static ExecutorService executor;

    /**
     * Limit UI related tasks to 1 to avoid concurrency problems.
     */
//    private static Semaphore uiSemaphore = new Semaphore(1);

    public static void init(Context c) {
        Security.removeProvider("BC");
        MLog.d("Remove provider");
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
        MLog.d("Insert provider");

        context      = new WeakReference<>(c);
        keyDiscovery = new KeyDiscoveryAPI(c);
        storage      = new MetadataStorage(c);
        executor     = new ThreadPoolExecutor(2, 20, 5000, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>(5));
    }

    /**
     * Generate a keypair and store the key metadata to MUSAP
     * @param sscd SSCD to generate the key with
     * @param req  Key Generation Request
     * @param callback Callback that will deliver success or failure
     */
    public static void generateKey(MusapSscdInterface sscd, KeyGenReq req, MusapCallback<MusapKey> callback) {
        new GenerateKeyTask(callback, context.get(), sscd, req).executeOnExecutor(executor);
    }

    /**
     * Bind a keypair and store the key metadata to MUSAP
     * @param sscd SSCD to bind the key with
     * @param req  Key Bind Request
     * @param callback Callback that will deliver success or failure
     */
    public static void bindKey(MusapSscdInterface sscd, KeyBindReq req, MusapCallback<MusapKey> callback) {
        new BindKeyTask(callback, context.get(), sscd, req).executeOnExecutor(executor);
    }

    /**
     * Sign data with given SSCD.
     * Note! MUSAP allows only one active signing operation at a time. If you try to start another
     * signature while one is in progress, MUSAP ignores the second signature.
     *
     * @param req  Request containing the data to sign
     * @param callback Callback that will deliver success or failure
     */
    public static void sign(SignatureReq req, MusapCallback<MusapSignature> callback) {

        // TODO: Some way to limit concurrent operations.
//        if (!uiSemaphore.tryAcquire()) {
//            MLog.d("UI operation is in progress, not starting a new one");
//            return;
//        }
        new SignTask(callback, context.get(), null, req).executeOnExecutor(executor);
    }


    /**
     * List SSCDs supported by this MUSAP library. To add an SSCD to this list, call {@link #enableSscd(MusapSscdInterface)} first.
     * @return List of SSCDs that can be used to generate or bind keys
     */
    public static List<MusapSscdInterface> listEnabledSscds() {
        return keyDiscovery.listEnabledSscds();
    }

    /**
     * List SSCDs supported by this MUSAP library. To add an SSCD to this list, call {@link #enableSscd(MusapSscdInterface)} first.
     * @param req Search request that filters the output
     * @return List of SSCDs that can be used to generate or bind keys
     */
    public static List<MusapSscdInterface> listEnabledSscds(SscdSearchReq req) {
        if (req == null) return Collections.emptyList();
        return listEnabledSscds().stream().filter(sscd -> req.matches(sscd.getSscdInfo())).collect(Collectors.toList());
    }

    /**
     * List active SSCDs that have user keys generated or bound
     * @return List of active SSCDs
     */
    public static List<MusapSscd> listActiveSscds() {
        return storage.listActiveSscds();
    }

    /**
     * List active SSCDs that have user keys generated or bound
     * @return List of active SSCDs
     */
    public static List<MusapSscd> listActiveSscds(SscdSearchReq req) {
        if (req == null) return Collections.emptyList();
        return listActiveSscds().stream().filter(sscd -> req.matches(sscd)).collect(Collectors.toList());
    }

    /**
     * List all available keys
     * @return List of keys
     */
    public static List<MusapKey> listKeys() {
        MetadataStorage storage = new MetadataStorage(context.get());
        List<MusapKey> keys = storage.listKeys();
        MLog.d("Found " + keys.size() + " keys");
        return keys;
    }

    /**
     * List all keys that match the given search paramters
     * @param req Search request that filters the output
     * @return matching keys
     */
    public static List<MusapKey> listKeys(KeySearchReq req) {
        MetadataStorage storage = new MetadataStorage(context.get());
        List<MusapKey> keys = storage.listKeys(req);
        MLog.d("Found " + keys.size() + " keys");
        return keys;
    }

    public static boolean updateKey(UpdateKeyReq req) {
        MetadataStorage storage = new MetadataStorage(context.get());
        return storage.updateKeyMetaData(req);
    }

    /**
     * Enable an SSCD. This needs to be called for each SSCD that the application using MUSAP wants
     * to support. These will be searchable with {@link #listEnabledSscds()}}.
     * @param sscd SSCD to enable
     */
    public static void enableSscd(MusapSscdInterface sscd) {
        keyDiscovery.enableSscd(sscd);
    }

    /**
     * Get a key by KeyURI
     * @param keyUri KeyURI as String
     * @return Key or null if none found
     */
    public static MusapKey getKeyByUri(String keyUri) {
        MLog.d("Searching for key with KeyURI " + keyUri);
        MetadataStorage storage = new MetadataStorage(context.get());
        for (MusapKey key : storage.listKeys()) {
            if (key.getKeyUri().matches(new KeyURI(keyUri))) {
                MLog.d("Found key " + key.getKeyAlias());
                return key;
            }
        }
        MLog.d("Found no key!");
        return null;
    }

    /**
     * Get a key by KeyURI
     * @param keyUri KeyURI as {@link KeyURI} object
     * @return Key or null if none found
     */
    public static MusapKey getKeyByUri(KeyURI keyUri) {
        MetadataStorage storage = new MetadataStorage(context.get());
        for (MusapKey key : storage.listKeys()) {
            if (key.getKeyUri().matches(keyUri)) {
                return key;
            }
        }
        return null;
    }

    /**
     * Import MUSAP key data and SSCD details
     * @param data JSON data from another MUSA
     * @throws JsonSyntaxException if data is not parseable
     */
    public static void importData(String data) throws JsonSyntaxException {
        MusapImportData importData = MusapImportData.fromJson(data);
        MetadataStorage storage = new MetadataStorage(context.get());
        storage.addImportData(importData);
    }

    /**
     * Export MUSAP key data and SSCD details
     * @return JSON export that can be imported in another MUSAP
     */
    public static String exportData() {
        MetadataStorage storage = new MetadataStorage(context.get());
        return storage.getImportData().toJson();
    }

    /**
     * Remove a key from MUSAP.
     * @param key key to remove
     * @return true if key was found and removed
     */
    public static boolean removeKey(MusapKey key) {
        return keyDiscovery.removeKey(key);
    }

    /**
     * Remove an active SSCD from MUSAP.
     * @param sscd SSCD to remove
     */
    public static void removeSscd(MusapSscd sscd) {
        // TODO
    }


    /**
     * List enrolled Relying Parties
     * @return Relying Parties
     */
    public static List<RelyingParty> listRelyingParties() {
        return new MusapStorage(context.get()).listRelyingParties();
    }

    /**
     * Remove a previously linked Relying Party from this MUSAP app.
     * @param rp
     * @return True if removal was successful.
     */
    public static boolean removeRelyingParty(RelyingParty rp) {
        return new MusapStorage(context.get()).removeRelyingParty(rp);
    }

    /**
     * Enable a MUSAP Link connection.
     * Enabling allows the MUSAP Link to securely request signatures from this MUSAP.
     *
     * <p><b>Note: Only one connection can be active at a time.</b></p>
     * @param url URL of the MUSAP link service
     */
    public static void enableLink(String url, String fcmToken, MusapCallback<MusapLink> callback) {
        MusapLink link = new MusapLink(url, null);
        new EnrollDataTask(link, fcmToken, callback, context.get()).executeOnExecutor(executor);
    }

    /**
     * Disable the MUSAP Link connection
     */
    public static void disableLink() {
        new MusapStorage(context.get()).removeLink();
    }

    /**
     * Request coupling with an RP.
     * This requires a coupling code which can be retrieved by the web service via the MUSAP Link API.
     * @param couplingCode Coupling code entered by the user.
     * @param callback     Callback that delivers a RelyingParty object if the linking succeeded
     */
    public static void coupleWithRelyingParty(String couplingCode, MusapCallback<RelyingParty> callback) {
        String musapId = getMusapId();
        MusapLink link = getMusapLink();
        new CoupleTask(link, couplingCode, musapId, callback, context.get()).executeOnExecutor(executor);
    }

    /**
     * Send a SignatureCallback to MUSAP Link
     * @param signature Signature Callback
     * @param txnId     Transaction ID
     */
    public static void sendSignatureCallback(MusapSignature signature, String txnId) {
        MusapLink link = getMusapLink();
        if (link != null) {
            String musapId = getMusapId();
            link.setMusapId(musapId);
            new SignatureCallbackTask(link, signature, txnId,null, context.get()).executeOnExecutor(executor);
        }
    }

    /**
     * Check if MUSAP Link has been enabled
     * @return true if enabled
     */
    public static boolean isLinkEnabled() {
        return getMusapId() != null;
    }

    /**
     * Poll MUSAP Link for an incoming signature request. This should be called periodically and/or
     * when a notification wakes up the application.
     * Calls the callback when when signature is received, or polling failed.
     */
    public static void pollLink(String url, MusapCallback<PollResponsePayload> callback) {
        new PollTask(getMusapLink(), callback, context.get()).executeOnExecutor(executor);
    }

    /**
     * Enable or disable MUSAP debug logging.
     * This only takes effect for lifetime of this app instance.
     * Default value is true. This should be set off for production.
     * @param isDebug
     */
    public static void setDebugLog(boolean isDebug) {
        MLog.setDebugEnabled(isDebug);
    }

    /**
     * Get MUSAP ID given by a MUSAP Link service.
     * @return MUSAP ID. Null if MUSAP Link has not been enrolled.
     */
    public static String getMusapId() {
        return new MusapStorage(context.get()).getMusapId();
    }

    /**
     * Get the MUSAP Link properties
     * @return MUSAP Link or null if not enabled yet
     */
    public static MusapLink getMusapLink() {
        return new MusapStorage(context.get()).getMusapLink();
    }

}
