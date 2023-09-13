package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.SystemUtils;
import net.minecraft.core.BaseBlockPosition;
import net.minecraft.core.BlockPosition;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;

public class BehaviorVillageHeroGift extends Behavior<EntityVillager> {

    private static final Map<VillagerProfession, MinecraftKey> b = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(VillagerProfession.ARMORER, LootTables.al);
        hashmap.put(VillagerProfession.BUTCHER, LootTables.am);
        hashmap.put(VillagerProfession.CARTOGRAPHER, LootTables.an);
        hashmap.put(VillagerProfession.CLERIC, LootTables.ao);
        hashmap.put(VillagerProfession.FARMER, LootTables.ap);
        hashmap.put(VillagerProfession.FISHERMAN, LootTables.aq);
        hashmap.put(VillagerProfession.FLETCHER, LootTables.ar);
        hashmap.put(VillagerProfession.LEATHERWORKER, LootTables.as);
        hashmap.put(VillagerProfession.LIBRARIAN, LootTables.at);
        hashmap.put(VillagerProfession.MASON, LootTables.au);
        hashmap.put(VillagerProfession.SHEPHERD, LootTables.av);
        hashmap.put(VillagerProfession.TOOLSMITH, LootTables.aw);
        hashmap.put(VillagerProfession.WEAPONSMITH, LootTables.ax);
    });
    private int c = 600;
    private boolean d;
    private long e;

    public BehaviorVillageHeroGift(int i) {
        super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.NEAREST_VISIBLE_PLAYER, MemoryStatus.VALUE_PRESENT), i);
    }

    protected boolean a(WorldServer worldserver, EntityVillager entityvillager) {
        if (!this.b(entityvillager)) {
            return false;
        } else if (this.c > 0) {
            --this.c;
            return false;
        } else {
            return true;
        }
    }

    protected void a(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.d = false;
        this.e = i;
        EntityHuman entityhuman = (EntityHuman) this.c(entityvillager).get();

        entityvillager.getBehaviorController().setMemory(MemoryModuleType.INTERACTION_TARGET, (Object) entityhuman);
        BehaviorUtil.a((EntityLiving) entityvillager, (EntityLiving) entityhuman);
    }

    protected boolean b(WorldServer worldserver, EntityVillager entityvillager, long i) {
        return this.b(entityvillager) && !this.d;
    }

    protected void d(WorldServer worldserver, EntityVillager entityvillager, long i) {
        EntityHuman entityhuman = (EntityHuman) this.c(entityvillager).get();

        BehaviorUtil.a((EntityLiving) entityvillager, (EntityLiving) entityhuman);
        if (this.a(entityvillager, entityhuman)) {
            if (i - this.e > 20L) {
                this.a(entityvillager, (EntityLiving) entityhuman);
                this.d = true;
            }
        } else {
            BehaviorUtil.a(entityvillager, (Entity) entityhuman, 0.5F, 5);
        }

    }

    protected void c(WorldServer worldserver, EntityVillager entityvillager, long i) {
        this.c = a(worldserver);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.INTERACTION_TARGET);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.WALK_TARGET);
        entityvillager.getBehaviorController().removeMemory(MemoryModuleType.LOOK_TARGET);
    }

    private void a(EntityVillager entityvillager, EntityLiving entityliving) {
        List<ItemStack> list = this.a(entityvillager);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            BehaviorUtil.a((EntityLiving) entityvillager, itemstack, entityliving.getPositionVector());
        }

    }

    private List<ItemStack> a(EntityVillager entityvillager) {
        if (entityvillager.isBaby()) {
            return ImmutableList.of(new ItemStack(Items.bi));
        } else {
            VillagerProfession villagerprofession = entityvillager.getVillagerData().getProfession();

            if (BehaviorVillageHeroGift.b.containsKey(villagerprofession)) {
                LootTable loottable = entityvillager.world.getMinecraftServer().getLootTableRegistry().getLootTable((MinecraftKey) BehaviorVillageHeroGift.b.get(villagerprofession));
                LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) entityvillager.world)).set(LootContextParameters.ORIGIN, entityvillager.getPositionVector()).set(LootContextParameters.THIS_ENTITY, entityvillager).a(entityvillager.getRandom());

                return loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.GIFT));
            } else {
                return ImmutableList.of(new ItemStack(Items.WHEAT_SEEDS));
            }
        }
    }

    private boolean b(EntityVillager entityvillager) {
        return this.c(entityvillager).isPresent();
    }

    private Optional<EntityHuman> c(EntityVillager entityvillager) {
        return entityvillager.getBehaviorController().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER).filter(this::a);
    }

    private boolean a(EntityHuman entityhuman) {
        return entityhuman.hasEffect(MobEffects.HERO_OF_THE_VILLAGE);
    }

    private boolean a(EntityVillager entityvillager, EntityHuman entityhuman) {
        BlockPosition blockposition = entityhuman.getChunkCoordinates();
        BlockPosition blockposition1 = entityvillager.getChunkCoordinates();

        return blockposition1.a((BaseBlockPosition) blockposition, 5.0D);
    }

    private static int a(WorldServer worldserver) {
        return 600 + worldserver.random.nextInt(6001);
    }
}
