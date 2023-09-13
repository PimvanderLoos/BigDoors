package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterBlockEntityKeepPacked extends DataConverterNamedEntity {

    public DataConverterBlockEntityKeepPacked(Schema schema, boolean flag) {
        super(schema, flag, "BlockEntityKeepPacked", DataConverterTypes.BLOCK_ENTITY, "DUMMY");
    }

    private static Dynamic<?> fixTag(Dynamic<?> dynamic) {
        return dynamic.set("keepPacked", dynamic.createBoolean(true));
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), DataConverterBlockEntityKeepPacked::fixTag);
    }
}
