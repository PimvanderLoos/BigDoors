package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import java.util.stream.Stream;

public class DataConverterMobSpawner extends DataFix {

    public DataConverterMobSpawner(Schema schema, boolean flag) {
        super(schema, flag);
    }

    private Dynamic<?> fix(Dynamic<?> dynamic) {
        if (!"MobSpawner".equals(dynamic.get("id").asString(""))) {
            return dynamic;
        } else {
            Optional<String> optional = dynamic.get("EntityId").asString().result();

            if (optional.isPresent()) {
                Dynamic<?> dynamic1 = (Dynamic) DataFixUtils.orElse(dynamic.get("SpawnData").result(), dynamic.emptyMap());

                dynamic1 = dynamic1.set("id", dynamic1.createString(((String) optional.get()).isEmpty() ? "Pig" : (String) optional.get()));
                dynamic = dynamic.set("SpawnData", dynamic1);
                dynamic = dynamic.remove("EntityId");
            }

            Optional<? extends Stream<? extends Dynamic<?>>> optional1 = dynamic.get("SpawnPotentials").asStreamOpt().result();

            if (optional1.isPresent()) {
                dynamic = dynamic.set("SpawnPotentials", dynamic.createList(((Stream) optional1.get()).map((dynamic2) -> {
                    Optional<String> optional2 = dynamic2.get("Type").asString().result();

                    if (optional2.isPresent()) {
                        Dynamic<?> dynamic3 = ((Dynamic) DataFixUtils.orElse(dynamic2.get("Properties").result(), dynamic2.emptyMap())).set("id", dynamic2.createString((String) optional2.get()));

                        return dynamic2.set("Entity", dynamic3).remove("Type").remove("Properties");
                    } else {
                        return dynamic2;
                    }
                })));
            }

            return dynamic;
        }
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(DataConverterTypes.UNTAGGED_SPAWNER);

        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(DataConverterTypes.UNTAGGED_SPAWNER), type, (typed) -> {
            Dynamic<?> dynamic = (Dynamic) typed.get(DSL.remainderFinder());

            dynamic = dynamic.set("id", dynamic.createString("MobSpawner"));
            DataResult<? extends Pair<? extends Typed<?>, ?>> dataresult = type.readTyped(this.fix(dynamic));

            return !dataresult.result().isPresent() ? typed : (Typed) ((Pair) dataresult.result().get()).getFirst();
        });
    }
}
