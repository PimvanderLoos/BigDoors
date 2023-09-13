package net.minecraft.tags;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.ExtraCodecs;

public class TagEntry {

    private static final Codec<TagEntry> FULL_CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("id").forGetter(TagEntry::elementOrTag), Codec.BOOL.optionalFieldOf("required", true).forGetter((tagentry) -> {
            return tagentry.required;
        })).apply(instance, TagEntry::new);
    });
    public static final Codec<TagEntry> CODEC = Codec.either(ExtraCodecs.TAG_OR_ELEMENT_ID, TagEntry.FULL_CODEC).xmap((either) -> {
        return (TagEntry) either.map((extracodecs_d) -> {
            return new TagEntry(extracodecs_d, true);
        }, (tagentry) -> {
            return tagentry;
        });
    }, (tagentry) -> {
        return tagentry.required ? Either.left(tagentry.elementOrTag()) : Either.right(tagentry);
    });
    private final MinecraftKey id;
    private final boolean tag;
    private final boolean required;

    private TagEntry(MinecraftKey minecraftkey, boolean flag, boolean flag1) {
        this.id = minecraftkey;
        this.tag = flag;
        this.required = flag1;
    }

    private TagEntry(ExtraCodecs.d extracodecs_d, boolean flag) {
        this.id = extracodecs_d.id();
        this.tag = extracodecs_d.tag();
        this.required = flag;
    }

    private ExtraCodecs.d elementOrTag() {
        return new ExtraCodecs.d(this.id, this.tag);
    }

    public static TagEntry element(MinecraftKey minecraftkey) {
        return new TagEntry(minecraftkey, false, true);
    }

    public static TagEntry optionalElement(MinecraftKey minecraftkey) {
        return new TagEntry(minecraftkey, false, false);
    }

    public static TagEntry tag(MinecraftKey minecraftkey) {
        return new TagEntry(minecraftkey, true, true);
    }

    public static TagEntry optionalTag(MinecraftKey minecraftkey) {
        return new TagEntry(minecraftkey, true, false);
    }

    public <T> boolean build(TagEntry.a<T> tagentry_a, Consumer<T> consumer) {
        if (this.tag) {
            Collection<T> collection = tagentry_a.tag(this.id);

            if (collection == null) {
                return !this.required;
            }

            collection.forEach(consumer);
        } else {
            T t0 = tagentry_a.element(this.id);

            if (t0 == null) {
                return !this.required;
            }

            consumer.accept(t0);
        }

        return true;
    }

    public void visitRequiredDependencies(Consumer<MinecraftKey> consumer) {
        if (this.tag && this.required) {
            consumer.accept(this.id);
        }

    }

    public void visitOptionalDependencies(Consumer<MinecraftKey> consumer) {
        if (this.tag && !this.required) {
            consumer.accept(this.id);
        }

    }

    public boolean verifyIfPresent(Predicate<MinecraftKey> predicate, Predicate<MinecraftKey> predicate1) {
        return !this.required || (this.tag ? predicate1 : predicate).test(this.id);
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder();

        if (this.tag) {
            stringbuilder.append('#');
        }

        stringbuilder.append(this.id);
        if (!this.required) {
            stringbuilder.append('?');
        }

        return stringbuilder.toString();
    }

    public interface a<T> {

        @Nullable
        T element(MinecraftKey minecraftkey);

        @Nullable
        Collection<T> tag(MinecraftKey minecraftkey);
    }
}
