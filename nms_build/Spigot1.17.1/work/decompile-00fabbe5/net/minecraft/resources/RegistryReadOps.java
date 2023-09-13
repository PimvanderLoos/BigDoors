package net.minecraft.resources;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.SystemUtils;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.core.IRegistryWritable;
import net.minecraft.core.RegistryMaterials;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryReadOps<T> extends DynamicOpsWrapper<T> {

    static final Logger LOGGER = LogManager.getLogger();
    private static final String JSON = ".json";
    private final RegistryReadOps.b resources;
    private final IRegistryCustom registryAccess;
    private final Map<ResourceKey<? extends IRegistry<?>>, RegistryReadOps.a<?>> readCache;
    private final RegistryReadOps<JsonElement> jsonOps;

    public static <T> RegistryReadOps<T> a(DynamicOps<T> dynamicops, IResourceManager iresourcemanager, IRegistryCustom iregistrycustom) {
        return a(dynamicops, RegistryReadOps.b.a(iresourcemanager), iregistrycustom);
    }

    public static <T> RegistryReadOps<T> a(DynamicOps<T> dynamicops, RegistryReadOps.b registryreadops_b, IRegistryCustom iregistrycustom) {
        RegistryReadOps<T> registryreadops = new RegistryReadOps<>(dynamicops, registryreadops_b, iregistrycustom, Maps.newIdentityHashMap());

        IRegistryCustom.a(iregistrycustom, registryreadops);
        return registryreadops;
    }

    public static <T> RegistryReadOps<T> b(DynamicOps<T> dynamicops, IResourceManager iresourcemanager, IRegistryCustom iregistrycustom) {
        return b(dynamicops, RegistryReadOps.b.a(iresourcemanager), iregistrycustom);
    }

    public static <T> RegistryReadOps<T> b(DynamicOps<T> dynamicops, RegistryReadOps.b registryreadops_b, IRegistryCustom iregistrycustom) {
        return new RegistryReadOps<>(dynamicops, registryreadops_b, iregistrycustom, Maps.newIdentityHashMap());
    }

    private RegistryReadOps(DynamicOps<T> dynamicops, RegistryReadOps.b registryreadops_b, IRegistryCustom iregistrycustom, IdentityHashMap<ResourceKey<? extends IRegistry<?>>, RegistryReadOps.a<?>> identityhashmap) {
        super(dynamicops);
        this.resources = registryreadops_b;
        this.registryAccess = iregistrycustom;
        this.readCache = identityhashmap;
        this.jsonOps = dynamicops == JsonOps.INSTANCE ? this : new RegistryReadOps<>(JsonOps.INSTANCE, registryreadops_b, iregistrycustom, identityhashmap);
    }

    protected <E> DataResult<Pair<Supplier<E>, T>> a(T t0, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        Optional<IRegistryWritable<E>> optional = this.registryAccess.a(resourcekey);

        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + resourcekey);
        } else {
            IRegistryWritable<E> iregistrywritable = (IRegistryWritable) optional.get();
            DataResult<Pair<MinecraftKey, T>> dataresult = MinecraftKey.CODEC.decode(this.delegate, t0);

            if (!dataresult.result().isPresent()) {
                return !flag ? DataResult.error("Inline definitions not allowed here") : codec.decode(this, t0).map((pair) -> {
                    return pair.mapFirst((object) -> {
                        return () -> {
                            return object;
                        };
                    });
                });
            } else {
                Pair<MinecraftKey, T> pair = (Pair) dataresult.result().get();
                MinecraftKey minecraftkey = (MinecraftKey) pair.getFirst();

                return this.a(resourcekey, iregistrywritable, codec, minecraftkey).map((supplier) -> {
                    return Pair.of(supplier, pair.getSecond());
                });
            }
        }
    }

    public <E> DataResult<RegistryMaterials<E>> a(RegistryMaterials<E> registrymaterials, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec) {
        Collection<MinecraftKey> collection = this.resources.a(resourcekey);
        DataResult<RegistryMaterials<E>> dataresult = DataResult.success(registrymaterials, Lifecycle.stable());
        String s = resourcekey.a().getKey() + "/";
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey = (MinecraftKey) iterator.next();
            String s1 = minecraftkey.getKey();

            if (!s1.endsWith(".json")) {
                RegistryReadOps.LOGGER.warn("Skipping resource {} since it is not a json file", minecraftkey);
            } else if (!s1.startsWith(s)) {
                RegistryReadOps.LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", minecraftkey);
            } else {
                String s2 = s1.substring(s.length(), s1.length() - ".json".length());
                MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s2);

                dataresult = dataresult.flatMap((registrymaterials1) -> {
                    return this.a(resourcekey, (IRegistryWritable) registrymaterials1, codec, minecraftkey1).map((supplier) -> {
                        return registrymaterials1;
                    });
                });
            }
        }

        return dataresult.setPartial(registrymaterials);
    }

    private <E> DataResult<Supplier<E>> a(ResourceKey<? extends IRegistry<E>> resourcekey, final IRegistryWritable<E> iregistrywritable, Codec<E> codec, MinecraftKey minecraftkey) {
        final ResourceKey<E> resourcekey1 = ResourceKey.a(resourcekey, minecraftkey);
        RegistryReadOps.a<E> registryreadops_a = this.b(resourcekey);
        DataResult<Supplier<E>> dataresult = (DataResult) registryreadops_a.values.get(resourcekey1);

        if (dataresult != null) {
            return dataresult;
        } else {
            Supplier<E> supplier = Suppliers.memoize(() -> {
                E e0 = iregistrywritable.a(resourcekey1);

                if (e0 == null) {
                    throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + resourcekey1);
                } else {
                    return e0;
                }
            });

            registryreadops_a.values.put(resourcekey1, DataResult.success(supplier));
            Optional<DataResult<Pair<E, OptionalInt>>> optional = this.resources.a(this.jsonOps, resourcekey, resourcekey1, codec);
            DataResult dataresult1;

            if (!optional.isPresent()) {
                dataresult1 = DataResult.success(new Supplier<E>() {
                    public E get() {
                        return iregistrywritable.a(resourcekey1);
                    }

                    public String toString() {
                        return resourcekey1.toString();
                    }
                }, Lifecycle.stable());
            } else {
                DataResult<Pair<E, OptionalInt>> dataresult2 = (DataResult) optional.get();
                Optional<Pair<E, OptionalInt>> optional1 = dataresult2.result();

                if (optional1.isPresent()) {
                    Pair<E, OptionalInt> pair = (Pair) optional1.get();

                    iregistrywritable.a((OptionalInt) pair.getSecond(), resourcekey1, pair.getFirst(), dataresult2.lifecycle());
                }

                dataresult1 = dataresult2.map((pair1) -> {
                    return () -> {
                        return iregistrywritable.a(resourcekey1);
                    };
                });
            }

            registryreadops_a.values.put(resourcekey1, dataresult1);
            return dataresult1;
        }
    }

    private <E> RegistryReadOps.a<E> b(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (RegistryReadOps.a) this.readCache.computeIfAbsent(resourcekey, (resourcekey1) -> {
            return new RegistryReadOps.a<>();
        });
    }

    protected <E> DataResult<IRegistry<E>> a(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (DataResult) this.registryAccess.a(resourcekey).map((iregistrywritable) -> {
            return DataResult.success(iregistrywritable, iregistrywritable.b());
        }).orElseGet(() -> {
            return DataResult.error("Unknown registry: " + resourcekey);
        });
    }

    public interface b {

        Collection<MinecraftKey> a(ResourceKey<? extends IRegistry<?>> resourcekey);

        <E> Optional<DataResult<Pair<E, OptionalInt>>> a(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder);

        static RegistryReadOps.b a(final IResourceManager iresourcemanager) {
            return new RegistryReadOps.b() {
                @Override
                public Collection<MinecraftKey> a(ResourceKey<? extends IRegistry<?>> resourcekey) {
                    return iresourcemanager.a(resourcekey.a().getKey(), (s) -> {
                        return s.endsWith(".json");
                    });
                }

                @Override
                public <E> Optional<DataResult<Pair<E, OptionalInt>>> a(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder) {
                    MinecraftKey minecraftkey = resourcekey1.a();
                    String s = minecraftkey.getNamespace();
                    String s1 = resourcekey.a().getKey();
                    MinecraftKey minecraftkey1 = new MinecraftKey(s, s1 + "/" + minecraftkey.getKey() + ".json");

                    if (!iresourcemanager.b(minecraftkey1)) {
                        return Optional.empty();
                    } else {
                        try {
                            IResource iresource = iresourcemanager.a(minecraftkey1);

                            Optional optional;

                            try {
                                InputStreamReader inputstreamreader = new InputStreamReader(iresource.b(), StandardCharsets.UTF_8);

                                try {
                                    JsonParser jsonparser = new JsonParser();
                                    JsonElement jsonelement = jsonparser.parse(inputstreamreader);

                                    optional = Optional.of(decoder.parse(dynamicops, jsonelement).map((object) -> {
                                        return Pair.of(object, OptionalInt.empty());
                                    }));
                                } catch (Throwable throwable) {
                                    try {
                                        inputstreamreader.close();
                                    } catch (Throwable throwable1) {
                                        throwable.addSuppressed(throwable1);
                                    }

                                    throw throwable;
                                }

                                inputstreamreader.close();
                            } catch (Throwable throwable2) {
                                if (iresource != null) {
                                    try {
                                        iresource.close();
                                    } catch (Throwable throwable3) {
                                        throwable2.addSuppressed(throwable3);
                                    }
                                }

                                throw throwable2;
                            }

                            if (iresource != null) {
                                iresource.close();
                            }

                            return optional;
                        } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                            return Optional.of(DataResult.error("Failed to parse " + minecraftkey1 + " file: " + ioexception.getMessage()));
                        }
                    }
                }

                public String toString() {
                    return "ResourceAccess[" + iresourcemanager + "]";
                }
            };
        }

        public static final class a implements RegistryReadOps.b {

            private final Map<ResourceKey<?>, JsonElement> data = Maps.newIdentityHashMap();
            private final Object2IntMap<ResourceKey<?>> ids = new Object2IntOpenCustomHashMap(SystemUtils.k());
            private final Map<ResourceKey<?>, Lifecycle> lifecycles = Maps.newIdentityHashMap();

            public a() {}

            public <E> void a(IRegistryCustom.Dimension iregistrycustom_dimension, ResourceKey<E> resourcekey, Encoder<E> encoder, int i, E e0, Lifecycle lifecycle) {
                DataResult<JsonElement> dataresult = encoder.encodeStart(RegistryWriteOps.a(JsonOps.INSTANCE, iregistrycustom_dimension), e0);
                Optional<PartialResult<JsonElement>> optional = dataresult.error();

                if (optional.isPresent()) {
                    RegistryReadOps.LOGGER.error("Error adding element: {}", ((PartialResult) optional.get()).message());
                } else {
                    this.data.put(resourcekey, (JsonElement) dataresult.result().get());
                    this.ids.put(resourcekey, i);
                    this.lifecycles.put(resourcekey, lifecycle);
                }
            }

            @Override
            public Collection<MinecraftKey> a(ResourceKey<? extends IRegistry<?>> resourcekey) {
                return (Collection) this.data.keySet().stream().filter((resourcekey1) -> {
                    return resourcekey1.a(resourcekey);
                }).map((resourcekey1) -> {
                    String s = resourcekey1.a().getNamespace();
                    String s1 = resourcekey.a().getKey();

                    return new MinecraftKey(s, s1 + "/" + resourcekey1.a().getKey() + ".json");
                }).collect(Collectors.toList());
            }

            @Override
            public <E> Optional<DataResult<Pair<E, OptionalInt>>> a(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder) {
                JsonElement jsonelement = (JsonElement) this.data.get(resourcekey1);

                return jsonelement == null ? Optional.of(DataResult.error("Unknown element: " + resourcekey1)) : Optional.of(decoder.parse(dynamicops, jsonelement).setLifecycle((Lifecycle) this.lifecycles.get(resourcekey1)).map((object) -> {
                    return Pair.of(object, OptionalInt.of(this.ids.getInt(resourcekey1)));
                }));
            }
        }
    }

    private static final class a<E> {

        final Map<ResourceKey<E>, DataResult<Supplier<E>>> values = Maps.newIdentityHashMap();

        a() {}
    }
}
