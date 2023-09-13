package net.minecraft.core;

import java.util.Optional;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public interface HolderGetter<T> {

    Optional<Holder.c<T>> get(ResourceKey<T> resourcekey);

    default Holder.c<T> getOrThrow(ResourceKey<T> resourcekey) {
        return (Holder.c) this.get(resourcekey).orElseThrow(() -> {
            return new IllegalStateException("Missing element " + resourcekey);
        });
    }

    Optional<HolderSet.Named<T>> get(TagKey<T> tagkey);

    default HolderSet.Named<T> getOrThrow(TagKey<T> tagkey) {
        return (HolderSet.Named) this.get(tagkey).orElseThrow(() -> {
            return new IllegalStateException("Missing tag " + tagkey);
        });
    }

    public interface a {

        <T> Optional<HolderGetter<T>> lookup(ResourceKey<? extends IRegistry<? extends T>> resourcekey);

        default <T> HolderGetter<T> lookupOrThrow(ResourceKey<? extends IRegistry<? extends T>> resourcekey) {
            return (HolderGetter) this.lookup(resourcekey).orElseThrow(() -> {
                return new IllegalStateException("Registry " + resourcekey.location() + " not found");
            });
        }
    }
}
