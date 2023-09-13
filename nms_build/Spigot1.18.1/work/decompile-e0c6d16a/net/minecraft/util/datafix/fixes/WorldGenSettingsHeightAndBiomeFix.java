package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import java.util.stream.Stream;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class WorldGenSettingsHeightAndBiomeFix extends DataFix {

    private static final String NAME = "WorldGenSettingsHeightAndBiomeFix";
    public static final String WAS_PREVIOUSLY_INCREASED_KEY = "has_increased_height_already";

    public WorldGenSettingsHeightAndBiomeFix(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.WORLD_GEN_SETTINGS);
        OpticFinder<?> opticfinder = type.findField("dimensions");
        Type<?> type1 = this.getOutputSchema().getType(DataConverterTypes.WORLD_GEN_SETTINGS);
        Type<?> type2 = type1.findFieldType("dimensions");

        return this.fixTypeEverywhereTyped("WorldGenSettingsHeightAndBiomeFix", type, type1, (typed) -> {
            OptionalDynamic<?> optionaldynamic = ((Dynamic) typed.get(DSL.remainderFinder())).get("has_increased_height_already");
            boolean flag = optionaldynamic.result().isEmpty();
            boolean flag1 = optionaldynamic.asBoolean(true);

            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return dynamic.remove("has_increased_height_already");
            }).updateTyped(opticfinder, type2, (typed1) -> {
                Dynamic<?> dynamic = (Dynamic) typed1.write().result().orElseThrow(() -> {
                    return new IllegalStateException("Malformed WorldGenSettings.dimensions");
                });

                dynamic = dynamic.update("minecraft:overworld", (dynamic1) -> {
                    return dynamic1.update("generator", (dynamic2) -> {
                        String s = dynamic2.get("type").asString("");

                        if ("minecraft:noise".equals(s)) {
                            MutableBoolean mutableboolean = new MutableBoolean();

                            dynamic2 = dynamic2.update("biome_source", (dynamic3) -> {
                                String s1 = dynamic3.get("type").asString("");

                                if (!"minecraft:vanilla_layered".equals(s1) && (!flag || !"minecraft:multi_noise".equals(s1))) {
                                    return dynamic3;
                                } else {
                                    if (dynamic3.get("large_biomes").asBoolean(false)) {
                                        mutableboolean.setTrue();
                                    }

                                    return dynamic3.createMap(ImmutableMap.of(dynamic3.createString("preset"), dynamic3.createString("minecraft:overworld"), dynamic3.createString("type"), dynamic3.createString("minecraft:multi_noise")));
                                }
                            });
                            return mutableboolean.booleanValue() ? dynamic2.update("settings", (dynamic3) -> {
                                return "minecraft:overworld".equals(dynamic3.asString("")) ? dynamic3.createString("minecraft:large_biomes") : dynamic3;
                            }) : dynamic2;
                        } else {
                            return "minecraft:flat".equals(s) ? (flag1 ? dynamic2 : dynamic2.update("settings", (dynamic3) -> {
                                return dynamic3.update("layers", WorldGenSettingsHeightAndBiomeFix::updateLayers);
                            })) : dynamic2;
                        }
                    });
                });
                return (Typed) ((Pair) type2.readTyped(dynamic).result().orElseThrow(() -> {
                    return new IllegalStateException("WorldGenSettingsHeightAndBiomeFix failed.");
                })).getFirst();
            });
        });
    }

    private static Dynamic<?> updateLayers(Dynamic<?> dynamic) {
        Dynamic<?> dynamic1 = dynamic.createMap(ImmutableMap.of(dynamic.createString("height"), dynamic.createInt(64), dynamic.createString("block"), dynamic.createString("minecraft:air")));

        return dynamic.createList(Stream.concat(Stream.of(dynamic1), dynamic.asStream()));
    }
}
