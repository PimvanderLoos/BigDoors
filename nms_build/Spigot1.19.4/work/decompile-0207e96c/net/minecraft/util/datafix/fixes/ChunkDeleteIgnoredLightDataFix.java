package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;

public class ChunkDeleteIgnoredLightDataFix extends DataFix {

    public ChunkDeleteIgnoredLightDataFix(Schema schema) {
        super(schema, true);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        OpticFinder<?> opticfinder = type.findField("sections");

        return this.fixTypeEverywhereTyped("ChunkDeleteIgnoredLightDataFix", type, (typed) -> {
            boolean flag = ((Dynamic) typed.get(DSL.remainderFinder())).get("isLightOn").asBoolean(false);

            return !flag ? typed.updateTyped(opticfinder, (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    return dynamic.remove("BlockLight").remove("SkyLight");
                });
            }) : typed;
        });
    }
}
