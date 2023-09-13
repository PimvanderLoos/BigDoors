package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Codec;
import com.mojang.serialization.OptionalDynamic;
import java.util.List;

public class DataConverterDropChances extends DataFix {

    private static final Codec<List<Float>> a = Codec.FLOAT.listOf();

    public DataConverterDropChances(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(DataConverterTypes.ENTITY), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
                if (a(dynamic.get("HandDropChances"), 2)) {
                    dynamic = dynamic.remove("HandDropChances");
                }

                if (a(dynamic.get("ArmorDropChances"), 4)) {
                    dynamic = dynamic.remove("ArmorDropChances");
                }

                return dynamic;
            });
        });
    }

    private static boolean a(OptionalDynamic<?> optionaldynamic, int i) {
        Codec codec = DataConverterDropChances.a;

        codec.getClass();
        return (Boolean) optionaldynamic.flatMap(codec::parse).map((list) -> {
            return list.size() == i && list.stream().allMatch((ofloat) -> {
                return ofloat == 0.0F;
            });
        }).result().orElse(false);
    }
}
