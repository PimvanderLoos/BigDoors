package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockLectern;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EnumBlockMirror;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import org.apache.commons.lang3.exception.ExceptionUtils;

class ReportGameListener implements GameTestHarnessListener {

    private final GameTestHarnessInfo originalTestInfo;
    private final GameTestHarnessTicker testTicker;
    private final BlockPosition structurePos;
    int attempts;
    int successes;

    public ReportGameListener(GameTestHarnessInfo gametestharnessinfo, GameTestHarnessTicker gametestharnessticker, BlockPosition blockposition) {
        this.originalTestInfo = gametestharnessinfo;
        this.testTicker = gametestharnessticker;
        this.structurePos = blockposition;
        this.attempts = 0;
        this.successes = 0;
    }

    @Override
    public void a(GameTestHarnessInfo gametestharnessinfo) {
        a(this.originalTestInfo, Blocks.LIGHT_GRAY_STAINED_GLASS);
        ++this.attempts;
    }

    @Override
    public void b(GameTestHarnessInfo gametestharnessinfo) {
        ++this.successes;
        if (!gametestharnessinfo.x()) {
            a(gametestharnessinfo, gametestharnessinfo.c() + " passed!");
        } else {
            if (this.successes >= gametestharnessinfo.z()) {
                a(gametestharnessinfo, gametestharnessinfo + " passed " + this.successes + " times of " + this.attempts + " attempts.");
            } else {
                a(this.originalTestInfo.g(), EnumChatFormat.GREEN, "Flaky test " + this.originalTestInfo + " succeeded, attempt: " + this.attempts + " successes: " + this.successes);
                this.a();
            }

        }
    }

    @Override
    public void c(GameTestHarnessInfo gametestharnessinfo) {
        if (!gametestharnessinfo.x()) {
            a(gametestharnessinfo, gametestharnessinfo.n());
        } else {
            GameTestHarnessTestFunction gametestharnesstestfunction = this.originalTestInfo.v();
            String s = "Flaky test " + this.originalTestInfo + " failed, attempt: " + this.attempts + "/" + gametestharnesstestfunction.i();

            if (gametestharnesstestfunction.j() > 1) {
                s = s + ", successes: " + this.successes + " (" + gametestharnesstestfunction.j() + " required)";
            }

            a(this.originalTestInfo.g(), EnumChatFormat.YELLOW, s);
            if (gametestharnessinfo.y() - this.attempts + this.successes >= gametestharnessinfo.z()) {
                this.a();
            } else {
                a(gametestharnessinfo, (Throwable) (new ExhaustedAttemptsException(this.attempts, this.successes, gametestharnessinfo)));
            }

        }
    }

    public static void a(GameTestHarnessInfo gametestharnessinfo, String s) {
        a(gametestharnessinfo, Blocks.LIME_STAINED_GLASS);
        b(gametestharnessinfo, s);
    }

    private static void b(GameTestHarnessInfo gametestharnessinfo, String s) {
        a(gametestharnessinfo.g(), EnumChatFormat.GREEN, s);
        GlobalTestReporter.b(gametestharnessinfo);
    }

