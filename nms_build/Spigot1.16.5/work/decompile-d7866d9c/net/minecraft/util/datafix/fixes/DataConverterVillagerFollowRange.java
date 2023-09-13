package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterVillagerFollowRange extends DataConverterNamedEntity {

    public DataConverterVillagerFollowRange(Schema schema) {
        super(schema, false, "Villager Follow Range Fix", DataConverterTypes.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), DataConverterVillagerFollowRange::a);
    }

    private static Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.update("Attributes", (dynamic1) -> {
            return dynamic.createList(dynamic1.asStream().map((dynamic2) -> {
                return dynamic2.get("Name").asString("").equals("generic.follow_range") && dynamic2.get("Base").asDouble(0.0D) == 16.0D ? dynamic2.set("Base", dynamic2.createDouble(48.0D)) : dynamic2;
            }));
        });
    }
}
