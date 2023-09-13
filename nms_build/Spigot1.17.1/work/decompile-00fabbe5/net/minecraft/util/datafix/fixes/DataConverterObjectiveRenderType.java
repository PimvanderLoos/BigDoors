package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;

public class DataConverterObjectiveRenderType extends DataFix {

    public DataConverterObjectiveRenderType(Schema schema, boolean flag) {
        super(schema, flag);
    }

    private static IScoreboardCriteria.EnumScoreboardHealthDisplay a(String s) {
        return s.equals("health") ? IScoreboardCriteria.EnumScoreboardHealthDisplay.HEARTS : IScoreboardCriteria.EnumScoreboardHealthDisplay.INTEGER;
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.OBJECTIVE);

        return this.fixTypeEverywhereTyped("ObjectiveRenderTypeFix", type, (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                Optional<String> optional = dynamic.get("RenderType").asString().result();

                if (!optional.isPresent()) {
                    String s = dynamic.get("CriteriaName").asString("");
                    IScoreboardCriteria.EnumScoreboardHealthDisplay iscoreboardcriteria_enumscoreboardhealthdisplay = a(s);

                    return dynamic.set("RenderType", dynamic.createString(iscoreboardcriteria_enumscoreboardhealthdisplay.a()));
                } else {
                    return dynamic;
                }
            });
        });
    }
}
