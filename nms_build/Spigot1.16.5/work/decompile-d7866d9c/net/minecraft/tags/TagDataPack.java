package net.minecraft.tags;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import net.minecraft.util.ChatDeserializer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TagDataPack<T> {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson b = new Gson();
    private static final int c = ".json".length();
    private final Function<MinecraftKey, Optional<T>> d;
    private final String e;
    private final String f;

    public TagDataPack(Function<MinecraftKey, Optional<T>> function, String s, String s1) {
        this.d = function;
        this.e = s;
        this.f = s1;
    }

    public CompletableFuture<Map<MinecraftKey, Tag.a>> a(IResourceManager iresourcemanager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<MinecraftKey, Tag.a> map = Maps.newHashMap();
            Iterator iterator = iresourcemanager.a(this.e, (s) -> {
                return s.endsWith(".json");
            }).iterator();

            while (iterator.hasNext()) {
                MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
                String s = minecraftkey.getKey();
                MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s.substring(this.e.length() + 1, s.length() - TagDataPack.c));

                try {
                    Iterator iterator1 = iresourcemanager.c(minecraftkey).iterator();

                    while (iterator1.hasNext()) {
                        IResource iresource = (IResource) iterator1.next();

                        try {
                            InputStream inputstream = iresource.b();
                            Throwable throwable = null;

                            try {
                                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                                Throwable throwable1 = null;

                                try {
                                    JsonObject jsonobject = (JsonObject) ChatDeserializer.a(TagDataPack.b, (Reader) bufferedreader, JsonObject.class);

                                    if (jsonobject == null) {
                                        TagDataPack.LOGGER.error("Couldn't load {} tag list {} from {} in data pack {} as it is empty or null", this.f, minecraftkey1, minecraftkey, iresource.d());
                                    } else {
                                        ((Tag.a) map.computeIfAbsent(minecraftkey1, (minecraftkey2) -> {
                                            return Tag.a.a();
                                        })).a(jsonobject, iresource.d());
                                    }
                                } catch (Throwable throwable2) {
                                    throwable1 = throwable2;
                                    throw throwable2;
                                } finally {
                                    if (bufferedreader != null) {
                                        if (throwable1 != null) {
                                            try {
                                                bufferedreader.close();
                                            } catch (Throwable throwable3) {
                                                throwable1.addSuppressed(throwable3);
                                            }
                                        } else {
                                            bufferedreader.close();
                                        }
                                    }

                                }
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
                        } catch (RuntimeException | IOException ioexception) {
                            TagDataPack.LOGGER.error("Couldn't read {} tag list {} from {} in data pack {}", this.f, minecraftkey1, minecraftkey, iresource.d(), ioexception);
                        } finally {
                            IOUtils.closeQuietly(iresource);
                        }
                    }
                } catch (IOException ioexception1) {
                    TagDataPack.LOGGER.error("Couldn't read {} tag list {} from {}", this.f, minecraftkey1, minecraftkey, ioexception1);
                }
            }

            return map;
        }, executor);
    }

    public Tags<T> a(Map<MinecraftKey, Tag.a> map) {
        Map<MinecraftKey, Tag<T>> map1 = Maps.newHashMap();
        Function<MinecraftKey, Tag<T>> function = map1::get;
        Function function1 = (minecraftkey) -> {
            return ((Optional) this.d.apply(minecraftkey)).orElse((Object) null);
        };

        while (!map.isEmpty()) {
            boolean flag = false;
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<MinecraftKey, Tag.a> entry = (Entry) iterator.next();
                Optional<Tag<T>> optional = ((Tag.a) entry.getValue()).a(function, function1);

                if (optional.isPresent()) {
                    map1.put(entry.getKey(), optional.get());
                    iterator.remove();
                    flag = true;
                }
            }

            if (!flag) {
                break;
            }
        }

        map.forEach((minecraftkey, tag_a) -> {
            TagDataPack.LOGGER.error("Couldn't load {} tag {} as it is missing following references: {}", this.f, minecraftkey, tag_a.b(function, function1).map(Objects::toString).collect(Collectors.joining(",")));
        });
        return Tags.a((Map) map1);
    }
}
