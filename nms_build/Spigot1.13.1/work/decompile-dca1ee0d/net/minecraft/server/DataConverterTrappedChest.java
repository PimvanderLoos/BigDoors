package net.minecraft.server;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DataConverterTrappedChest extends DataFix {

    private static final Logger a = LogManager.getLogger();

    public DataConverterTrappedChest(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        Type type = this.getOutputSchema().getType(DataConverterTypes.c);
        Type type1 = type.findFieldType("Level");
        Type type2 = type1.findFieldType("TileEntities");

        if (!(type2 instanceof ListType)) {
            throw new IllegalStateException("Tile entity type is not a list type.");
        } else {
            ListType listtype = (ListType) type2;
            OpticFinder opticfinder = DSL.fieldFinder("TileEntities", listtype);
            Type type3 = this.getInputSchema().getType(DataConverterTypes.c);
            OpticFinder opticfinder1 = type3.findField("Level");
            OpticFinder opticfinder2 = opticfinder1.type().findField("Sections");
            Type type4 = opticfinder2.type();

            if (!(type4 instanceof ListType)) {
                throw new IllegalStateException("Expecting sections to be a list.");
            } else {
                Type type5 = ((ListType) type4).getElement();
                OpticFinder opticfinder3 = DSL.typeFinder(type5);

                return TypeRewriteRule.seq((new DataConverterAddChoices(this.getOutputSchema(), "AddTrappedChestFix", DataConverterTypes.j)).makeRule(), this.fixTypeEverywhereTyped("Trapped Chest fix", type3, (typed) -> {
                    return typed.updateTyped(opticfinder, (typedx) -> {
                        Optional optional = typedx.getOptionalTyped(opticfinder);

                        if (!optional.isPresent()) {
                            return typedx;
                        } else {
                            List list = ((Typed) optional.get()).getAllTyped(opticfinder1);
                            IntOpenHashSet intopenhashset = new IntOpenHashSet();
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                Typed typed1 = (Typed) iterator.next();
                                DataConverterTrappedChest.a dataconvertertrappedchest_a = new DataConverterTrappedChest.a(typed1, this.getInputSchema());

                                if (!dataconvertertrappedchest_a.b()) {
                                    for (int i = 0; i < 4096; ++i) {
                                        int j = dataconvertertrappedchest_a.c(i);

                                        if (dataconvertertrappedchest_a.a(j)) {
                                            intopenhashset.add(dataconvertertrappedchest_a.c() << 12 | i);
                                        }
                                    }
                                }
                            }

                            Dynamic dynamic = (Dynamic) typedx.get(DSL.remainderFinder());
                            int k = dynamic.getInt("xPos");
                            int l = dynamic.getInt("zPos");
                            TaggedChoiceType taggedchoicetype = this.getInputSchema().findChoiceType(DataConverterTypes.j);

                            return typedx.updateTyped(opticfinder2, (typed) -> {
                                return typed.updateTyped(taggedchoicetype.finder(), (typedx) -> {
                                    Dynamic dynamic = (Dynamic) typedx.getOrCreate(DSL.remainderFinder());
                                    int i = dynamic.getInt("x") - (j << 4);
                                    int k = dynamic.getInt("y");
                                    int l = dynamic.getInt("z") - (i1 << 4);

                                    return intset.contains(DataConverterLeaves.a(i, k, l)) ? typedx.update(taggedchoicetype.finder(), (pair) -> {
                                        return pair.mapFirst((s) -> {
                                            if (!Objects.equals(s, "minecraft:chest")) {
                                                DataConverterTrappedChest.a.warn("Block Entity was expected to be a chest");
                                            }

                                            return "minecraft:trapped_chest";
                                        });
                                    }) : typedx;
                                });
                            });
                        }
                    });
                }));
            }
        }
    }

    public static final class a extends DataConverterLeaves.b {

        @Nullable
        private IntSet f;

        public a(Typed<?> typed, Schema schema) {
            super(typed, schema);
        }

        protected boolean a() {
            this.f = new IntOpenHashSet();

            for (int i = 0; i < this.c.size(); ++i) {
                Dynamic dynamic = (Dynamic) this.c.get(i);
                String s = dynamic.getString("Name");

                if (Objects.equals(s, "minecraft:trapped_chest")) {
                    this.f.add(i);
                }
            }

            return this.f.isEmpty();
        }

        public boolean a(int i) {
            return this.f.contains(i);
        }
    }
}
