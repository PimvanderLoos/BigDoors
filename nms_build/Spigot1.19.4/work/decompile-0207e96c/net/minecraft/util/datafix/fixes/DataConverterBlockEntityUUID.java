package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterBlockEntityUUID extends DataConverterUUIDBase {

    public DataConverterBlockEntityUUID(Schema schema) {
        super(schema, DataConverterTypes.BLOCK_ENTITY);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), (typed) -> {
            typed = this.updateNamedChoice(typed, "minecraft:conduit", this::updateConduit);
            typed = this.updateNamedChoice(typed, "minecraft:skull", this::updateSkull);
            return typed;
        });
    }

    private Dynamic<?> updateSkull(Dynamic<?> dynamic) {
        return (Dynamic) dynamic.get("Owner").get().map((dynamic1) -> {
            return (Dynamic) replaceUUIDString(dynamic1, "Id", "Id").orElse(dynamic1);
        }).map((dynamic1) -> {
            return dynamic.remove("Owner").set("SkullOwner", dynamic1);
        }).result().orElse(dynamic);
    }

    private Dynamic<?> updateConduit(Dynamic<?> dynamic) {
        return (Dynamic) replaceUUIDMLTag(dynamic, "target_uuid", "Target").orElse(dynamic);
    }
}
