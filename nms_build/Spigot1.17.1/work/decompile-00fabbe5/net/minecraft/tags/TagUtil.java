package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;

public class TagUtil<T> {

    private final ResourceKey<? extends IRegistry<T>> key;
    private final String directory;
    private Tags<T> source = Tags.c();
    private final List<TagUtil.a<T>> wrappers = Lists.newArrayList();

    public TagUtil(ResourceKey<? extends IRegistry<T>> resourcekey, String s) {
        this.key = resourcekey;
        this.directory = s;
    }

    public Tag.e<T> a(String s) {
        TagUtil.a<T> tagutil_a = new TagUtil.a<>(new MinecraftKey(s));

        this.wrappers.add(tagutil_a);
        return tagutil_a;
    }

    public void a() {
        this.source = Tags.c();
        Tag<T> tag = TagSet.a();

        this.wrappers.forEach((tagutil_a) -> {
            tagutil_a.a((minecraftkey) -> {
                return tag;
            });
        });
    }

    public void a(ITagRegistry itagregistry) {
        Tags<T> tags = itagregistry.a(this.key);

        this.source = tags;
        this.wrappers.forEach((tagutil_a) -> {
            Objects.requireNonNull(tags);
            tagutil_a.a(tags::a);
        });
    }

    public Tags<T> b() {
        return this.source;
    }

    public Set<MinecraftKey> b(ITagRegistry itagregistry) {
        Tags<T> tags = itagregistry.a(this.key);
        Set<MinecraftKey> set = (Set) this.wrappers.stream().map(TagUtil.a::a).collect(Collectors.toSet());
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.copyOf(tags.b());

        return Sets.difference(set, immutableset);
    }

    public ResourceKey<? extends IRegistry<T>> c() {
        return this.key;
    }

    public String d() {
        return this.directory;
    }

    protected void a(ITagRegistry.a itagregistry_a) {
        itagregistry_a.a(this.key, Tags.a((Map) this.wrappers.stream().collect(Collectors.toMap(Tag.e::a, (tagutil_a) -> {
            return tagutil_a;
        }))));
    }

    private static class a<T> implements Tag.e<T> {

        @Nullable
        private Tag<T> tag;
        protected final MinecraftKey name;

        a(MinecraftKey minecraftkey) {
            this.name = minecraftkey;
        }

        @Override
        public MinecraftKey a() {
            return this.name;
        }

        private Tag<T> c() {
            if (this.tag == null) {
                throw new IllegalStateException("Tag " + this.name + " used before it was bound");
            } else {
                return this.tag;
            }
        }

        void a(Function<MinecraftKey, Tag<T>> function) {
            this.tag = (Tag) function.apply(this.name);
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
