package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public class DataConverterMobSpawner extends DataFix {

    public DataConverterMobSpawner(Schema schema, boolean flag) {
        super(schema, flag);
    }

    private Dynamic<?> a(Dynamic<?> dynamic) {
        if (!"MobSpawner".equals(dynamic.getString("id"))) {
            return dynamic;
        } else {
            Optional optional = dynamic.get("EntityId").flatMap(Dynamic::getStringValue);

            if (optional.isPresent()) {
                Dynamic dynamic1 = (Dynamic) DataFixUtils.orElse(dynamic.get("SpawnData"), dynamic.emptyMap());

                dynamic1 = dynamic1.set("id", dynamic1.createString(((String) optional.get()).isEmpty() ? "Pig" : (String) optional.get()));
                dynamic = dynamic.set("SpawnData", dynamic1);
                dynamic = dynamic.remove("EntityId");
            }

            Optional optional1 = dynamic.get("SpawnPotentials").flatMap(Dynamic::getStream);

            if (optional1.isPresent()) {
                dynamic = dynamic.set("SpawnPotentials", dynamic.createList(((Stream) optional1.get()).map((dynamic) -> {
                    Optional optional = dynamic.get("Type").flatMap(Dynamic::getStringValue);

                    if (optional.isPresent()) {
                        Dynamic dynamic1 = ((Dynamic) DataFixUtils.orElse(dynamic.get("Properties"), dynamic.emptyMap())).set("id", dynamic.createString((String) optional.get()));

                        return dynamic.set("Entity", dynamic1).remove("Type").remove("Properties");
                    } else {
                        return dynamic;
                    }
                })));
            }

            return dynamic;
        }
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(DataConverterTypes.r);

        return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(DataConverterTypes.r), type, (typed) -> {
            Dynamic dynamic = (Dynamic) typed.get(DSL.remainderFinder());

            dynamic = dynamic.set("id", dynamic.createString("MobSpawner"));
            Pair pair = type.readTyped(this.a(dynamic));

            return !((Optional) pair.getSecond()).isPresent() ? typed : (Typed) ((Optional) pair.getSecond()).get();
        });
    }
}
