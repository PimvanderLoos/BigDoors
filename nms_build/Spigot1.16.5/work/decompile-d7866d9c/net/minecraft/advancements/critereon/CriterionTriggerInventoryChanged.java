package net.minecraft.advancements.critereon;

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

    private static final MinecraftKey a = new MinecraftKey("inventory_changed");

    public CriterionTriggerInventoryChanged() {}

    @Override
    public MinecraftKey a() {
        return CriterionTriggerInventoryChanged.a;
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

        private final CriterionConditionValue.IntegerRange a;
        private final CriterionConditionValue.IntegerRange b;
        private final CriterionConditionValue.IntegerRange c;
        private final CriterionConditionItem[] d;

        public a(CriterionConditionEntity.b criterionconditionentity_b, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange1, CriterionConditionValue.IntegerRange criterionconditionvalue_integerrange2, CriterionConditionItem[] acriterionconditionitem) {
            super(CriterionTriggerInventoryChanged.a, criterionconditionentity_b);
            this.a = criterionconditionvalue_integerrange;
            this.b = criterionconditionvalue_integerrange1;
            this.c = criterionconditionvalue_integerrange2;
            this.d = acriterionconditionitem;
        }

        public static CriterionTriggerInventoryChanged.a a(CriterionConditionItem... acriterionconditionitem) {
            return new CriterionTriggerInventoryChanged.a(CriterionConditionEntity.b.a, CriterionConditionValue.IntegerRange.e, CriterionConditionValue.IntegerRange.e, CriterionConditionValue.IntegerRange.e, acriterionconditionitem);
        }

        public static CriterionTriggerInventoryChanged.a a(IMaterial... aimaterial) {
            CriterionConditionItem[] acriterionconditionitem = new CriterionConditionItem[aimaterial.length];

            for (int i = 0; i < aimaterial.length; ++i) {
                acriterionconditionitem[i] = new CriterionConditionItem((Tag) null, aimaterial[i].getItem(), CriterionConditionValue.IntegerRange.e, CriterionConditionValue.IntegerRange.e, CriterionConditionEnchantments.b, CriterionConditionEnchantments.b, (PotionRegistry) null, CriterionConditionNBT.a);
            }

            return a(acriterionconditionitem);
        }

        @Override
        public JsonObject a(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.a(lootserializationcontext);

            if (!this.a.c() || !this.b.c() || !this.c.c()) {
                JsonObject jsonobject1 = new JsonObject();

                jsonobject1.add("occupied", this.a.d());
                jsonobject1.add("full", this.b.d());
                jsonobject1.add("empty", this.c.d());
                jsonobject.add("slots", jsonobject1);
            }

            if (this.d.length > 0) {
                JsonArray jsonarray = new JsonArray();
                CriterionConditionItem[] acriterionconditionitem = this.d;
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
            if (!this.b.d(i)) {
                return false;
            } else if (!this.c.d(j)) {
                return false;
            } else if (!this.a.d(k)) {
                return false;
            } else {
                int l = this.d.length;

                if (l == 0) {
                    return true;
                } else if (l != 1) {
                    List<CriterionConditionItem> list = new ObjectArrayList(this.d);
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
                    return !itemstack.isEmpty() && this.d[0].a(itemstack);
                }
            }
        }
    }
}
