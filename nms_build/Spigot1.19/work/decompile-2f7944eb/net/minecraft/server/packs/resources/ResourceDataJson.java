package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.profiling.GameProfilerFiller;
import org.slf4j.Logger;

public abstract class ResourceDataJson extends ResourceDataAbstract<Map<MinecraftKey, JsonElement>> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PATH_SUFFIX = ".json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String directory;

    public ResourceDataJson(Gson gson, String s) {
        this.gson = gson;
        this.directory = s;
    }

    @Override
    protected Map<MinecraftKey, JsonElement> prepare(IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Map<MinecraftKey, JsonElement> map = Maps.newHashMap();
        int i = this.directory.length() + 1;
        Iterator iterator = iresourcemanager.listResources(this.directory, (minecraftkey) -> {
            return minecraftkey.getPath().endsWith(".json");
        }).entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, IResource> entry = (Entry) iterator.next();
            MinecraftKey minecraftkey = (MinecraftKey) entry.getKey();
            String s = minecraftkey.getPath();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s.substring(i, s.length() - ResourceDataJson.PATH_SUFFIX_LENGTH));

            try {
                BufferedReader bufferedreader = ((IResource) entry.getValue()).openAsReader();

                try {
                    JsonElement jsonelement = (JsonElement) ChatDeserializer.fromJson(this.gson, (Reader) bufferedreader, JsonElement.class);

                    if (jsonelement != null) {
                        JsonElement jsonelement1 = (JsonElement) map.put(minecraftkey1, jsonelement);

                        if (jsonelement1 != null) {
                            throw new IllegalStateException("Duplicate data file ignored with ID " + minecraftkey1);
                        }
                    } else {
                        ResourceDataJson.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", minecraftkey1, minecraftkey);
                    }
                } catch (Throwable throwable) {
                    if (bufferedreader != null) {
                        try {
                            bufferedreader.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    }

                    throw throwable;
                }

                if (bufferedreader != null) {
                    bufferedreader.close();
                }
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                ResourceDataJson.LOGGER.error("Couldn't parse data file {} from {}", new Object[]{minecraftkey1, minecraftkey, jsonparseexception});
            }
        }

        return map;
    }
}
