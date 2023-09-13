package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.PackType;
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
    public static final GameVersion BUILT_IN = new MinecraftVersion();
    private final String id;
    private final String name;
    private final boolean stable;
    private final int worldVersion;
    private final int protocolVersion;
    private final int resourcePackVersion;
    private final int dataPackVersion;
    private final Date buildTime;
    private final String releaseTarget;

    private MinecraftVersion() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "1.17.1";
        this.stable = true;
        this.worldVersion = 2730;
        this.protocolVersion = SharedConstants.c();
        this.resourcePackVersion = 7;
        this.dataPackVersion = 7;
        this.buildTime = new Date();
        this.releaseTarget = "1.17.1";
    }

    private MinecraftVersion(JsonObject jsonobject) {
        this.id = ChatDeserializer.h(jsonobject, "id");
        this.name = ChatDeserializer.h(jsonobject, "name");
        this.releaseTarget = ChatDeserializer.h(jsonobject, "release_target");
        this.stable = ChatDeserializer.j(jsonobject, "stable");
        this.worldVersion = ChatDeserializer.n(jsonobject, "world_version");
        this.protocolVersion = ChatDeserializer.n(jsonobject, "protocol_version");
        JsonObject jsonobject1 = ChatDeserializer.t(jsonobject, "pack_version");

        this.resourcePackVersion = ChatDeserializer.n(jsonobject1, "resource");
        this.dataPackVersion = ChatDeserializer.n(jsonobject1, "data");
        this.buildTime = Date.from(ZonedDateTime.parse(ChatDeserializer.h(jsonobject, "build_time")).toInstant());
    }

    public static GameVersion a() {
        try {
            InputStream inputstream = MinecraftVersion.class.getResourceAsStream("/version.json");

            MinecraftVersion minecraftversion;
            label64:
            {
                GameVersion gameversion;

                try {
                    if (inputstream != null) {
                        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);

                        try {
                            minecraftversion = new MinecraftVersion(ChatDeserializer.a((Reader) inputstreamreader));
                        } catch (Throwable throwable) {
                            try {
                                inputstreamreader.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }

                            throw throwable;
                        }

                        inputstreamreader.close();
                        break label64;
                    }

                    MinecraftVersion.LOGGER.warn("Missing version information!");
                    gameversion = MinecraftVersion.BUILT_IN;
                } catch (Throwable throwable2) {
                    if (inputstream != null) {
                        try {
                            inputstream.close();
                        } catch (Throwable throwable3) {
                            throwable2.addSuppressed(throwable3);
                        }
                    }

                    throw throwable2;
                }

                if (inputstream != null) {
                    inputstream.close();
                }

                return gameversion;
            }

            if (inputstream != null) {
                inputstream.close();
            }

            return minecraftversion;
        } catch (JsonParseException | IOException ioexception) {
            throw new IllegalStateException("Game version information is corrupt", ioexception);
        }
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getReleaseTarget() {
        return this.releaseTarget;
    }

    public int getWorldVersion() {
        return this.worldVersion;
    }

    public int getProtocolVersion() {
        return this.protocolVersion;
    }

    public int getPackVersion(PackType packtype) {
        return packtype == PackType.DATA ? this.dataPackVersion : this.resourcePackVersion;
    }

    public Date getBuildTime() {
        return this.buildTime;
    }

    public boolean isStable() {
        return this.stable;
    }
}
