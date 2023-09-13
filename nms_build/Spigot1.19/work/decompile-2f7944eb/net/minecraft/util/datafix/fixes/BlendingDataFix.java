package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.SectionPosition;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class BlendingDataFix extends DataFix {

    private final String name;
    private static final Set<String> STATUSES_TO_SKIP_BLENDING = Set.of("minecraft:empty", "minecraft:structure_starts", "minecraft:structure_references", "minecraft:biomes");

    public BlendingDataFix(Schema schema) {
        super(schema, false);
        this.name = "Blending Data Fix v" + schema.getVersionKey();
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getOutputSchema().getType(DataConverterTypes.CHUNK);

        return this.fixTypeEverywhereTyped(this.name, type, (typed) -> {
            return typed.update(DSL.remainderFinder(), BlendingDataFix::updateChunkTag);
        });
    }

    private static Dynamic<?> updateChunkTag(Dynamic<?> dynamic) {
        dynamic = dynamic.remove("blending_data");
        Optional<? extends Dynamic<?>> optional = dynamic.get("Status").result();

        if (optional.isPresent()) {
            String s = DataConverterSchemaNamed.ensureNamespaced(((Dynamic) optional.get()).asString("empty"));
            Optional<? extends Dynamic<?>> optional1 = dynamic.get("below_zero_retrogen").result();

            if (!BlendingDataFix.STATUSES_TO_SKIP_BLENDING.contains(s)) {
                dynamic = updateBlendingData(dynamic, 384, -64);
            } else if (optional1.isPresent()) {
                Dynamic<?> dynamic1 = (Dynamic) optional1.get();
                String s1 = DataConverterSchemaNamed.ensureNamespaced(dynamic1.get("target_status").asString("empty"));

                if (!BlendingDataFix.STATUSES_TO_SKIP_BLENDING.contains(s1)) {
                    dynamic = updateBlendingData(dynamic, 256, 0);
                }
            }
        }

        return dynamic;
    }

    private static Dynamic<?> updateBlendingData(Dynamic<?> dynamic, int i, int j) {
        return dynamic.set("blending_data", dynamic.createMap(Map.of(dynamic.createString("min_section"), dynamic.createInt(SectionPosition.blockToSectionCoord(j)), dynamic.createString("max_section"), dynamic.createInt(SectionPosition.blockToSectionCoord(j + i)))));
    }
}
