package net.minecraft.world.damagesource;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagsBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;

public class CombatTracker {

    public static final int RESET_DAMAGE_STATUS_TIME = 100;
    public static final int RESET_COMBAT_STATUS_TIME = 300;
    private final List<CombatEntry> entries = Lists.newArrayList();
    private final EntityLiving mob;
    private int lastDamageTime;
    private int combatStartTime;
    private int combatEndTime;
    private boolean inCombat;
    private boolean takingDamage;
    @Nullable
    private String nextLocation;

    public CombatTracker(EntityLiving entityliving) {
        this.mob = entityliving;
    }

    public void prepareForDamage() {
        this.resetPreparedStatus();
        Optional<BlockPosition> optional = this.mob.getLastClimbablePos();

        if (optional.isPresent()) {
            IBlockData iblockdata = this.mob.level.getBlockState((BlockPosition) optional.get());

            if (!iblockdata.is(Blocks.LADDER) && !iblockdata.is((Tag) TagsBlock.TRAPDOORS)) {
                if (iblockdata.is(Blocks.VINE)) {
                    this.nextLocation = "vines";
                } else if (!iblockdata.is(Blocks.WEEPING_VINES) && !iblockdata.is(Blocks.WEEPING_VINES_PLANT)) {
                    if (!iblockdata.is(Blocks.TWISTING_VINES) && !iblockdata.is(Blocks.TWISTING_VINES_PLANT)) {
                        if (iblockdata.is(Blocks.SCAFFOLDING)) {
                            this.nextLocation = "scaffolding";
                        } else {
                            this.nextLocation = "other_climbable";
                        }
                    } else {
                        this.nextLocation = "twisting_vines";
                    }
                } else {
                    this.nextLocation = "weeping_vines";
                }
            } else {
                this.nextLocation = "ladder";
            }
        } else if (this.mob.isInWater()) {
            this.nextLocation = "water";
        }

    }

    public void recordDamage(DamageSource damagesource, float f, float f1) {
        this.recheckStatus();
        this.prepareForDamage();
        CombatEntry combatentry = new CombatEntry(damagesource, this.mob.tickCount, f, f1, this.nextLocation, this.mob.fallDistance);

        this.entries.add(combatentry);
        this.lastDamageTime = this.mob.tickCount;
        this.takingDamage = true;
        if (combatentry.isCombatRelated() && !this.inCombat && this.mob.isAlive()) {
            this.inCombat = true;
            this.combatStartTime = this.mob.tickCount;
            this.combatEndTime = this.combatStartTime;
            this.mob.onEnterCombat();
        }

    }

    public IChatBaseComponent getDeathMessage() {
        if (this.entries.isEmpty()) {
            return new ChatMessage("death.attack.generic", new Object[]{this.mob.getDisplayName()});
        } else {
            CombatEntry combatentry = this.getMostSignificantFall();
            CombatEntry combatentry1 = (CombatEntry) this.entries.get(this.entries.size() - 1);
            IChatBaseComponent ichatbasecomponent = combatentry1.getAttackerName();
            Entity entity = combatentry1.getSource().getEntity();
            Object object;

            if (combatentry != null && combatentry1.getSource() == DamageSource.FALL) {
                IChatBaseComponent ichatbasecomponent1 = combatentry.getAttackerName();

                if (combatentry.getSource() != DamageSource.FALL && combatentry.getSource() != DamageSource.OUT_OF_WORLD) {
                    if (ichatbasecomponent1 != null && !ichatbasecomponent1.equals(ichatbasecomponent)) {
                        Entity entity1 = combatentry.getSource().getEntity();
                        ItemStack itemstack = entity1 instanceof EntityLiving ? ((EntityLiving) entity1).getMainHandItem() : ItemStack.EMPTY;

                        if (!itemstack.isEmpty() && itemstack.hasCustomHoverName()) {
                            object = new ChatMessage("death.fell.assist.item", new Object[]{this.mob.getDisplayName(), ichatbasecomponent1, itemstack.getDisplayName()});
                        } else {
                            object = new ChatMessage("death.fell.assist", new Object[]{this.mob.getDisplayName(), ichatbasecomponent1});
                        }
                    } else if (ichatbasecomponent != null) {
                        ItemStack itemstack1 = entity instanceof EntityLiving ? ((EntityLiving) entity).getMainHandItem() : ItemStack.EMPTY;

                        if (!itemstack1.isEmpty() && itemstack1.hasCustomHoverName()) {
                            object = new ChatMessage("death.fell.finish.item", new Object[]{this.mob.getDisplayName(), ichatbasecomponent, itemstack1.getDisplayName()});
                        } else {
                            object = new ChatMessage("death.fell.finish", new Object[]{this.mob.getDisplayName(), ichatbasecomponent});
                        }
                    } else {
                        object = new ChatMessage("death.fell.killer", new Object[]{this.mob.getDisplayName()});
                    }
                } else {
                    object = new ChatMessage("death.fell.accident." + this.getFallLocation(combatentry), new Object[]{this.mob.getDisplayName()});
                }
            } else {
                object = combatentry1.getSource().getLocalizedDeathMessage(this.mob);
            }

            return (IChatBaseComponent) object;
        }
    }

