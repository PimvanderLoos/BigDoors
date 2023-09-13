package net.minecraft.util.datafix.fixes;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.Dynamic;
import java.util.List;
import java.util.Optional;

public class DataConverterFurnaceRecipesUsed extends DataFix {

    public DataConverterFurnaceRecipesUsed(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        return this.a(this.getOutputSchema().getTypeRaw(DataConverterTypes.RECIPE));
    }

    private <R> TypeRewriteRule a(Type<R> type) {
        Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> type1 = DSL.and(DSL.optional(DSL.field("RecipesUsed", DSL.and(DSL.compoundList(type, DSL.intType()), DSL.remainderType()))), DSL.remainderType());
        OpticFinder<?> opticfinder = DSL.namedChoice("minecraft:furnace", this.getInputSchema().getChoiceType(DataConverterTypes.BLOCK_ENTITY, "minecraft:furnace"));
        OpticFinder<?> opticfinder1 = DSL.namedChoice("minecraft:blast_furnace", this.getInputSchema().getChoiceType(DataConverterTypes.BLOCK_ENTITY, "minecraft:blast_furnace"));
        OpticFinder<?> opticfinder2 = DSL.namedChoice("minecraft:smoker", this.getInputSchema().getChoiceType(DataConverterTypes.BLOCK_ENTITY, "minecraft:smoker"));
        Type<?> type2 = this.getOutputSchema().getChoiceType(DataConverterTypes.BLOCK_ENTITY, "minecraft:furnace");
        Type<?> type3 = this.getOutputSchema().getChoiceType(DataConverterTypes.BLOCK_ENTITY, "minecraft:blast_furnace");
        Type<?> type4 = this.getOutputSchema().getChoiceType(DataConverterTypes.BLOCK_ENTITY, "minecraft:smoker");
        Type<?> type5 = this.getInputSchema().getType(DataConverterTypes.BLOCK_ENTITY);
        Type<?> type6 = this.getOutputSchema().getType(DataConverterTypes.BLOCK_ENTITY);

        return this.fixTypeEverywhereTyped("FurnaceRecipesFix", type5, type6, (typed) -> {
            return typed.updateTyped(opticfinder, type2, (typed1) -> {
                return this.a(type, type1, typed1);
            }).updateTyped(opticfinder1, type3, (typed1) -> {
                return this.a(type, type1, typed1);
            }).updateTyped(opticfinder2, type4, (typed1) -> {
                return this.a(type, type1, typed1);
            });
        });
    }

    private <R> Typed<?> a(Type<R> type, Type<Pair<Either<Pair<List<Pair<R, Integer>>, Dynamic<?>>, Unit>, Dynamic<?>>> type1, Typed<?> typed) {
        Dynamic<?> dynamic = (Dynamic) typed.getOrCreate(DSL.remainderFinder());
        int i = dynamic.get("RecipesUsedSize").asInt(0);

        dynamic = dynamic.remove("RecipesUsedSize");
        List<Pair<R, Integer>> list = Lists.newArrayList();

        for (int j = 0; j < i; ++j) {
            String s = "RecipeLocation" + j;
            String s1 = "RecipeAmount" + j;
            Optional<? extends Dynamic<?>> optional = dynamic.get(s).result();
            int k = dynamic.get(s1).asInt(0);

            if (k > 0) {
                optional.ifPresent((dynamic1) -> {
                    Optional<? extends Pair<R, ? extends Dynamic<?>>> optional1 = type.read(dynamic1).result();

                    optional1.ifPresent((pair) -> {
                        list.add(Pair.of(pair.getFirst(), k));
                    });
                });
            }

            dynamic = dynamic.remove(s).remove(s1);
        }

        return typed.set(DSL.remainderFinder(), type1, Pair.of(Either.left(Pair.of(list, dynamic.emptyMap())), dynamic));
    }
}
