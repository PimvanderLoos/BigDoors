package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.packs.resources.IResourceManager;
import org.slf4j.Logger;

public interface RegistryResourceAccess {

    <E> Map<ResourceKey<E>, RegistryResourceAccess.a<E>> listResources(ResourceKey<? extends IRegistry<E>> resourcekey);

    <E> Optional<RegistryResourceAccess.a<E>> getResource(ResourceKey<E> resourcekey);

    static RegistryResourceAccess forResourceManager(final IResourceManager iresourcemanager) {
        return new RegistryResourceAccess() {
            private static final String JSON = ".json";

            @Override
            public <E> Map<ResourceKey<E>, RegistryResourceAccess.a<E>> listResources(ResourceKey<? extends IRegistry<E>> resourcekey) {
                String s = registryDirPath(resourcekey.location());
                Map<ResourceKey<E>, RegistryResourceAccess.a<E>> map = Maps.newHashMap();

                iresourcemanager.listResources(s, (minecraftkey) -> {
                    return minecraftkey.getPath().endsWith(".json");
                }).forEach((minecraftkey, iresource) -> {
                    String s1 = minecraftkey.getPath();
                    String s2 = s1.substring(s.length() + 1, s1.length() - ".json".length());
                    ResourceKey<E> resourcekey1 = ResourceKey.create(resourcekey, new MinecraftKey(minecraftkey.getNamespace(), s2));

                    map.put(resourcekey1, (dynamicops, decoder) -> {
                        try {
                            BufferedReader bufferedreader = iresource.openAsReader();

                            DataResult dataresult;

                            try {
                                dataresult = this.decodeElement(dynamicops, decoder, bufferedreader);
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

                            return dataresult;
                        } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                            return DataResult.error("Failed to parse " + minecraftkey + " file: " + ioexception.getMessage());
                        }
                    });
                });
                return map;
            }

            @Override
            public <E> Optional<RegistryResourceAccess.a<E>> getResource(ResourceKey<E> resourcekey) {
                MinecraftKey minecraftkey = elementPath(resourcekey);

                return iresourcemanager.getResource(minecraftkey).map((iresource) -> {
                    return (dynamicops, decoder) -> {
                        try {
                            BufferedReader bufferedreader = iresource.openAsReader();

                            DataResult dataresult;

                            try {
                                dataresult = this.decodeElement(dynamicops, decoder, bufferedreader);
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

                            return dataresult;
                        } catch (JsonIOException | JsonSyntaxException | IOException ioexception) {
                            return DataResult.error("Failed to parse " + minecraftkey + " file: " + ioexception.getMessage());
                        }
                    };
                });
            }

            private <E> DataResult<RegistryResourceAccess.ParsedEntry<E>> decodeElement(DynamicOps<JsonElement> dynamicops, Decoder<E> decoder, Reader reader) throws IOException {
                JsonElement jsonelement = JsonParser.parseReader(reader);

                return decoder.parse(dynamicops, jsonelement).map(RegistryResourceAccess.ParsedEntry::createWithoutId);
            }

            private static String registryDirPath(MinecraftKey minecraftkey) {
                return minecraftkey.getPath();
            }

            private static <E> MinecraftKey elementPath(ResourceKey<E> resourcekey) {
                String s = resourcekey.location().getNamespace();
                String s1 = registryDirPath(resourcekey.registry());

                return new MinecraftKey(s, s1 + "/" + resourcekey.location().getPath() + ".json");
            }

            public String toString() {
                return "ResourceAccess[" + iresourcemanager + "]";
            }
        };
    }

    public static final class InMemoryStorage implements RegistryResourceAccess {

        private static final Logger LOGGER = LogUtils.getLogger();
        private final Map<ResourceKey<?>, RegistryResourceAccess.InMemoryStorage.Entry> entries = Maps.newIdentityHashMap();

        public InMemoryStorage() {}

        public <E> void add(IRegistryCustom iregistrycustom, ResourceKey<E> resourcekey, Encoder<E> encoder, int i, E e0, Lifecycle lifecycle) {
            DataResult<JsonElement> dataresult = encoder.encodeStart(RegistryOps.create(JsonOps.INSTANCE, iregistrycustom), e0);
            Optional<PartialResult<JsonElement>> optional = dataresult.error();

            if (optional.isPresent()) {
                RegistryResourceAccess.InMemoryStorage.LOGGER.error("Error adding element: {}", ((PartialResult) optional.get()).message());
            } else {
                this.entries.put(resourcekey, new RegistryResourceAccess.InMemoryStorage.Entry((JsonElement) dataresult.result().get(), i, lifecycle));
            }

        }

        @Override
        public <E> Map<ResourceKey<E>, RegistryResourceAccess.a<E>> listResources(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return (Map) this.entries.entrySet().stream().filter((java_util_map_entry) -> {
                return ((ResourceKey) java_util_map_entry.getKey()).isFor(resourcekey);
            }).collect(Collectors.toMap((java_util_map_entry) -> {
                return (ResourceKey) java_util_map_entry.getKey();
            }, (java_util_map_entry) -> {
                RegistryResourceAccess.InMemoryStorage.Entry registryresourceaccess_inmemorystorage_entry = (RegistryResourceAccess.InMemoryStorage.Entry) java_util_map_entry.getValue();

                Objects.requireNonNull(registryresourceaccess_inmemorystorage_entry);
                return registryresourceaccess_inmemorystorage_entry::parse;
            }));
        }

        @Override
        public <E> Optional<RegistryResourceAccess.a<E>> getResource(ResourceKey<E> resourcekey) {
            RegistryResourceAccess.InMemoryStorage.Entry registryresourceaccess_inmemorystorage_entry = (RegistryResourceAccess.InMemoryStorage.Entry) this.entries.get(resourcekey);

            if (registryresourceaccess_inmemorystorage_entry == null) {
                DataResult<RegistryResourceAccess.ParsedEntry<E>> dataresult = DataResult.error("Unknown element: " + resourcekey);

                return Optional.of((dynamicops, decoder) -> {
                    return dataresult;
                });
            } else {
                Objects.requireNonNull(registryresourceaccess_inmemorystorage_entry);
                return Optional.of(registryresourceaccess_inmemorystorage_entry::parse);
            }
        }

        private static record Entry(JsonElement data, int id, Lifecycle lifecycle) {

            public <E> DataResult<RegistryResourceAccess.ParsedEntry<E>> parse(DynamicOps<JsonElement> dynamicops, Decoder<E> decoder) {
                return decoder.parse(dynamicops, this.data).setLifecycle(this.lifecycle).map((object) -> {
                    return RegistryResourceAccess.ParsedEntry.createWithId(object, this.id);
                });
            }
        }
    }

    @FunctionalInterface
    public interface a<E> {

        DataResult<RegistryResourceAccess.ParsedEntry<E>> parseElement(DynamicOps<JsonElement> dynamicops, Decoder<E> decoder);
    }

    public static record ParsedEntry<E> (E value, OptionalInt fixedId) {

        public static <E> RegistryResourceAccess.ParsedEntry<E> createWithoutId(E e0) {
            return new RegistryResourceAccess.ParsedEntry<>(e0, OptionalInt.empty());
        }

        public static <E> RegistryResourceAccess.ParsedEntry<E> createWithId(E e0, int i) {
            return new RegistryResourceAccess.ParsedEntry<>(e0, OptionalInt.of(i));
        }
    }
}
