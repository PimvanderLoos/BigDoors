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
    private Tags<T> source = Tags.empty();
    private final List<TagUtil.a<T>> wrappers = Lists.newArrayList();

    public TagUtil(ResourceKey<? extends IRegistry<T>> resourcekey, String s) {
        this.key = resourcekey;
        this.directory = s;
    }

    public Tag.e<T> bind(String s) {
        TagUtil.a<T> tagutil_a = new TagUtil.a<>(new MinecraftKey(s));

        this.wrappers.add(tagutil_a);
        return tagutil_a;
    }

    public void resetToEmpty() {
        this.source = Tags.empty();
        Tag<T> tag = TagSet.empty();

        this.wrappers.forEach((tagutil_a) -> {
            tagutil_a.rebind((minecraftkey) -> {
                return tag;
            });
        });
    }

    public void reset(ITagRegistry itagregistry) {
        Tags<T> tags = itagregistry.getOrEmpty(this.key);

        this.source = tags;
        this.wrappers.forEach((tagutil_a) -> {
            Objects.requireNonNull(tags);
            tagutil_a.rebind(tags::getTag);
        });
    }

    public Tags<T> getAllTags() {
        return this.source;
    }

    public Set<MinecraftKey> getMissingTags(ITagRegistry itagregistry) {
        Tags<T> tags = itagregistry.getOrEmpty(this.key);
        Set<MinecraftKey> set = (Set) this.wrappers.stream().map(TagUtil.a::getName).collect(Collectors.toSet());
        ImmutableSet<MinecraftKey> immutableset = ImmutableSet.copyOf(tags.getAvailableTags());

        return Sets.difference(set, immutableset);
    }

    public ResourceKey<? extends IRegistry<T>> getKey() {
        return this.key;
    }

    public String getDirectory() {
        return this.directory;
    }

    protected void addToCollection(ITagRegistry.a itagregistry_a) {
        itagregistry_a.add(this.key, Tags.of((Map) this.wrappers.stream().collect(Collectors.toMap(Tag.e::getName, (tagutil_a) -> {
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
        public MinecraftKey getName() {
            return this.name;
        }

        private Tag<T> resolve() {
            if (this.tag == null) {
                throw new IllegalStateException("Tag " + this.name + " used before it was bound");
            } else {
                return this.tag;
            }
        }

        void rebind(Function<MinecraftKey, Tag<T>> function) {
            this.tag = (Tag) function.apply(this.name);
        }

        @Override
        public boolean contains(T t0) {
            return this.resolve().contains(t0);
        }

        @Override
        public List<T> getValues() {
            return this.resolve().getValues();
        }
    }
}
