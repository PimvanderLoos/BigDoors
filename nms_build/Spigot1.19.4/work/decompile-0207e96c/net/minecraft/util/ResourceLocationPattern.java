package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.MinecraftKey;

public class ResourceLocationPattern {

    public static final Codec<ResourceLocationPattern> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter((resourcelocationpattern) -> {
            return resourcelocationpattern.namespacePattern;
        }), ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter((resourcelocationpattern) -> {
            return resourcelocationpattern.pathPattern;
        })).apply(instance, ResourceLocationPattern::new);
    });
    private final Optional<Pattern> namespacePattern;
    private final Predicate<String> namespacePredicate;
    private final Optional<Pattern> pathPattern;
    private final Predicate<String> pathPredicate;
    private final Predicate<MinecraftKey> locationPredicate;

    private ResourceLocationPattern(Optional<Pattern> optional, Optional<Pattern> optional1) {
        this.namespacePattern = optional;
        this.namespacePredicate = (Predicate) optional.map(Pattern::asPredicate).orElse((s) -> {
            return true;
        });
        this.pathPattern = optional1;
        this.pathPredicate = (Predicate) optional1.map(Pattern::asPredicate).orElse((s) -> {
            return true;
        });
        this.locationPredicate = (minecraftkey) -> {
            return this.namespacePredicate.test(minecraftkey.getNamespace()) && this.pathPredicate.test(minecraftkey.getPath());
        };
    }

    public Predicate<String> namespacePredicate() {
        return this.namespacePredicate;
    }

    public Predicate<String> pathPredicate() {
        return this.pathPredicate;
    }

    public Predicate<MinecraftKey> locationPredicate() {
        return this.locationPredicate;
    }
}
