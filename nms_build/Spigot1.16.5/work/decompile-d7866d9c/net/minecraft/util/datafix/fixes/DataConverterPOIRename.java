package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;

public abstract class DataConverterPOIRename extends DataFix {

    public DataConverterPOIRename(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<Pair<String, Dynamic<?>>> type = DSL.named(DataConverterTypes.POI_CHUNK.typeName(), DSL.remainderType());

        if (!Objects.equals(type, this.getInputSchema().getType(DataConverterTypes.POI_CHUNK))) {
            throw new IllegalStateException("Poi type is not what was expected.");
        } else {
            return this.fixTypeEverywhere("POI rename", type, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond(this::a);
                };
            });
        }
    }

    private <T> Dynamic<T> a(Dynamic<T> dynamic) {
        return dynamic.update("Sections", (dynamic1) -> {
            return dynamic1.updateMapValues((pair) -> {
                return pair.mapSecond((dynamic2) -> {
                    return dynamic2.update("Records", (dynamic3) -> {
                        return (Dynamic) DataFixUtils.orElse(this.b(dynamic3), dynamic3);
                    });
                });
            });
        });
    }

    private <T> Optional<Dynamic<T>> b(Dynamic<T> dynamic) {
        return dynamic.asStreamOpt().map((stream) -> {
            return dynamic.createList(stream.map((dynamic1) -> {
                return dynamic1.update("type", (dynamic2) -> {
                    DataResult dataresult = dynamic2.asString().map(this::a);

                    dynamic2.getClass();
                    return (Dynamic) DataFixUtils.orElse(dataresult.map(dynamic2::createString).result(), dynamic2);
                });
            }));
        }).result();
    }

    protected abstract String a(String s);
}
