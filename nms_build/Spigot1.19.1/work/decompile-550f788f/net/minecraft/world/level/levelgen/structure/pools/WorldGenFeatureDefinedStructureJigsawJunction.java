package net.minecraft.world.level.levelgen.structure.pools;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

public class WorldGenFeatureDefinedStructureJigsawJunction {

    private final int sourceX;
    private final int sourceGroundY;
    private final int sourceZ;
    private final int deltaY;
    private final WorldGenFeatureDefinedStructurePoolTemplate.Matching destProjection;

    public WorldGenFeatureDefinedStructureJigsawJunction(int i, int j, int k, int l, WorldGenFeatureDefinedStructurePoolTemplate.Matching worldgenfeaturedefinedstructurepooltemplate_matching) {
        this.sourceX = i;
        this.sourceGroundY = j;
        this.sourceZ = k;
        this.deltaY = l;
        this.destProjection = worldgenfeaturedefinedstructurepooltemplate_matching;
    }

    public int getSourceX() {
        return this.sourceX;
    }

    public int getSourceGroundY() {
        return this.sourceGroundY;
    }

    public int getSourceZ() {
        return this.sourceZ;
    }

    public int getDeltaY() {
        return this.deltaY;
    }

    public WorldGenFeatureDefinedStructurePoolTemplate.Matching getDestProjection() {
        return this.destProjection;
    }

    public <T> Dynamic<T> serialize(DynamicOps<T> dynamicops) {
        Builder<T, T> builder = ImmutableMap.builder();

        builder.put(dynamicops.createString("source_x"), dynamicops.createInt(this.sourceX)).put(dynamicops.createString("source_ground_y"), dynamicops.createInt(this.sourceGroundY)).put(dynamicops.createString("source_z"), dynamicops.createInt(this.sourceZ)).put(dynamicops.createString("delta_y"), dynamicops.createInt(this.deltaY)).put(dynamicops.createString("dest_proj"), dynamicops.createString(this.destProjection.getName()));
        return new Dynamic(dynamicops, dynamicops.createMap(builder.build()));
    }

    public static <T> WorldGenFeatureDefinedStructureJigsawJunction deserialize(Dynamic<T> dynamic) {
        return new WorldGenFeatureDefinedStructureJigsawJunction(dynamic.get("source_x").asInt(0), dynamic.get("source_ground_y").asInt(0), dynamic.get("source_z").asInt(0), dynamic.get("delta_y").asInt(0), WorldGenFeatureDefinedStructurePoolTemplate.Matching.byName(dynamic.get("dest_proj").asString("")));
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        } else if (object != null && this.getClass() == object.getClass()) {
            WorldGenFeatureDefinedStructureJigsawJunction worldgenfeaturedefinedstructurejigsawjunction = (WorldGenFeatureDefinedStructureJigsawJunction) object;

            return this.sourceX != worldgenfeaturedefinedstructurejigsawjunction.sourceX ? false : (this.sourceZ != worldgenfeaturedefinedstructurejigsawjunction.sourceZ ? false : (this.deltaY != worldgenfeaturedefinedstructurejigsawjunction.deltaY ? false : this.destProjection == worldgenfeaturedefinedstructurejigsawjunction.destProjection));
        } else {
            return false;
        }
    }

    public int hashCode() {
        int i = this.sourceX;

        i = 31 * i + this.sourceGroundY;
        i = 31 * i + this.sourceZ;
        i = 31 * i + this.deltaY;
        i = 31 * i + this.destProjection.hashCode();
        return i;
    }

    public String toString() {
        return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + "}";
    }
}
