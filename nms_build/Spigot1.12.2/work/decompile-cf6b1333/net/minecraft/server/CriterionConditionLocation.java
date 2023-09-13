package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class CriterionConditionLocation {

    public static CriterionConditionLocation a = new CriterionConditionLocation(CriterionConditionValue.a, CriterionConditionValue.a, CriterionConditionValue.a, (BiomeBase) null, (String) null, (DimensionManager) null);
    private final CriterionConditionValue c;
    private final CriterionConditionValue d;
    private final CriterionConditionValue e;
    @Nullable
    final BiomeBase b;
    @Nullable
    private final String f;
    @Nullable
    private final DimensionManager g;

    public CriterionConditionLocation(CriterionConditionValue criterionconditionvalue, CriterionConditionValue criterionconditionvalue1, CriterionConditionValue criterionconditionvalue2, @Nullable BiomeBase biomebase, @Nullable String s, @Nullable DimensionManager dimensionmanager) {
        this.c = criterionconditionvalue;
        this.d = criterionconditionvalue1;
        this.e = criterionconditionvalue2;
        this.b = biomebase;
        this.f = s;
        this.g = dimensionmanager;
    }

    public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
        return this.a(worldserver, (float) d0, (float) d1, (float) d2);
    }

    public boolean a(WorldServer worldserver, float f, float f1, float f2) {
        if (!this.c.a(f)) {
            return false;
        } else if (!this.d.a(f1)) {
            return false;
        } else if (!this.e.a(f2)) {
            return false;
        } else if (this.g != null && this.g != worldserver.worldProvider.getDimensionManager()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition((double) f, (double) f1, (double) f2);

            return this.b != null && this.b != worldserver.getBiome(blockposition) ? false : this.f == null || worldserver.getChunkProviderServer().a(worldserver, this.f, blockposition);
        }
    }

    public static CriterionConditionLocation a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "location");
            JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "position", new JsonObject());
            CriterionConditionValue criterionconditionvalue = CriterionConditionValue.a(jsonobject1.get("x"));
            CriterionConditionValue criterionconditionvalue1 = CriterionConditionValue.a(jsonobject1.get("y"));
            CriterionConditionValue criterionconditionvalue2 = CriterionConditionValue.a(jsonobject1.get("z"));
            DimensionManager dimensionmanager = jsonobject.has("dimension") ? DimensionManager.a(ChatDeserializer.h(jsonobject, "dimension")) : null;
            String s = jsonobject.has("feature") ? ChatDeserializer.h(jsonobject, "feature") : null;
            BiomeBase biomebase = null;

            if (jsonobject.has("biome")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "biome"));

                biomebase = (BiomeBase) BiomeBase.REGISTRY_ID.get(minecraftkey);
                if (biomebase == null) {
                    throw new JsonSyntaxException("Unknown biome \'" + minecraftkey + "\'");
                }
            }

            return new CriterionConditionLocation(criterionconditionvalue, criterionconditionvalue1, criterionconditionvalue2, biomebase, s, dimensionmanager);
        } else {
            return CriterionConditionLocation.a;
        }
    }
}
