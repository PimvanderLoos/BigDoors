package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.server.level.WorldServer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.feature.StructureGenerator;
import net.minecraft.world.level.saveddata.maps.MapIcon;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameter;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.phys.Vec3D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionExplorationMap extends LootItemFunctionConditional {

    static final Logger LOGGER = LogManager.getLogger();
    public static final StructureGenerator<?> DEFAULT_FEATURE = StructureGenerator.BURIED_TREASURE;
    public static final String DEFAULT_DECORATION_NAME = "mansion";
    public static final MapIcon.Type DEFAULT_DECORATION = MapIcon.Type.MANSION;
    public static final byte DEFAULT_ZOOM = 2;
    public static final int DEFAULT_SEARCH_RADIUS = 50;
    public static final boolean DEFAULT_SKIP_EXISTING = true;
    final StructureGenerator<?> destination;
    final MapIcon.Type mapDecoration;
    final byte zoom;
    final int searchRadius;
    final boolean skipKnownStructures;

    LootItemFunctionExplorationMap(LootItemCondition[] alootitemcondition, StructureGenerator<?> structuregenerator, MapIcon.Type mapicon_type, byte b0, int i, boolean flag) {
        super(alootitemcondition);
        this.destination = structuregenerator;
        this.mapDecoration = mapicon_type;
        this.zoom = b0;
        this.searchRadius = i;
        this.skipKnownStructures = flag;
    }

    @Override
    public LootItemFunctionType getType() {
        return LootItemFunctions.EXPLORATION_MAP;
    }

    @Override
    public Set<LootContextParameter<?>> getReferencedContextParams() {
        return ImmutableSet.of(LootContextParameters.ORIGIN);
    }

    @Override
    public ItemStack run(ItemStack itemstack, LootTableInfo loottableinfo) {
        if (!itemstack.is(Items.MAP)) {
            return itemstack;
        } else {
            Vec3D vec3d = (Vec3D) loottableinfo.getParamOrNull(LootContextParameters.ORIGIN);

            if (vec3d != null) {
                WorldServer worldserver = loottableinfo.getLevel();
                BlockPosition blockposition = worldserver.findNearestMapFeature(this.destination, new BlockPosition(vec3d), this.searchRadius, this.skipKnownStructures);

                if (blockposition != null) {
                    ItemStack itemstack1 = ItemWorldMap.create(worldserver, blockposition.getX(), blockposition.getZ(), this.zoom, true, true);

                    ItemWorldMap.renderBiomePreviewMap(worldserver, itemstack1);
                    WorldMap.addTargetDecoration(itemstack1, blockposition, "+", this.mapDecoration);
                    String s = this.destination.getFeatureName();

                    itemstack1.setHoverName(new ChatMessage("filled_map." + s.toLowerCase(Locale.ROOT)));
                    return itemstack1;
                }
            }

            return itemstack;
        }
    }

    public static LootItemFunctionExplorationMap.a makeExplorationMap() {
        return new LootItemFunctionExplorationMap.a();
    }

    public static class a extends LootItemFunctionConditional.a<LootItemFunctionExplorationMap.a> {

        private StructureGenerator<?> destination;
        private MapIcon.Type mapDecoration;
        private byte zoom;
        private int searchRadius;
        private boolean skipKnownStructures;

        public a() {
            this.destination = LootItemFunctionExplorationMap.DEFAULT_FEATURE;
            this.mapDecoration = LootItemFunctionExplorationMap.DEFAULT_DECORATION;
            this.zoom = 2;
            this.searchRadius = 50;
            this.skipKnownStructures = true;
        }

        @Override
        protected LootItemFunctionExplorationMap.a getThis() {
            return this;
        }

        public LootItemFunctionExplorationMap.a setDestination(StructureGenerator<?> structuregenerator) {
            this.destination = structuregenerator;
            return this;
        }

        public LootItemFunctionExplorationMap.a setMapDecoration(MapIcon.Type mapicon_type) {
            this.mapDecoration = mapicon_type;
            return this;
        }

        public LootItemFunctionExplorationMap.a setZoom(byte b0) {
            this.zoom = b0;
            return this;
        }

        public LootItemFunctionExplorationMap.a setSearchRadius(int i) {
            this.searchRadius = i;
            return this;
        }

        public LootItemFunctionExplorationMap.a setSkipKnownStructures(boolean flag) {
            this.skipKnownStructures = flag;
            return this;
        }

        @Override
        public LootItemFunction build() {
            return new LootItemFunctionExplorationMap(this.getConditions(), this.destination, this.mapDecoration, this.zoom, this.searchRadius, this.skipKnownStructures);
        }
    }

    public static class b extends LootItemFunctionConditional.c<LootItemFunctionExplorationMap> {

        public b() {}

        public void serialize(JsonObject jsonobject, LootItemFunctionExplorationMap lootitemfunctionexplorationmap, JsonSerializationContext jsonserializationcontext) {
            super.serialize(jsonobject, (LootItemFunctionConditional) lootitemfunctionexplorationmap, jsonserializationcontext);
            if (!lootitemfunctionexplorationmap.destination.equals(LootItemFunctionExplorationMap.DEFAULT_FEATURE)) {
                jsonobject.add("destination", jsonserializationcontext.serialize(lootitemfunctionexplorationmap.destination.getFeatureName()));
            }

            if (lootitemfunctionexplorationmap.mapDecoration != LootItemFunctionExplorationMap.DEFAULT_DECORATION) {
                jsonobject.add("decoration", jsonserializationcontext.serialize(lootitemfunctionexplorationmap.mapDecoration.toString().toLowerCase(Locale.ROOT)));
            }

            if (lootitemfunctionexplorationmap.zoom != 2) {
                jsonobject.addProperty("zoom", lootitemfunctionexplorationmap.zoom);
            }

            if (lootitemfunctionexplorationmap.searchRadius != 50) {
                jsonobject.addProperty("search_radius", lootitemfunctionexplorationmap.searchRadius);
            }

            if (!lootitemfunctionexplorationmap.skipKnownStructures) {
                jsonobject.addProperty("skip_existing_chunks", lootitemfunctionexplorationmap.skipKnownStructures);
            }

        }

        @Override
        public LootItemFunctionExplorationMap deserialize(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            StructureGenerator<?> structuregenerator = readStructure(jsonobject);
            String s = jsonobject.has("decoration") ? ChatDeserializer.getAsString(jsonobject, "decoration") : "mansion";
            MapIcon.Type mapicon_type = LootItemFunctionExplorationMap.DEFAULT_DECORATION;

            try {
                mapicon_type = MapIcon.Type.valueOf(s.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException illegalargumentexception) {
                LootItemFunctionExplorationMap.LOGGER.error("Error while parsing loot table decoration entry. Found {}. Defaulting to {}", s, LootItemFunctionExplorationMap.DEFAULT_DECORATION);
            }

            byte b0 = ChatDeserializer.getAsByte(jsonobject, "zoom", (byte) 2);
            int i = ChatDeserializer.getAsInt(jsonobject, "search_radius", 50);
            boolean flag = ChatDeserializer.getAsBoolean(jsonobject, "skip_existing_chunks", true);

            return new LootItemFunctionExplorationMap(alootitemcondition, structuregenerator, mapicon_type, b0, i, flag);
        }

        private static StructureGenerator<?> readStructure(JsonObject jsonobject) {
            if (jsonobject.has("destination")) {
                String s = ChatDeserializer.getAsString(jsonobject, "destination");
                StructureGenerator<?> structuregenerator = (StructureGenerator) StructureGenerator.STRUCTURES_REGISTRY.get(s.toLowerCase(Locale.ROOT));

                if (structuregenerator != null) {
                    return structuregenerator;
                }
            }

            return LootItemFunctionExplorationMap.DEFAULT_FEATURE;
        }
    }
}
