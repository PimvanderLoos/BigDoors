package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterArmorStand extends DataConverterNamedEntity {

    public DataConverterArmorStand(Schema schema, boolean flag) {
        super(schema, flag, "EntityArmorStandSilentFix", DataConverterTypes.ENTITY, "ArmorStand");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.get("Silent").asBoolean(false) && !dynamic.get("Marker").asBoolean(false) ? dynamic.remove("Silent") : dynamic;
    }

    @Override
    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
