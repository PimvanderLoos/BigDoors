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
    public MinecraftKey a() {
        return CriterionTriggerInventoryChanged.ID;
    }

    @Override
    public CriterionTriggerInventoryChanged.a b(JsonObject jsonobject, CriterionConditionEntity.b criterionconditionentity_b, LootDeserializationContext lootdeserializationcontext) {
        JsonObject jsonobject1 = ChatDeserializer.a(jsonobject, "slots", new JsonObject());
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange = CriterionConditionValue.IntegerRange.a(jsonobject1.get("occupied"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1 = CriterionConditionValue.IntegerRange.a(jsonobject1.get("full"));
        CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange2 = CriterionConditionValue.IntegerRange.a(jsonobject1.get("empty"));
        CriterionConditionItem[] acriterionconditionitem = CriterionConditionItem.b(jsonobject.get("items"));

        return new CriterionTriggerInventoryChanged.a(criterionconditionentity_b, criterionconditionvalue_integerrange, criterionconditionvalue_integerrange1, criterionconditionvalue_integerrange2, acriterionconditionitem);
    }

    public void a(EntityPlayer entityplayer, PlayerInventory playerinventory, ItemStack itemstack) {
        int i = 0;
        int j = 0;
        int k = 0;

        for (int l = 0; l < playerinventory.getSize(); ++l) {
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

        this.a(entityplayer, playerinventory, itemstack, i, j, k);
    }

    private void a(EntityPlayer entityplayer, PlayerInventory playerinventory, ItemStack itemstack, int i, int j, int k) {
        this.a(entityplayer, (criteriontriggerinventorychanged_a) -> {
            return criteriontriggerinventorychanged_a.a(playerinventory, itemstack, i, j, k);
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

        public static CriterionTriggerInventoryChanged.a a(CriterionConditionItem... acriterionconditionitem) {
            return new CriterionTriggerInventoryChanged.a(CriterionConditionEntity.b.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, acriterionconditionitem);
        }

        public static CriterionTriggerInventoryChanged.a a(IMaterial... aimaterial) {
            CriterionConditionItem[] acriterionconditionitem = new CriterionConditionItem[aimaterial.length];

            for (int i = 0; i < aimaterial.length; ++i) {
                acriterionconditionitem[i] = new CriterionConditionItem((Tag) null, ImmutableSet.of(aimaterial[i].getItem()), CriterionConditionValue.IntegerRange.ANY, CriterionConditionValue.IntegerRange.ANY, CriterionConditionEnchantments.NONE, CriterionConditionEnchantments.NONE, (PotionRegistry) null, CriterionConditionNBT.ANY);
            }

            return a(acriterionconditionitem);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            if (!this.slotsOccupied.c() || !this.slotsFull.c() || !this.slotsEmpty.c()) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.add("occupied", this.slotsOccupied.d());
                jsonobject1.add("full", this.slotsFull.d());
                jsonobject1.add("empty", this.slotsEmpty.d());
                jsonobject.add("slots", jsonobject1);
            }

            if (this.predicates.length > 0) {
                JsonArray jsonarray = new JsonArray();
                CriterionConditionItem[] acriterionconditionitem = this.predicates;
                int i = acriterionconditionitem.length;

                for (int j = 0; j < i; ++j) {
                    CriterionConditionItem criterionconditionitem = acriterionconditionitem[j];

                    jsonarray.add(criterionconditionitem.a());
                }

                jsonobject.add("items", jsonarray);
            }

            return jsonobject;
        }

        public boolean a(PlayerInventory playerinventory, ItemStack itemstack, int i, int j, int k) {
            if (!this.slotsFull.d(i)) {
                return false;
            } else if (!this.slotsEmpty.d(j)) {
                return false;
            } else if (!this.slotsOccupied.d(k)) {
                return false;
            } else {
                int l = this.predicates.length;

                if (l == 0) {
                    return true;
                } else if (l != 1) {
                    List<CriterionConditionItem> list = new ObjectArrayList(this.predicates);
                    int i1 = playerinventory.getSize();

                    for (int j1 = 0; j1 < i1; ++j1) {
                        if (list.isEmpty()) {
                            return true;
                        }

                        ItemStack itemstack1 = playerinventory.getItem(j1);

                        if (!itemstack1.isEmpty()) {
                            list.removeIf((criterionconditionitem) -> {
                                return criterionconditionitem.a(itemstack1);
                            });
                        }
                    }

                    return list.isEmpty();
                } else {
                    return !itemstack.isEmpty() && this.predicates[0].a(itemstack);
                }
            }
        }
    }
}