    protected static void a(GameTestHarnessInfo gametestharnessinfo, Throwable throwable) {
        a(gametestharnessinfo, gametestharnessinfo.r() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
        c(gametestharnessinfo, SystemUtils.d(throwable));
        b(gametestharnessinfo, throwable);
    }

    protected static void b(GameTestHarnessInfo gametestharnessinfo, Throwable throwable) {
        String s = throwable.getMessage();
        String s1 = s + (throwable.getCause() == null ? "" : " cause: " + SystemUtils.d(throwable.getCause()));

        s = gametestharnessinfo.r() ? "" : "(optional) ";
        String s2 = s + gametestharnessinfo.c() + " failed! " + s1;

        a(gametestharnessinfo.g(), gametestharnessinfo.r() ? EnumChatFormat.RED : EnumChatFormat.YELLOW, s2);
        Throwable throwable1 = (Throwable) MoreObjects.firstNonNull(ExceptionUtils.getRootCause(throwable), throwable);

        if (throwable1 instanceof GameTestHarnessAssertionPosition) {
            GameTestHarnessAssertionPosition gametestharnessassertionposition = (GameTestHarnessAssertionPosition) throwable1;

            a(gametestharnessinfo.g(), gametestharnessassertionposition.c(), gametestharnessassertionposition.a());
        }

        GlobalTestReporter.a(gametestharnessinfo);
    }

    private void a() {
        this.originalTestInfo.o();
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(this.originalTestInfo.v(), this.originalTestInfo.u(), this.originalTestInfo.g());

        gametestharnessinfo.a();
        this.testTicker.a(gametestharnessinfo);
        gametestharnessinfo.a((GameTestHarnessListener) this);
        gametestharnessinfo.a(this.structurePos, 2);
    }

    protected static void a(GameTestHarnessInfo gametestharnessinfo, Block block) {
        WorldServer worldserver = gametestharnessinfo.g();
        BlockPosition blockposition = gametestharnessinfo.d();
        BlockPosition blockposition1 = new BlockPosition(-1, -1, -1);
        BlockPosition blockposition2 = DefinedStructure.a(blockposition.f(blockposition1), EnumBlockMirror.NONE, gametestharnessinfo.u(), blockposition);

        worldserver.setTypeUpdate(blockposition2, Blocks.BEACON.getBlockData().a(gametestharnessinfo.u()));
        BlockPosition blockposition3 = blockposition2.c(0, 1, 0);

        worldserver.setTypeUpdate(blockposition3, block.getBlockData());

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPosition blockposition4 = blockposition2.c(i, -1, j);

                worldserver.setTypeUpdate(blockposition4, Blocks.IRON_BLOCK.getBlockData());
            }
        }

    }

    private static void c(GameTestHarnessInfo gametestharnessinfo, String s) {
        WorldServer worldserver = gametestharnessinfo.g();
        BlockPosition blockposition = gametestharnessinfo.d();
        BlockPosition blockposition1 = new BlockPosition(-1, 1, -1);
        BlockPosition blockposition2 = DefinedStructure.a(blockposition.f(blockposition1), EnumBlockMirror.NONE, gametestharnessinfo.u(), blockposition);

        worldserver.setTypeUpdate(blockposition2, Blocks.LECTERN.getBlockData().a(gametestharnessinfo.u()));
        IBlockData iblockdata = worldserver.getType(blockposition2);
        ItemStack itemstack = a(gametestharnessinfo.c(), gametestharnessinfo.r(), s);

        BlockLectern.a((EntityHuman) null, (World) worldserver, blockposition2, iblockdata, itemstack);
    }

    private static ItemStack a(String s, boolean flag, String s1) {
        ItemStack itemstack = new ItemStack(Items.WRITABLE_BOOK);
        NBTTagList nbttaglist = new NBTTagList();
        StringBuffer stringbuffer = new StringBuffer();

        Arrays.stream(s.split("\\.")).forEach((s2) -> {
            stringbuffer.append(s2).append('\n');
        });
        if (!flag) {
            stringbuffer.append("(optional)\n");
        }

        stringbuffer.append("-------------------\n");
        nbttaglist.add(NBTTagString.a(stringbuffer + s1));
        itemstack.a("pages", (NBTBase) nbttaglist);
        return itemstack;
    }

    protected static void a(WorldServer worldserver, EnumChatFormat enumchatformat, String s) {
        worldserver.a((entityplayer) -> {
            return true;
        }).forEach((entityplayer) -> {
            entityplayer.sendMessage((new ChatComponentText(s)).a(enumchatformat), SystemUtils.NIL_UUID);
        });
    }

    private static void a(WorldServer worldserver, BlockPosition blockposition, String s) {
        PacketDebug.a(worldserver, blockposition, s, -2130771968, Integer.MAX_VALUE);
    }
}
