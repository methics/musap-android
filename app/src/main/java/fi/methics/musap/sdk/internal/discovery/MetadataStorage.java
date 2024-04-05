package fi.methics.musap.sdk.internal.discovery;

import java.util.List;

import fi.methics.musap.sdk.internal.datatype.MusapKey;
import fi.methics.musap.sdk.internal.datatype.SscdInfo;

public interface MetadataStorage {

    boolean addKey(MusapKey key, SscdInfo sscd);

    List<MusapKey> listKeys();

    List<MusapKey> listKeys(KeySearchReq req);

    boolean removeKey(MusapKey key);

    boolean addSscd(SscdInfo sscd);

    List<SscdInfo> listActiveSscds();
}
