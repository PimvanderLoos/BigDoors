package net.minecraft.server;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;

public class CriterionConditionLocation {

    public static final CriterionConditionLocation a = new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, (BiomeBase) null, (StructureGenerator) null, (DimensionManager) null, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
    private final CriterionConditionValue.FloatRange b;
    private final CriterionConditionValue.FloatRange c;
    private final CriterionConditionValue.FloatRange d;
    @Nullable
    private final BiomeBase e;
    @Nullable
    private final StructureGenerator<?> f;
    @Nullable
    private final DimensionManager g;
    private final CriterionConditionLight h;
    private final CriterionConditionBlock i;
    private final CriterionConditionFluid j;

    public CriterionConditionLocation(CriterionConditionValue.FloatRange criterionconditionvalue_floatrange, CriterionConditionValue.FloatRange criterionconditionvalue_floatrange1, CriterionConditionValue.FloatRange criterionconditionvalue_floatrange2, @Nullable BiomeBase biomebase, @Nullable StructureGenerator<?> structuregenerator, @Nullable DimensionManager dimensionmanager, CriterionConditionLight criterionconditionlight, CriterionConditionBlock criterionconditionblock, CriterionConditionFluid criterionconditionfluid) {
        this.b = criterionconditionvalue_floatrange;
        this.c = criterionconditionvalue_floatrange1;
        this.d = criterionconditionvalue_floatrange2;
        this.e = biomebase;
        this.f = structuregenerator;
        this.g = dimensionmanager;
        this.h = criterionconditionlight;
        this.i = criterionconditionblock;
        this.j = criterionconditionfluid;
    }

    public static CriterionConditionLocation a(BiomeBase biomebase) {
        return new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, biomebase, (StructureGenerator) null, (DimensionManager) null, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
    }

    public static CriterionConditionLocation a(DimensionManager dimensionmanager) {
        return new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, (BiomeBase) null, (StructureGenerator) null, dimensionmanager, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
    }

    public static CriterionConditionLocation a(StructureGenerator<?> structuregenerator) {
        return new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, (BiomeBase) null, structuregenerator, (DimensionManager) null, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
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
            boolean flag = worldserver.n(blockposition);

            return this.e != null && (!flag || this.e != worldserver.getBiome(blockposition)) ? false : (this.f != null && (!flag || !this.f.b(worldserver, blockposition)) ? false : (!this.h.a(worldserver, blockposition) ? false : (!this.i.a(worldserver, blockposition) ? false : this.j.a(worldserver, blockposition))));
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
                jsonobject.addProperty("feature", (String) WorldGenerator.ao.inverse().get(this.f));
            }

            if (this.e != null) {
                jsonobject.addProperty("biome", IRegistry.BIOME.getKey(this.e).toString());
            }

            jsonobject.add("light", this.h.a());
            jsonobject.add("block", this.i.a());
            jsonobject.add("fluid", this.j.a());
            return jsonobject;
        }
    }

    public static CriterionConditionLocation a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "location");
            JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "position", new JsonObject());
            CriterionConditionValue.FloatRange criterionconditionvalue_floatrange = CriterionConditionValue.FloatRange.a(jsonobject1.get("x"));
            CriterionConditionValue.FloatRange criterionconditionvalue_floatrange1 = CriterionConditionValue.FloatRange.a(jsonobject1.get("y"));
            CriterionConditionValue.FloatRange criterionconditionvalue_floatrange2 = CriterionConditionValue.FloatRange.a(jsonobject1.get("z"));
            DimensionManager dimensionmanager = jsonobject.has("dimension") ? DimensionManager.a(new MinecraftKey(ChatDeserializer.h(jsonobject, "dimension"))) : null;
            StructureGenerator<?> structuregenerator = jsonobject.has("feature") ? (StructureGenerator) WorldGenerator.ao.get(ChatDeserializer.h(jsonobject, "feature")) : null;
            BiomeBase biomebase = null;

            if (jsonobject.has("biome")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "biome"));

                biomebase = (BiomeBase) IRegistry.BIOME.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown biome '" + minecraftkey + "'");
                });
            }

            CriterionConditionLight criterionconditionlight = CriterionConditionLight.a(jsonobject.get("light"));
            CriterionConditionBlock criterionconditionblock = CriterionConditionBlock.a(jsonobject.get("block"));
            CriterionConditionFluid criterionconditionfluid = CriterionConditionFluid.a(jsonobject.get("fluid"));

            return new CriterionConditionLocation(criterionconditionvalue_floatrange, criterionconditionvalue_floatrange1, criterionconditionvalue_floatrange2, biomebase, structuregenerator, dimensionmanager, criterionconditionlight, criterionconditionblock, criterionconditionfluid);
        } else {
            return CriterionConditionLocation.a;
        }
    }

    public static class a {

        private CriterionConditionValue.FloatRange a;
        private CriterionConditionValue.FloatRange b;
        private CriterionConditionValue.FloatRange c;
        @Nullable
        private BiomeBase d;
        @Nullable
        private StructureGenerator<?> e;
        @Nullable
        private DimensionManager f;
        private CriterionConditionLight g;
        private CriterionConditionBlock h;
        private CriterionConditionFluid i;

        public a() {
            this.a = CriterionConditionValue.FloatRange.e;
            this.b = CriterionConditionValue.FloatRange.e;
            this.c = CriterionConditionValue.FloatRange.e;
            this.g = CriterionConditionLight.a;
            this.h = CriterionConditionBlock.a;
            this.i = CriterionConditionFluid.a;
        }

        public static CriterionConditionLocation.a a() {
            return new CriterionConditionLocation.a();
        }

        public CriterionConditionLocation.a a(@Nullable BiomeBase biomebase) {
            this.d = biomebase;
            return this;
        }

        public CriterionConditionLocation b() {
            return new CriterionConditionLocation(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i);
        }
    }
}
