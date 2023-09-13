package net.minecraft.util.datafix.fixes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SavedDataFeaturePoolElementFix extends DataFix {

    private static final Pattern INDEX_PATTERN = Pattern.compile("\\[(\\d+)\\]");
    private static final Set<String> PIECE_TYPE = Sets.newHashSet(new String[]{"minecraft:jigsaw", "minecraft:nvi", "minecraft:pcp", "minecraft:bastionremnant", "minecraft:runtime"});
    private static final Set<String> FEATURES = Sets.newHashSet(new String[]{"minecraft:tree", "minecraft:flower", "minecraft:block_pile", "minecraft:random_patch"});

    public SavedDataFeaturePoolElementFix(Schema schema) {
        super(schema, false);
    }

    public TypeRewriteRule makeRule() {
        return this.writeFixAndRead("SavedDataFeaturePoolElementFix", this.getInputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE), this.getOutputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE), SavedDataFeaturePoolElementFix::fixTag);
    }

    private static <T> Dynamic<T> fixTag(Dynamic<T> dynamic) {
        return dynamic.update("Children", SavedDataFeaturePoolElementFix::updateChildren);
    }

    private static <T> Dynamic<T> updateChildren(Dynamic<T> dynamic) {
        DataResult dataresult = dynamic.asStreamOpt().map(SavedDataFeaturePoolElementFix::updateChildren);

        Objects.requireNonNull(dynamic);
        return (Dynamic) dataresult.map(dynamic::createList).result().orElse(dynamic);
    }

    private static Stream<? extends Dynamic<?>> updateChildren(Stream<? extends Dynamic<?>> stream) {
        return stream.map((dynamic) -> {
            String s = dynamic.get("id").asString("");

            if (!SavedDataFeaturePoolElementFix.PIECE_TYPE.contains(s)) {
                return dynamic;
            } else {
                OptionalDynamic<?> optionaldynamic = dynamic.get("pool_element");

                return !optionaldynamic.get("element_type").asString("").equals("minecraft:feature_pool_element") ? dynamic : dynamic.update("pool_element", (dynamic1) -> {
                    return dynamic1.update("feature", SavedDataFeaturePoolElementFix::fixFeature);
                });
            }
        });
    }

    private static <T> OptionalDynamic<T> get(Dynamic<T> dynamic, String... astring) {
        if (astring.length == 0) {
            throw new IllegalArgumentException("Missing path");
        } else {
            OptionalDynamic<T> optionaldynamic = dynamic.get(astring[0]);

            for (int i = 1; i < astring.length; ++i) {
                String s = astring[i];
                Matcher matcher = SavedDataFeaturePoolElementFix.INDEX_PATTERN.matcher(s);

                if (matcher.matches()) {
                    int j = Integer.parseInt(matcher.group(1));
                    List<? extends Dynamic<T>> list = optionaldynamic.asList(Function.identity());

                    if (j >= 0 && j < list.size()) {
                        optionaldynamic = new OptionalDynamic(dynamic.getOps(), DataResult.success((Dynamic) list.get(j)));
                    } else {
                        optionaldynamic = new OptionalDynamic(dynamic.getOps(), DataResult.error(() -> {
                            return "Missing id:" + j;
                        }));
                    }
                } else {
                    optionaldynamic = optionaldynamic.get(s);
                }
            }

            return optionaldynamic;
        }
    }

    @VisibleForTesting
    protected static Dynamic<?> fixFeature(Dynamic<?> dynamic) {
        Optional<String> optional = getReplacement(get(dynamic, "type").asString(""), get(dynamic, "name").asString(""), get(dynamic, "config", "state_provider", "type").asString(""), get(dynamic, "config", "state_provider", "state", "Name").asString(""), get(dynamic, "config", "state_provider", "entries", "[0]", "data", "Name").asString(""), get(dynamic, "config", "foliage_placer", "type").asString(""), get(dynamic, "config", "leaves_provider", "state", "Name").asString(""));

        return optional.isPresent() ? dynamic.createString((String) optional.get()) : dynamic;
    }

    private static Optional<String> getReplacement(String s, String s1, String s2, String s3, String s4, String s5, String s6) {
        String s7;

        if (!s.isEmpty()) {
            s7 = s;
        } else {
            if (s1.isEmpty()) {
                return Optional.empty();
            }

            if ("minecraft:normal_tree".equals(s1)) {
                s7 = "minecraft:tree";
            } else {
                s7 = s1;
            }
        }

        if (SavedDataFeaturePoolElementFix.FEATURES.contains(s7)) {
            if ("minecraft:random_patch".equals(s7)) {
                if ("minecraft:simple_state_provider".equals(s2)) {
                    if ("minecraft:sweet_berry_bush".equals(s3)) {
                        return Optional.of("minecraft:patch_berry_bush");
                    }

                    if ("minecraft:cactus".equals(s3)) {
                        return Optional.of("minecraft:patch_cactus");
                    }
                } else if ("minecraft:weighted_state_provider".equals(s2) && ("minecraft:grass".equals(s4) || "minecraft:fern".equals(s4))) {
                    return Optional.of("minecraft:patch_taiga_grass");
                }
            } else if ("minecraft:block_pile".equals(s7)) {
                if (!"minecraft:simple_state_provider".equals(s2) && !"minecraft:rotated_block_provider".equals(s2)) {
                    if ("minecraft:weighted_state_provider".equals(s2)) {
                        if ("minecraft:packed_ice".equals(s4) || "minecraft:blue_ice".equals(s4)) {
                            return Optional.of("minecraft:pile_ice");
                        }

                        if ("minecraft:jack_o_lantern".equals(s4) || "minecraft:pumpkin".equals(s4)) {
                            return Optional.of("minecraft:pile_pumpkin");
                        }
                    }
                } else {
                    if ("minecraft:hay_block".equals(s3)) {
                        return Optional.of("minecraft:pile_hay");
                    }

                    if ("minecraft:melon".equals(s3)) {
                        return Optional.of("minecraft:pile_melon");
                    }

                    if ("minecraft:snow".equals(s3)) {
                        return Optional.of("minecraft:pile_snow");
                    }
                }
            } else {
                if ("minecraft:flower".equals(s7)) {
                    return Optional.of("minecraft:flower_plain");
                }

                if ("minecraft:tree".equals(s7)) {
                    if ("minecraft:acacia_foliage_placer".equals(s5)) {
                        return Optional.of("minecraft:acacia");
                    }

                    if ("minecraft:blob_foliage_placer".equals(s5) && "minecraft:oak_leaves".equals(s6)) {
                        return Optional.of("minecraft:oak");
                    }

                    if ("minecraft:pine_foliage_placer".equals(s5)) {
                        return Optional.of("minecraft:pine");
                    }

                    if ("minecraft:spruce_foliage_placer".equals(s5)) {
                        return Optional.of("minecraft:spruce");
                    }
                }
            }
        }

        return Optional.empty();
    }
}
