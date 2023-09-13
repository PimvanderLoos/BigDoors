package net.minecraft.core;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public interface HolderLookup<T> {

    Optional<Holder<T>> get(ResourceKey<T> resourcekey);

    Stream<ResourceKey<T>> listElements();

    Optional<? extends HolderSet<T>> get(TagKey<T> tagkey);

    Stream<TagKey<T>> listTags();

    static <T> HolderLookup<T> forRegistry(IRegistry<T> iregistry) {
        return new HolderLookup.a<>(iregistry);
    }

    public static class a<T> implements HolderLookup<T> {

        protected final IRegistry<T> registry;

        public a(IRegistry<T> iregistry) {
            this.registry = iregistry;
        }

        @Override
        public Optional<Holder<T>> get(ResourceKey<T> resourcekey) {
            return this.registry.getHolder(resourcekey);
        }

        @Override
        public Stream<ResourceKey<T>> listElements() {
            return this.registry.entrySet().stream().map(Entry::getKey);
        }

        @Override
        public Optional<? extends HolderSet<T>> get(TagKey<T> tagkey) {
            return this.registry.getTag(tagkey);
        }

        @Override
        public Stream<TagKey<T>> listTags() {
            return this.registry.getTagNames();
        }
    }
}
