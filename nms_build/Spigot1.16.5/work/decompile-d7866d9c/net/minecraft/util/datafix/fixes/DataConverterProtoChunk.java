package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import it.unimi.dsi.fastutil.shorts.ShortList;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DataConverterProtoChunk extends DataFix {

    public DataConverterProtoChunk(Schema schema, boolean flag) {
        super(schema, flag);
    }

    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getType(DataConverterTypes.CHUNK);
        Type<?> type1 = this.getOutputSchema().getType(DataConverterTypes.CHUNK);
        Type<?> type2 = type.findFieldType("Level");
        Type<?> type3 = type1.findFieldType("Level");
        Type<?> type4 = type2.findFieldType("TileTicks");
        OpticFinder<?> opticfinder = DSL.fieldFinder("Level", type2);
        OpticFinder<?> opticfinder1 = DSL.fieldFinder("TileTicks", type4);

        return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("ChunkToProtoChunkFix", type, this.getOutputSchema().getType(DataConverterTypes.CHUNK), (typed) -> {
            return typed.updateTyped(opticfinder, type3, (typed1) -> {
                Optional<? extends Stream<? extends Dynamic<?>>> optional = typed1.getOptionalTyped(opticfinder1).flatMap((typed2) -> {
                    return typed2.write().result();
                }).flatMap((dynamic) -> {
                    return dynamic.asStreamOpt().result();
                });
                Dynamic<?> dynamic = (Dynamic) typed1.get(DSL.remainderFinder());
                boolean flag = dynamic.get("TerrainPopulated").asBoolean(false) && (!dynamic.get("LightPopulated").asNumber().result().isPresent() || dynamic.get("LightPopulated").asBoolean(false));

                dynamic = dynamic.set("Status", dynamic.createString(flag ? "mobs_spawned" : "empty"));
                dynamic = dynamic.set("hasLegacyStructureData", dynamic.createBoolean(true));
                Dynamic dynamic1;

                if (flag) {
                    Optional<ByteBuffer> optional1 = dynamic.get("Biomes").asByteBufferOpt().result();

                    if (optional1.isPresent()) {
                        ByteBuffer bytebuffer = (ByteBuffer) optional1.get();
                        int[] aint = new int[256];

                        for (int i = 0; i < aint.length; ++i) {
                            if (i < bytebuffer.capacity()) {
                                aint[i] = bytebuffer.get(i) & 255;
                            }
                        }

                        dynamic = dynamic.set("Biomes", dynamic.createIntList(Arrays.stream(aint)));
                    }

                    List<ShortList> list = (List) IntStream.range(0, 16).mapToObj((j) -> {
                        return new ShortArrayList();
                    }).collect(Collectors.toList());

                    if (optional.isPresent()) {
                        ((Stream) optional.get()).forEach((dynamic2) -> {
                            int j = dynamic2.get("x").asInt(0);
                            int k = dynamic2.get("y").asInt(0);
                            int l = dynamic2.get("z").asInt(0);
                            short short0 = a(j, k, l);

                            ((ShortList) list.get(k >> 4)).add(short0);
                        });
                        dynamic = dynamic.set("ToBeTicked", dynamic.createList(list.stream().map((shortlist) -> {
                            Stream stream = shortlist.stream();

                            dynamic.getClass();
                            return dynamic.createList(stream.map(dynamic::createShort));
                        })));
                    }

                    dynamic1 = (Dynamic) DataFixUtils.orElse(typed1.set(DSL.remainderFinder(), dynamic).write().result(), dynamic);
                } else {
                    dynamic1 = dynamic;
                }

                return (Typed) ((Pair) type3.readTyped(dynamic1).result().orElseThrow(() -> {
                    return new IllegalStateException("Could not read the new chunk");
                })).getFirst();
            });
        }), this.writeAndRead("Structure biome inject", this.getInputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE), this.getOutputSchema().getType(DataConverterTypes.STRUCTURE_FEATURE)));
    }

    private static short a(int i, int j, int k) {
        return (short) (i & 15 | (j & 15) << 4 | (k & 15) << 8);
    }
}
