package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;

public class StructureSettingsFlattenFix extends DataFix {

    public StructureSettingsFlattenFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.WORLD_GEN_SETTINGS);
        OpticFinder<?> opticfinder = type.findField("dimensions");

        return this.fixTypeEverywhereTyped("StructureSettingsFlatten", type, (typed) -> {
            return typed.updateTyped(opticfinder, (typed1) -> {
                Dynamic<?> dynamic = (Dynamic) typed1.write().result().orElseThrow();
                Dynamic<?> dynamic1 = dynamic.updateMapValues(StructureSettingsFlattenFix::fixDimension);

                return (Typed) ((Pair) opticfinder.type().readTyped(dynamic1).result().orElseThrow()).getFirst();
            });
        });
    }

    private static Pair<Dynamic<?>, Dynamic<?>> fixDimension(Pair<Dynamic<?>, Dynamic<?>> pair) {
        Dynamic<?> dynamic = (Dynamic) pair.getSecond();

        return Pair.of((Dynamic) pair.getFirst(), dynamic.update("generator", (dynamic1) -> {
            return dynamic1.update("settings", (dynamic2) -> {
                return dynamic2.update("structures", StructureSettingsFlattenFix::fixStructures);
            });
        }));
    }

    private static Dynamic<?> fixStructures(Dynamic<?> dynamic) {
        Dynamic<?> dynamic1 = dynamic.get("structures").orElseEmptyMap().updateMapValues((pair) -> {
            return pair.mapSecond((dynamic2) -> {
                return dynamic2.set("type", dynamic.createString("minecraft:random_spread"));
            });
        });

        return (Dynamic) DataFixUtils.orElse(dynamic.get("stronghold").result().map((dynamic2) -> {
            return dynamic1.set("minecraft:stronghold", dynamic2.set("type", dynamic.createString("minecraft:concentric_rings")));
        }), dynamic1);
    }
}
