package net.minecraft.server.packs.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ResourceLocationPattern;

public class ResourceFilterSection {

    private static final Codec<ResourceFilterSection> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.list(ResourceLocationPattern.CODEC).fieldOf("block").forGetter((resourcefiltersection) -> {
            return resourcefiltersection.blockList;
        })).apply(instance, ResourceFilterSection::new);
    });
    public static final MetadataSectionType<ResourceFilterSection> TYPE = MetadataSectionType.fromCodec("filter", ResourceFilterSection.CODEC);
    private final List<ResourceLocationPattern> blockList;

    public ResourceFilterSection(List<ResourceLocationPattern> list) {
        this.blockList = List.copyOf(list);
    }

    public boolean isNamespaceFiltered(String s) {
        return this.blockList.stream().anyMatch((resourcelocationpattern) -> {
            return resourcelocationpattern.namespacePredicate().test(s);
        });
    }

    public boolean isPathFiltered(String s) {
        return this.blockList.stream().anyMatch((resourcelocationpattern) -> {
            return resourcelocationpattern.pathPredicate().test(s);
        });
    }
}
