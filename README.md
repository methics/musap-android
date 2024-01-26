# MUSAP Android Library

MUSAP (Multiple SSCDs with Unified Signature API) is an Android library designed to simplify the integration of multiple Secure Signature Creation Devices (SSCD) with a unified signature API. 
It provides a set of tools and utilities to streamline the implementation of secure signature creation mechanisms in Android applications.

## Features
* **Multiple SSCD Integration**: MUSAP simplifies the integration of multiple Secure Signature Creation Devices into your Android application.
* **Unified Signature API**: Utilize a unified API for signature operations, abstracting the complexities of individual SSCD implementations.
* **Secure Signature Creation**: Implement secure and standardized methods for creating digital signatures within your application.
* **Customizable**: MUSAP is designed with flexibility in mind, allowing developers to customize and extend its functionality according to specific project requirements.

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
        MusapClient.enableSscd(new AndroidKeystoreSscd(this));
        MusapClient.enableSscd(new YubiKeySscd(this));
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

# Contributing to musap-android

We welcome contributions to musap-android! This document provides guidelines for contributing to the project.

## How to Contribute

1. *Fork the repository* - Start by forking the repository to your GitHub account.
2. *Clone your fork* - Clone your fork to your local machine for development.
3. *Create a new branch* - Create a branch in your local repository for your contribution.

## Submitting Changes

1. *Commit your changes* - Make your changes in your branch and commit them with a clear, descriptive message.
2. *Push to your fork* - Push your changes to your fork on GitHub.
3. *Create a Pull Request* - Submit a pull request from your fork to the main repository. Provide a clear description of your changes.

## Coding Guidelines

- Follow the existing coding style.
- Write tests for your changes.
- Ensure your code passes all tests.

## Reporting Issues

- Use the GitHub issue tracker to report bugs.
- Provide detailed information about the issue, including steps to reproduce it.

Thank you for contributing to musap-android!

# Security Policy for musap-android

## Reporting a Vulnerability

If you believe you have found a security vulnerability in musap-android, please follow these steps:

1. **Do not report security vulnerabilities through public GitHub issues.**
2. **Email the maintainers** - Send an email to methics.info@methics.fi detailing the vulnerability. Include steps to reproduce, if possible.
3. **Wait for response** - Allow the maintainers time to respond and assess the vulnerability.

## Security Patch Process

- The maintainers will confirm the receipt of your report.
- A security advisory will be created on GitHub to track the issue.
- A fix will be developed and tested in a private repository.
- Once the fix is ready, it will be released in a new version of the software.

## Disclosure Policy

- We believe in responsible disclosure of vulnerabilities.
- We will coordinate with you to determine an appropriate disclosure date.

## Commitment to Security

- We are committed to ensuring the security and privacy of our users.
- Regular audits and updates are conducted to maintain the security of the project.

Your efforts to responsibly disclose your findings are greatly appreciated and will be acknowledged.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.


