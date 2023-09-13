package net.minecraft.core;

import com.mojang.serialization.Lifecycle;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.flag.FeatureFlagSet;

public interface HolderLookup<T> extends HolderGetter<T> {

    Stream<Holder.c<T>> listElements();

    default Stream<ResourceKey<T>> listElementIds() {
        return this.listElements().map(Holder.c::key);
    }

    Stream<HolderSet.Named<T>> listTags();

    default Stream<TagKey<T>> listTagIds() {
        return this.listTags().map(HolderSet.Named::key);
    }

    default HolderLookup<T> filterElements(final Predicate<T> predicate) {
        return new HolderLookup.a<T>(this) {
            @Override
            public Optional<Holder.c<T>> get(ResourceKey<T> resourcekey) {
                return this.parent.get(resourcekey).filter((holder_c) -> {
                    return predicate.test(holder_c.value());
                });
            }

            @Override
            public Stream<Holder.c<T>> listElements() {
                return this.parent.listElements().filter((holder_c) -> {
                    return predicate.test(holder_c.value());
                });
            }
        };
    }

    public interface b {

        <T> Optional<HolderLookup.c<T>> lookup(ResourceKey<? extends IRegistry<? extends T>> resourcekey);

        default <T> HolderLookup.c<T> lookupOrThrow(ResourceKey<? extends IRegistry<? extends T>> resourcekey) {
            return (HolderLookup.c) this.lookup(resourcekey).orElseThrow(() -> {
                return new IllegalStateException("Registry " + resourcekey.location() + " not found");
            });
        }

        default HolderGetter.a asGetterLookup() {
            return new HolderGetter.a() {
                @Override
                public <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends IRegistry<? extends T>> resourcekey) {
                    return b.this.lookup(resourcekey).map((holderlookup_c) -> {
                        return holderlookup_c;
                    });
                }
            };
        }

        static HolderLookup.b create(Stream<HolderLookup.c<?>> stream) {
            final Map<ResourceKey<? extends IRegistry<?>>, HolderLookup.c<?>> map = (Map) stream.collect(Collectors.toUnmodifiableMap(HolderLookup.c::key, (holderlookup_c) -> {
                return holderlookup_c;
            }));

            return new HolderLookup.b() {
                @Override
                public <T> Optional<HolderLookup.c<T>> lookup(ResourceKey<? extends IRegistry<? extends T>> resourcekey) {
                    return Optional.ofNullable((HolderLookup.c) map.get(resourcekey));
                }
            };
        }
    }

    public static class a<T> implements HolderLookup<T> {

        protected final HolderLookup<T> parent;

        public a(HolderLookup<T> holderlookup) {
            this.parent = holderlookup;
        }

        @Override
        public Optional<Holder.c<T>> get(ResourceKey<T> resourcekey) {
            return this.parent.get(resourcekey);
        }

        @Override
        public Stream<Holder.c<T>> listElements() {
            return this.parent.listElements();
        }

        @Override
        public Optional<HolderSet.Named<T>> get(TagKey<T> tagkey) {
            return this.parent.get(tagkey);
        }

        @Override
        public Stream<HolderSet.Named<T>> listTags() {
            return this.parent.listTags();
        }
    }

    public interface c<T> extends HolderLookup<T>, HolderOwner<T> {

        ResourceKey<? extends IRegistry<? extends T>> key();

        Lifecycle registryLifecycle();

        default HolderLookup<T> filterFeatures(FeatureFlagSet featureflagset) {
            return (HolderLookup) (FeatureElement.FILTERED_REGISTRIES.contains(this.key()) ? this.filterElements((object) -> {
                return ((FeatureElement) object).isEnabled(featureflagset);
            }) : this);
        }

        public abstract static class a<T> implements HolderLookup.c<T> {

            public a() {}

            protected abstract HolderLookup.c<T> parent();

            @Override
            public ResourceKey<? extends IRegistry<? extends T>> key() {
                return this.parent().key();
            }

            @Override
            public Lifecycle registryLifecycle() {
                return this.parent().registryLifecycle();
            }

            @Override
            public Optional<Holder.c<T>> get(ResourceKey<T> resourcekey) {
                return this.parent().get(resourcekey);
            }

            @Override
            public Stream<Holder.c<T>> listElements() {
                return this.parent().listElements();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> tagkey) {
                return this.parent().get(tagkey);
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
                return this.parent().listTags();
            }
        }
    }
}
