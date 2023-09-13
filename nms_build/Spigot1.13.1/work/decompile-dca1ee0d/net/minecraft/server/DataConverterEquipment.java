package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class DataConverterEquipment extends DataFix {

    public DataConverterEquipment(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.a(this.getInputSchema().getTypeRaw(DataConverterTypes.ITEM_STACK));
    }

    private <IS> TypeRewriteRule a(Type<IS> type) {
        Type type1 = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(type))), DSL.remainderType());
        Type type2 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(type))), DSL.optional(DSL.field("HandItems", DSL.list(type))), DSL.remainderType());
        OpticFinder opticfinder = DSL.typeFinder(type1);
        OpticFinder opticfinder1 = DSL.fieldFinder("Equipment", DSL.list(type));

        return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(DataConverterTypes.ENTITY), this.getOutputSchema().getType(DataConverterTypes.ENTITY), (typed) -> {
            Either either = Either.right(DSL.unit());
            Either either1 = Either.right(DSL.unit());
            Dynamic dynamic = (Dynamic) typed.getOrCreate(DSL.remainderFinder());
            Optional optional = typed.getOptional(opticfinder);

            if (optional.isPresent()) {
                List list = (List) optional.get();
                Object object = ((Optional) type.read(dynamic.emptyMap()).getSecond()).orElseThrow(() -> {
                    return new IllegalStateException("Could not parse newly created empty itemstack.");
                });

                if (!list.isEmpty()) {
                    either = Either.left(Lists.newArrayList(new Object[] { list.get(0), object}));
                }

                if (list.size() > 1) {
                    ArrayList arraylist = Lists.newArrayList(new Object[] { object, object, object, object});

                    for (int i = 1; i < Math.min(list.size(), 5); ++i) {
                        arraylist.set(i - 1, list.get(i));
                    }

                    either1 = Either.left(arraylist);
                }
            }

            Optional optional1 = dynamic.get("DropChances").flatMap(Dynamic::getStream);

            if (optional1.isPresent()) {
                Iterator iterator = Stream.concat((Stream) optional1.get(), Stream.generate(() -> {
                    return dynamic.createInt(0);
                })).iterator();
                float f = ((Dynamic) iterator.next()).getNumberValue(Integer.valueOf(0)).floatValue();
                Dynamic dynamic1;

                if (!dynamic.get("HandDropChances").isPresent()) {
                    dynamic1 = dynamic.emptyMap().merge(dynamic.createFloat(f)).merge(dynamic.createFloat(0.0F));
                    dynamic = dynamic.set("HandDropChances", dynamic1);
                }

                if (!dynamic.get("ArmorDropChances").isPresent()) {
                    dynamic1 = dynamic.emptyMap().merge(dynamic.createFloat(((Dynamic) iterator.next()).getNumberValue(Integer.valueOf(0)).floatValue())).merge(dynamic.createFloat(((Dynamic) iterator.next()).getNumberValue(Integer.valueOf(0)).floatValue())).merge(dynamic.createFloat(((Dynamic) iterator.next()).getNumberValue(Integer.valueOf(0)).floatValue())).merge(dynamic.createFloat(((Dynamic) iterator.next()).getNumberValue(Integer.valueOf(0)).floatValue()));
                    dynamic = dynamic.set("ArmorDropChances", dynamic1);
                }

                dynamic = dynamic.remove("DropChances");
            }

            return typed.set(opticfinder1, type1, Pair.of(either, Pair.of(either1, dynamic)));
        });
    }
}
