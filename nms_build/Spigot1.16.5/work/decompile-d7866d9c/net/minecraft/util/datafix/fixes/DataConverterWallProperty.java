package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.Set;

public class DataConverterWallProperty extends DataFix {

    private static final Set<String> a = ImmutableSet.of("minecraft:andesite_wall", "minecraft:brick_wall", "minecraft:cobblestone_wall", "minecraft:diorite_wall", "minecraft:end_stone_brick_wall", "minecraft:granite_wall", new String[]{"minecraft:mossy_cobblestone_wall", "minecraft:mossy_stone_brick_wall", "minecraft:nether_brick_wall", "minecraft:prismarine_wall", "minecraft:red_nether_brick_wall", "minecraft:red_sandstone_wall", "minecraft:sandstone_wall", "minecraft:stone_brick_wall"});

    public DataConverterWallProperty(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("WallPropertyFix", this.getInputSchema().getType(DataConverterTypes.BLOCK_STATE), (typed) -> {
            return typed.update(DSL.remainderFinder(), DataConverterWallProperty::a);
        });
    }

    private static String a(String s) {
        return "true".equals(s) ? "low" : "none";
    }

    private static <T> Dynamic<T> a(Dynamic<T> dynamic, String s) {
        return dynamic.update(s, (dynamic1) -> {
            Optional optional = dynamic1.asString().result().map(DataConverterWallProperty::a);

            dynamic1.getClass();
            return (Dynamic) DataFixUtils.orElse(optional.map(dynamic1::createString), dynamic1);
        });
    }

    private static <T> Dynamic<T> a(Dynamic<T> dynamic) {
        Optional optional = dynamic.get("Name").asString().result();
        Set set = DataConverterWallProperty.a;

        set.getClass();
        boolean flag = optional.filter(set::contains).isPresent();

        return !flag ? dynamic : dynamic.update("Properties", (dynamic1) -> {
            Dynamic<?> dynamic2 = a(dynamic1, "east");

            dynamic2 = a(dynamic2, "west");
            dynamic2 = a(dynamic2, "north");
            return a(dynamic2, "south");
        });
    }
}
