package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class CriterionConditionLocation {

    public static final CriterionConditionLocation a = new CriterionConditionLocation(CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e, (BiomeBase) null, (String) null, (DimensionManager) null);
    private final CriterionConditionValue.c b;
    private final CriterionConditionValue.c c;
    private final CriterionConditionValue.c d;
    @Nullable
    private final BiomeBase e;
    @Nullable
    private final String f;
    @Nullable
    private final DimensionManager g;

    public CriterionConditionLocation(CriterionConditionValue.c criterionconditionvalue_c, CriterionConditionValue.c criterionconditionvalue_c1, CriterionConditionValue.c criterionconditionvalue_c2, @Nullable BiomeBase biomebase, @Nullable String s, @Nullable DimensionManager dimensionmanager) {
        this.b = criterionconditionvalue_c;
        this.c = criterionconditionvalue_c1;
        this.d = criterionconditionvalue_c2;
        this.e = biomebase;
        this.f = s;
        this.g = dimensionmanager;
    }

    public static CriterionConditionLocation a(BiomeBase biomebase) {
        return new CriterionConditionLocation(CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e, biomebase, (String) null, (DimensionManager) null);
    }

    public static CriterionConditionLocation a(DimensionManager dimensionmanager) {
        return new CriterionConditionLocation(CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e, (BiomeBase) null, (String) null, dimensionmanager);
    }

    public static CriterionConditionLocation a(String s) {
        return new CriterionConditionLocation(CriterionConditionValue.c.e, CriterionConditionValue.c.e, CriterionConditionValue.c.e, (BiomeBase) null, s, (DimensionManager) null);
    }

    public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
        return this.a(worldserver, (float) d0, (float) d1, (float) d2);
    }

    public boolean a(WorldServer worldserver, float f, float f1, float f2) {
        if (!this.b.d(f)) {
            return false;
        } else if (!this.c.d(f1)) {
            return false;
        } else if (!this.d.d(f2)) {
            return false;
        } else if (this.g != null && this.g != worldserver.worldProvider.getDimensionManager()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition((double) f, (double) f1, (double) f2);

            return this.e != null && this.e != worldserver.getBiome(blockposition) ? false : this.f == null || WorldGenerator.a(worldserver, this.f, blockposition);
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionLocation.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!this.b.c() || !this.c.c() || !this.d.c()) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.add("x", this.b.d());
                jsonobject1.add("y", this.c.d());
                jsonobject1.add("z", this.d.d());
                jsonobject.add("position", jsonobject1);
            }

            if (this.g != null) {
                jsonobject.addProperty("dimension", DimensionManager.a(this.g).toString());
            }

            if (this.f != null) {
                jsonobject.addProperty("feature", this.f);
            }

            if (this.e != null) {
                jsonobject.addProperty("biome", IRegistry.BIOME.getKey(this.e).toString());
            }

            return jsonobject;
        }
    }

    public static CriterionConditionLocation a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "location");
            JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "position", new JsonObject());
            CriterionConditionValue.c criterionconditionvalue_c = CriterionConditionValue.c.a(jsonobject1.get("x"));
            CriterionConditionValue.c criterionconditionvalue_c1 = CriterionConditionValue.c.a(jsonobject1.get("y"));
            CriterionConditionValue.c criterionconditionvalue_c2 = CriterionConditionValue.c.a(jsonobject1.get("z"));
            DimensionManager dimensionmanager = jsonobject.has("dimension") ? DimensionManager.a(new MinecraftKey(ChatDeserializer.h(jsonobject, "dimension"))) : null;
            String s = jsonobject.has("feature") ? ChatDeserializer.h(jsonobject, "feature") : null;
            BiomeBase biomebase = null;

            if (jsonobject.has("biome")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "biome"));

                biomebase = (BiomeBase) IRegistry.BIOME.get(minecraftkey);
                if (biomebase == null) {
                    throw new JsonSyntaxException("Unknown biome \'" + minecraftkey + "\'");
                }
            }

            return new CriterionConditionLocation(criterionconditionvalue_c, criterionconditionvalue_c1, criterionconditionvalue_c2, biomebase, s, dimensionmanager);
        } else {
            return CriterionConditionLocation.a;
        }
    }
}
