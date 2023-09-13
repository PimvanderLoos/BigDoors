package net.minecraft.server;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistryReadOps<T> extends DynamicOpsWrapper<T> {

    private static final Logger LOGGER = LogManager.getLogger();
    private final IResourceManager c;
    private final IRegistryCustom d;
    private final Map<ResourceKey<? extends IRegistry<?>>, RegistryReadOps.a<?>> e = Maps.newIdentityHashMap();

    public static <T> RegistryReadOps<T> a(DynamicOps<T> dynamicops, IResourceManager iresourcemanager, IRegistryCustom iregistrycustom) {
        return new RegistryReadOps<>(dynamicops, iresourcemanager, iregistrycustom);
    }

    private RegistryReadOps(DynamicOps<T> dynamicops, IResourceManager iresourcemanager, IRegistryCustom iregistrycustom) {
        super(dynamicops);
        this.c = iresourcemanager;
        this.d = iregistrycustom;
    }

    protected <E> DataResult<Pair<Supplier<E>, T>> a(T t0, ResourceKey<IRegistry<E>> resourcekey, MapCodec<E> mapcodec) {
        Optional<IRegistryWritable<E>> optional = this.d.a(resourcekey);

        if (!optional.isPresent()) {
            return DataResult.error("Unknown registry: " + resourcekey);
        } else {
            IRegistryWritable<E> iregistrywritable = (IRegistryWritable) optional.get();
            DataResult<Pair<MinecraftKey, T>> dataresult = MinecraftKey.a.decode(this.a, t0);

            if (!dataresult.result().isPresent()) {
                return Codecs.a(resourcekey, mapcodec).codec().decode(this.a, t0).map((pair) -> {
                    return pair.mapFirst((pair1) -> {
                        iregistrywritable.a((ResourceKey) pair1.getFirst(), pair1.getSecond());
                        iregistrywritable.d((ResourceKey) pair1.getFirst());
                        return pair1::getSecond;
                    });
                });
            } else {
                Pair<MinecraftKey, T> pair = (Pair) dataresult.result().get();
                MinecraftKey minecraftkey = (MinecraftKey) pair.getFirst();

                return this.a(resourcekey, iregistrywritable, mapcodec, minecraftkey).map((supplier) -> {
                    return Pair.of(supplier, pair.getSecond());
                });
            }
        }
    }

    public <E> DataResult<RegistryMaterials<E>> a(RegistryMaterials<E> registrymaterials, ResourceKey<IRegistry<E>> resourcekey, MapCodec<E> mapcodec) {
        MinecraftKey minecraftkey = resourcekey.a();
        Collection<MinecraftKey> collection = this.c.a(minecraftkey, (s) -> {
            return s.endsWith(".json");
        });
        DataResult<RegistryMaterials<E>> dataresult = DataResult.success(registrymaterials, Lifecycle.stable());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            MinecraftKey minecraftkey1 = (MinecraftKey) iterator.next();
            String s = minecraftkey1.getKey();

            if (!s.endsWith(".json")) {
                RegistryReadOps.LOGGER.warn("Skipping resource {} since it is not a json file", minecraftkey1);
            } else if (!s.startsWith(minecraftkey.getKey() + "/")) {
                RegistryReadOps.LOGGER.warn("Skipping resource {} since it does not have a registry name prefix", minecraftkey1);
            } else {
                String s1 = s.substring(0, s.length() - ".json".length()).substring(minecraftkey.getKey().length() + 1);
                int i = s1.indexOf(47);

                if (i < 0) {
                    RegistryReadOps.LOGGER.warn("Skipping resource {} since it does not have a namespace", minecraftkey1);
                } else {
                    String s2 = s1.substring(0, i);
                    String s3 = s1.substring(i + 1);
                    MinecraftKey minecraftkey2 = new MinecraftKey(s2, s3);

                    dataresult = dataresult.flatMap((registrymaterials1) -> {
                        return this.a(resourcekey, (IRegistryWritable) registrymaterials1, mapcodec, minecraftkey2).map((supplier) -> {
                            return registrymaterials1;
                        });
                    });
                }
            }
        }

        return dataresult.setPartial(registrymaterials);
    }

    private <E> DataResult<Supplier<E>> a(ResourceKey<IRegistry<E>> resourcekey, IRegistryWritable<E> iregistrywritable, MapCodec<E> mapcodec, MinecraftKey minecraftkey) {
        ResourceKey<E> resourcekey1 = ResourceKey.a(resourcekey, minecraftkey);
        E e0 = iregistrywritable.a(resourcekey1);

        if (e0 != null) {
            return DataResult.success(() -> {
                return e0;
            }, Lifecycle.stable());
        } else {
            RegistryReadOps.a<E> registryreadops_a = this.a(resourcekey);
            DataResult<Supplier<E>> dataresult = (DataResult) registryreadops_a.a.get(resourcekey1);

            if (dataresult != null) {
                return dataresult;
            } else {
                Supplier<E> supplier = Suppliers.memoize(() -> {
                    E e1 = iregistrywritable.a(resourcekey1);

                    if (e1 == null) {
                        throw new RuntimeException("Error during recursive registry parsing, element resolved too early: " + resourcekey1);
                    } else {
                        return e1;
                    }
                });

                registryreadops_a.a.put(resourcekey1, DataResult.success(supplier));
                DataResult<E> dataresult1 = this.a(resourcekey, resourcekey1, mapcodec);

                dataresult1.result().ifPresent((object) -> {
                    iregistrywritable.a(resourcekey1, object);
                });
                DataResult<Supplier<E>> dataresult2 = dataresult1.map((object) -> {
                    return () -> {
                        return object;
                    };
                });

                registryreadops_a.a.put(resourcekey1, dataresult2);
                return dataresult2;
            }
        }
    }

    private <E> DataResult<E> a(ResourceKey<IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, MapCodec<E> mapcodec) {
        MinecraftKey minecraftkey = new MinecraftKey(resourcekey.a().getNamespace(), resourcekey.a().getKey() + "/" + resourcekey1.a().getNamespace() + "/" + resourcekey1.a().getKey() + ".json");

        try {
            IResource iresource = this.c.a(minecraftkey);
            Throwable throwable = null;

            DataResult dataresult;

            try {
                InputStreamReader inputstreamreader = new InputStreamReader(iresource.b(), StandardCharsets.UTF_8);
                Throwable throwable1 = null;

                try {
                    JsonParser jsonparser = new JsonParser();
                    JsonElement jsonelement = jsonparser.parse(inputstreamreader);

                    dataresult = mapcodec.codec().parse(new RegistryReadOps<>(JsonOps.INSTANCE, this.c, this.d), jsonelement);
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
            return DataResult.error("Failed to parse file: " + ioexception.getMessage());
        }
    }

    private <E> RegistryReadOps.a<E> a(ResourceKey<IRegistry<E>> resourcekey) {
        return (RegistryReadOps.a) this.e.computeIfAbsent(resourcekey, (resourcekey1) -> {
            return new RegistryReadOps.a<>();
        });
    }

    static final class a<E> {

        private final Map<ResourceKey<E>, DataResult<Supplier<E>>> a;

        private a() {
            this.a = Maps.newIdentityHashMap();
        }
    }
}
