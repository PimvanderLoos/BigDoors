package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class DataConverterPOIRebuild extends DataFix {

    public DataConverterPOIRebuild(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<Pair<String, Dynamic<?>>> type = DSL.named(DataConverterTypes.POI_CHUNK.typeName(), DSL.remainderType());

        if (!Objects.equals(type, this.getInputSchema().getType(DataConverterTypes.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        } else {
            return this.fixTypeEverywhere("POI rebuild", type, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond(DataConverterPOIRebuild::a);
                };
            });
        }
    }

    private static <T> Dynamic<T> a(Dynamic<T> dynamic) {
        return dynamic.update("Sections", (dynamic1) -> {
            return dynamic1.updateMapValues((pair) -> {
                return pair.mapSecond((dynamic2) -> {
                    return dynamic2.remove("Valid");
                });
            });
        });
    }
}
