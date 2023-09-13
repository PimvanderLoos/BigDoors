package net.minecraft.server.level;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreak;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.InteractionResultWrapper;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.ItemActionContext;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerInteractManager {

    private static final Logger LOGGER = LogManager.getLogger();
    protected WorldServer level;
    protected final EntityPlayer player;
    private EnumGamemode gameModeForPlayer;
    @Nullable
    private EnumGamemode previousGameModeForPlayer;
    private boolean isDestroyingBlock;
    private int destroyProgressStart;
    private BlockPosition destroyPos;
    private int gameTicks;
    private boolean hasDelayedDestroy;
    private BlockPosition delayedDestroyPos;
    private int delayedTickStart;
    private int lastSentState;

    public PlayerInteractManager(EntityPlayer entityplayer) {
        this.gameModeForPlayer = EnumGamemode.DEFAULT_MODE;
        this.destroyPos = BlockPosition.ZERO;
        this.delayedDestroyPos = BlockPosition.ZERO;
        this.lastSentState = -1;
        this.player = entityplayer;
        this.level = entityplayer.getWorldServer();
    }

    public boolean setGameMode(EnumGamemode enumgamemode) {
        if (enumgamemode == this.gameModeForPlayer) {
            return false;
        } else {
            this.a(enumgamemode, this.gameModeForPlayer);
            return true;
        }
    }

    protected void a(EnumGamemode enumgamemode, @Nullable EnumGamemode enumgamemode1) {
        this.previousGameModeForPlayer = enumgamemode1;
        this.gameModeForPlayer = enumgamemode;
        enumgamemode.a(this.player.getAbilities());
        this.player.updateAbilities();
        this.player.server.getPlayerList().sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, new EntityPlayer[]{this.player}));
        this.level.everyoneSleeping();
    }

    public EnumGamemode getGameMode() {
        return this.gameModeForPlayer;
    }

    @Nullable
    public EnumGamemode c() {
        return this.previousGameModeForPlayer;
    }

    public boolean d() {
        return this.gameModeForPlayer.g();
    }

    public boolean isCreative() {
        return this.gameModeForPlayer.isCreative();
    }

    public void a() {
        ++this.gameTicks;
        IBlockData iblockdata;

        if (this.hasDelayedDestroy) {
            iblockdata = this.level.getType(this.delayedDestroyPos);
            if (iblockdata.isAir()) {
                this.hasDelayedDestroy = false;
            } else {
                float f = this.a(iblockdata, this.delayedDestroyPos, this.delayedTickStart);

                if (f >= 1.0F) {
                    this.hasDelayedDestroy = false;
                    this.breakBlock(this.delayedDestroyPos);
                }
            }
        } else if (this.isDestroyingBlock) {
            iblockdata = this.level.getType(this.destroyPos);
            if (iblockdata.isAir()) {
                this.level.a(this.player.getId(), this.destroyPos, -1);
                this.lastSentState = -1;
                this.isDestroyingBlock = false;
            } else {
                this.a(iblockdata, this.destroyPos, this.destroyProgressStart);
            }
        }

    }

    private float a(IBlockData iblockdata, BlockPosition blockposition, int i) {
        int j = this.gameTicks - i;
        float f = iblockdata.getDamage(this.player, this.player.level, blockposition) * (float) (j + 1);
        int k = (int) (f * 10.0F);

        if (k != this.lastSentState) {
            this.level.a(this.player.getId(), blockposition, k);
            this.lastSentState = k;
        }

        return f;
    }

    public void a(BlockPosition blockposition, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, EnumDirection enumdirection, int i) {
        double d0 = this.player.locX() - ((double) blockposition.getX() + 0.5D);
        double d1 = this.player.locY() - ((double) blockposition.getY() + 0.5D) + 1.5D;
        double d2 = this.player.locZ() - ((double) blockposition.getZ() + 0.5D);
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;

        if (d3 > 36.0D) {
            this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false, "too far"));
        } else if (blockposition.getY() >= i) {
            this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false, "too high"));
        } else {
            IBlockData iblockdata;

            if (packetplayinblockdig_enumplayerdigtype == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                if (!this.level.a((EntityHuman) this.player, blockposition)) {
                    this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false, "may not interact"));
                    return;
                }

                if (this.isCreative()) {
                    this.a(blockposition, packetplayinblockdig_enumplayerdigtype, "creative destroy");
                    return;
                }

                if (this.player.a((World) this.level, blockposition, this.gameModeForPlayer)) {
                    this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false, "block action restricted"));
                    return;
                }

                this.destroyProgressStart = this.gameTicks;
                float f = 1.0F;

                iblockdata = this.level.getType(blockposition);
                if (!iblockdata.isAir()) {
                    iblockdata.attack(this.level, blockposition, this.player);
                    f = iblockdata.getDamage(this.player, this.player.level, blockposition);
                }

                if (!iblockdata.isAir() && f >= 1.0F) {
                    this.a(blockposition, packetplayinblockdig_enumplayerdigtype, "insta mine");
                } else {
                    if (this.isDestroyingBlock) {
                        this.player.connection.sendPacket(new PacketPlayOutBlockBreak(this.destroyPos, this.level.getType(this.destroyPos), PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK, false, "abort destroying since another started (client insta mine, server disagreed)"));
                    }

                    this.isDestroyingBlock = true;
                    this.destroyPos = blockposition.immutableCopy();
                    int j = (int) (f * 10.0F);

                    this.level.a(this.player.getId(), blockposition, j);
                    this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true, "actual start of destroying"));
                    this.lastSentState = j;
                }
            } else if (packetplayinblockdig_enumplayerdigtype == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                if (blockposition.equals(this.destroyPos)) {
                    int k = this.gameTicks - this.destroyProgressStart;

                    iblockdata = this.level.getType(blockposition);
                    if (!iblockdata.isAir()) {
                        float f1 = iblockdata.getDamage(this.player, this.player.level, blockposition) * (float) (k + 1);

                        if (f1 >= 0.7F) {
                            this.isDestroyingBlock = false;
                            this.level.a(this.player.getId(), blockposition, -1);
                            this.a(blockposition, packetplayinblockdig_enumplayerdigtype, "destroyed");
                            return;
                        }

                        if (!this.hasDelayedDestroy) {
                            this.isDestroyingBlock = false;
                            this.hasDelayedDestroy = true;
                            this.delayedDestroyPos = blockposition;
                            this.delayedTickStart = this.destroyProgressStart;
                        }
                    }
                }

                this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true, "stopped destroying"));
            } else if (packetplayinblockdig_enumplayerdigtype == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                this.isDestroyingBlock = false;
                if (!Objects.equals(this.destroyPos, blockposition)) {
                    PlayerInteractManager.LOGGER.warn("Mismatch in destroy block pos: {} {}", this.destroyPos, blockposition);
                    this.level.a(this.player.getId(), this.destroyPos, -1);
                    this.player.connection.sendPacket(new PacketPlayOutBlockBreak(this.destroyPos, this.level.getType(this.destroyPos), packetplayinblockdig_enumplayerdigtype, true, "aborted mismatched destroying"));
                }

                this.level.a(this.player.getId(), blockposition, -1);
                this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true, "aborted destroying"));
            }

        }
    }

    public void a(BlockPosition blockposition, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, String s) {
        if (this.breakBlock(blockposition)) {
            this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true, s));
        } else {
            this.player.connection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.level.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false, s));
        }

    }

    public boolean breakBlock(BlockPosition blockposition) {
        IBlockData iblockdata = this.level.getType(blockposition);

        if (!this.player.getItemInMainHand().getItem().a(iblockdata, (World) this.level, blockposition, (EntityHuman) this.player)) {
            return false;
        } else {
            TileEntity tileentity = this.level.getTileEntity(blockposition);
            Block block = iblockdata.getBlock();

            if (block instanceof GameMasterBlock && !this.player.isCreativeAndOp()) {
                this.level.notify(blockposition, iblockdata, iblockdata, 3);
                return false;
            } else if (this.player.a((World) this.level, blockposition, this.gameModeForPlayer)) {
                return false;
            } else {
                block.a((World) this.level, blockposition, iblockdata, (EntityHuman) this.player);
                boolean flag = this.level.a(blockposition, false);

                if (flag) {
                    block.postBreak(this.level, blockposition, iblockdata);
                }

                if (this.isCreative()) {
                    return true;
                } else {
                    ItemStack itemstack = this.player.getItemInMainHand();
                    ItemStack itemstack1 = itemstack.cloneItemStack();
                    boolean flag1 = this.player.hasBlock(iblockdata);

                    itemstack.a(this.level, iblockdata, blockposition, this.player);
                    if (flag && flag1) {
                        block.a(this.level, this.player, blockposition, iblockdata, tileentity, itemstack1);
                    }

                    return true;
                }
            }
        }
    }

    public EnumInteractionResult a(EntityPlayer entityplayer, World world, ItemStack itemstack, EnumHand enumhand) {
        if (this.gameModeForPlayer == EnumGamemode.SPECTATOR) {
            return EnumInteractionResult.PASS;
        } else if (entityplayer.getCooldownTracker().hasCooldown(itemstack.getItem())) {
            return EnumInteractionResult.PASS;
        } else {
            int i = itemstack.getCount();
            int j = itemstack.getDamage();
            InteractionResultWrapper<ItemStack> interactionresultwrapper = itemstack.a(world, (EntityHuman) entityplayer, enumhand);
            ItemStack itemstack1 = (ItemStack) interactionresultwrapper.b();

            if (itemstack1 == itemstack && itemstack1.getCount() == i && itemstack1.o() <= 0 && itemstack1.getDamage() == j) {
                return interactionresultwrapper.a();
            } else if (interactionresultwrapper.a() == EnumInteractionResult.FAIL && itemstack1.o() > 0 && !entityplayer.isHandRaised()) {
                return interactionresultwrapper.a();
            } else {
                entityplayer.a(enumhand, itemstack1);
                if (this.isCreative()) {
                    itemstack1.setCount(i);
                    if (itemstack1.f() && itemstack1.getDamage() != j) {
                        itemstack1.setDamage(j);
                    }
                }

                if (itemstack1.isEmpty()) {
                    entityplayer.a(enumhand, ItemStack.EMPTY);
                }

                if (!entityplayer.isHandRaised()) {
                    entityplayer.inventoryMenu.updateInventory();
                }

                return interactionresultwrapper.a();
            }
        }
    }

    public EnumInteractionResult a(EntityPlayer entityplayer, World world, ItemStack itemstack, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (this.gameModeForPlayer == EnumGamemode.SPECTATOR) {
            ITileInventory itileinventory = iblockdata.b(world, blockposition);

            if (itileinventory != null) {
                entityplayer.openContainer(itileinventory);
                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.PASS;
            }
        } else {
            boolean flag = !entityplayer.getItemInMainHand().isEmpty() || !entityplayer.getItemInOffHand().isEmpty();
            boolean flag1 = entityplayer.eZ() && flag;
            ItemStack itemstack1 = itemstack.cloneItemStack();

            if (!flag1) {
                EnumInteractionResult enuminteractionresult = iblockdata.interact(world, entityplayer, enumhand, movingobjectpositionblock);

                if (enuminteractionresult.a()) {
                    CriterionTriggers.ITEM_USED_ON_BLOCK.a(entityplayer, blockposition, itemstack1);
                    return enuminteractionresult;
                }
            }

            if (!itemstack.isEmpty() && !entityplayer.getCooldownTracker().hasCooldown(itemstack.getItem())) {
                ItemActionContext itemactioncontext = new ItemActionContext(entityplayer, enumhand, movingobjectpositionblock);
                EnumInteractionResult enuminteractionresult1;

                if (this.isCreative()) {
                    int i = itemstack.getCount();

                    enuminteractionresult1 = itemstack.placeItem(itemactioncontext);
                    itemstack.setCount(i);
                } else {
                    enuminteractionresult1 = itemstack.placeItem(itemactioncontext);
                }

                if (enuminteractionresult1.a()) {
                    CriterionTriggers.ITEM_USED_ON_BLOCK.a(entityplayer, blockposition, itemstack1);
                }

                return enuminteractionresult1;
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    public void a(WorldServer worldserver) {
        this.level = worldserver;
    }
}
