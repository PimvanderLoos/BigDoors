package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;

public class WorldProviderNormal extends WorldProvider {

    public WorldProviderNormal() {}

    public DimensionManager getDimensionManager() {
        return DimensionManager.OVERWORLD;
    }

    public boolean a(int i, int j) {
        return !this.b.e(i, j) && super.a(i, j);
    }

    protected void m() {
        this.e = true;
    }

    public ChunkGenerator<? extends GeneratorSettings> getChunkGenerator() {
        WorldType worldtype = this.b.getWorldData().getType();
        ChunkGeneratorType<GeneratorSettingsFlat, ChunkProviderFlat> chunkgeneratortype = ChunkGeneratorType.e;
        ChunkGeneratorType<GeneratorSettingsDebug, ChunkProviderDebug> chunkgeneratortype1 = ChunkGeneratorType.d;
        ChunkGeneratorType<GeneratorSettingsNether, ChunkProviderHell> chunkgeneratortype2 = ChunkGeneratorType.b;
        ChunkGeneratorType<GeneratorSettingsEnd, ChunkProviderTheEnd> chunkgeneratortype3 = ChunkGeneratorType.c;
        ChunkGeneratorType<GeneratorSettingsOverworld, ChunkProviderGenerate> chunkgeneratortype4 = ChunkGeneratorType.a;
        BiomeLayout<BiomeLayoutFixedConfiguration, WorldChunkManagerHell> biomelayout = BiomeLayout.b;
        BiomeLayout<BiomeLayoutOverworldConfiguration, WorldChunkManagerOverworld> biomelayout1 = BiomeLayout.c;
        BiomeLayout<BiomeLayoutCheckerboardConfiguration, WorldChunkManagerCheckerBoard> biomelayout2 = BiomeLayout.a;

        if (worldtype == WorldType.FLAT) {
            GeneratorSettingsFlat generatorsettingsflat = GeneratorSettingsFlat.a(new Dynamic(DynamicOpsNBT.a, this.b.getWorldData().getGeneratorOptions()));
            BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration = ((BiomeLayoutFixedConfiguration) biomelayout.b()).a(generatorsettingsflat.t());

            return chunkgeneratortype.create(this.b, biomelayout.a(biomelayoutfixedconfiguration), generatorsettingsflat);
        } else if (worldtype == WorldType.DEBUG_ALL_BLOCK_STATES) {
            BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration1 = ((BiomeLayoutFixedConfiguration) biomelayout.b()).a(Biomes.PLAINS);

            return chunkgeneratortype1.create(this.b, biomelayout.a(biomelayoutfixedconfiguration1), chunkgeneratortype1.b());
        } else if (worldtype != WorldType.g) {
            GeneratorSettingsOverworld generatorsettingsoverworld = (GeneratorSettingsOverworld) chunkgeneratortype4.b();
            BiomeLayoutOverworldConfiguration biomelayoutoverworldconfiguration = ((BiomeLayoutOverworldConfiguration) biomelayout1.b()).a(this.b.getWorldData()).a(generatorsettingsoverworld);

            return chunkgeneratortype4.create(this.b, biomelayout1.a(biomelayoutoverworldconfiguration), generatorsettingsoverworld);
        } else {
            WorldChunkManager worldchunkmanager = null;
            JsonElement jsonelement = (JsonElement) Dynamic.convert(DynamicOpsNBT.a, JsonOps.INSTANCE, this.b.getWorldData().getGeneratorOptions());
            JsonObject jsonobject = jsonelement.getAsJsonObject();

            if (jsonobject.has("biome_source") && jsonobject.getAsJsonObject("biome_source").has("type") && jsonobject.getAsJsonObject("biome_source").has("options")) {
                MinecraftKey minecraftkey = new MinecraftKey(jsonobject.getAsJsonObject("biome_source").getAsJsonPrimitive("type").getAsString());
                JsonObject jsonobject1 = jsonobject.getAsJsonObject("biome_source").getAsJsonObject("options");
                BiomeBase[] abiomebase = new BiomeBase[] { Biomes.OCEAN};

                if (jsonobject1.has("biomes")) {
                    JsonArray jsonarray = jsonobject1.getAsJsonArray("biomes");

                    abiomebase = jsonarray.size() > 0 ? new BiomeBase[jsonarray.size()] : new BiomeBase[] { Biomes.OCEAN};

                    for (int i = 0; i < jsonarray.size(); ++i) {
                        BiomeBase biomebase = (BiomeBase) IRegistry.BIOME.get(new MinecraftKey(jsonarray.get(i).getAsString()));

                        abiomebase[i] = biomebase != null ? biomebase : Biomes.OCEAN;
                    }
                }

                if (BiomeLayout.b.c().equals(minecraftkey)) {
                    BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration2 = ((BiomeLayoutFixedConfiguration) biomelayout.b()).a(abiomebase[0]);

                    worldchunkmanager = biomelayout.a(biomelayoutfixedconfiguration2);
                }

                if (BiomeLayout.a.c().equals(minecraftkey)) {
                    int j = jsonobject1.has("size") ? jsonobject1.getAsJsonPrimitive("size").getAsInt() : 2;
                    BiomeLayoutCheckerboardConfiguration biomelayoutcheckerboardconfiguration = ((BiomeLayoutCheckerboardConfiguration) biomelayout2.b()).a(abiomebase).a(j);

                    worldchunkmanager = biomelayout2.a(biomelayoutcheckerboardconfiguration);
                }

                if (BiomeLayout.c.c().equals(minecraftkey)) {
                    BiomeLayoutOverworldConfiguration biomelayoutoverworldconfiguration1 = ((BiomeLayoutOverworldConfiguration) biomelayout1.b()).a(new GeneratorSettingsOverworld()).a(this.b.getWorldData());

                    worldchunkmanager = biomelayout1.a(biomelayoutoverworldconfiguration1);
                }
            }

            if (worldchunkmanager == null) {
                worldchunkmanager = biomelayout.a(((BiomeLayoutFixedConfiguration) biomelayout.b()).a(Biomes.OCEAN));
            }

            IBlockData iblockdata = Blocks.STONE.getBlockData();
            IBlockData iblockdata1 = Blocks.WATER.getBlockData();

            if (jsonobject.has("chunk_generator") && jsonobject.getAsJsonObject("chunk_generator").has("options")) {
                String s;
                Block block;

                if (jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_block")) {
                    s = jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_block").getAsString();
                    block = (Block) IRegistry.BLOCK.getOrDefault(new MinecraftKey(s));
                    if (block != null) {
                        iblockdata = block.getBlockData();
                    }
                }

                if (jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").has("default_fluid")) {
                    s = jsonobject.getAsJsonObject("chunk_generator").getAsJsonObject("options").getAsJsonPrimitive("default_fluid").getAsString();
                    block = (Block) IRegistry.BLOCK.getOrDefault(new MinecraftKey(s));
                    if (block != null) {
                        iblockdata1 = block.getBlockData();
                    }
                }
            }

            if (jsonobject.has("chunk_generator") && jsonobject.getAsJsonObject("chunk_generator").has("type")) {
                MinecraftKey minecraftkey1 = new MinecraftKey(jsonobject.getAsJsonObject("chunk_generator").getAsJsonPrimitive("type").getAsString());

                if (ChunkGeneratorType.b.d().equals(minecraftkey1)) {
                    GeneratorSettingsNether generatorsettingsnether = (GeneratorSettingsNether) chunkgeneratortype2.b();

                    generatorsettingsnether.a(iblockdata);
                    generatorsettingsnether.b(iblockdata1);
                    return chunkgeneratortype2.create(this.b, worldchunkmanager, generatorsettingsnether);
                }

                if (ChunkGeneratorType.c.d().equals(minecraftkey1)) {
                    GeneratorSettingsEnd generatorsettingsend = (GeneratorSettingsEnd) chunkgeneratortype3.b();

                    generatorsettingsend.a(new BlockPosition(0, 64, 0));
                    generatorsettingsend.a(iblockdata);
                    generatorsettingsend.b(iblockdata1);
                    return chunkgeneratortype3.create(this.b, worldchunkmanager, generatorsettingsend);
                }
            }

            GeneratorSettingsOverworld generatorsettingsoverworld1 = (GeneratorSettingsOverworld) chunkgeneratortype4.b();

            generatorsettingsoverworld1.a(iblockdata);
            generatorsettingsoverworld1.b(iblockdata1);
            return chunkgeneratortype4.create(this.b, worldchunkmanager, generatorsettingsoverworld1);
        }
    }

