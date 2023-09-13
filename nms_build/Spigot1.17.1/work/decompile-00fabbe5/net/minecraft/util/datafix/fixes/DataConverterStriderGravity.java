package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterStriderGravity extends DataConverterNamedEntity {

    public DataConverterStriderGravity(Schema schema, boolean flag) {
        super(schema, flag, "StriderGravityFix", DataConverterTypes.ENTITY, "minecraft:strider");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.get("NoGravity").asBoolean(false) ? dynamic.set("NoGravity", dynamic.createBoolean(false)) : dynamic;
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
