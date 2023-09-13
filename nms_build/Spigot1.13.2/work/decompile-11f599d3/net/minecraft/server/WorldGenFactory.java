package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldGenFactory {

    private static final Logger a = LogManager.getLogger();
    public static final Map<String, Class<? extends StructureStart>> structureStartMap = Maps.newHashMap();
    private static final Map<Class<? extends StructureStart>, String> c = Maps.newHashMap();
    private static final Map<String, Class<? extends StructurePiece>> d = Maps.newHashMap();
    private static final Map<Class<? extends StructurePiece>, String> e = Maps.newHashMap();

    private static void b(Class<? extends StructureStart> oclass, String s) {
        WorldGenFactory.structureStartMap.put(s, oclass);
        WorldGenFactory.c.put(oclass, s);
    }

    public static void a(Class<? extends StructurePiece> oclass, String s) {
        WorldGenFactory.d.put(s, oclass);
        WorldGenFactory.e.put(oclass, s);
    }

    public static String a(StructureStart structurestart) {
        return (String) WorldGenFactory.c.get(structurestart.getClass());
    }

    public static String a(StructurePiece structurepiece) {
        return (String) WorldGenFactory.e.get(structurepiece.getClass());
    }

    @Nullable
    public static StructureStart a(NBTTagCompound nbttagcompound, GeneratorAccess generatoraccess) {
        StructureStart structurestart = null;
        String s = nbttagcompound.getString("id");

        if ("INVALID".equals(s)) {
            return StructureGenerator.a;
        } else {
            try {
                Class<? extends StructureStart> oclass = (Class) WorldGenFactory.structureStartMap.get(s);

                if (oclass != null) {
                    structurestart = (StructureStart) oclass.newInstance();
                }
            } catch (Exception exception) {
                WorldGenFactory.a.warn("Failed Start with id {}", s);
                exception.printStackTrace();
            }

            if (structurestart != null) {
                structurestart.a(generatoraccess, nbttagcompound);
            } else {
                WorldGenFactory.a.warn("Skipping Structure with id {}", s);
            }

            return structurestart;
        }
    }

    public static StructurePiece b(NBTTagCompound nbttagcompound, GeneratorAccess generatoraccess) {
        StructurePiece structurepiece = null;

        try {
            Class<? extends StructurePiece> oclass = (Class) WorldGenFactory.d.get(nbttagcompound.getString("id"));

            if (oclass != null) {
                structurepiece = (StructurePiece) oclass.newInstance();
            }
        } catch (Exception exception) {
            WorldGenFactory.a.warn("Failed Piece with id {}", nbttagcompound.getString("id"));
            exception.printStackTrace();
        }

        if (structurepiece != null) {
            structurepiece.a(generatoraccess, nbttagcompound);
        } else {
            WorldGenFactory.a.warn("Skipping Piece with id {}", nbttagcompound.getString("id"));
        }

        return structurepiece;
    }

    static {
        b(WorldGenMineshaft.a.class, "Mineshaft");
        b(WorldGenVillage.a.class, "Village");
        b(WorldGenNether.a.class, "Fortress");
        b(WorldGenStronghold.a.class, "Stronghold");
        b(WorldGenFeatureJunglePyramid.a.class, "Jungle_Pyramid");
        b(WorldGenFeatureOceanRuin.a.class, "Ocean_Ruin");
        b(WorldGenFeatureDesertPyramid.a.class, "Desert_Pyramid");
        b(WorldGenFeatureIgloo.a.class, "Igloo");
        b(WorldGenFeatureSwampHut.a.class, "Swamp_Hut");
        b(WorldGenMonument.a.class, "Monument");
        b(WorldGenEndCity.a.class, "EndCity");
        b(WorldGenWoodlandMansion.a.class, "Mansion");
        b(WorldGenBuriedTreasure.a.class, "Buried_Treasure");
        b(WorldGenFeatureShipwreck.a.class, "Shipwreck");
        WorldGenMineshaftPieces.a();
        WorldGenVillagePieces.a();
        WorldGenNetherPieces.a();
        WorldGenStrongholdPieces.a();
        WorldGenJunglePyramidPiece.ad_();
        WorldGenFeatureOceanRuinPieces.a();
        WorldGenIglooPiece.a();
        WorldGenWitchHut.b();
        WorldGenDesertPyramidPiece.ac_();
        WorldGenMonumentPieces.a();
        WorldGenEndCityPieces.a();
        WorldGenWoodlandMansionPieces.a();
        WorldGenBuriedTreasurePieces.a();
        WorldGenShipwreck.a();
    }
}
