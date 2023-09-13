package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class EffectDurationFix extends DataFix {

    private static final Set<String> ITEM_TYPES = Set.of("minecraft:potion", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:tipped_arrow");

    public EffectDurationFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Schema schema = this.getInputSchema();
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.ITEM_STACK);
        OpticFinder<Pair<String, String>> opticfinder = DSL.fieldFinder("id", DSL.named(DataConverterTypes.ITEM_NAME.typeName(), DataConverterSchemaNamed.namespacedString()));
        OpticFinder<?> opticfinder1 = type.findField("tag");

        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("EffectDurationEntity", schema.getType(DataConverterTypes.ENTITY), (typed) -> {
            return typed.update(DSL.remainderFinder(), this::updateEntity);
        }), new TypeRewriteRule[]{this.fixTypeEverywhereTyped("EffectDurationPlayer", schema.getType(DataConverterTypes.PLAYER), (typed) -> {
                    return typed.update(DSL.remainderFinder(), this::updateEntity);
                }), this.fixTypeEverywhereTyped("EffectDurationItem", type, (typed) -> {
                    Optional<Pair<String, String>> optional = typed.getOptional(opticfinder);
                    Set set = EffectDurationFix.ITEM_TYPES;

                    Objects.requireNonNull(set);
                    if (optional.filter(set::contains).isPresent()) {
                        Optional<? extends Typed<?>> optional1 = typed.getOptionalTyped(opticfinder1);

                        if (optional1.isPresent()) {
                            Dynamic<?> dynamic = (Dynamic) ((Typed) optional1.get()).get(DSL.remainderFinder());
                            Typed<?> typed1 = ((Typed) optional1.get()).set(DSL.remainderFinder(), dynamic.update("CustomPotionEffects", this::fix));

                            return typed.set(opticfinder1, typed1);
                        }
                    }

                    return typed;
                })});
    }

    private Dynamic<?> fixEffect(Dynamic<?> dynamic) {
        return dynamic.update("FactorCalculationData", (dynamic1) -> {
            int i = dynamic1.get("effect_changed_timestamp").asInt(-1);

            dynamic1 = dynamic1.remove("effect_changed_timestamp");
            int j = dynamic.get("Duration").asInt(-1);
            int k = i - j;

            return dynamic1.set("ticks_active", dynamic1.createInt(k));
        });
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        return dynamic.createList(dynamic.asStream().map(this::fixEffect));
    }

    private Dynamic<?> updateEntity(Dynamic<?> dynamic) {
        dynamic = dynamic.update("Effects", this::fix);
        dynamic = dynamic.update("ActiveEffects", this::fix);
        dynamic = dynamic.update("CustomPotionEffects", this::fix);
        return dynamic;
    }
}
