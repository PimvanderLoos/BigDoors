package net.minecraft.server.packs;

import java.util.Map;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;

public class BuiltInMetadata {

    private static final BuiltInMetadata EMPTY = new BuiltInMetadata(Map.of());
    private final Map<ResourcePackMetaParser<?>, ?> values;

    private BuiltInMetadata(Map<ResourcePackMetaParser<?>, ?> map) {
        this.values = map;
    }

    public <T> T get(ResourcePackMetaParser<T> resourcepackmetaparser) {
        return this.values.get(resourcepackmetaparser);
    }

    public static BuiltInMetadata of() {
        return BuiltInMetadata.EMPTY;
    }

    public static <T> BuiltInMetadata of(ResourcePackMetaParser<T> resourcepackmetaparser, T t0) {
        return new BuiltInMetadata(Map.of(resourcepackmetaparser, t0));
    }

    public static <T1, T2> BuiltInMetadata of(ResourcePackMetaParser<T1> resourcepackmetaparser, T1 t1, ResourcePackMetaParser<T2> resourcepackmetaparser1, T2 t2) {
        return new BuiltInMetadata(Map.of(resourcepackmetaparser, t1, resourcepackmetaparser1, t2));
    }
}
