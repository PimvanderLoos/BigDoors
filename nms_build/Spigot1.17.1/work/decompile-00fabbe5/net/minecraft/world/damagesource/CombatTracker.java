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

    public void a() {
        this.l();
        Optional<BlockPosition> optional = this.mob.ea();

        if (optional.isPresent()) {
            IBlockData iblockdata = this.mob.level.getType((BlockPosition) optional.get());

            if (!iblockdata.a(Blocks.LADDER) && !iblockdata.a((Tag) TagsBlock.TRAPDOORS)) {
                if (iblockdata.a(Blocks.VINE)) {
                    this.nextLocation = "vines";
                } else if (!iblockdata.a(Blocks.WEEPING_VINES) && !iblockdata.a(Blocks.WEEPING_VINES_PLANT)) {
                    if (!iblockdata.a(Blocks.TWISTING_VINES) && !iblockdata.a(Blocks.TWISTING_VINES_PLANT)) {
                        if (iblockdata.a(Blocks.SCAFFOLDING)) {
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

    public void trackDamage(DamageSource damagesource, float f, float f1) {
        this.g();
        this.a();
        CombatEntry combatentry = new CombatEntry(damagesource, this.mob.tickCount, f, f1, this.nextLocation, this.mob.fallDistance);

        this.entries.add(combatentry);
        this.lastDamageTime = this.mob.tickCount;
        this.takingDamage = true;
        if (combatentry.f() && !this.inCombat && this.mob.isAlive()) {
            this.inCombat = true;
            this.combatStartTime = this.mob.tickCount;
            this.combatEndTime = this.combatStartTime;
            this.mob.enterCombat();
        }

    }

    public IChatBaseComponent getDeathMessage() {
        if (this.entries.isEmpty()) {
            return new ChatMessage("death.attack.generic", new Object[]{this.mob.getScoreboardDisplayName()});
        } else {
            CombatEntry combatentry = this.k();
            CombatEntry combatentry1 = (CombatEntry) this.entries.get(this.entries.size() - 1);
            IChatBaseComponent ichatbasecomponent = combatentry1.h();
            Entity entity = combatentry1.a().getEntity();
            Object object;

            if (combatentry != null && combatentry1.a() == DamageSource.FALL) {
                IChatBaseComponent ichatbasecomponent1 = combatentry.h();

                if (combatentry.a() != DamageSource.FALL && combatentry.a() != DamageSource.OUT_OF_WORLD) {
                    if (ichatbasecomponent1 != null && !ichatbasecomponent1.equals(ichatbasecomponent)) {
                        Entity entity1 = combatentry.a().getEntity();
                        ItemStack itemstack = entity1 instanceof EntityLiving ? ((EntityLiving) entity1).getItemInMainHand() : ItemStack.EMPTY;

                        if (!itemstack.isEmpty() && itemstack.hasName()) {
                            object = new ChatMessage("death.fell.assist.item", new Object[]{this.mob.getScoreboardDisplayName(), ichatbasecomponent1, itemstack.G()});
                        } else {
                            object = new ChatMessage("death.fell.assist", new Object[]{this.mob.getScoreboardDisplayName(), ichatbasecomponent1});
                        }
                    } else if (ichatbasecomponent != null) {
                        ItemStack itemstack1 = entity instanceof EntityLiving ? ((EntityLiving) entity).getItemInMainHand() : ItemStack.EMPTY;

                        if (!itemstack1.isEmpty() && itemstack1.hasName()) {
                            object = new ChatMessage("death.fell.finish.item", new Object[]{this.mob.getScoreboardDisplayName(), ichatbasecomponent, itemstack1.G()});
                        } else {
                            object = new ChatMessage("death.fell.finish", new Object[]{this.mob.getScoreboardDisplayName(), ichatbasecomponent});
                        }
                    } else {
                        object = new ChatMessage("death.fell.killer", new Object[]{this.mob.getScoreboardDisplayName()});
                    }
                } else {
                    object = new ChatMessage("death.fell.accident." + this.a(combatentry), new Object[]{this.mob.getScoreboardDisplayName()});
                }
            } else {
                object = combatentry1.a().getLocalizedDeathMessage(this.mob);
            }

            return (IChatBaseComponent) object;
        }
    }

    @Nullable
    public EntityLiving c() {
        EntityLiving entityliving = null;
        EntityHuman entityhuman = null;
        float f = 0.0F;
        float f1 = 0.0F;
        Iterator iterator = this.entries.iterator();

        while (iterator.hasNext()) {
            CombatEntry combatentry = (CombatEntry) iterator.next();

            if (combatentry.a().getEntity() instanceof EntityHuman && (entityhuman == null || combatentry.c() > f1)) {
                f1 = combatentry.c();
                entityhuman = (EntityHuman) combatentry.a().getEntity();
            }

            if (combatentry.a().getEntity() instanceof EntityLiving && (entityliving == null || combatentry.c() > f)) {
                f = combatentry.c();
                entityliving = (EntityLiving) combatentry.a().getEntity();
            }
        }

        if (entityhuman != null && f1 >= f / 3.0F) {
            return entityhuman;
        } else {
            return entityliving;
        }
    }

    @Nullable
    private CombatEntry k() {
        CombatEntry combatentry = null;
        CombatEntry combatentry1 = null;
        float f = 0.0F;
        float f1 = 0.0F;

        for (int i = 0; i < this.entries.size(); ++i) {
            CombatEntry combatentry2 = (CombatEntry) this.entries.get(i);
            CombatEntry combatentry3 = i > 0 ? (CombatEntry) this.entries.get(i - 1) : null;

            if ((combatentry2.a() == DamageSource.FALL || combatentry2.a() == DamageSource.OUT_OF_WORLD) && combatentry2.j() > 0.0F && (combatentry == null || combatentry2.j() > f1)) {
                if (i > 0) {
                    combatentry = combatentry3;
                } else {
                    combatentry = combatentry2;
                }

                f1 = combatentry2.j();
            }

            if (combatentry2.g() != null && (combatentry1 == null || combatentry2.c() > f)) {
                combatentry1 = combatentry2;
                f = combatentry2.c();
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

    private String a(CombatEntry combatentry) {
        return combatentry.g() == null ? "generic" : combatentry.g();
    }

    public boolean d() {
        this.g();
        return this.takingDamage;
    }

    public boolean e() {
        this.g();
        return this.inCombat;
    }

    public int f() {
        return this.inCombat ? this.mob.tickCount - this.combatStartTime : this.combatEndTime - this.combatStartTime;
    }

    private void l() {
        this.nextLocation = null;
    }

    public void g() {
        int i = this.inCombat ? 300 : 100;

        if (this.takingDamage && (!this.mob.isAlive() || this.mob.tickCount - this.lastDamageTime > i)) {
            boolean flag = this.inCombat;

            this.takingDamage = false;
            this.inCombat = false;
            this.combatEndTime = this.mob.tickCount;
            if (flag) {
                this.mob.exitCombat();
            }

            this.entries.clear();
        }

    }

    public EntityLiving h() {
        return this.mob;
    }

    @Nullable
    public CombatEntry i() {
        return this.entries.isEmpty() ? null : (CombatEntry) this.entries.get(this.entries.size() - 1);
    }

    public int j() {
        EntityLiving entityliving = this.c();

        return entityliving == null ? -1 : entityliving.getId();
    }
}
