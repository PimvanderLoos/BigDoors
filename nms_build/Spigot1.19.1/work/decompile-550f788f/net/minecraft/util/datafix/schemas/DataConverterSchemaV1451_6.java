package net.minecraft.util.datafix.schemas;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.Hook.HookFunction;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.util.datafix.fixes.DataConverterTypes;

public class DataConverterSchemaV1451_6 extends DataConverterSchemaNamed {

    public static final String SPECIAL_OBJECTIVE_MARKER = "_special";
    protected static final HookFunction UNPACK_OBJECTIVE_ID = new HookFunction() {
        public <T> T apply(DynamicOps<T> dynamicops, T t0) {
            Dynamic<T> dynamic = new Dynamic(dynamicops, t0);

            return ((Dynamic) DataFixUtils.orElse(dynamic.get("CriteriaName").asString().get().left().map((s) -> {
                int i = s.indexOf(58);

                if (i < 0) {
                    return Pair.of("_special", s);
                } else {
                    try {
                        MinecraftKey minecraftkey = MinecraftKey.of(s.substring(0, i), '.');
                        MinecraftKey minecraftkey1 = MinecraftKey.of(s.substring(i + 1), '.');

                        return Pair.of(minecraftkey.toString(), minecraftkey1.toString());
                    } catch (Exception exception) {
                        return Pair.of("_special", s);
                    }
                }
            }).map((pair) -> {
                return dynamic.set("CriteriaType", dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString((String) pair.getFirst()), dynamic.createString("id"), dynamic.createString((String) pair.getSecond()))));
            }), dynamic)).getValue();
        }
    };
    protected static final HookFunction REPACK_OBJECTIVE_ID = new HookFunction() {
        private String packWithDot(String s) {
            MinecraftKey minecraftkey = MinecraftKey.tryParse(s);

            return minecraftkey != null ? minecraftkey.getNamespace() + "." + minecraftkey.getPath() : s;
        }

        public <T> T apply(DynamicOps<T> dynamicops, T t0) {
            Dynamic<T> dynamic = new Dynamic(dynamicops, t0);
            Optional<Dynamic<T>> optional = dynamic.get("CriteriaType").get().get().left().flatMap((dynamic1) -> {
                Optional<String> optional1 = dynamic1.get("type").asString().get().left();
                Optional<String> optional2 = dynamic1.get("id").asString().get().left();

                if (optional1.isPresent() && optional2.isPresent()) {
                    String s = (String) optional1.get();

                    if (s.equals("_special")) {
                        return Optional.of(dynamic.createString((String) optional2.get()));
                    } else {
                        String s1 = this.packWithDot(s);

                        return Optional.of(dynamic1.createString(s1 + ":" + this.packWithDot((String) optional2.get())));
                    }
                } else {
                    return Optional.empty();
                }
            });

            return ((Dynamic) DataFixUtils.orElse(optional.map((dynamic1) -> {
                return dynamic.set("CriteriaName", dynamic1).remove("CriteriaType");
            }), dynamic)).getValue();
        }
    };

    public DataConverterSchemaV1451_6(int i, Schema schema) {
        super(i, schema);
    }

    public void registerTypes(Schema schema, Map<String, Supplier<TypeTemplate>> map, Map<String, Supplier<TypeTemplate>> map1) {
        super.registerTypes(schema, map, map1);
        Supplier<TypeTemplate> supplier = () -> {
            return DSL.compoundList(DataConverterTypes.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
        };

        schema.registerType(false, DataConverterTypes.STATS, () -> {
            return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(DataConverterTypes.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate) supplier.get(), "minecraft:used", (TypeTemplate) supplier.get(), "minecraft:broken", (TypeTemplate) supplier.get(), "minecraft:picked_up", (TypeTemplate) supplier.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate) supplier.get(), "minecraft:killed", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(DataConverterTypes.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(namespacedString()), DSL.constType(DSL.intType())))));
        });
        Map<String, Supplier<TypeTemplate>> map2 = createCriterionTypes(schema);

        schema.registerType(false, DataConverterTypes.OBJECTIVE, () -> {
            return DSL.hook(DSL.optionalFields("CriteriaType", DSL.taggedChoiceLazy("type", DSL.string(), map2)), DataConverterSchemaV1451_6.UNPACK_OBJECTIVE_ID, DataConverterSchemaV1451_6.REPACK_OBJECTIVE_ID);
        });
    }

    protected static Map<String, Supplier<TypeTemplate>> createCriterionTypes(Schema schema) {
        Supplier<TypeTemplate> supplier = () -> {
            return DSL.optionalFields("id", DataConverterTypes.ITEM_NAME.in(schema));
        };
        Supplier<TypeTemplate> supplier1 = () -> {
            return DSL.optionalFields("id", DataConverterTypes.BLOCK_NAME.in(schema));
        };
        Supplier<TypeTemplate> supplier2 = () -> {
            return DSL.optionalFields("id", DataConverterTypes.ENTITY_NAME.in(schema));
        };
        Map<String, Supplier<TypeTemplate>> map = Maps.newHashMap();

        map.put("minecraft:mined", supplier1);
        map.put("minecraft:crafted", supplier);
        map.put("minecraft:used", supplier);
        map.put("minecraft:broken", supplier);
        map.put("minecraft:picked_up", supplier);
        map.put("minecraft:dropped", supplier);
        map.put("minecraft:killed", supplier2);
        map.put("minecraft:killed_by", supplier2);
        map.put("minecraft:custom", () -> {
            return DSL.optionalFields("id", DSL.constType(namespacedString()));
        });
        map.put("_special", () -> {
            return DSL.optionalFields("id", DSL.constType(DSL.string()));
        });
        return map;
    }
}
