package net.minecraft.tags;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.MinecraftKey;

public class TagStatic {

    private static final Map<MinecraftKey, TagUtil<?>> a = Maps.newHashMap();

    public static <T> TagUtil<T> a(MinecraftKey minecraftkey, Function<ITagRegistry, Tags<T>> function) {
        TagUtil<T> tagutil = new TagUtil<>(function);
        TagUtil<?> tagutil1 = (TagUtil) TagStatic.a.putIfAbsent(minecraftkey, tagutil);

        if (tagutil1 != null) {
            throw new IllegalStateException("Duplicate entry for static tag collection: " + minecraftkey);
        } else {
            return tagutil;
        }
    }

    public static void a(ITagRegistry itagregistry) {
        TagStatic.a.values().forEach((tagutil) -> {
            tagutil.a(itagregistry);
        });
    }

    public static Multimap<MinecraftKey, MinecraftKey> b(ITagRegistry itagregistry) {
        Multimap<MinecraftKey, MinecraftKey> multimap = HashMultimap.create();

        TagStatic.a.forEach((minecraftkey, tagutil) -> {
            multimap.putAll(minecraftkey, tagutil.b(itagregistry));
        });
        return multimap;
    }

    public static void b() {
        TagUtil[] atagutil = new TagUtil[]{TagsBlock.a, TagsItem.a, TagsFluid.a, TagsEntity.a};
        boolean flag = Stream.of(atagutil).anyMatch((tagutil) -> {
            return !TagStatic.a.containsValue(tagutil);
        });

        if (flag) {
            throw new IllegalStateException("Missing helper registrations");
        }
    }
}
