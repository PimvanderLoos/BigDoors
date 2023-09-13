package net.minecraft.server.packs.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.util.profiling.GameProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ResourceDataJson extends ResourceDataAbstract<Map<MinecraftKey, JsonElement>> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PATH_SUFFIX = ".json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    private final Gson gson;
    private final String directory;

    public ResourceDataJson(Gson gson, String s) {
        this.gson = gson;
        this.directory = s;
    }

    @Override
    protected Map<MinecraftKey, JsonElement> b(IResourceManager iresourcemanager, GameProfilerFiller gameprofilerfiller) {
        Map<MinecraftKey, JsonElement> map = Maps.newHashMap();
        int i = this.directory.length() + 1;
        Iterator iterator = iresourcemanager.a(this.directory, (s) -> {
            return s.endsWith(".json");
        }).iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s = minecraftkey.getKey();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s.substring(i, s.length() - ResourceDataJson.PATH_SUFFIX_LENGTH));

            try {
                IResource iresource = iresourcemanager.a(minecraftkey);

                try {
                    InputStream inputstream = iresource.b();

                    try {
                        BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

                        try {
                            JsonElement jsonelement = (JsonElement) ChatDeserializer.a(this.gson, (Reader) bufferedreader, JsonElement.class);

                            if (jsonelement != null) {
                                JsonElement jsonelement1 = (JsonElement) map.put(minecraftkey1, jsonelement);

                                if (jsonelement1 != null) {
                                    throw new IllegalStateException("Duplicate data file ignored with ID " + minecraftkey1);
                                }
                            } else {
                                ResourceDataJson.LOGGER.error("Couldn't load data file {} from {} as it's null or empty", minecraftkey1, minecraftkey);
                            }
                        } catch (Throwable throwable) {
                            try {
                                bufferedreader.close();
                            } catch (Throwable throwable1) {
                                throwable.addSuppressed(throwable1);
                            }

                            throw throwable;
                        }

                        bufferedreader.close();
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
                } catch (Throwable throwable4) {
                    if (iresource != null) {
                        try {
                            iresource.close();
                        } catch (Throwable throwable5) {
                            throwable4.addSuppressed(throwable5);
                        }
                    }

                    throw throwable4;
                }

                if (iresource != null) {
                    iresource.close();
                }
            } catch (IllegalArgumentException | IOException | JsonParseException jsonparseexception) {
                ResourceDataJson.LOGGER.error("Couldn't parse data file {} from {}", minecraftkey1, minecraftkey, jsonparseexception);
            }
        }

        return map;
    }
}
