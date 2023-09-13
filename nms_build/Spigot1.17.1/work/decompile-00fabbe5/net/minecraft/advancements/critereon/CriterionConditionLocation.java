package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import java.util.Objects;
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
    public static final CriterionConditionLocation ANY = new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, (ResourceKey) null, (StructureGenerator) null, (ResourceKey) null, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    private final CriterionConditionValue.DoubleRange x;
    private final CriterionConditionValue.DoubleRange y;
    private final CriterionConditionValue.DoubleRange z;
    @Nullable
    private final ResourceKey<BiomeBase> biome;
    @Nullable
    private final StructureGenerator<?> feature;
    @Nullable
    private final ResourceKey<World> dimension;
    @Nullable
    private final Boolean smokey;
    private final CriterionConditionLight light;
    private final CriterionConditionBlock block;
    private final CriterionConditionFluid fluid;

    public CriterionConditionLocation(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1, CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange2, @Nullable ResourceKey<BiomeBase> resourcekey, @Nullable StructureGenerator<?> structuregenerator, @Nullable ResourceKey<World> resourcekey1, @Nullable Boolean obool, CriterionConditionLight criterionconditionlight, CriterionConditionBlock criterionconditionblock, CriterionConditionFluid criterionconditionfluid) {
        this.x = criterionconditionvalue_doublerange;
        this.y = criterionconditionvalue_doublerange1;
        this.z = criterionconditionvalue_doublerange2;
        this.biome = resourcekey;
        this.feature = structuregenerator;
        this.dimension = resourcekey1;
        this.smokey = obool;
        this.light = criterionconditionlight;
        this.block = criterionconditionblock;
        this.fluid = criterionconditionfluid;
    }

    public static CriterionConditionLocation a(ResourceKey<BiomeBase> resourcekey) {
        return new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, resourcekey, (StructureGenerator) null, (ResourceKey) null, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    }

    public static CriterionConditionLocation b(ResourceKey<World> resourcekey) {
        return new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, (ResourceKey) null, (StructureGenerator) null, resourcekey, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    }

    public static CriterionConditionLocation a(StructureGenerator<?> structuregenerator) {
        return new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, (ResourceKey) null, structuregenerator, (ResourceKey) null, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    }

    public boolean a(WorldServer worldserver, double d0, double d1, double d2) {
        if (!this.x.d(d0)) {
            return false;
        } else if (!this.y.d(d1)) {
            return false;
        } else if (!this.z.d(d2)) {
            return false;
        } else if (this.dimension != null && this.dimension != worldserver.getDimensionKey()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(d0, d1, d2);
            boolean flag = worldserver.o(blockposition);
            Optional<ResourceKey<BiomeBase>> optional = worldserver.t().d(IRegistry.BIOME_REGISTRY).c((Object) worldserver.getBiome(blockposition));

            return !optional.isPresent() ? false : (this.biome != null && (!flag || this.biome != optional.get()) ? false : (this.feature != null && (!flag || !worldserver.getStructureManager().a(blockposition, true, this.feature).e()) ? false : (this.smokey != null && (!flag || this.smokey != BlockCampfire.a((World) worldserver, blockposition)) ? false : (!this.light.a(worldserver, blockposition) ? false : (!this.block.a(worldserver, blockposition) ? false : this.fluid.a(worldserver, blockposition))))));
        }
    }

    public JsonElement a() {
        if (this == CriterionConditionLocation.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!this.x.c() || !this.y.c() || !this.z.c()) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.add("x", this.x.d());
                jsonobject1.add("y", this.y.d());
                jsonobject1.add("z", this.z.d());
                jsonobject.add("position", jsonobject1);
            }

            if (this.dimension != null) {
                DataResult dataresult = World.RESOURCE_KEY_CODEC.encodeStart(JsonOps.INSTANCE, this.dimension);
                Logger logger = CriterionConditionLocation.LOGGER;

                Objects.requireNonNull(logger);
                dataresult.resultOrPartial(logger::error).ifPresent((jsonelement) -> {
                    jsonobject.add("dimension", jsonelement);
                });
            }

            if (this.feature != null) {
                jsonobject.addProperty("feature", this.feature.g());
            }

            if (this.biome != null) {
                jsonobject.addProperty("biome", this.biome.a().toString());
            }

            if (this.smokey != null) {
                jsonobject.addProperty("smokey", this.smokey);
            }

            jsonobject.add("light", this.light.a());
            jsonobject.add("block", this.block.a());
            jsonobject.add("fluid", this.fluid.a());
            return jsonobject;
        }
    }

    public static CriterionConditionLocation a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.m(jsonelement, "location");
            JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "position", new JsonObject());
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange = CriterionConditionValue.DoubleRange.a(jsonobject1.get("x"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1 = CriterionConditionValue.DoubleRange.a(jsonobject1.get("y"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange2 = CriterionConditionValue.DoubleRange.a(jsonobject1.get("z"));
            ResourceKey resourcekey;

            if (jsonobject.has("dimension")) {
                DataResult dataresult = MinecraftKey.CODEC.parse(JsonOps.INSTANCE, jsonobject.get("dimension"));
                Logger logger = CriterionConditionLocation.LOGGER;

                Objects.requireNonNull(logger);
                resourcekey = (ResourceKey) dataresult.resultOrPartial(logger::error).map((minecraftkey) -> {
                    return ResourceKey.a(IRegistry.DIMENSION_REGISTRY, minecraftkey);
                }).orElse((Object) null);
            } else {
                resourcekey = null;
            }

            ResourceKey<World> resourcekey1 = resourcekey;
            StructureGenerator<?> structuregenerator = jsonobject.has("feature") ? (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(ChatDeserializer.h(jsonobject, "feature")) : null;
            ResourceKey<BiomeBase> resourcekey2 = null;

            if (jsonobject.has("biome")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "biome"));

                resourcekey2 = ResourceKey.a(IRegistry.BIOME_REGISTRY, minecraftkey);
            }

            Boolean obool = jsonobject.has("smokey") ? jsonobject.get("smokey").getAsBoolean() : null;
            CriterionConditionLight criterionconditionlight = CriterionConditionLight.a(jsonobject.get("light"));
            CriterionConditionBlock criterionconditionblock = CriterionConditionBlock.a(jsonobject.get("block"));
            CriterionConditionFluid criterionconditionfluid = CriterionConditionFluid.a(jsonobject.get("fluid"));

            return new CriterionConditionLocation(criterionconditionvalue_doublerange, criterionconditionvalue_doublerange1, criterionconditionvalue_doublerange2, resourcekey2, structuregenerator, resourcekey1, obool, criterionconditionlight, criterionconditionblock, criterionconditionfluid);
        } else {
            return CriterionConditionLocation.ANY;
        }
    }

    public static class a {

        private CriterionConditionValue.DoubleRange x;
        private CriterionConditionValue.DoubleRange y;
        private CriterionConditionValue.DoubleRange z;
        @Nullable
        private ResourceKey<BiomeBase> biome;
        @Nullable
        private StructureGenerator<?> feature;
        @Nullable
        private ResourceKey<World> dimension;
        @Nullable
        private Boolean smokey;
        private CriterionConditionLight light;
        private CriterionConditionBlock block;
        private CriterionConditionFluid fluid;

        public a() {
            this.x = CriterionConditionValue.DoubleRange.ANY;
            this.y = CriterionConditionValue.DoubleRange.ANY;
            this.z = CriterionConditionValue.DoubleRange.ANY;
            this.light = CriterionConditionLight.ANY;
            this.block = CriterionConditionBlock.ANY;
            this.fluid = CriterionConditionFluid.ANY;
        }

        public static CriterionConditionLocation.a a() {
            return new CriterionConditionLocation.a();
        }

        public CriterionConditionLocation.a a(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.x = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionLocation.a b(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.y = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionLocation.a c(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.z = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionLocation.a a(@Nullable ResourceKey<BiomeBase> resourcekey) {
            this.biome = resourcekey;
            return this;
        }

        public CriterionConditionLocation.a a(@Nullable StructureGenerator<?> structuregenerator) {
            this.feature = structuregenerator;
            return this;
        }

        public CriterionConditionLocation.a b(@Nullable ResourceKey<World> resourcekey) {
            this.dimension = resourcekey;
            return this;
        }

        public CriterionConditionLocation.a a(CriterionConditionLight criterionconditionlight) {
            this.light = criterionconditionlight;
            return this;
        }

        public CriterionConditionLocation.a a(CriterionConditionBlock criterionconditionblock) {
            this.block = criterionconditionblock;
            return this;
        }

        public CriterionConditionLocation.a a(CriterionConditionFluid criterionconditionfluid) {
            this.fluid = criterionconditionfluid;
            return this;
        }

        public CriterionConditionLocation.a a(Boolean obool) {
            this.smokey = obool;
            return this;
        }

        public CriterionConditionLocation b() {
            return new CriterionConditionLocation(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}
