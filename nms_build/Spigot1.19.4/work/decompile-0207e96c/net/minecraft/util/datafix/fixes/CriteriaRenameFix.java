package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.function.UnaryOperator;

public class CriteriaRenameFix extends DataFix {

    private final String name;
    private final String advancementId;
    private final UnaryOperator<String> conversions;

    public CriteriaRenameFix(Schema schema, String s, String s1, UnaryOperator<String> unaryoperator) {
        super(schema, false);
        this.name = s;
        this.advancementId = s1;
        this.conversions = unaryoperator;
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(DataConverterTypes.ADVANCEMENTS), (typed) -> {
            return typed.update(DSL.remainderFinder(), this::fixAdvancements);
        });
    }

    private Dynamic<?> fixAdvancements(Dynamic<?> dynamic) {
        return dynamic.update(this.advancementId, (dynamic1) -> {
            return dynamic1.update("criteria", (dynamic2) -> {
                return dynamic2.updateMapValues((pair) -> {
                    return pair.mapFirst((dynamic3) -> {
                        return (Dynamic) DataFixUtils.orElse(dynamic3.asString().map((s) -> {
                            return dynamic3.createString((String) this.conversions.apply(s));
                        }).result(), dynamic3);
                    });
                });
            });
        });
    }
}
