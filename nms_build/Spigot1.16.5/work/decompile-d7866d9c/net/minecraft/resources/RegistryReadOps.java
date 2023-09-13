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

    private static final Logger LOGGER = LogManager.getLogger();
    private final RegistryReadOps.b c;
    private final IRegistryCustom.Dimension d;
    private final Map<ResourceKey<? extends IRegistry<?>>, RegistryReadOps.a<?>> e;
    private final RegistryReadOps<JsonElement> f;

    public static <T> RegistryReadOps<T> a(DynamicOps<T> dynamicops, IResourceManager iresourcemanager, IRegistryCustom.Dimension iregistrycustom_dimension) {
        return a(dynamicops, RegistryReadOps.b.a(iresourcemanager), iregistrycustom_dimension);
    }

    public static <T> RegistryReadOps<T> a(DynamicOps<T> dynamicops, RegistryReadOps.b registryreadops_b, IRegistryCustom.Dimension iregistrycustom_dimension) {
        RegistryReadOps<T> registryreadops = new RegistryReadOps<>(dynamicops, registryreadops_b, iregistrycustom_dimension, Maps.newIdentityHashMap());

        IRegistryCustom.a(iregistrycustom_dimension, registryreadops);
        return registryreadops;
    }

    private RegistryReadOps(DynamicOps<T> dynamicops, RegistryReadOps.b registryreadops_b, IRegistryCustom.Dimension iregistrycustom_dimension, IdentityHashMap<ResourceKey<? extends IRegistry<?>>, RegistryReadOps.a<?>> identityhashmap) {
        super(dynamicops);
        this.c = registryreadops_b;
        this.d = iregistrycustom_dimension;
        this.e = identityhashmap;
        this.f = dynamicops == JsonOps.INSTANCE ? this : new RegistryReadOps<>(JsonOps.INSTANCE, registryreadops_b, iregistrycustom_dimension, identityhashmap);
    }

    protected <E> DataResult<Pair<Supplier<E>, T>> a(T t0, ResourceKey<? extends IRegistry<E>> resourcekey, Codec<E> codec, boolean flag) {
        Optional<IRegistryWritable<E>> optional = this.d.a(resourcekey);

        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + resourcekey);
        } else {
            IRegistryWritable<E> iregistrywritable = (IRegistryWritable) optional.get();
            DataResult<Pair<MinecraftKey, T>> dataresult = MinecraftKey.a.decode(this.a, t0);

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
        Collection<MinecraftKey> collection = this.c.a(resourcekey);
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

    private <E> DataResult<Supplier<E>> a(ResourceKey<? extends IRegistry<E>> resourcekey, IRegistryWritable<E> iregistrywritable, Codec<E> codec, MinecraftKey minecraftkey) {
        ResourceKey<E> resourcekey1 = ResourceKey.a(resourcekey, minecraftkey);
        RegistryReadOps.a<E> registryreadops_a = this.b(resourcekey);
        DataResult<Supplier<E>> dataresult = (DataResult) registryreadops_a.a.get(resourcekey1);

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

            registryreadops_a.a.put(resourcekey1, DataResult.success(supplier));
            DataResult<Pair<E, OptionalInt>> dataresult1 = this.c.a(this.f, resourcekey, resourcekey1, codec);
            Optional<Pair<E, OptionalInt>> optional = dataresult1.result();

            if (optional.isPresent()) {
                Pair<E, OptionalInt> pair = (Pair) optional.get();

                iregistrywritable.a((OptionalInt) pair.getSecond(), resourcekey1, pair.getFirst(), dataresult1.lifecycle());
            }

            DataResult dataresult2;

            if (!optional.isPresent() && iregistrywritable.a(resourcekey1) != null) {
                dataresult2 = DataResult.success(() -> {
                    return iregistrywritable.a(resourcekey1);
                }, Lifecycle.stable());
            } else {
                dataresult2 = dataresult1.map((pair1) -> {
                    return () -> {
                        return iregistrywritable.a(resourcekey1);
                    };
                });
            }

            registryreadops_a.a.put(resourcekey1, dataresult2);
            return dataresult2;
        }
    }

    private <E> RegistryReadOps.a<E> b(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (RegistryReadOps.a) this.e.computeIfAbsent(resourcekey, (resourcekey1) -> {
            return new RegistryReadOps.a<>();
        });
    }

    protected <E> DataResult<IRegistry<E>> a(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return (DataResult) this.d.a(resourcekey).map((iregistrywritable) -> {
            return DataResult.success(iregistrywritable, iregistrywritable.b());
        }).orElseGet(() -> {
            return DataResult.error("Unknown registry: " + resourcekey);
        });
    }

    public interface b {

        Collection<MinecraftKey> a(ResourceKey<? extends IRegistry<?>> resourcekey);

        <E> DataResult<Pair<E, OptionalInt>> a(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder);

        static RegistryReadOps.b a(final IResourceManager iresourcemanager) {
            return new RegistryReadOps.b() {
                @Override
                public Collection<MinecraftKey> a(ResourceKey<? extends IRegistry<?>> resourcekey) {
                    return iresourcemanager.a(resourcekey.a().getKey(), (s) -> {
                        return s.endsWith(".json");
                    });
                }

                @Override
                public <E> DataResult<Pair<E, OptionalInt>> a(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder) {
                    MinecraftKey minecraftkey = resourcekey1.a();
                    MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), resourcekey.a().getKey() + "/" + minecraftkey.getKey() + ".json");

                    try {
                        IResource iresource = iresourcemanager.a(minecraftkey1);
                        Throwable throwable = null;

                        DataResult dataresult;

                        try {
                            InputStreamReader inputstreamreader = new InputStreamReader(iresource.b(), StandardCharsets.UTF_8);
                            Throwable throwable1 = null;

                            try {
                                JsonParser jsonparser = new JsonParser();
                                JsonElement jsonelement = jsonparser.parse(inputstreamreader);

                                dataresult = decoder.parse(dynamicops, jsonelement).map((object) -> {
                                    return Pair.of(object, OptionalInt.empty());
                                });
                            } catch (Throwable throwable2) {
                                throwable1 = throwable2;
                                throw throwable2;
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
                        } catch (Throwable throwable4) {
                            throwable = throwable4;
                            throw throwable4;
                        } finally {
                            if (iresource != null) {
                                if (throwable != null) {
                                    try {
                                        iresource.close();
                                    } catch (Throwable throwable5) {
                                        throwable.addSuppressed(throwable5);
                                    }
                                } else {
                                    iresource.close();
                                }
                            }

                        }

                        return dataresult;
                    } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                        return DataResult.error("Failed to parse " + minecraftkey1 + " file: " + ioexception.getMessage());
                    }
                }

                public String toString() {
                    return "ResourceAccess[" + iresourcemanager + "]";
                }
            };
        }

        public static final class a implements RegistryReadOps.b {

            private final Map<ResourceKey<?>, JsonElement> a = Maps.newIdentityHashMap();
            private final Object2IntMap<ResourceKey<?>> b = new Object2IntOpenCustomHashMap(SystemUtils.k());
            private final Map<ResourceKey<?>, Lifecycle> c = Maps.newIdentityHashMap();

            public a() {}

            public <E> void a(IRegistryCustom.Dimension iregistrycustom_dimension, ResourceKey<E> resourcekey, Encoder<E> encoder, int i, E e0, Lifecycle lifecycle) {
                DataResult<JsonElement> dataresult = encoder.encodeStart(RegistryWriteOps.a(JsonOps.INSTANCE, iregistrycustom_dimension), e0);
                Optional<PartialResult<JsonElement>> optional = dataresult.error();

                if (optional.isPresent()) {
                    RegistryReadOps.LOGGER.error("Error adding element: {}", ((PartialResult) optional.get()).message());
                } else {
                    this.a.put(resourcekey, dataresult.result().get());
                    this.b.put(resourcekey, i);
                    this.c.put(resourcekey, lifecycle);
                }
            }

            @Override
            public Collection<MinecraftKey> a(ResourceKey<? extends IRegistry<?>> resourcekey) {
                return (Collection) this.a.keySet().stream().filter((resourcekey1) -> {
                    return resourcekey1.a(resourcekey);
                }).map((resourcekey1) -> {
                    return new MinecraftKey(resourcekey1.a().getNamespace(), resourcekey.a().getKey() + "/" + resourcekey1.a().getKey() + ".json");
                }).collect(Collectors.toList());
            }

            @Override
            public <E> DataResult<Pair<E, OptionalInt>> a(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder) {
                JsonElement jsonelement = (JsonElement) this.a.get(resourcekey1);

                return jsonelement == null ? DataResult.error("Unknown element: " + resourcekey1) : decoder.parse(dynamicops, jsonelement).setLifecycle((Lifecycle) this.c.get(resourcekey1)).map((object) -> {
                    return Pair.of(object, OptionalInt.of(this.b.getInt(resourcekey1)));
                });
            }
        }
    }

    static final class a<E> {

        private final Map<ResourceKey<E>, DataResult<Supplier<E>>> a;

        private a() {
            this.a = Maps.newIdentityHashMap();
        }
    }
}
