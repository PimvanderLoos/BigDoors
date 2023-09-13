package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.BlockCampfire;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CriterionConditionLocation {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final CriterionConditionLocation a = new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, (ResourceKey) null, (StructureGenerator) null, (ResourceKey) null, (Boolean) null, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
    private final CriterionConditionValue.FloatRange c;
    private final CriterionConditionValue.FloatRange d;
    private final CriterionConditionValue.FloatRange e;
    @Nullable
    private final ResourceKey<BiomeBase> f;
    @Nullable
    private final StructureGenerator<?> g;
    @Nullable
    private final ResourceKey<World> h;
    @Nullable
    private final Boolean i;
    private final CriterionConditionLight j;
    private final CriterionConditionBlock k;
    private final CriterionConditionFluid l;

    public CriterionConditionLocation(CriterionConditionValue.FloatRange criterionconditionvalue_floatrange, CriterionConditionValue.FloatRange criterionconditionvalue_floatrange1, CriterionConditionValue.FloatRange criterionconditionvalue_floatrange2, @Nullable ResourceKey<BiomeBase> resourcekey, @Nullable StructureGenerator<?> structuregenerator, @Nullable ResourceKey<World> resourcekey1, @Nullable Boolean obool, CriterionConditionLight criterionconditionlight, CriterionConditionBlock criterionconditionblock, CriterionConditionFluid criterionconditionfluid) {
        this.c = criterionconditionvalue_floatrange;
        this.d = criterionconditionvalue_floatrange1;
        this.e = criterionconditionvalue_floatrange2;
        this.f = resourcekey;
        this.g = structuregenerator;
        this.h = resourcekey1;
        this.i = obool;
        this.j = criterionconditionlight;
        this.k = criterionconditionblock;
        this.l = criterionconditionfluid;
    }

    public static CriterionConditionLocation a(ResourceKey<BiomeBase> resourcekey) {
        return new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, resourcekey, (StructureGenerator) null, (ResourceKey) null, (Boolean) null, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
    }

    public static CriterionConditionLocation b(ResourceKey<World> resourcekey) {
        return new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, (ResourceKey) null, (StructureGenerator) null, resourcekey, (Boolean) null, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
    }

    public static CriterionConditionLocation a(StructureGenerator<?> structuregenerator) {
        return new CriterionConditionLocation(CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, CriterionConditionValue.FloatRange.e, (ResourceKey) null, structuregenerator, (ResourceKey) null, (Boolean) null, CriterionConditionLight.a, CriterionConditionBlock.a, CriterionConditionFluid.a);
    }

    public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
        return this.a(worldserver, (float) d0, (float) d1, (float) d2);
    }

    public boolean a(WorldServer worldserver, float f, float f1, float f2) {
        if (!this.c.d(f)) {
            return false;
        } else if (!this.d.d(f1)) {
            return false;
        } else if (!this.e.d(f2)) {
            return false;
        } else if (this.h != null && this.h != worldserver.getDimensionKey()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition((double) f, (double) f1, (double) f2);
            boolean flag = worldserver.p(blockposition);
            Optional<ResourceKey<BiomeBase>> optional = worldserver.r().b(IRegistry.ay).c(worldserver.getBiome(blockposition));

            return !optional.isPresent() ? false : (this.f != null && (!flag || this.f != optional.get()) ? false : (this.g != null && (!flag || !worldserver.getStructureManager().a(blockposition, true, this.g).e()) ? false : (this.i != null && (!flag || this.i != BlockCampfire.a((World) worldserver, blockposition)) ? false : (!this.j.a(worldserver, blockposition) ? false : (!this.k.a(worldserver, blockposition) ? false : this.l.a(worldserver, blockposition))))));
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionLocation.a) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!this.c.c() || !this.d.c() || !this.e.c()) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.add("x", this.c.d());
                jsonobject1.add("y", this.d.d());
                jsonobject1.add("z", this.e.d());
                jsonobject.add("position", jsonobject1);
            }

            if (this.h != null) {
                DataResult dataresult = World.f.encodeStart(JsonOps.INSTANCE, this.h);
                Logger logger = CriterionConditionLocation.LOGGER;

                logger.getClass();
                dataresult.resultOrPartial(logger::error).ifPresent((jsonelement) -> {
                    jsonobject.add("dimension", jsonelement);
                });
            }

            if (this.g != null) {
                jsonobject.addProperty("feature", this.g.i());
            }

            if (this.f != null) {
                jsonobject.addProperty("biome", this.f.a().toString());
            }

            if (this.i != null) {
                jsonobject.addProperty("smokey", this.i);
            }

            jsonobject.add("light", this.j.a());
            jsonobject.add("block", this.k.a());
            jsonobject.add("fluid", this.l.a());
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
            ResourceKey resourcekey;

            if (jsonobject.has("dimension")) {
                DataResult dataresult = MinecraftKey.a.parse(JsonOps.INSTANCE, jsonobject.get("dimension"));
                Logger logger = CriterionConditionLocation.LOGGER;

                logger.getClass();
                resourcekey = (ResourceKey) dataresult.resultOrPartial(logger::error).map((minecraftkey) -> {
                    return ResourceKey.a(IRegistry.L, minecraftkey);
                }).orElse((Object) null);
            } else {
                resourcekey = null;
            }

            ResourceKey<World> resourcekey1 = resourcekey;
            StructureGenerator<?> structuregenerator = jsonobject.has("feature") ? (StructureGenerator) StructureGenerator.a.get(ChatDeserializer.h(jsonobject, "feature")) : null;
            ResourceKey<BiomeBase> resourcekey2 = null;

            if (jsonobject.has("biome")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "biome"));

                resourcekey2 = ResourceKey.a(IRegistry.ay, minecraftkey);
            }

            Boolean obool = jsonobject.has("smokey") ? jsonobject.get("smokey").getAsBoolean() : null;
            CriterionConditionLight criterionconditionlight = CriterionConditionLight.a(jsonobject.get("light"));
            CriterionConditionBlock criterionconditionblock = CriterionConditionBlock.a(jsonobject.get("block"));
            CriterionConditionFluid criterionconditionfluid = CriterionConditionFluid.a(jsonobject.get("fluid"));

            return new CriterionConditionLocation(criterionconditionvalue_floatrange, criterionconditionvalue_floatrange1, criterionconditionvalue_floatrange2, resourcekey2, structuregenerator, resourcekey1, obool, criterionconditionlight, criterionconditionblock, criterionconditionfluid);
        } else {
            return CriterionConditionLocation.a;
        }
    }

    public static class a {

        private CriterionConditionValue.FloatRange a;
        private CriterionConditionValue.FloatRange b;
        private CriterionConditionValue.FloatRange c;
        @Nullable
        private ResourceKey<BiomeBase> d;
        @Nullable
        private StructureGenerator<?> e;
        @Nullable
        private ResourceKey<World> f;
        @Nullable
        private Boolean g;
        private CriterionConditionLight h;
        private CriterionConditionBlock i;
        private CriterionConditionFluid j;

        public a() {
            this.a = CriterionConditionValue.FloatRange.e;
            this.b = CriterionConditionValue.FloatRange.e;
            this.c = CriterionConditionValue.FloatRange.e;
            this.h = CriterionConditionLight.a;
            this.i = CriterionConditionBlock.a;
            this.j = CriterionConditionFluid.a;
        }

        public static CriterionConditionLocation.a a() {
            return new CriterionConditionLocation.a();
        }

        public CriterionConditionLocation.a a(@Nullable ResourceKey<BiomeBase> resourcekey) {
            this.d = resourcekey;
            return this;
        }

        public CriterionConditionLocation.a a(CriterionConditionBlock criterionconditionblock) {
            this.i = criterionconditionblock;
            return this;
        }

        public CriterionConditionLocation.a a(Boolean obool) {
            this.g = obool;
            return this;
        }

        public CriterionConditionLocation b() {
            return new CriterionConditionLocation(this.a, this.b, this.c, this.d, this.e, this.f, this.g, this.h, this.i, this.j);
        }
    }
}
