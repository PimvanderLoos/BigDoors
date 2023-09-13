package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;

public class DataConverterObjectiveRenderType extends DataFix {

    public DataConverterObjectiveRenderType(Schema schema, boolean flag) {
        super(schema, flag);
    }

    private static String getRenderType(String s) {
        return s.equals("health") ? "hearts" : "integer";
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.OBJECTIVE);

        return this.fixTypeEverywhereTyped("ObjectiveRenderTypeFix", type, (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                Optional<String> optional = dynamic.get("RenderType").asString().result();

                if (optional.isEmpty()) {
                    String s = dynamic.get("CriteriaName").asString("");
                    String s1 = getRenderType(s);

                    return dynamic.set("RenderType", dynamic.createString(s1));
                } else {
                    return dynamic;
                }
            });
        });
    }
}