    @Nullable
    public BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        for (int i = chunkcoordintpair.d(); i <= chunkcoordintpair.f(); ++i) {
            for (int j = chunkcoordintpair.e(); j <= chunkcoordintpair.g(); ++j) {
                BlockPosition blockposition = this.a(i, j, flag);

                if (blockposition != null) {
                    return blockposition;
                }
            }
        }

        return null;
    }

    @Nullable
    public BlockPosition a(int i, int j, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, 0, j);
        BiomeBase biomebase = this.b.getBiome(blockposition_mutableblockposition);
        IBlockData iblockdata = biomebase.r().a();

        if (flag && !iblockdata.getBlock().a(TagsBlock.VALID_SPAWN)) {
            return null;
        } else {
            Chunk chunk = this.b.getChunkAt(i >> 4, j >> 4);
            int k = chunk.a(HeightMap.Type.MOTION_BLOCKING, i & 15, j & 15);

            if (k < 0) {
                return null;
            } else if (chunk.a(HeightMap.Type.WORLD_SURFACE, i & 15, j & 15) > chunk.a(HeightMap.Type.OCEAN_FLOOR, i & 15, j & 15)) {
                return null;
            } else {
                for (int l = k + 1; l >= 0; --l) {
                    blockposition_mutableblockposition.c(i, l, j);
                    IBlockData iblockdata1 = this.b.getType(blockposition_mutableblockposition);

                    if (!iblockdata1.s().e()) {
                        break;
                    }

                    if (iblockdata1.equals(iblockdata)) {
                        return blockposition_mutableblockposition.up().h();
                    }
                }

                return null;
            }
        }
    }

    public float a(long i, float f) {
        int j = (int) (i % 24000L);
        float f1 = ((float) j + f) / 24000.0F - 0.25F;

        if (f1 < 0.0F) {
            ++f1;
        }

        if (f1 > 1.0F) {
            --f1;
        }

        float f2 = f1;

        f1 = 1.0F - (float) ((Math.cos((double) f1 * 3.141592653589793D) + 1.0D) / 2.0D);
        f1 = f2 + (f1 - f2) / 3.0F;
        return f1;
    }

    public boolean isOverworld() {
        return true;
    }

    public boolean canRespawn() {
        return true;
    }
}
