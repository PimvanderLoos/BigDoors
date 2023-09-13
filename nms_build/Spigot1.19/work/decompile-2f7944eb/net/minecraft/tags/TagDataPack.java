package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.resources.IResource;
import net.minecraft.server.packs.resources.IResourceManager;
import org.slf4j.Logger;

public class TagDataPack<T> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String PATH_SUFFIX = ".json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();
    final Function<MinecraftKey, Optional<T>> idToValue;
    private final String directory;

    public TagDataPack(Function<MinecraftKey, Optional<T>> function, String s) {
        this.idToValue = function;
        this.directory = s;
    }

    public Map<MinecraftKey, List<TagDataPack.a>> load(IResourceManager iresourcemanager) {
        Map<MinecraftKey, List<TagDataPack.a>> map = Maps.newHashMap();
        Iterator iterator = iresourcemanager.listResourceStacks(this.directory, (minecraftkey) -> {
            return minecraftkey.getPath().endsWith(".json");
        }).entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<MinecraftKey, List<IResource>> entry = (Entry) iterator.next();
            MinecraftKey minecraftkey = (MinecraftKey) entry.getKey();
            String s = minecraftkey.getPath();
            MinecraftKey minecraftkey1 = new MinecraftKey(minecraftkey.getNamespace(), s.substring(this.directory.length() + 1, s.length() - TagDataPack.PATH_SUFFIX_LENGTH));
            Iterator iterator1 = ((List) entry.getValue()).iterator();

            while (iterator1.hasNext()) {
                IResource iresource = (IResource) iterator1.next();

                try {
                    BufferedReader bufferedreader = iresource.openAsReader();

                    try {
                        JsonElement jsonelement = JsonParser.parseReader(bufferedreader);
                        List<TagDataPack.a> list = (List) map.computeIfAbsent(minecraftkey1, (minecraftkey2) -> {
                            return new ArrayList();
                        });
                        DataResult dataresult = TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, jsonelement));
                        Logger logger = TagDataPack.LOGGER;

                        Objects.requireNonNull(logger);
                        TagFile tagfile = (TagFile) dataresult.getOrThrow(false, logger::error);

                        if (tagfile.replace()) {
                            list.clear();
                        }

                        String s1 = iresource.sourcePackId();

                        tagfile.entries().forEach((tagentry) -> {
                            list.add(new TagDataPack.a(tagentry, s1));
                        });
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
                } catch (Exception exception) {
                    TagDataPack.LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{minecraftkey1, minecraftkey, iresource.sourcePackId(), exception});
                }
            }
        }

        return map;
    }

    private static void visitDependenciesAndElement(Map<MinecraftKey, List<TagDataPack.a>> map, Multimap<MinecraftKey, MinecraftKey> multimap, Set<MinecraftKey> set, MinecraftKey minecraftkey, BiConsumer<MinecraftKey, List<TagDataPack.a>> biconsumer) {
        if (set.add(minecraftkey)) {
            multimap.get(minecraftkey).forEach((minecraftkey1) -> {
                visitDependenciesAndElement(map, multimap, set, minecraftkey1, biconsumer);
            });
            List<TagDataPack.a> list = (List) map.get(minecraftkey);

            if (list != null) {
                biconsumer.accept(minecraftkey, list);
            }

        }
    }

    private static boolean isCyclic(Multimap<MinecraftKey, MinecraftKey> multimap, MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        Collection<MinecraftKey> collection = multimap.get(minecraftkey1);

        return collection.contains(minecraftkey) ? true : collection.stream().anyMatch((minecraftkey2) -> {
            return isCyclic(multimap, minecraftkey, minecraftkey2);
        });
    }

    private static void addDependencyIfNotCyclic(Multimap<MinecraftKey, MinecraftKey> multimap, MinecraftKey minecraftkey, MinecraftKey minecraftkey1) {
        if (!isCyclic(multimap, minecraftkey, minecraftkey1)) {
            multimap.put(minecraftkey, minecraftkey1);
        }

    }

    private Either<Collection<TagDataPack.a>, Collection<T>> build(TagEntry.a<T> tagentry_a, List<TagDataPack.a> list) {
        Builder<T> builder = ImmutableSet.builder();
        List<TagDataPack.a> list1 = new ArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            TagDataPack.a tagdatapack_a = (TagDataPack.a) iterator.next();
            TagEntry tagentry = tagdatapack_a.entry();

            Objects.requireNonNull(builder);
            if (!tagentry.build(tagentry_a, builder::add)) {
                list1.add(tagdatapack_a);
            }
        }

        return list1.isEmpty() ? Either.right(builder.build()) : Either.left(list1);
    }

    public Map<MinecraftKey, Collection<T>> build(Map<MinecraftKey, List<TagDataPack.a>> map) {
        final Map<MinecraftKey, Collection<T>> map1 = Maps.newHashMap();
        TagEntry.a<T> tagentry_a = new TagEntry.a<T>() {
            @Nullable
            @Override
            public T element(MinecraftKey minecraftkey) {
                return ((Optional) TagDataPack.this.idToValue.apply(minecraftkey)).orElse((Object) null);
            }

            @Nullable
            @Override
            public Collection<T> tag(MinecraftKey minecraftkey) {
                return (Collection) map1.get(minecraftkey);
            }
        };
        Multimap<MinecraftKey, MinecraftKey> multimap = HashMultimap.create();

        map.forEach((minecraftkey, list) -> {
            list.forEach((tagdatapack_a) -> {
                tagdatapack_a.entry.visitRequiredDependencies((minecraftkey1) -> {
                    addDependencyIfNotCyclic(multimap, minecraftkey, minecraftkey1);
                });
            });
        });
        map.forEach((minecraftkey, list) -> {
            list.forEach((tagdatapack_a) -> {
                tagdatapack_a.entry.visitOptionalDependencies((minecraftkey1) -> {
                    addDependencyIfNotCyclic(multimap, minecraftkey, minecraftkey1);
                });
            });
        });
        Set<MinecraftKey> set = Sets.newHashSet();

        map.keySet().forEach((minecraftkey) -> {
            visitDependenciesAndElement(map, multimap, set, minecraftkey, (minecraftkey1, list) -> {
                this.build(tagentry_a, list).ifLeft((collection) -> {
                    TagDataPack.LOGGER.error("Couldn't load tag {} as it is missing following references: {}", minecraftkey1, collection.stream().map(Objects::toString).collect(Collectors.joining(", ")));
                }).ifRight((collection) -> {
                    map1.put(minecraftkey1, collection);
                });
            });
        });
        return map1;
    }

    public Map<MinecraftKey, Collection<T>> loadAndBuild(IResourceManager iresourcemanager) {
        return this.build(this.load(iresourcemanager));
    }

    public static record a(TagEntry entry, String source) {

        public String toString() {
            return this.entry + " (from " + this.source + ")";
        }
    }
}
