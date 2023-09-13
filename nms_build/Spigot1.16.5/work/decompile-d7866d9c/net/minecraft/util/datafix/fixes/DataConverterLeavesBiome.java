package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;

public class DataConverterLeavesBiome extends DataFix {

    public DataConverterLeavesBiome(Schema schema, boolean flag) {
        super(schema, flag);
    }

    protected TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        OpticFinder<?> opticfinder = type.findField("Level");

        return this.fixTypeEverywhereTyped("Leaves fix", type, (typed) -> {
            return typed.updateTyped(opticfinder, (typed1) -> {
                return typed1.update(DSL.remainderFinder(), (dynamic) -> {
                    Optional<IntStream> optional = dynamic.get("Biomes").asIntStreamOpt().result();

                    if (!optional.isPresent()) {
                        return dynamic;
                    } else {
                        int[] aint = ((IntStream) optional.get()).toArray();
                        int[] aint1 = new int[1024];

                        int i;

                        for (i = 0; i < 4; ++i) {
                            for (int j = 0; j < 4; ++j) {
                                int k = (j << 2) + 2;
                                int l = (i << 2) + 2;
                                int i1 = l << 4 | k;

                                aint1[i << 2 | j] = i1 < aint.length ? aint[i1] : -1;
                            }
                        }

                        for (i = 1; i < 64; ++i) {
                            System.arraycopy(aint1, 0, aint1, i * 16, 16);
                        }

                        return dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(aint1)));
                    }
                });
            });
        });
    }
}
