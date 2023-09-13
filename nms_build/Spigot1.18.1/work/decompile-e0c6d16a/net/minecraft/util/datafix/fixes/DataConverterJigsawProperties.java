package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterJigsawProperties extends DataConverterNamedEntity {

    public DataConverterJigsawProperties(Schema schema, boolean flag) {
        super(schema, flag, "JigsawPropertiesFix", DataConverterTypes.BLOCK_ENTITY, "minecraft:jigsaw");
    }

    private static Dynamic<?> fixTag(Dynamic<?> dynamic) {
        String s = dynamic.get("attachement_type").asString("minecraft:empty");
        String s1 = dynamic.get("target_pool").asString("minecraft:empty");

        return dynamic.set("name", dynamic.createString(s)).set("target", dynamic.createString(s)).remove("attachement_type").set("pool", dynamic.createString(s1)).remove("target_pool");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), DataConverterJigsawProperties::fixTag);
    }
}
