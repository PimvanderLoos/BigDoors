package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.packs.metadata.ResourcePackMetaParser;
import net.minecraft.util.ExtraCodecs;
import org.slf4j.Logger;

public class ResourceFilterSection {

    static final Logger LOGGER = LogUtils.getLogger();
    static final Codec<ResourceFilterSection> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.list(ResourceFilterSection.a.CODEC).fieldOf("block").forGetter((resourcefiltersection) -> {
            return resourcefiltersection.blockList;
        })).apply(instance, ResourceFilterSection::new);
    });
    public static final ResourcePackMetaParser<ResourceFilterSection> SERIALIZER = new ResourcePackMetaParser<ResourceFilterSection>() {
        @Override
        public String getMetadataSectionName() {
            return "filter";
        }

        @Override
        public ResourceFilterSection fromJson(JsonObject jsonobject) {
            DataResult dataresult = ResourceFilterSection.CODEC.parse(JsonOps.INSTANCE, jsonobject);
            Logger logger = ResourceFilterSection.LOGGER;

            Objects.requireNonNull(logger);
            return (ResourceFilterSection) dataresult.getOrThrow(false, logger::error);
        }
    };
    private final List<ResourceFilterSection.a> blockList;

    public ResourceFilterSection(List<ResourceFilterSection.a> list) {
        this.blockList = List.copyOf(list);
    }

    public boolean isNamespaceFiltered(String s) {
        return this.blockList.stream().anyMatch((resourcefiltersection_a) -> {
            return resourcefiltersection_a.namespacePredicate.test(s);
        });
    }

    public boolean isPathFiltered(String s) {
        return this.blockList.stream().anyMatch((resourcefiltersection_a) -> {
            return resourcefiltersection_a.pathPredicate.test(s);
        });
    }

    private static class a implements Predicate<MinecraftKey> {

        static final Codec<ResourceFilterSection.a> CODEC = RecordCodecBuilder.create((instance) -> {
            return instance.group(ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter((resourcefiltersection_a) -> {
                return resourcefiltersection_a.namespacePattern;
            }), ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter((resourcefiltersection_a) -> {
                return resourcefiltersection_a.pathPattern;
            })).apply(instance, ResourceFilterSection.a::new);
        });
        private final Optional<Pattern> namespacePattern;
        final Predicate<String> namespacePredicate;
        private final Optional<Pattern> pathPattern;
        final Predicate<String> pathPredicate;

        private a(Optional<Pattern> optional, Optional<Pattern> optional1) {
            this.namespacePattern = optional;
            this.namespacePredicate = (Predicate) optional.map(Pattern::asPredicate).orElse((s) -> {
                return true;
            });
            this.pathPattern = optional1;
            this.pathPredicate = (Predicate) optional1.map(Pattern::asPredicate).orElse((s) -> {
                return true;
            });
        }

        public boolean test(MinecraftKey minecraftkey) {
            return this.namespacePredicate.test(minecraftkey.getNamespace()) && this.pathPredicate.test(minecraftkey.getPath());
        }
    }
}
