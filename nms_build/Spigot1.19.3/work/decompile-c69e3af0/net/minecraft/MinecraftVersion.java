package net.minecraft;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.bridge.game.PackType;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.storage.DataVersion;
import org.slf4j.Logger;

public class MinecraftVersion implements WorldVersion {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final WorldVersion BUILT_IN = new MinecraftVersion();
    private final String id;
    private final String name;
    private final boolean stable;
    private final DataVersion worldVersion;
    private final int protocolVersion;
    private final int resourcePackVersion;
    private final int dataPackVersion;
    private final Date buildTime;

    private MinecraftVersion() {
        this.id = UUID.randomUUID().toString().replaceAll("-", "");
        this.name = "1.19.3";
        this.stable = true;
        this.worldVersion = new DataVersion(3218, "main");
        this.protocolVersion = SharedConstants.getProtocolVersion();
        this.resourcePackVersion = 12;
        this.dataPackVersion = 10;
        this.buildTime = new Date();
    }

    private MinecraftVersion(JsonObject jsonobject) {
        this.id = ChatDeserializer.getAsString(jsonobject, "id");
        this.name = ChatDeserializer.getAsString(jsonobject, "name");
        this.stable = ChatDeserializer.getAsBoolean(jsonobject, "stable");
        this.worldVersion = new DataVersion(ChatDeserializer.getAsInt(jsonobject, "world_version"), ChatDeserializer.getAsString(jsonobject, "series_id", DataVersion.MAIN_SERIES));
        this.protocolVersion = ChatDeserializer.getAsInt(jsonobject, "protocol_version");
        JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "pack_version");

        this.resourcePackVersion = ChatDeserializer.getAsInt(jsonobject1, "resource");
        this.dataPackVersion = ChatDeserializer.getAsInt(jsonobject1, "data");
        this.buildTime = Date.from(ZonedDateTime.parse(ChatDeserializer.getAsString(jsonobject, "build_time")).toInstant());
    }

    public static WorldVersion tryDetectVersion() {
        try {
            InputStream inputstream = MinecraftVersion.class.getResourceAsStream("/version.json");

            MinecraftVersion minecraftversion;
            label64:
            {
                WorldVersion worldversion;

                try {
                    if (inputstream != null) {
                        InputStreamReader inputstreamreader = new InputStreamReader(inputstream);

                        try {
                            minecraftversion = new MinecraftVersion(ChatDeserializer.parse((Reader) inputstreamreader));
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
                    worldversion = MinecraftVersion.BUILT_IN;
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

                return worldversion;
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

    @Override
    public DataVersion getDataVersion() {
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
