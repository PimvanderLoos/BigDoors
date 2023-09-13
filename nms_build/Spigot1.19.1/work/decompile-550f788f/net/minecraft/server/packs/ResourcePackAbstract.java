package net.minecraft.server.packs;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.util.ChatDeserializer;
import org.slf4j.Logger;

public abstract class ResourcePackAbstract implements IResourcePack {

    private static final Logger LOGGER = LogUtils.getLogger();
    protected final File file;

    public ResourcePackAbstract(File file) {
        this.file = file;
    }

    private static String getPathFromLocation(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return String.format(Locale.ROOT, "%s/%s/%s", enumresourcepacktype.getDirectory(), minecraftkey.getNamespace(), minecraftkey.getPath());
    }

    protected static String getRelativePath(File file, File file1) {
        return file.toURI().relativize(file1.toURI()).getPath();
    }

    @Override
    public InputStream getResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) throws IOException {
        return this.getResource(getPathFromLocation(enumresourcepacktype, minecraftkey));
    }

    @Override
    public boolean hasResource(EnumResourcePackType enumresourcepacktype, MinecraftKey minecraftkey) {
        return this.hasResource(getPathFromLocation(enumresourcepacktype, minecraftkey));
    }

    protected abstract InputStream getResource(String s) throws IOException;

    @Override
    public InputStream getRootResource(String s) throws IOException {
        if (!s.contains("/") && !s.contains("\\")) {
            return this.getResource(s);
        } else {
            throw new IllegalArgumentException("Root resources can only be filenames, not paths (no / allowed!)");
        }
    }

    protected abstract boolean hasResource(String s);

    protected void logWarning(String s) {
        ResourcePackAbstract.LOGGER.warn("ResourcePack: ignored non-lowercase namespace: {} in {}", s, this.file);
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(ResourcePackMetaParser<T> resourcepackmetaparser) throws IOException {
        InputStream inputstream = this.getResource("pack.mcmeta");

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
    public String getName() {
        return this.file.getName();
    }
}
