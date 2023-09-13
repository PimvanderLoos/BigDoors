package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Objects;
import org.slf4j.Logger;

public interface Signer {

    Logger LOGGER = LogUtils.getLogger();

    byte[] sign(SignatureUpdater signatureupdater);

    default byte[] sign(byte[] abyte) {
        return this.sign((signatureupdater_a) -> {
            signatureupdater_a.update(abyte);
        });
    }

    static Signer from(PrivateKey privatekey, String s) {
        return (signatureupdater) -> {
            try {
                Signature signature = Signature.getInstance(s);

                signature.initSign(privatekey);
                Objects.requireNonNull(signature);
                signatureupdater.update(signature::update);
                return signature.sign();
            } catch (Exception exception) {
                throw new IllegalStateException("Failed to sign message", exception);
            }
        };
    }
}
