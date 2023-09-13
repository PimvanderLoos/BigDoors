package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;

public class BlendingDataRemoveFromNetherEndFix extends DataFix {

    public BlendingDataRemoveFromNetherEndFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(DataConverterTypes.CHUNK);

        return this.fixTypeEverywhereTyped("BlendingDataRemoveFromNetherEndFix", type, (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return updateChunkTag(dynamic, dynamic.get("__context"));
            });
        });
    }

    private static Dynamic<?> updateChunkTag(Dynamic<?> dynamic, OptionalDynamic<?> optionaldynamic) {
        boolean flag = "minecraft:overworld".equals(optionaldynamic.get("dimension").asString().result().orElse(""));

        return flag ? dynamic : dynamic.remove("blending_data");
    }
}
