package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import net.minecraft.network.chat.IChatBaseComponent;

public class DataConverterObjectiveDisplayName extends DataFix {

    public DataConverterObjectiveDisplayName(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.OBJECTIVE);

        return this.fixTypeEverywhereTyped("ObjectiveDisplayNameFix", type, (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                return dynamic.update("DisplayName", (dynamic1) -> {
                    DataResult dataresult = dynamic1.asString().map((s) -> {
                        return IChatBaseComponent.ChatSerializer.toJson(IChatBaseComponent.literal(s));
                    });

                    Objects.requireNonNull(dynamic);
                    return (Dynamic) DataFixUtils.orElse(dataresult.map(dynamic::createString).result(), dynamic1);
                });
            });
        });
    }
}
