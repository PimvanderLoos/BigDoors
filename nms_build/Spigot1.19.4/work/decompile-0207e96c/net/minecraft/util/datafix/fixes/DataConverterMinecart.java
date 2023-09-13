package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Objects;

public class DataConverterMinecart extends DataFix {

    private static final List<String> MINECART_BY_ID = Lists.newArrayList(new String[]{"MinecartRideable", "MinecartChest", "MinecartFurnace"});

    public DataConverterMinecart(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        TaggedChoiceType<String> taggedchoicetype = this.getInputSchema().findChoiceType(DataConverterTypes.ENTITY);
        TaggedChoiceType<String> taggedchoicetype1 = this.getOutputSchema().findChoiceType(DataConverterTypes.ENTITY);

        return this.fixTypeEverywhere("EntityMinecartIdentifiersFix", taggedchoicetype, taggedchoicetype1, (dynamicops) -> {
            return (pair) -> {
                if (!Objects.equals(pair.getFirst(), "Minecart")) {
                    return pair;
                } else {
                    Typed<? extends Pair<String, ?>> typed = (Typed) taggedchoicetype.point(dynamicops, "Minecart", pair.getSecond()).orElseThrow(IllegalStateException::new);
                    Dynamic<?> dynamic = (Dynamic) typed.getOrCreate(DSL.remainderFinder());
                    int i = dynamic.get("Type").asInt(0);
                    String s;

                    if (i > 0 && i < DataConverterMinecart.MINECART_BY_ID.size()) {
                        s = (String) DataConverterMinecart.MINECART_BY_ID.get(i);
                    } else {
                        s = "MinecartRideable";
                    }

                    return Pair.of(s, (DataResult) typed.write().map((dynamic1) -> {
                        return ((Type) taggedchoicetype1.types().get(s)).read(dynamic1);
                    }).result().orElseThrow(() -> {
                        return new IllegalStateException("Could not read the new minecart.");
                    }));
                }
            };
        });
    }
}
