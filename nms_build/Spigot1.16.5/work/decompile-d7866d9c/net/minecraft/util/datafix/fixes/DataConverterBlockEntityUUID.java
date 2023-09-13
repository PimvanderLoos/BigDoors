package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;

public class DataConverterBlockEntityUUID extends DataConverterUUIDBase {

    public DataConverterBlockEntityUUID(Schema schema) {
        super(schema, DataConverterTypes.BLOCK_ENTITY);
    }

    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.b), (typed) -> {
            typed = this.a(typed, "minecraft:conduit", this::c);
            typed = this.a(typed, "minecraft:skull", this::b);
            return typed;
        });
    }

    private Dynamic<?> b(Dynamic<?> dynamic) {
        return (Dynamic) dynamic.get("Owner").get().map((dynamic1) -> {
            return (Dynamic) a(dynamic1, "Id", "Id").orElse(dynamic1);
        }).map((dynamic1) -> {
            return dynamic.remove("Owner").set("SkullOwner", dynamic1);
        }).result().orElse(dynamic);
    }

    private Dynamic<?> c(Dynamic<?> dynamic) {
        return (Dynamic) b(dynamic, "target_uuid", "Target").orElse(dynamic);
    }
}
