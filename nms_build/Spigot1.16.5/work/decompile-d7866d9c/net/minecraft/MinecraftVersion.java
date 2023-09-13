package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.ChatDeserializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftVersion implements GameVersion {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final GameVersion a = new MinecraftVersion();
    private final String c;
    private final String d;
    private final boolean e;
    private final int f;
    private final int g;
    private final int h;
    private final Date i;
    private final String j;

    private MinecraftVersion() {
        this.c = UUID.randomUUID().toString().replaceAll("-", "");
        this.d = "1.16.5";
        this.e = true;
        this.f = 2586;
        this.g = SharedConstants.b();
        this.h = 6;
        this.i = new Date();
        this.j = "1.16.5";
    }

    private MinecraftVersion(JsonObject jsonobject) {
        this.c = ChatDeserializer.h(jsonobject, "id");
        this.d = ChatDeserializer.h(jsonobject, "name");
        this.j = ChatDeserializer.h(jsonobject, "release_target");
        this.e = ChatDeserializer.j(jsonobject, "stable");
        this.f = ChatDeserializer.n(jsonobject, "world_version");
        this.g = ChatDeserializer.n(jsonobject, "protocol_version");
        this.h = ChatDeserializer.n(jsonobject, "pack_version");
        this.i = Date.from(ZonedDateTime.parse(ChatDeserializer.h(jsonobject, "build_time")).toInstant());
    }

    public static GameVersion a() {
        try {
            InputStream inputstream = MinecraftVersion.class.getResourceAsStream("/version.json");
            Throwable throwable = null;

            GameVersion gameversion;

            try {
                if (inputstream != null) {
                    InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
                    Throwable throwable1 = null;

                    try {
                        Object object;

                        try {
                            object = new MinecraftVersion(ChatDeserializer.a((Reader) inputstreamreader));
                            return (GameVersion) object;
                        } catch (Throwable throwable2) {
                            object = throwable2;
                            throwable1 = throwable2;
                            throw throwable2;
                        }
                    } finally {
                        if (inputstreamreader != null) {
                            if (throwable1 != null) {
                                try {
                                    inputstreamreader.close();
                                } catch (Throwable throwable3) {
                                    throwable1.addSuppressed(throwable3);
                                }
                            } else {
                                inputstreamreader.close();
                            }
                        }

                    }
                }

                MinecraftVersion.LOGGER.warn("Missing version information!");
                gameversion = MinecraftVersion.a;
            } catch (Throwable throwable4) {
                throwable = throwable4;
                throw throwable4;
            } finally {
                if (inputstream != null) {
                    if (throwable != null) {
                        try {
                            inputstream.close();
                        } catch (Throwable throwable5) {
                            throwable.addSuppressed(throwable5);
                        }
                    } else {
                        inputstream.close();
                    }
                }

            }

            return gameversion;
        } catch (JsonParseException | IOException ioexception) {
            throw new IllegalStateException("Game version information is corrupt", ioexception);
        }
    }

    public String getId() {
        return this.c;
    }

    public String getName() {
        return this.d;
    }

    public String getReleaseTarget() {
        return this.j;
    }

    public int getWorldVersion() {
        return this.f;
    }

    public int getProtocolVersion() {
        return this.g;
    }

    public int getPackVersion() {
        return this.h;
    }

    public Date getBuildTime() {
        return this.i;
    }

    public boolean isStable() {
        return this.e;
    }
}
