package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;

public class DataConverterWolf extends DataConverterNamedEntity {

    public DataConverterWolf(Schema schema, boolean flag) {
        super(schema, flag, "EntityWolfColorFix", DataConverterTypes.ENTITY, "minecraft:wolf");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.update("CollarColor", (dynamic) -> {
            return dynamic.createByte((byte) (15 - dynamic.getNumberValue(Integer.valueOf(0)).intValue()));
        });
    }

    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
