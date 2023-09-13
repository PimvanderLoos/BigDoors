package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;

public class EntityGoatMissingStateFix extends DataConverterNamedEntity {

    public EntityGoatMissingStateFix(Schema schema) {
        super(schema, false, "EntityGoatMissingStateFix", DataConverterTypes.ENTITY, "minecraft:goat");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.set("HasLeftHorn", dynamic.createBoolean(true)).set("HasRightHorn", dynamic.createBoolean(true));
        });
    }
}
