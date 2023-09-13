package net.minecraft.server;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedirectStream extends PrintStream {

    protected static final Logger a = LogManager.getLogger();
    protected final String b;

    public RedirectStream(String s, OutputStream outputstream) {
        super(outputstream);
        this.b = s;
    }

    public void println(String s) {
        this.a(s);
    }

    public void println(Object object) {
        this.a(String.valueOf(object));
    }

    protected void a(String s) {
        RedirectStream.a.info("[{}]: {}", this.b, s);
    }
}
