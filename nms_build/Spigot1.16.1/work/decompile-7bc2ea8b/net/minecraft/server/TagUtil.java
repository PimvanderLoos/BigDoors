package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class TagUtil<T> {

    private final Tags<T> a = new Tags<>((minecraftkey) -> {
        return Optional.empty();
    }, "", "");
    private Tags<T> b;
    private final List<TagUtil.a<T>> c;

    public TagUtil() {
        this.b = this.a;
        this.c = Lists.newArrayList();
    }

    public Tag.e<T> a(String s) {
        TagUtil.a<T> tagutil_a = new TagUtil.a<>(new MinecraftKey(s));

        this.c.add(tagutil_a);
        return tagutil_a;
    }

    public void a(Tags<T> tags) {
        this.b = tags;
        this.c.forEach((tagutil_a) -> {
            tagutil_a.a(tags::a);
        });
    }

    public Tags<T> b() {
        return this.b;
    }

    public List<TagUtil.a<T>> c() {
        return this.c;
    }

    public Set<MinecraftKey> b(Tags<T> tags) {
        Set<MinecraftKey> set = (Set) this.c.stream().map(TagUtil.a::a).collect(Collectors.toSet());
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.copyOf(tags.a());

        return Sets.difference(set, immutableset);
    }

    public static class a<T> implements Tag.e<T> {

        @Nullable
        private Tag<T> b;
        protected final MinecraftKey a;

        private a(MinecraftKey minecraftkey) {
            this.a = minecraftkey;
        }

        @Override
        public MinecraftKey a() {
            return this.a;
        }

        private Tag<T> c() {
            if (this.b == null) {
                throw new IllegalStateException("Tag " + this.a + " used before it was bound");
            } else {
                return this.b;
            }
        }

        void a(Function<MinecraftKey, Tag<T>> function) {
            this.b = (Tag) function.apply(this.a);
        }

        @Override
        public boolean isTagged(T t0) {
            return this.c().isTagged(t0);
        }

        @Override
        public List<T> getTagged() {
            return this.c().getTagged();
        }
    }
}
