package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterCustomNameEntity extends DataFix {

    public DataConverterCustomNameEntity(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        OpticFinder<String> opticfinder = DSL.fieldFinder("id", DataConverterSchemaNamed.namespacedString());

        return this.fixTypeEverywhereTyped("EntityCustomNameToComponentFix", this.getInputSchema().getType(DataConverterTypes.ENTITY), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                Optional<String> optional = typed.getOptional(opticfinder);

                return optional.isPresent() && Objects.equals(optional.get(), "minecraft:commandblock_minecart") ? dynamic : fixTagCustomName(dynamic);
            });
        });
    }

    public static Dynamic<?> fixTagCustomName(Dynamic<?> dynamic) {
        String s = dynamic.get("CustomName").asString("");

        return s.isEmpty() ? dynamic.remove("CustomName") : dynamic.set("CustomName", dynamic.createString(IChatBaseComponent.ChatSerializer.toJson(IChatBaseComponent.literal(s))));
    }
}
