package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;

public class WorldGenSettingsDisallowOldCustomWorldsFix extends DataFix {

    public WorldGenSettingsDisallowOldCustomWorldsFix(Schema schema) {
        super(schema, false);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.WORLD_GEN_SETTINGS);
        OpticFinder<?> opticfinder = type.findField("dimensions");

        return this.fixTypeEverywhereTyped("WorldGenSettingsDisallowOldCustomWorldsFix_" + this.getOutputSchema().getVersionKey(), type, (typed) -> {
            return typed.updateTyped(opticfinder, (typed1) -> {
                typed1.write().map((dynamic) -> {
                    return dynamic.getMapValues().map((map) -> {
                        map.forEach((dynamic1, dynamic2) -> {
                            if (dynamic2.get("type").asString().result().isEmpty()) {
                                throw new IllegalStateException("Unable load old custom worlds.");
                            }
                        });
                        return map;
                    });
                });
                return typed1;
            });
        });
    }
}
