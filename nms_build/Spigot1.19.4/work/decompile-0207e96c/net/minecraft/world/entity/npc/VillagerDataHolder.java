package net.minecraft.world.entity.npc;

import net.minecraft.world.entity.VariantHolder;

public interface VillagerDataHolder extends VariantHolder<VillagerType> {

    VillagerData getVillagerData();

    void setVillagerData(VillagerData villagerdata);

    @Override
    default VillagerType getVariant() {
        return this.getVillagerData().getType();
    }

    default void setVariant(VillagerType villagertype) {
        this.setVillagerData(this.getVillagerData().setType(villagertype));
    }
}
