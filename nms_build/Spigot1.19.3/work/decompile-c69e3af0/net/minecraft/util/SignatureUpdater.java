package net.minecraft.util;

import java.security.SignatureException;

@FunctionalInterface
public interface SignatureUpdater {

    void update(SignatureUpdater.a signatureupdater_a) throws SignatureException;

    @FunctionalInterface
    public interface a {

        void update(byte[] abyte) throws SignatureException;
    }
}
