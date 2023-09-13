package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.ChatDeserializer;
import org.slf4j.Logger;

public abstract class ResourcePackAbstract implements IResourcePack {

    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final boolean isBuiltin;

    protected ResourcePackAbstract(String s, boolean flag) {
        this.name = s;
        this.isBuiltin = flag;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(ResourcePackMetaParser<T> resourcepackmetaparser) throws IOException {
        IoSupplier<InputStream> iosupplier = this.getRootResource(new String[]{"pack.mcmeta"});

        if (iosupplier == null) {
            return null;
        } else {
            InputStream inputstream = (InputStream) iosupplier.get();

            Object object;

            try {
                object = getMetadataFromStream(resourcepackmetaparser, inputstream);
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

            return object;
        }
    }

    @Nullable
    public static <T> T getMetadataFromStream(ResourcePackMetaParser<T> resourcepackmetaparser, InputStream inputstream) {
        JsonObject jsonobject;

        try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

            try {
                jsonobject = ChatDeserializer.parse((Reader) bufferedreader);
            } catch (Throwable throwable) {
                try {
                    bufferedreader.close();
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }

                throw throwable;
            }

            bufferedreader.close();
        } catch (Exception exception) {
            ResourcePackAbstract.LOGGER.error("Couldn't load {} metadata", resourcepackmetaparser.getMetadataSectionName(), exception);
            return null;
        }

        if (!jsonobject.has(resourcepackmetaparser.getMetadataSectionName())) {
            return null;
        } else {
            try {
                return resourcepackmetaparser.fromJson(ChatDeserializer.getAsJsonObject(jsonobject, resourcepackmetaparser.getMetadataSectionName()));
            } catch (Exception exception1) {
                ResourcePackAbstract.LOGGER.error("Couldn't load {} metadata", resourcepackmetaparser.getMetadataSectionName(), exception1);
                return null;
            }
        }
    }

    @Override
    public String packId() {
        return this.name;
    }

    @Override
    public boolean isBuiltin() {
        return this.isBuiltin;
    }
}
