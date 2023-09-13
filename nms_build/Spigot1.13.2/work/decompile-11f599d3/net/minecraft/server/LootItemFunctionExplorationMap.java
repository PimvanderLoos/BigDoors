package net.minecraft.server;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Locale;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootItemFunctionExplorationMap extends LootItemFunction {

    private static final Logger a = LogManager.getLogger();
    private final String b;
    private final MapIcon.Type c;
    private final byte d;
    private final int e;
    private final boolean f;

    public LootItemFunctionExplorationMap(LootItemCondition[] alootitemcondition, String s, MapIcon.Type mapicon_type, byte b0, int i, boolean flag) {
        super(alootitemcondition);
        this.b = s;
        this.c = mapicon_type;
        this.d = b0;
        this.e = i;
        this.f = flag;
    }

    public ItemStack a(ItemStack itemstack, Random random, LootTableInfo loottableinfo) {
        if (itemstack.getItem() != Items.MAP) {
            return itemstack;
        } else {
            BlockPosition blockposition = loottableinfo.e();

            if (blockposition == null) {
                return itemstack;
            } else {
                WorldServer worldserver = loottableinfo.h();
                BlockPosition blockposition1 = worldserver.a(this.b, blockposition, this.e, this.f);

                if (blockposition1 != null) {
                    ItemStack itemstack1 = ItemWorldMap.createFilledMapView(worldserver, blockposition1.getX(), blockposition1.getZ(), this.d, true, true);

                    ItemWorldMap.applySepiaFilter(worldserver, itemstack1);
                    WorldMap.decorateMap(itemstack1, blockposition1, "+", this.c);
                    itemstack1.a((IChatBaseComponent) (new ChatMessage("filled_map." + this.b.toLowerCase(Locale.ROOT), new Object[0])));
                    return itemstack1;
                } else {
                    return itemstack;
                }
            }
        }
    }

    public static class a extends LootItemFunction.a<LootItemFunctionExplorationMap> {

        protected a() {
            super(new MinecraftKey("exploration_map"), LootItemFunctionExplorationMap.class);
        }

        public void a(JsonObject jsonobject, LootItemFunctionExplorationMap lootitemfunctionexplorationmap, JsonSerializationContext jsonserializationcontext) {
            jsonobject.add("destination", jsonserializationcontext.serialize(lootitemfunctionexplorationmap.b));
            jsonobject.add("decoration", jsonserializationcontext.serialize(lootitemfunctionexplorationmap.c.toString().toLowerCase(Locale.ROOT)));
        }

        public LootItemFunctionExplorationMap b(JsonObject jsonobject, JsonDeserializationContext jsondeserializationcontext, LootItemCondition[] alootitemcondition) {
            String s = jsonobject.has("destination") ? ChatDeserializer.h(jsonobject, "destination") : "Buried_Treasure";

            s = WorldGenerator.aF.containsKey(s.toLowerCase(Locale.ROOT)) ? s : "Buried_Treasure";
            String s1 = jsonobject.has("decoration") ? ChatDeserializer.h(jsonobject, "decoration") : "mansion";
            MapIcon.Type mapicon_type = MapIcon.Type.MANSION;

            try {
                mapicon_type = MapIcon.Type.valueOf(s1.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException illegalargumentexception) {
                LootItemFunctionExplorationMap.a.error("Error while parsing loot table decoration entry. Found {}. Defaulting to MANSION", s1);
            }

            byte b0 = jsonobject.has("zoom") ? ChatDeserializer.o(jsonobject, "zoom") : 2;
            int i = jsonobject.has("search_radius") ? ChatDeserializer.n(jsonobject, "search_radius") : 50;
            boolean flag = jsonobject.has("skip_existing_chunks") ? ChatDeserializer.j(jsonobject, "skip_existing_chunks") : true;

            return new LootItemFunctionExplorationMap(alootitemcondition, s, mapicon_type, b0, i, flag);
        }
    }
}
