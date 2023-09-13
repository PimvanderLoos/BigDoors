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

    public static CriterionConditionLocation inBiome(ResourceKey<BiomeBase> resourcekey) {
        return new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, resourcekey, (StructureGenerator) null, (ResourceKey) null, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    }

    public static CriterionConditionLocation inDimension(ResourceKey<World> resourcekey) {
        return new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, (ResourceKey) null, (StructureGenerator) null, resourcekey, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    }

    public static CriterionConditionLocation inFeature(StructureGenerator<?> structuregenerator) {
        return new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, CriterionConditionValue.DoubleRange.ANY, (ResourceKey) null, structuregenerator, (ResourceKey) null, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    }

    public static CriterionConditionLocation atYLocation(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
        return new CriterionConditionLocation(CriterionConditionValue.DoubleRange.ANY, criterionconditionvalue_doublerange, CriterionConditionValue.DoubleRange.ANY, (ResourceKey) null, (StructureGenerator) null, (ResourceKey) null, (Boolean) null, CriterionConditionLight.ANY, CriterionConditionBlock.ANY, CriterionConditionFluid.ANY);
    }

    public boolean matches(WorldServer worldserver, double d0, double d1, double d2) {
        if (!this.x.matches(d0)) {
            return false;
        } else if (!this.y.matches(d1)) {
            return false;
        } else if (!this.z.matches(d2)) {
            return false;
        } else if (this.dimension != null && this.dimension != worldserver.dimension()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(d0, d1, d2);
            boolean flag = worldserver.isLoaded(blockposition);
            Optional<ResourceKey<BiomeBase>> optional = worldserver.registryAccess().registryOrThrow(IRegistry.BIOME_REGISTRY).getResourceKey(worldserver.getBiome(blockposition));

            return !optional.isPresent() ? false : (this.biome != null && (!flag || this.biome != optional.get()) ? false : (this.feature != null && (!flag || !worldserver.structureFeatureManager().getStructureWithPieceAt(blockposition, this.feature).isValid()) ? false : (this.smokey != null && (!flag || this.smokey != BlockCampfire.isSmokeyPos(worldserver, blockposition)) ? false : (!this.light.matches(worldserver, blockposition) ? false : (!this.block.matches(worldserver, blockposition) ? false : this.fluid.matches(worldserver, blockposition))))));
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionLocation.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.add("x", this.x.serializeToJson());
                jsonobject1.add("y", this.y.serializeToJson());
                jsonobject1.add("z", this.z.serializeToJson());
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
                jsonobject.addProperty("feature", this.feature.getFeatureName());
            }

            if (this.biome != null) {
                jsonobject.addProperty("biome", this.biome.location().toString());
            }

            if (this.smokey != null) {
                jsonobject.addProperty("smokey", this.smokey);
            }

            jsonobject.add("light", this.light.serializeToJson());
            jsonobject.add("block", this.block.serializeToJson());
            jsonobject.add("fluid", this.fluid.serializeToJson());
            return jsonobject;
        }
    }

    public static CriterionConditionLocation fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "location");
            JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "position", new JsonObject());
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange = CriterionConditionValue.DoubleRange.fromJson(jsonobject1.get("x"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange1 = CriterionConditionValue.DoubleRange.fromJson(jsonobject1.get("y"));
            CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange2 = CriterionConditionValue.DoubleRange.fromJson(jsonobject1.get("z"));
            ResourceKey resourcekey;

            if (jsonobject.has("dimension")) {
                DataResult dataresult = MinecraftKey.CODEC.parse(JsonOps.INSTANCE, jsonobject.get("dimension"));
                Logger logger = CriterionConditionLocation.LOGGER;

                Objects.requireNonNull(logger);
                resourcekey = (ResourceKey) dataresult.resultOrPartial(logger::error).map((minecraftkey) -> {
                    return ResourceKey.create(IRegistry.DIMENSION_REGISTRY, minecraftkey);
                }).orElse((Object) null);
            } else {
                resourcekey = null;
            }

            ResourceKey<World> resourcekey1 = resourcekey;
            StructureGenerator<?> structuregenerator = jsonobject.has("feature") ? (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(ChatDeserializer.getAsString(jsonobject, "feature")) : null;
            ResourceKey<BiomeBase> resourcekey2 = null;

            if (jsonobject.has("biome")) {
                MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "biome"));

                resourcekey2 = ResourceKey.create(IRegistry.BIOME_REGISTRY, minecraftkey);
            }

            Boolean obool = jsonobject.has("smokey") ? jsonobject.get("smokey").getAsBoolean() : null;
            CriterionConditionLight criterionconditionlight = CriterionConditionLight.fromJson(jsonobject.get("light"));
            CriterionConditionBlock criterionconditionblock = CriterionConditionBlock.fromJson(jsonobject.get("block"));
            CriterionConditionFluid criterionconditionfluid = CriterionConditionFluid.fromJson(jsonobject.get("fluid"));

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

        public static CriterionConditionLocation.a location() {
            return new CriterionConditionLocation.a();
        }

        public CriterionConditionLocation.a setX(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.x = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionLocation.a setY(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.y = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionLocation.a setZ(CriterionConditionValue.DoubleRange criterionconditionvalue_doublerange) {
            this.z = criterionconditionvalue_doublerange;
            return this;
        }

        public CriterionConditionLocation.a setBiome(@Nullable ResourceKey<BiomeBase> resourcekey) {
            this.biome = resourcekey;
            return this;
        }

        public CriterionConditionLocation.a setFeature(@Nullable StructureGenerator<?> structuregenerator) {
            this.feature = structuregenerator;
            return this;
        }

        public CriterionConditionLocation.a setDimension(@Nullable ResourceKey<World> resourcekey) {
            this.dimension = resourcekey;
            return this;
        }

        public CriterionConditionLocation.a setLight(CriterionConditionLight criterionconditionlight) {
            this.light = criterionconditionlight;
            return this;
        }

        public CriterionConditionLocation.a setBlock(CriterionConditionBlock criterionconditionblock) {
            this.block = criterionconditionblock;
            return this;
        }

        public CriterionConditionLocation.a setFluid(CriterionConditionFluid criterionconditionfluid) {
            this.fluid = criterionconditionfluid;
            return this;
        }

        public CriterionConditionLocation.a setSmokey(Boolean obool) {
            this.smokey = obool;
            return this;
        }

        public CriterionConditionLocation build() {
            return new CriterionConditionLocation(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.smokey, this.light, this.block, this.fluid);
        }
    }
}
