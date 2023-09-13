package net.minecraft.gametest.framework;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import net.minecraft.EnumChatFormat;
import net.minecraft.SystemUtils;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketDebug;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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
    public void testStructureLoaded(GameTestHarnessInfo gametestharnessinfo) {
        spawnBeacon(this.originalTestInfo, Blocks.LIGHT_GRAY_STAINED_GLASS);
        ++this.attempts;
    }

    @Override
    public void testPassed(GameTestHarnessInfo gametestharnessinfo) {
        ++this.successes;
        if (!gametestharnessinfo.isFlaky()) {
            String s = gametestharnessinfo.getTestName();

            reportPassed(gametestharnessinfo, s + " passed! (" + gametestharnessinfo.getRunTime() + "ms)");
        } else {
            if (this.successes >= gametestharnessinfo.requiredSuccesses()) {
                reportPassed(gametestharnessinfo, gametestharnessinfo + " passed " + this.successes + " times of " + this.attempts + " attempts.");
            } else {
                say(this.originalTestInfo.getLevel(), EnumChatFormat.GREEN, "Flaky test " + this.originalTestInfo + " succeeded, attempt: " + this.attempts + " successes: " + this.successes);
                this.rerunTest();
            }

        }
    }

    @Override
    public void testFailed(GameTestHarnessInfo gametestharnessinfo) {
        if (!gametestharnessinfo.isFlaky()) {
            reportFailure(gametestharnessinfo, gametestharnessinfo.getError());
        } else {
            GameTestHarnessTestFunction gametestharnesstestfunction = this.originalTestInfo.getTestFunction();
            String s = "Flaky test " + this.originalTestInfo + " failed, attempt: " + this.attempts + "/" + gametestharnesstestfunction.getMaxAttempts();

            if (gametestharnesstestfunction.getRequiredSuccesses() > 1) {
                s = s + ", successes: " + this.successes + " (" + gametestharnesstestfunction.getRequiredSuccesses() + " required)";
            }

            say(this.originalTestInfo.getLevel(), EnumChatFormat.YELLOW, s);
            if (gametestharnessinfo.maxAttempts() - this.attempts + this.successes >= gametestharnessinfo.requiredSuccesses()) {
                this.rerunTest();
            } else {
                reportFailure(gametestharnessinfo, new ExhaustedAttemptsException(this.attempts, this.successes, gametestharnessinfo));
            }

        }
    }

    public static void reportPassed(GameTestHarnessInfo gametestharnessinfo, String s) {
        spawnBeacon(gametestharnessinfo, Blocks.LIME_STAINED_GLASS);
        visualizePassedTest(gametestharnessinfo, s);
    }

    private static void visualizePassedTest(GameTestHarnessInfo gametestharnessinfo, String s) {
        say(gametestharnessinfo.getLevel(), EnumChatFormat.GREEN, s);
        GlobalTestReporter.onTestSuccess(gametestharnessinfo);
    }

    protected static void reportFailure(GameTestHarnessInfo gametestharnessinfo, Throwable throwable) {
        spawnBeacon(gametestharnessinfo, gametestharnessinfo.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
        spawnLectern(gametestharnessinfo, SystemUtils.describeError(throwable));
        visualizeFailedTest(gametestharnessinfo, throwable);
    }

    protected static void visualizeFailedTest(GameTestHarnessInfo gametestharnessinfo, Throwable throwable) {
        String s = throwable.getMessage();
        String s1 = s + (throwable.getCause() == null ? "" : " cause: " + SystemUtils.describeError(throwable.getCause()));

        s = gametestharnessinfo.isRequired() ? "" : "(optional) ";
        String s2 = s + gametestharnessinfo.getTestName() + " failed! " + s1;

        say(gametestharnessinfo.getLevel(), gametestharnessinfo.isRequired() ? EnumChatFormat.RED : EnumChatFormat.YELLOW, s2);
        Throwable throwable1 = (Throwable) MoreObjects.firstNonNull(ExceptionUtils.getRootCause(throwable), throwable);

        if (throwable1 instanceof GameTestHarnessAssertionPosition) {
            GameTestHarnessAssertionPosition gametestharnessassertionposition = (GameTestHarnessAssertionPosition) throwable1;

            showRedBox(gametestharnessinfo.getLevel(), gametestharnessassertionposition.getAbsolutePos(), gametestharnessassertionposition.getMessageToShowAtBlock());
        }

        GlobalTestReporter.onTestFailed(gametestharnessinfo);
    }

    private void rerunTest() {
        this.originalTestInfo.clearStructure();
        GameTestHarnessInfo gametestharnessinfo = new GameTestHarnessInfo(this.originalTestInfo.getTestFunction(), this.originalTestInfo.getRotation(), this.originalTestInfo.getLevel());

        gametestharnessinfo.startExecution();
        this.testTicker.add(gametestharnessinfo);
        gametestharnessinfo.addListener(this);
        gametestharnessinfo.spawnStructure(this.structurePos, 2);
    }

    protected static void spawnBeacon(GameTestHarnessInfo gametestharnessinfo, Block block) {
        WorldServer worldserver = gametestharnessinfo.getLevel();
        BlockPosition blockposition = gametestharnessinfo.getStructureBlockPos();
        BlockPosition blockposition1 = new BlockPosition(-1, -1, -1);
        BlockPosition blockposition2 = DefinedStructure.transform(blockposition.offset(blockposition1), EnumBlockMirror.NONE, gametestharnessinfo.getRotation(), blockposition);

        worldserver.setBlockAndUpdate(blockposition2, Blocks.BEACON.defaultBlockState().rotate(gametestharnessinfo.getRotation()));
        BlockPosition blockposition3 = blockposition2.offset(0, 1, 0);

        worldserver.setBlockAndUpdate(blockposition3, block.defaultBlockState());

        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                BlockPosition blockposition4 = blockposition2.offset(i, -1, j);

                worldserver.setBlockAndUpdate(blockposition4, Blocks.IRON_BLOCK.defaultBlockState());
            }
        }

    }

    private static void spawnLectern(GameTestHarnessInfo gametestharnessinfo, String s) {
        WorldServer worldserver = gametestharnessinfo.getLevel();
        BlockPosition blockposition = gametestharnessinfo.getStructureBlockPos();
        BlockPosition blockposition1 = new BlockPosition(-1, 1, -1);
        BlockPosition blockposition2 = DefinedStructure.transform(blockposition.offset(blockposition1), EnumBlockMirror.NONE, gametestharnessinfo.getRotation(), blockposition);

        worldserver.setBlockAndUpdate(blockposition2, Blocks.LECTERN.defaultBlockState().rotate(gametestharnessinfo.getRotation()));
        IBlockData iblockdata = worldserver.getBlockState(blockposition2);
        ItemStack itemstack = createBook(gametestharnessinfo.getTestName(), gametestharnessinfo.isRequired(), s);

        BlockLectern.tryPlaceBook((Entity) null, worldserver, blockposition2, iblockdata, itemstack);
    }

    private static ItemStack createBook(String s, boolean flag, String s1) {
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
        nbttaglist.add(NBTTagString.valueOf(stringbuffer + s1));
        itemstack.addTagElement("pages", nbttaglist);
        return itemstack;
    }

    protected static void say(WorldServer worldserver, EnumChatFormat enumchatformat, String s) {
        worldserver.getPlayers((entityplayer) -> {
            return true;
        }).forEach((entityplayer) -> {
            entityplayer.sendSystemMessage(IChatBaseComponent.literal(s).withStyle(enumchatformat));
        });
    }

    private static void showRedBox(WorldServer worldserver, BlockPosition blockposition, String s) {
        PacketDebug.sendGameTestAddMarker(worldserver, blockposition, s, -2130771968, Integer.MAX_VALUE);
    }
}
