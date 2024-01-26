# MUSAP Android Library

MUSAP (Multiple SSCDs with Unified Signature API) is an Android library designed to simplify the integration of multiple Secure Signature Creation Devices (SSCD) with a unified signature API. 
It provides a set of tools and utilities to streamline the implementation of secure signature creation mechanisms in Android applications.

## Features
* **Multiple SSCD Integration**: MUSAP simplifies the integration of multiple Secure Signature Creation Devices into your Android application.
* **Unified Signature API**: Utilize a unified API for signature operations, abstracting the complexities of individual SSCD implementations.
* **Secure Signature Creation**: Implement secure and standardized methods for creating digital signatures within your application.
* **Customizable**: MUSAP is designed with flexibility in mind, allowing developers to customize and extend its functionality according to specific project requirements.

### Reference implementation app

We have a reference implementation app available that serves as an example on how to use the library. You can find the app project [here](https://github.com/methics/musap-demo-android).

## Installing

To integrate MUSAP into your Android project, follow these steps:

1. Add the following dependency to your app's build.gradle file:

```gradle
    implementation (files("libs/musap-[version].aar"))
```

2. Add the following dependencies required by the MUSAP library:
```gradle
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation ("org.slf4j:slf4j-api:2.0.7")
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.71")
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging")
```

## Usage

### Enabling an SSCD

Call `MusapClient.init()` and `MusapClient.enableSscd()`

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MusapClient.init(this);
        MusapClient.enableSscd(new AndroidKeystoreSscd(this), "ANDROID");
        MusapClient.enableSscd(new YubiKeySscd(this), "YUBIKEY");
    }
}

```

### Generating a key

Create a key generation request and call `MusapClient.generateKey()`. The key generation result is delivered asynchronously through the given callback.

```java
KeyGenReq req = new KeyGenReqBuilder()
        .setActivity(this.getActivity())
        .setView(this.getView())
        .setAlias("my key")
        .setKeyAlgorithm(KeyAlgorithm.RSA_2K)
        .createKeyGenReq();

MusapClient.generateKey(sscd, req, new MusapCallback<MusapKey>() {
    @Override
    public void onSuccess(MusapKey result) {
        MLog.d("Successfully generated key " + alias);
    }

    @Override
    public void onException(MusapException e) {
        MLog.e("Failed to generate key " + alias, e);
    }
});

```

### Signing

Select a key, create a signature request and a `MusapSigner`. Finally call `MusapSigner.sign()`. The signature result is delivered asynchronously through the given callback.

```java
MusapKey       key = MusapClient.getKeyByUri(keyuri);
SignatureReq   req = new SignatureReqBuilder().setKey(key).setData(data).setActivity(this.getActivity()).createSignatureReq();
MusapSigner signer = new MusapSigner(key, this.getActivity());

try {
    signer.sign(data, new MusapCallback<MusapSignature>() {
        @Override
        public void onSuccess(MusapSignature mSig) {
            String signatureStr = mSig.getB64Signature();
            MLog.d("Signature successful: " + signatureStr);
        }

        @Override
        public void onException(MusapException e) {
            MLog.e("Failed to sign", e.getCause());
        }
    });
} catch (MusapException e) {
    MLog.e("Failed to sign", e.getCause());
}

```

### Binding Keys

Select a key, create a signature request and a `MusapSigner`. Finally call `MusapSigner.sign()`. The signature result is delivered asynchronously through the given callback.

```java
KeyBindReq req = new KeyBindReq.Builder()
        .setActivity(this.getActivity())
        .setView(this.getView())
        .setRole("personal")
        .setKeyAlias(alias)
        .createKeyBindReq();
MusapSscd sscd = this.listActiveSscds().get(0);
try {
    MusapClient.bindKey.sign(sscd, req, new MusapCallback<MusapSignature>() {
        @Override
        public void onSuccess(MusapKey key) {
            MLog.d("Bind succeeded");
        }

        @Override
        public void onException(MusapException e) {
            MLog.e("Failed to bind", e.getCause());
        }
    });
} catch (MusapException e) {
    MLog.e("Failed to bind", e.getCause());
}

```

### Listing Keys

Select a key, create a signature request and a `MusapSigner`. Finally call `MusapSigner.sign()`. The signature result is delivered asynchronously through the given callback.

```java
List<MusapKey> keys = MusapClient.listKeys();

for (MusapKey key : keys) {
    // get your data
}
```

### Get enabled SSCDs

Get list of SSCDs that have been enabled MusapClient.enableSscd().
```java

List<MusapSscd> enabledSscds = MusapClient.listEnabledSscds();

for (MusapSscd sscd : enabledSscds) {
    // get your data
}
```


### Get active SSCDs

Get a list of SSCDs that have active keys.

```java

List<MusapSscd> activeSscds = MusapClient.listActiveSscds();

for (MusapSscd sscd : activeSscds) {
    // get your data
}
```

## Architecture

### MUSAP Library

![musap-lib-overview](https://github.com/methics/musap-android/assets/4453264/48bb375b-d651-42ad-b94c-7f794c1ef330)

### MUSAP Link

![link-library-architecture](https://github.com/methics/musap-android/assets/4453264/66119517-1fbe-4829-9e5b-c50ca9643dde)

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.


