package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;

public class DataConverterAdvancementBase extends DataFix {

    private final String a;
    private final Function<String, String> b;

    public DataConverterAdvancementBase(Schema schema, boolean flag, String s, Function<String, String> function) {
        super(schema, flag);
        this.a = s;
        this.b = function;
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.a, this.getInputSchema().getType(DataConverterTypes.ADVANCEMENTS), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return dynamic.updateMapValues((pair) -> {
                    String s = ((Dynamic) pair.getFirst()).asString("");

                    return pair.mapFirst((dynamic1) -> {
                        return dynamic.createString((String) this.b.apply(s));
                    });
                });
            });
        });
    }
}
