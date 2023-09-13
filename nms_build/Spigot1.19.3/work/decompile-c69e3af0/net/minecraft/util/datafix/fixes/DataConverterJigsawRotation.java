package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.Optional;

public class DataConverterJigsawRotation extends DataFix {

    private static final Map<String, String> RENAMES = ImmutableMap.builder().put("down", "down_south").put("up", "up_north").put("north", "north_up").put("south", "south_up").put("west", "west_up").put("east", "east_up").build();

    public DataConverterJigsawRotation(Schema schema, boolean flag) {
        super(schema, flag);
    }

    private static Dynamic<?> fix(Dynamic<?> dynamic) {
        Optional<String> optional = dynamic.get("Name").asString().result();

        return optional.equals(Optional.of("minecraft:jigsaw")) ? dynamic.update("Properties", (dynamic1) -> {
            String s = dynamic1.get("facing").asString("north");

            return dynamic1.remove("facing").set("orientation", dynamic1.createString((String) DataConverterJigsawRotation.RENAMES.getOrDefault(s, s)));
        }) : dynamic;
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("jigsaw_rotation_fix", this.getInputSchema().getType(DataConverterTypes.BLOCK_STATE), (typed) -> {
            return typed.update(DSL.remainderFinder(), DataConverterJigsawRotation::fix);
        });
    }
}
