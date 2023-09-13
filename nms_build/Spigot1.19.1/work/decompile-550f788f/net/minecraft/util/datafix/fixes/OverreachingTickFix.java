package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;

public class OverreachingTickFix extends DataFix {

    public OverreachingTickFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        OpticFinder<?> opticfinder = type.findField("block_ticks");

        return this.fixTypeEverywhereTyped("Handle ticks saved in the wrong chunk", type, (typed) -> {
            Optional<? extends Typed<?>> optional = typed.getOptionalTyped(opticfinder);
            Optional<? extends Dynamic<?>> optional1 = optional.isPresent() ? ((Typed) optional.get()).write().result() : Optional.empty();

            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                int i = dynamic.get("xPos").asInt(0);
                int j = dynamic.get("zPos").asInt(0);
                Optional<? extends Dynamic<?>> optional2 = dynamic.get("fluid_ticks").get().result();

                dynamic = extractOverreachingTicks(dynamic, i, j, optional1, "neighbor_block_ticks");
                dynamic = extractOverreachingTicks(dynamic, i, j, optional2, "neighbor_fluid_ticks");
                return dynamic;
            });
        });
    }

    private static Dynamic<?> extractOverreachingTicks(Dynamic<?> dynamic, int i, int j, Optional<? extends Dynamic<?>> optional, String s) {
        if (optional.isPresent()) {
            List<? extends Dynamic<?>> list = ((Dynamic) optional.get()).asStream().filter((dynamic1) -> {
                int k = dynamic1.get("x").asInt(0);
                int l = dynamic1.get("z").asInt(0);
                int i1 = Math.abs(i - (k >> 4));
                int j1 = Math.abs(j - (l >> 4));

                return (i1 != 0 || j1 != 0) && i1 <= 1 && j1 <= 1;
            }).toList();

            if (!list.isEmpty()) {
                dynamic = dynamic.set("UpgradeData", dynamic.get("UpgradeData").orElseEmptyMap().set(s, dynamic.createList(list.stream())));
            }
        }

        return dynamic;
    }
}
