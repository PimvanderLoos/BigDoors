package net.minecraft.server.level;

import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.world.EnumHand;
import net.minecraft.world.EnumInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPositionBlock;

public class DemoPlayerInteractManager extends PlayerInteractManager {

    public static final int DEMO_DAYS = 5;
    public static final int TOTAL_PLAY_TICKS = 120500;
    private boolean displayedIntro;
    private boolean demoHasEnded;
    private int demoEndedReminder;
    private int gameModeTicks;

    public DemoPlayerInteractManager(EntityPlayer entityplayer) {
        super(entityplayer);
    }

    @Override
    public void a() {
        super.a();
        ++this.gameModeTicks;
        long i = this.level.getTime();
        long j = i / 24000L + 1L;

        if (!this.displayedIntro && this.gameModeTicks > 20) {
            this.displayedIntro = true;
            this.player.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.DEMO_EVENT, 0.0F));
        }

        this.demoHasEnded = i > 120500L;
        if (this.demoHasEnded) {
            ++this.demoEndedReminder;
        }

        if (i % 24000L == 500L) {
            if (j <= 6L) {
                if (j == 6L) {
                    this.player.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.DEMO_EVENT, 104.0F));
                } else {
                    this.player.sendMessage(new ChatMessage("demo.day." + j), SystemUtils.NIL_UUID);
                }
            }
        } else if (j == 1L) {
            if (i == 100L) {
                this.player.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.DEMO_EVENT, 101.0F));
            } else if (i == 175L) {
                this.player.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.DEMO_EVENT, 102.0F));
            } else if (i == 250L) {
                this.player.connection.sendPacket(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.DEMO_EVENT, 103.0F));
            }
        } else if (j == 5L && i % 24000L == 22000L) {
            this.player.sendMessage(new ChatMessage("demo.day.warning"), SystemUtils.NIL_UUID);
        }

    }

    private void f() {
        if (this.demoEndedReminder > 100) {
            this.player.sendMessage(new ChatMessage("demo.reminder"), SystemUtils.NIL_UUID);
            this.demoEndedReminder = 0;
        }

    }

    @Override
    public void a(BlockPosition blockposition, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, EnumDirection enumdirection, int i) {
        if (this.demoHasEnded) {
            this.f();
        } else {
            super.a(blockposition, packetplayinblockdig_enumplayerdigtype, enumdirection, i);
        }
    }

    @Override
    public EnumInteractionResult a(EntityPlayer entityplayer, World world, ItemStack itemstack, EnumHand enumhand) {
        if (this.demoHasEnded) {
            this.f();
            return EnumInteractionResult.PASS;
        } else {
            return super.a(entityplayer, world, itemstack, enumhand);
        }
    }

    @Override
    public EnumInteractionResult a(EntityPlayer entityplayer, World world, ItemStack itemstack, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (this.demoHasEnded) {
            this.f();
            return EnumInteractionResult.PASS;
        } else {
            return super.a(entityplayer, world, itemstack, enumhand, movingobjectpositionblock);
        }
    }
}
