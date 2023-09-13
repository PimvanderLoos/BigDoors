package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.level.WorldServer;
import net.minecraft.stats.StatisticList;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumMobSpawn;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.MobSpawnerAbstract;
import net.minecraft.world.level.RayTrace;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.BlockFluids;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityMobSpawner;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import net.minecraft.world.phys.Vec3D;

public class ItemMonsterEgg extends Item {

    private static final Map<EntityTypes<? extends EntityInsentient>, ItemMonsterEgg> BY_ID = Maps.newIdentityHashMap();
    private final int backgroundColor;
    private final int highlightColor;
    private final EntityTypes<?> defaultType;

    public ItemMonsterEgg(EntityTypes<? extends EntityInsentient> entitytypes, int i, int j, Item.Info item_info) {
        super(item_info);
        this.defaultType = entitytypes;
        this.backgroundColor = i;
        this.highlightColor = j;
        ItemMonsterEgg.BY_ID.put(entitytypes, this);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();

        if (!(world instanceof WorldServer)) {
            return EnumInteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = itemactioncontext.getItemStack();
            BlockPosition blockposition = itemactioncontext.getClickPosition();
            EnumDirection enumdirection = itemactioncontext.getClickedFace();
            IBlockData iblockdata = world.getType(blockposition);

            if (iblockdata.a(Blocks.SPAWNER)) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityMobSpawner) {
                    MobSpawnerAbstract mobspawnerabstract = ((TileEntityMobSpawner) tileentity).getSpawner();
                    EntityTypes<?> entitytypes = this.a(itemstack.getTag());

                    mobspawnerabstract.setMobName(entitytypes);
                    tileentity.update();
                    world.notify(blockposition, iblockdata, iblockdata, 3);
                    itemstack.subtract(1);
                    return EnumInteractionResult.CONSUME;
                }
            }

            BlockPosition blockposition1;

            if (iblockdata.getCollisionShape(world, blockposition).isEmpty()) {
                blockposition1 = blockposition;
            } else {
                blockposition1 = blockposition.shift(enumdirection);
            }

            EntityTypes<?> entitytypes1 = this.a(itemstack.getTag());

            if (entitytypes1.spawnCreature((WorldServer) world, itemstack, itemactioncontext.getEntity(), blockposition1, EnumMobSpawn.SPAWN_EGG, true, !Objects.equals(blockposition, blockposition1) && enumdirection == EnumDirection.UP) != null) {
                itemstack.subtract(1);
                world.a((Entity) itemactioncontext.getEntity(), GameEvent.ENTITY_PLACE, blockposition);
            }

            return EnumInteractionResult.CONSUME;
        }
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        MovingObjectPositionBlock movingobjectpositionblock = a(world, entityhuman, RayTrace.FluidCollisionOption.SOURCE_ONLY);

        if (movingobjectpositionblock.getType() != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            return InteractionResultWrapper.pass(itemstack);
        } else if (!(world instanceof WorldServer)) {
            return InteractionResultWrapper.success(itemstack);
        } else {
            MovingObjectPositionBlock movingobjectpositionblock1 = (MovingObjectPositionBlock) movingobjectpositionblock;
            BlockPosition blockposition = movingobjectpositionblock1.getBlockPosition();

            if (!(world.getType(blockposition).getBlock() instanceof BlockFluids)) {
                return InteractionResultWrapper.pass(itemstack);
            } else if (world.a(entityhuman, blockposition) && entityhuman.a(blockposition, movingobjectpositionblock1.getDirection(), itemstack)) {
                EntityTypes<?> entitytypes = this.a(itemstack.getTag());

                if (entitytypes.spawnCreature((WorldServer) world, itemstack, entityhuman, blockposition, EnumMobSpawn.SPAWN_EGG, false, false) == null) {
                    return InteractionResultWrapper.pass(itemstack);
                } else {
                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.subtract(1);
                    }

                    entityhuman.b(StatisticList.ITEM_USED.b(this));
                    world.a(GameEvent.ENTITY_PLACE, (Entity) entityhuman);
                    return InteractionResultWrapper.consume(itemstack);
                }
            } else {
                return InteractionResultWrapper.fail(itemstack);
            }
        }
    }

    public boolean a(@Nullable NBTTagCompound nbttagcompound, EntityTypes<?> entitytypes) {
        return Objects.equals(this.a(nbttagcompound), entitytypes);
    }

    public int a(int i) {
        return i == 0 ? this.backgroundColor : this.highlightColor;
    }

    @Nullable
    public static ItemMonsterEgg a(@Nullable EntityTypes<?> entitytypes) {
        return (ItemMonsterEgg) ItemMonsterEgg.BY_ID.get(entitytypes);
    }

    public static Iterable<ItemMonsterEgg> i() {
        return Iterables.unmodifiableIterable(ItemMonsterEgg.BY_ID.values());
    }

    public EntityTypes<?> a(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("EntityTag", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("EntityTag");

            if (nbttagcompound1.hasKeyOfType("id", 8)) {
                return (EntityTypes) EntityTypes.a(nbttagcompound1.getString("id")).orElse(this.defaultType);
            }
        }

        return this.defaultType;
    }

    public Optional<EntityInsentient> a(EntityHuman entityhuman, EntityInsentient entityinsentient, EntityTypes<? extends EntityInsentient> entitytypes, WorldServer worldserver, Vec3D vec3d, ItemStack itemstack) {
        if (!this.a(itemstack.getTag(), entitytypes)) {
            return Optional.empty();
        } else {
            Object object;

            if (entityinsentient instanceof EntityAgeable) {
                object = ((EntityAgeable) entityinsentient).createChild(worldserver, (EntityAgeable) entityinsentient);
            } else {
                object = (EntityInsentient) entitytypes.a((World) worldserver);
            }

            if (object == null) {
                return Optional.empty();
            } else {
                ((EntityInsentient) object).setBaby(true);
                if (!((EntityInsentient) object).isBaby()) {
                    return Optional.empty();
                } else {
                    ((EntityInsentient) object).setPositionRotation(vec3d.getX(), vec3d.getY(), vec3d.getZ(), 0.0F, 0.0F);
                    worldserver.addAllEntities((Entity) object);
                    if (itemstack.hasName()) {
                        ((EntityInsentient) object).setCustomName(itemstack.getName());
                    }

                    if (!entityhuman.getAbilities().instabuild) {
                        itemstack.subtract(1);
                    }

                    return Optional.of(object);
                }
            }
        }
    }
}
