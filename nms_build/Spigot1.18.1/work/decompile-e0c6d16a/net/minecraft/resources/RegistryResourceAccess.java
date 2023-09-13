package net.minecraft.resources;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DataResult.PartialResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistry;
import net.minecraft.core.IRegistryCustom;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface RegistryResourceAccess {

    <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends IRegistry<E>> resourcekey);

    <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder);

    static RegistryResourceAccess forResourceManager(final IResourceManager iresourcemanager) {
        return new RegistryResourceAccess() {
            private static final String JSON = ".json";

            @Override
            public <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends IRegistry<E>> resourcekey) {
                String s = registryDirPath(resourcekey);
                Set<ResourceKey<E>> set = new HashSet();

                iresourcemanager.listResources(s, (s1) -> {
                    return s1.endsWith(".json");
                }).forEach((minecraftkey) -> {
                    String s1 = minecraftkey.getPath();
                    String s2 = s1.substring(s.length() + 1, s1.length() - ".json".length());

                    set.add(ResourceKey.create(resourcekey, new MinecraftKey(minecraftkey.getNamespace(), s2)));
                });
                return set;
            }

            @Override
            public <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder) {
                MinecraftKey minecraftkey = elementPath(resourcekey, resourcekey1);

                if (!iresourcemanager.hasResource(minecraftkey)) {
                    return Optional.empty();
                } else {
                    try {
                        IResource iresource = iresourcemanager.getResource(minecraftkey);

                        Optional optional;

                        try {
                            InputStreamReader inputstreamreader = new InputStreamReader(iresource.getInputStream(), StandardCharsets.UTF_8);

                            try {
                                JsonElement jsonelement = JsonParser.parseReader(inputstreamreader);

                                optional = Optional.of(decoder.parse(dynamicops, jsonelement).map(RegistryResourceAccess.ParsedEntry::createWithoutId));
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
                        return Optional.of(DataResult.error("Failed to parse " + minecraftkey + " file: " + ioexception.getMessage()));
                    }
                }
            }

            private static String registryDirPath(ResourceKey<? extends IRegistry<?>> resourcekey) {
                return resourcekey.location().getPath();
            }

            private static <E> MinecraftKey elementPath(ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1) {
                String s = resourcekey1.location().getNamespace();
                String s1 = registryDirPath(resourcekey);

                return new MinecraftKey(s, s1 + "/" + resourcekey1.location().getPath() + ".json");
            }

            public String toString() {
                return "ResourceAccess[" + iresourcemanager + "]";
            }
        };
    }

    public static final class InMemoryStorage implements RegistryResourceAccess {

        private static final Logger LOGGER = LogManager.getLogger();
        private final Map<ResourceKey<?>, RegistryResourceAccess.InMemoryStorage.Entry> entries = Maps.newIdentityHashMap();

        public InMemoryStorage() {}

        public <E> void add(IRegistryCustom.Dimension iregistrycustom_dimension, ResourceKey<E> resourcekey, Encoder<E> encoder, int i, E e0, Lifecycle lifecycle) {
            DataResult<JsonElement> dataresult = encoder.encodeStart(RegistryWriteOps.create(JsonOps.INSTANCE, iregistrycustom_dimension), e0);
            Optional<PartialResult<JsonElement>> optional = dataresult.error();

            if (optional.isPresent()) {
                RegistryResourceAccess.InMemoryStorage.LOGGER.error("Error adding element: {}", ((PartialResult) optional.get()).message());
            } else {
                this.entries.put(resourcekey, new RegistryResourceAccess.InMemoryStorage.Entry((JsonElement) dataresult.result().get(), i, lifecycle));
            }

        }

        @Override
        public <E> Collection<ResourceKey<E>> listResources(ResourceKey<? extends IRegistry<E>> resourcekey) {
            return (Collection) this.entries.keySet().stream().flatMap((resourcekey1) -> {
                return resourcekey1.cast(resourcekey).stream();
            }).collect(Collectors.toList());
        }

        @Override
        public <E> Optional<DataResult<RegistryResourceAccess.ParsedEntry<E>>> parseElement(DynamicOps<JsonElement> dynamicops, ResourceKey<? extends IRegistry<E>> resourcekey, ResourceKey<E> resourcekey1, Decoder<E> decoder) {
            RegistryResourceAccess.InMemoryStorage.Entry registryresourceaccess_inmemorystorage_entry = (RegistryResourceAccess.InMemoryStorage.Entry) this.entries.get(resourcekey1);

            return registryresourceaccess_inmemorystorage_entry == null ? Optional.of(DataResult.error("Unknown element: " + resourcekey1)) : Optional.of(decoder.parse(dynamicops, registryresourceaccess_inmemorystorage_entry.data).setLifecycle(registryresourceaccess_inmemorystorage_entry.lifecycle).map((object) -> {
                return RegistryResourceAccess.ParsedEntry.createWithId(object, registryresourceaccess_inmemorystorage_entry.id);
            }));
        }

        private static record Entry(JsonElement a, int b, Lifecycle c) {

            final JsonElement data;
            final int id;
            final Lifecycle lifecycle;

            Entry(JsonElement jsonelement, int i, Lifecycle lifecycle) {
                this.data = jsonelement;
                this.id = i;
                this.lifecycle = lifecycle;
            }

            public JsonElement data() {
                return this.data;
            }

            public int id() {
                return this.id;
            }

            public Lifecycle lifecycle() {
                return this.lifecycle;
            }
        }
    }

    public static record ParsedEntry<E> (E a, OptionalInt b) {

        private final E value;
        private final OptionalInt fixedId;

        public ParsedEntry(E e0, OptionalInt optionalint) {
            this.value = e0;
            this.fixedId = optionalint;
        }

        public static <E> RegistryResourceAccess.ParsedEntry<E> createWithoutId(E e0) {
            return new RegistryResourceAccess.ParsedEntry<>(e0, OptionalInt.empty());
        }

        public static <E> RegistryResourceAccess.ParsedEntry<E> createWithId(E e0, int i) {
            return new RegistryResourceAccess.ParsedEntry<>(e0, OptionalInt.of(i));
        }

        public E value() {
            return this.value;
        }

        public OptionalInt fixedId() {
            return this.fixedId;
        }
    }
}
