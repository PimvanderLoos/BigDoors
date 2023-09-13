package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class DataConverterEquipment extends DataFix {

    public DataConverterEquipment(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        return this.cap(this.getInputSchema().getTypeRaw(DataConverterTypes.ITEM_STACK));
    }

    private <IS> TypeRewriteRule cap(Type<IS> type) {
        Type<Pair<Either<List<IS>, Unit>, Dynamic<?>>> type1 = DSL.and(DSL.optional(DSL.field("Equipment", DSL.list(type))), DSL.remainderType());
        Type<Pair<Either<List<IS>, Unit>, Pair<Either<List<IS>, Unit>, Dynamic<?>>>> type2 = DSL.and(DSL.optional(DSL.field("ArmorItems", DSL.list(type))), DSL.optional(DSL.field("HandItems", DSL.list(type))), DSL.remainderType());
        OpticFinder<Pair<Either<List<IS>, Unit>, Dynamic<?>>> opticfinder = DSL.typeFinder(type1);
        OpticFinder<List<IS>> opticfinder1 = DSL.fieldFinder("Equipment", DSL.list(type));

        return this.fixTypeEverywhereTyped("EntityEquipmentToArmorAndHandFix", this.getInputSchema().getType(DataConverterTypes.ENTITY), this.getOutputSchema().getType(DataConverterTypes.ENTITY), (typed) -> {
            Either<List<IS>, Unit> either = Either.right(DSL.unit());
            Either<List<IS>, Unit> either1 = Either.right(DSL.unit());
            Dynamic<?> dynamic = (Dynamic) typed.getOrCreate(DSL.remainderFinder());
            Optional<List<IS>> optional = typed.getOptional(opticfinder1);

            if (optional.isPresent()) {
                List<IS> list = (List) optional.get();
                IS is = ((Pair) type.read(dynamic.emptyMap()).result().orElseThrow(() -> {
                    return new IllegalStateException("Could not parse newly created empty itemstack.");
                })).getFirst();

                if (!list.isEmpty()) {
                    either = Either.left(Lists.newArrayList(new Object[]{list.get(0), is}));
                }

                if (list.size() > 1) {
                    List<IS> list1 = Lists.newArrayList(new Object[]{is, is, is, is});

                    for (int i = 1; i < Math.min(list.size(), 5); ++i) {
                        list1.set(i - 1, list.get(i));
                    }

                    either1 = Either.left(list1);
                }
            }

            Optional<? extends Stream<? extends Dynamic<?>>> optional1 = dynamic.get("DropChances").asStreamOpt().result();

            if (optional1.isPresent()) {
                Iterator<? extends Dynamic<?>> iterator = Stream.concat((Stream) optional1.get(), Stream.generate(() -> {
                    return dynamic.createInt(0);
                })).iterator();
                float f = ((Dynamic) iterator.next()).asFloat(0.0F);
                Stream stream;
                Dynamic dynamic1;

                if (!dynamic.get("HandDropChances").result().isPresent()) {
                    stream = Stream.of(f, 0.0F);
                    Objects.requireNonNull(dynamic);
                    dynamic1 = dynamic.createList(stream.map(dynamic::createFloat));
                    dynamic = dynamic.set("HandDropChances", dynamic1);
                }

                if (!dynamic.get("ArmorDropChances").result().isPresent()) {
                    stream = Stream.of(((Dynamic) iterator.next()).asFloat(0.0F), ((Dynamic) iterator.next()).asFloat(0.0F), ((Dynamic) iterator.next()).asFloat(0.0F), ((Dynamic) iterator.next()).asFloat(0.0F));
                    Objects.requireNonNull(dynamic);
                    dynamic1 = dynamic.createList(stream.map(dynamic::createFloat));
                    dynamic = dynamic.set("ArmorDropChances", dynamic1);
                }

                dynamic = dynamic.remove("DropChances");
            }

            return typed.set(opticfinder, type2, Pair.of(either, Pair.of(either1, dynamic)));
        });
    }
}
