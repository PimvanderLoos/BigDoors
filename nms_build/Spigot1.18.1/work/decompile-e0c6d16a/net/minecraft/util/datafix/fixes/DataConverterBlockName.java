package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import net.minecraft.util.datafix.schemas.DataConverterSchemaNamed;

public class DataConverterBlockName extends DataFix {

    public DataConverterBlockName(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.BLOCK_NAME);
        Type<?> type1 = this.getOutputSchema().getType(DataConverterTypes.BLOCK_NAME);
        Type<Pair<String, Either<Integer, String>>> type2 = DSL.named(DataConverterTypes.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), DataConverterSchemaNamed.namespacedString()));
        Type<Pair<String, String>> type3 = DSL.named(DataConverterTypes.BLOCK_NAME.typeName(), DataConverterSchemaNamed.namespacedString());

        if (Objects.equals(type, type2) && Objects.equals(type1, type3)) {
            return this.fixTypeEverywhere("BlockNameFlatteningFix", type2, type3, (dynamicops) -> {
                return (pair) -> {
                    return pair.mapSecond((either) -> {
                        return (String) either.map(DataConverterFlattenData::upgradeBlock, (s) -> {
                            return DataConverterFlattenData.upgradeBlock(DataConverterSchemaNamed.ensureNamespaced(s));
                        });
                    });
                };
            });
        } else {
            throw new IllegalStateException("Expected and actual types don't match.");
        }
    }
}
