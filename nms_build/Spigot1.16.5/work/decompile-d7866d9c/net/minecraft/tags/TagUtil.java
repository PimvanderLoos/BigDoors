package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;

public class TagUtil<T> {

    private Tags<T> a = Tags.c();
    private final List<TagUtil.a<T>> b = Lists.newArrayList();
    private final Function<ITagRegistry, Tags<T>> c;

    public TagUtil(Function<ITagRegistry, Tags<T>> function) {
        this.c = function;
    }

    public Tag.e<T> a(String s) {
        TagUtil.a<T> tagutil_a = new TagUtil.a<>(new MinecraftKey(s));

        this.b.add(tagutil_a);
        return tagutil_a;
    }

    public void a(ITagRegistry itagregistry) {
        Tags<T> tags = (Tags) this.c.apply(itagregistry);

        this.a = tags;
        this.b.forEach((tagutil_a) -> {
            tagutil_a.a(tags::a);
        });
    }

    public Tags<T> b() {
        return this.a;
    }

    public List<? extends Tag.e<T>> c() {
        return this.b;
    }

    public Set<MinecraftKey> b(ITagRegistry itagregistry) {
        Tags<T> tags = (Tags) this.c.apply(itagregistry);
        Set<MinecraftKey> set = (Set) this.b.stream().map(TagUtil.a::a).collect(Collectors.toSet());
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.copyOf(tags.b());

        return Sets.difference(set, immutableset);
    }

    static class a<T> implements Tag.e<T> {

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
