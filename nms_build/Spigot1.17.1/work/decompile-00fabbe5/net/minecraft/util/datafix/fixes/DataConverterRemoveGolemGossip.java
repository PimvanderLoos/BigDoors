package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterRemoveGolemGossip extends DataConverterNamedEntity {

    public DataConverterRemoveGolemGossip(Schema schema, boolean flag) {
        super(schema, flag, "Remove Golem Gossip Fix", DataConverterTypes.ENTITY, "minecraft:villager");
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), DataConverterRemoveGolemGossip::a);
    }

    private static Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.update("Gossips", (dynamic1) -> {
            return dynamic.createList(dynamic1.asStream().filter((dynamic2) -> {
                return !dynamic2.get("Type").asString("").equals("golem");
            }));
        });
    }
}
