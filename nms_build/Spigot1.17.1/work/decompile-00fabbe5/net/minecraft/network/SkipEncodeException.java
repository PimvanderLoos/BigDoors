package net.minecraft.network;

import io.netty.handler.codec.EncoderException;

public class SkipEncodeException extends EncoderException {

    public SkipEncodeException(Throwable throwable) {
        super(throwable);
    }
}
