package net.minecraft.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.minecraft.SharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EULA {

    private static final Logger LOGGER = LogManager.getLogger();
    private final Path file;
    private final boolean agreed;

    public EULA(Path path) {
        this.file = path;
        this.agreed = SharedConstants.IS_RUNNING_IN_IDE || this.b();
    }

    private boolean b() {
        try {
            InputStream inputstream = Files.newInputStream(this.file);

            boolean flag;

            try {
                Properties properties = new Properties();

                properties.load(inputstream);
                flag = Boolean.parseBoolean(properties.getProperty("eula", "false"));
            } catch (Throwable throwable) {
                if (inputstream != null) {
                    try {
                        inputstream.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                }

                throw throwable;
            }

            if (inputstream != null) {
                inputstream.close();
            }

            return flag;
        } catch (Exception exception) {
            EULA.LOGGER.warn("Failed to load {}", this.file);
            this.c();
            return false;
        }
    }

    public boolean a() {
        return this.agreed;
    }

    private void c() {
        if (!SharedConstants.IS_RUNNING_IN_IDE) {
            try {
                OutputStream outputstream = Files.newOutputStream(this.file);

                try {
                    Properties properties = new Properties();

                    properties.setProperty("eula", "false");
                    properties.store(outputstream, "By changing the setting below to TRUE you are indicating your agreement to our EULA (https://account.mojang.com/documents/minecraft_eula).");
                } catch (Throwable throwable) {
                    if (outputstream != null) {
                        try {
                            outputstream.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    }

                    throw throwable;
                }

                if (outputstream != null) {
                    outputstream.close();
                }
            } catch (Exception exception) {
                EULA.LOGGER.warn("Failed to save {}", this.file, exception);
            }

        }
    }
}
