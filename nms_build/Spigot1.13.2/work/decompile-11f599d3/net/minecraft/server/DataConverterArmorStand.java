package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class DataConverterArmorStand extends DataConverterNamedEntity {

    public DataConverterArmorStand(Schema schema, boolean flag) {
        super(schema, flag, "EntityArmorStandSilentFix", DataConverterTypes.ENTITY, "ArmorStand");
    }

    public Dynamic<?> a(Dynamic<?> dynamic) {
        return dynamic.getBoolean("Silent") && !dynamic.getBoolean("Marker") ? dynamic.remove("Silent") : dynamic;
    }

    protected Typed<?> a(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::a);
    }
}
