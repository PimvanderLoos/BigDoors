package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterCustomNameTile extends DataFix {

    public DataConverterCustomNameTile(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        OpticFinder<String> opticfinder = DSL.fieldFinder("id", DataConverterSchemaNamed.a());

        return this.fixTypeEverywhereTyped("BlockEntityCustomNameToComponentFix", this.getInputSchema().getType(DataConverterTypes.BLOCK_ENTITY), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                Optional<String> optional = typed.getOptional(opticfinder);

                return optional.isPresent() && Objects.equals(optional.get(), "minecraft:command_block") ? dynamic : DataConverterCustomNameEntity.a(dynamic);
            });
        });
    }
}
