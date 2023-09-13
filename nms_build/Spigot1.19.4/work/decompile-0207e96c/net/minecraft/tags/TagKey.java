package net.minecraft.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.Optional;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public record TagKey<T> (ResourceKey<? extends IRegistry<T>> registry, MinecraftKey location) {

    private static final Interner<TagKey<?>> VALUES = Interners.newWeakInterner();

    public static <T> Codec<TagKey<T>> codec(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return MinecraftKey.CODEC.xmap((minecraftkey) -> {
            return create(resourcekey, minecraftkey);
        }, TagKey::location);
    }

    public static <T> Codec<TagKey<T>> hashedCodec(ResourceKey<? extends IRegistry<T>> resourcekey) {
        return Codec.STRING.comapFlatMap((s) -> {
            return s.startsWith("#") ? MinecraftKey.read(s.substring(1)).map((minecraftkey) -> {
                return create(resourcekey, minecraftkey);
            }) : DataResult.error(() -> {
                return "Not a tag id";
            });
        }, (tagkey) -> {
            return "#" + tagkey.location;
        });
    }

    public static <T> TagKey<T> create(ResourceKey<? extends IRegistry<T>> resourcekey, MinecraftKey minecraftkey) {
        return (TagKey) TagKey.VALUES.intern(new TagKey<>(resourcekey, minecraftkey));
    }

    public boolean isFor(ResourceKey<? extends IRegistry<?>> resourcekey) {
        return this.registry == resourcekey;
    }

    public <E> Optional<TagKey<E>> cast(ResourceKey<? extends IRegistry<E>> resourcekey) {
        return this.isFor(resourcekey) ? Optional.of(this) : Optional.empty();
    }

    public String toString() {
        MinecraftKey minecraftkey = this.registry.location();

        return "TagKey[" + minecraftkey + " / " + this.location + "]";
    }
}
