package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public class TagStatic {

    private static final Set<ResourceKey<?>> HELPERS_IDS = Sets.newHashSet();
    private static final List<TagUtil<?>> HELPERS = Lists.newArrayList();

    public TagStatic() {}

    public static <T> TagUtil<T> create(ResourceKey<? extends IRegistry<T>> resourcekey, String s) {
        if (!TagStatic.HELPERS_IDS.add(resourcekey)) {
            throw new IllegalStateException("Duplicate entry for static tag collection: " + resourcekey);
        } else {
            TagUtil<T> tagutil = new TagUtil<>(resourcekey, s);

            TagStatic.HELPERS.add(tagutil);
            return tagutil;
        }
    }

    public static void resetAll(ITagRegistry itagregistry) {
        TagStatic.HELPERS.forEach((tagutil) -> {
            tagutil.reset(itagregistry);
        });
    }

    public static void resetAllToEmpty() {
        TagStatic.HELPERS.forEach(TagUtil::resetToEmpty);
    }

    public static Multimap<ResourceKey<? extends IRegistry<?>>, MinecraftKey> getAllMissingTags(ITagRegistry itagregistry) {
        Multimap<ResourceKey<? extends IRegistry<?>>, MinecraftKey> multimap = HashMultimap.create();

        TagStatic.HELPERS.forEach((tagutil) -> {
            multimap.putAll(tagutil.getKey(), tagutil.getMissingTags(itagregistry));
        });
        return multimap;
    }

    public static void bootStrap() {
        makeSureAllKnownHelpersAreLoaded();
    }

    private static Set<TagUtil<?>> getAllKnownHelpers() {
        return ImmutableSet.of(TagsBlock.HELPER, TagsItem.HELPER, TagsFluid.HELPER, TagsEntity.HELPER, GameEventTags.HELPER);
    }

    private static void makeSureAllKnownHelpersAreLoaded() {
        Set<ResourceKey<?>> set = (Set) getAllKnownHelpers().stream().map(TagUtil::getKey).collect(Collectors.toSet());

        if (!Sets.difference(TagStatic.HELPERS_IDS, set).isEmpty()) {
            throw new IllegalStateException("Missing helper registrations");
        }
    }

    public static void visitHelpers(Consumer<TagUtil<?>> consumer) {
        TagStatic.HELPERS.forEach(consumer);
    }

    public static ITagRegistry createCollection() {
        ITagRegistry.a itagregistry_a = new ITagRegistry.a();

        makeSureAllKnownHelpersAreLoaded();
        TagStatic.HELPERS.forEach((tagutil) -> {
            tagutil.addToCollection(itagregistry_a);
        });
        return itagregistry_a.build();
    }
}
