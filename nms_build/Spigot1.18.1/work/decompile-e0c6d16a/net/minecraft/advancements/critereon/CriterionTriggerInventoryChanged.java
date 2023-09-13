package net.minecraft.advancements.critereon;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionRegistry;
import net.minecraft.world.level.IMaterial;

public class CriterionTriggerInventoryChanged extends CriterionTriggerAbstract<CriterionTriggerInventoryChanged.a> {

    static final MinecraftKey ID = new MinecraftKey("inventory_changed");

    public CriterionTriggerInventoryChanged() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerInventoryChanged.ID;
    }

    @Override
    public CriterionTriggerInventoryChanged.a createInstance(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        JsonObject jsonobject1 = ChatDeserializer.getAsJsonObject(jsonobject, "slots", new JsonObject());
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.fromJson(jsonobject1.get("occupied"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.fromJson(jsonobject1.get("full"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange2 = CriterionConditionValue.IntegerRange.fromJson(jsonobject1.get("empty"));
        CriterionConditionItem[] acriterionconditionitem = CriterionConditionItem.fromJsonArray(jsonobject.get("items"));

        return new CriterionTriggerInventoryChanged.a(criterionconditionentity_b, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1, criterionconditionvalue_integerrange2, acriterionconditionitem);
    }

    public void trigger(EntityPlayer entityplayer, PlayerInventory playerinventory, ItemStack itemstack) {
        int i = 0;
        int j = 0;
        int k = 0;

        for (int l = 0; l < playerinventory.getContainerSize(); ++l) {
            ItemStack itemstack1 = playerinventory.getItem(l);

            if (itemstack1.isEmpty()) {
                ++j;
            } else {
                ++k;
                if (itemstack1.getCount() >= itemstack1.getMaxStackSize()) {
                    ++i;
                }
            }
        }

        this.trigger(entityplayer, playerinventory, itemstack, i, j, k);
    }

    private void trigger(EntityPlayer entityplayer, PlayerInventory playerinventory, ItemStack itemstack, int i, int j, int k) {
        this.trigger(entityplayer, (criteriontriggerinventorychanged_a) -> {
            return criteriontriggerinventorychanged_a.matches(playerinventory, itemstack, i, j, k);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final CriterionConditionValue.IntegerRange slotsOccupied;
        private final CriterionConditionValue.IntegerRange slotsFull;
        private final CriterionConditionValue.IntegerRange slotsEmpty;
        private final CriterionConditionItem[] predicates;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange2, CriterionConditionItem[] acriterionconditionitem) {
            super(CriterionTriggerInventoryChanged.ID, criterionconditionentity_b);
            this.slotsOccupied = criterionconditionvalue_integerrange;
            this.slotsFull = criterionconditionvalue_integerrange1;
            this.slotsEmpty = criterionconditionvalue_integerrange2;
            this.predicates = acriterionconditionitem;
        }

        public static CriterionTriggerInventoryChanged.a hasItems(CriterionConditionItem... acriterionconditionitem) {
            return new CriterionTriggerInventoryChanged.a(CriterionConditionEntity.b.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, acriterionconditionitem);
        }

        public static CriterionTriggerInventoryChanged.a hasItems(IMaterial... aimaterial) {
            CriterionConditionItem[] acriterionconditionitem = new CriterionConditionItem[aimaterial.length];

            for (int i = 0; i < aimaterial.length; ++i) {
                acriterionconditionitem[i] = new CriterionConditionItem((Tag) null, ImmutableSet.of(aimaterial[i].asItem()), CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionEnchantments.NONE, CriterionConditionEnchantments.NONE, (PotionRegistry) null, CriterionConditionNBT.ANY);
            }

            return hasItems(acriterionconditionitem);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (!this.slotsOccupied.isAny() || !this.slotsFull.isAny() || !this.slotsEmpty.isAny()) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.add("occupied", this.slotsOccupied.serializeToJson());
                jsonobject1.add("full", this.slotsFull.serializeToJson());
                jsonobject1.add("empty", this.slotsEmpty.serializeToJson());
                jsonobject.add("slots", jsonobject1);
            }

            if (this.predicates.length > 0) {
                JsonArray jsonarray = new JsonArray();
                CriterionConditionItem[] acriterionconditionitem = this.predicates;
                int i = acriterionconditionitem.length;

                for (int j = 0; j < i; ++j) {
                    CriterionConditionItem criterionconditionitem = acriterionconditionitem[j];

                    jsonarray.add(criterionconditionitem.serializeToJson());
                }

                jsonobject.add("items", jsonarray);
            }

            return jsonobject;
        }

        public boolean matches(PlayerInventory playerinventory, ItemStack itemstack, int i, int j, int k) {
            if (!this.slotsFull.matches(i)) {
                return false;
            } else if (!this.slotsEmpty.matches(j)) {
                return false;
            } else if (!this.slotsOccupied.matches(k)) {
                return false;
            } else {
                int l = this.predicates.length;

                if (l == 0) {
                    return true;
                } else if (l != 1) {
                    List<CriterionConditionItem> list = new ObjectArrayList(this.predicates);
                    int i1 = playerinventory.getContainerSize();

                    for (int j1 = 0; j1 < i1; ++j1) {
                        if (list.isEmpty()) {
                            return true;
                        }

                        ItemStack itemstack1 = playerinventory.getItem(j1);

                        if (!itemstack1.isEmpty()) {
                            list.removeIf((criterionconditionitem) -> {
                                return criterionconditionitem.matches(itemstack1);
                            });
                        }
                    }

                    return list.isEmpty();
                } else {
                    return !itemstack.isEmpty() && this.predicates[0].matches(itemstack);
                }
            }
        }
    }
}
