package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Optional;

public class DataConverterOminousBannerBlockEntityRename extends DataConverterNamedEntity {

    public DataConverterOminousBannerBlockEntityRename(Schema schema, boolean flag) {
        super(schema, flag, "OminousBannerBlockEntityRenameFix", DataConverterTypes.BLOCK_ENTITY, "minecraft:banner");
    }

    @Override
    protected Typed<?> fix(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixTag);
    }

    private Dynamic<?> fixTag(Dynamic<?> dynamic) {
        Optional<String> optional = dynamic.get("CustomName").asString().result();

        if (optional.isPresent()) {
            String s = (String) optional.get();

            s = s.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            return dynamic.set("CustomName", dynamic.createString(s));
        } else {
            return dynamic;
        }
    }
}
