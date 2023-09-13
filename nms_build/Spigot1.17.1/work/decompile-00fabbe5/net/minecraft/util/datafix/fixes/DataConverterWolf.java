package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterWolf extends DataConverterNamedEntity {

    public DataConverterWolf(Schema schema, boolean flag) {
        super(schema, flag, "EntityWolfColorFix", DataConverterTypes.ENTITY, "minecraft:wolf");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.update("CollarColor", (dynamic1) -> {
            return dynamic1.createByte((byte) (15 - dynamic1.asInt(0)));
        });
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
