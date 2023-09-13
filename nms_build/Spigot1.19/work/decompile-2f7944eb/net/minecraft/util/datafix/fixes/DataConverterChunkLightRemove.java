package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class DataConverterChunkLightRemove extends DataFix {

    public DataConverterChunkLightRemove(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        Type<?> type1 = type.findFieldType("Level");
        OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type1);

        return this.fixTypeEverywhereTyped("ChunkLightRemoveFix", type, this.getOutputSchema().getType(DataConverterTypes.CHUNK), (typed) -> {
            return typed.updateTyped(opticfinder, (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    return dynamic.remove("isLightOn");
                });
            });
        });
    }
}