    @Nullable
    public EntityLiving getKiller() {
        EntityLiving entityliving = null;
        EntityHuman entityhuman = null;
        float f = 0.0F;
        float f1 = 0.0F;
        Iterator iterator = this.entries.iterator();

        while (iterator.hasNext()) {
            CombatEntry combatentry = (CombatEntry) iterator.next();

            if (combatentry.getSource().getEntity() instanceof EntityHuman && (entityhuman == null || combatentry.getDamage() > f1)) {
                f1 = combatentry.getDamage();
                entityhuman = (EntityHuman) combatentry.getSource().getEntity();
            }

            if (combatentry.getSource().getEntity() instanceof EntityLiving && (entityliving == null || combatentry.getDamage() > f)) {
                f = combatentry.getDamage();
                entityliving = (EntityLiving) combatentry.getSource().getEntity();
            }
        }

        if (entityhuman != null && f1 >= f / 3.0F) {
            return entityhuman;
        } else {
            return entityliving;
        }
    }

    @Nullable
    private CombatEntry getMostSignificantFall() {
        CombatEntry combatentry = null;
        CombatEntry combatentry1 = null;
        float f = 0.0F;
        float f1 = 0.0F;

        for (int i = 0; i < this.entries.size(); ++i) {
            CombatEntry combatentry2 = (CombatEntry) this.entries.get(i);
            CombatEntry combatentry3 = i > 0 ? (CombatEntry) this.entries.get(i - 1) : null;

            if ((combatentry2.getSource() == DamageSource.FALL || combatentry2.getSource() == DamageSource.OUT_OF_WORLD) && combatentry2.getFallDistance() > 0.0F && (combatentry == null || combatentry2.getFallDistance() > f1)) {
                if (i > 0) {
                    combatentry = combatentry3;
                } else {
                    combatentry = combatentry2;
                }

                f1 = combatentry2.getFallDistance();
            }

            if (combatentry2.getLocation() != null && (combatentry1 == null || combatentry2.getDamage() > f)) {
                combatentry1 = combatentry2;
                f = combatentry2.getDamage();
            }
        }

        if (f1 > 5.0F && combatentry != null) {
            return combatentry;
        } else if (f > 5.0F && combatentry1 != null) {
            return combatentry1;
        } else {
            return null;
        }
    }

    private String getFallLocation(CombatEntry combatentry) {
        return combatentry.getLocation() == null ? "generic" : combatentry.getLocation();
    }

    public boolean isTakingDamage() {
        this.recheckStatus();
        return this.takingDamage;
    }

    public boolean isInCombat() {
        this.recheckStatus();
        return this.inCombat;
    }

    public int getCombatDuration() {
        return this.inCombat ? this.mob.tickCount - this.combatStartTime : this.combatEndTime - this.combatStartTime;
    }

    private void resetPreparedStatus() {
        this.nextLocation = null;
    }

    public void recheckStatus() {
        int i = this.inCombat ? 300 : 100;

        if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > i)) {
            boolean flag = this.inCombat;

            this.takingDamage = false;
            this.inCombat = false;
            this.combatEndTime = this.mob.tickCount;
            if (flag) {
                this.mob.onLeaveCombat();
            }

            this.entries.clear();
        }

    }

    public EntityLiving getMob() {
        return this.mob;
    }

    @Nullable
    public CombatEntry getLastEntry() {
        return this.entries.isEmpty() ? null : (CombatEntry) this.entries.get(this.entries.size() - 1);
    }

    public int getKillerId() {
        EntityLiving entityliving = this.getKiller();

        return entityliving == null ? -1 : entityliving.getId();
    }
}
