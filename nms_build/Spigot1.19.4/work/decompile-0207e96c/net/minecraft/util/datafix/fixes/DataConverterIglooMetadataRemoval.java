package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Objects;

public class DataConverterIglooMetadataRemoval extends DataFix {

    public DataConverterIglooMetadataRemoval(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE);

        return this.fixTypeEverywhereTyped("IglooMetadataRemovalFix", type, (typed) -> {
            return typed.update(DSL.remainderFinder(), DataConverterIglooMetadataRemoval::fixTag);
        });
    }

    private static <T> Dynamic<T> fixTag(Dynamic<T> dynamic) {
        boolean flag = (Boolean) dynamic.get("Children").asStreamOpt().map((stream) -> {
            return stream.allMatch(DataConverterIglooMetadataRemoval::isIglooPiece);
        }).result().orElse(false);

        return flag ? dynamic.set("id", dynamic.createString("Igloo")).remove("Children") : dynamic.update("Children", DataConverterIglooMetadataRemoval::removeIglooPieces);
    }

    private static <T> Dynamic<T> removeIglooPieces(Dynamic<T> dynamic) {
        DataResult dataresult = dynamic.asStreamOpt().map((stream) -> {
            return stream.filter((dynamic1) -> {
                return !isIglooPiece(dynamic1);
            });
        });

        Objects.requireNonNull(dynamic);
        return (Dynamic) dataresult.map(dynamic::createList).result().orElse(dynamic);
    }

    private static boolean isIglooPiece(Dynamic<?> dynamic) {
        return dynamic.get("id").asString("").equals("Iglu");
    }
}
