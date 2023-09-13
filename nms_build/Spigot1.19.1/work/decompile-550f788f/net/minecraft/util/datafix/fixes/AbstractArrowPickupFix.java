package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.function.Function;

public class AbstractArrowPickupFix extends DataFix {

    public AbstractArrowPickupFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();

        return this.fixTypeEverywhereTyped("AbstractArrowPickupFix", schema.getType(DataConverterTypes.ENTITY), this::updateProjectiles);
    }

    private Typed<?> updateProjectiles(Typed<?> typed) {
        typed = this.updateEntity(typed, "minecraft:arrow", AbstractArrowPickupFix::updatePickup);
        typed = this.updateEntity(typed, "minecraft:spectral_arrow", AbstractArrowPickupFix::updatePickup);
        typed = this.updateEntity(typed, "minecraft:trident", AbstractArrowPickupFix::updatePickup);
        return typed;
    }

    private static Dynamic<?> updatePickup(Dynamic<?> dynamic) {
        if (dynamic.get("pickup").result().isPresent()) {
            return dynamic;
        } else {
            boolean flag = dynamic.get("player").asBoolean(true);

            return dynamic.set("pickup", dynamic.createByte((byte) (flag ? 1 : 0))).remove("player");
        }
    }

    private Typed<?> updateEntity(Typed<?> typed, String s, Function<Dynamic<?>, Dynamic<?>> function) {
        Type<?> type = this.getInputSchema().getChoiceType(DataConverterTypes.ENTITY, s);
        Type<?> type1 = this.getOutputSchema().getChoiceType(DataConverterTypes.ENTITY, s);

        return typed.updateTyped(DSL.namedChoice(s, type), type1, (typed1) -> {
            return typed1.update(DSL.remainderFinder(), function);
        });
    }
}
