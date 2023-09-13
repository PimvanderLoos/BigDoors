package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.OutputStream;
import org.slf4j.Logger;

public class DebugOutputStream extends RedirectStream {

    private static final Logger LOGGER = LogUtils.getLogger();

    public DebugOutputStream(String s, OutputStream outputstream) {
        super(s, outputstream);
    }

    @Override
    protected void logLine(String s) {
        StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
        StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length)];

        DebugOutputStream.LOGGER.info("[{}]@.({}:{}): {}", new Object[]{this.name, stacktraceelement.getFileName(), stacktraceelement.getLineNumber(), s});
    }
}
