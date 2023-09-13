package net.minecraft.server;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;

public class CommandDifficulty extends CommandAbstract {

    public CommandDifficulty() {}

    public String getCommand() {
        return "difficulty";
    }

    public int a() {
        return 2;
    }

    public String getUsage(ICommandListener icommandlistener) {
        return "commands.difficulty.usage";
    }

    public void execute(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring) throws CommandException {
        if (astring.length <= 0) {
            throw new ExceptionUsage("commands.difficulty.usage", new Object[0]);
        } else {
            EnumDifficulty enumdifficulty = this.e(astring[0]);

            minecraftserver.a(enumdifficulty);
            a(icommandlistener, (ICommand) this, "commands.difficulty.success", new Object[] { new ChatMessage(enumdifficulty.b(), new Object[0])});
        }
    }

    protected EnumDifficulty e(String s) throws ExceptionInvalidNumber {
        return !"peaceful".equalsIgnoreCase(s) && !"p".equalsIgnoreCase(s) ? (!"easy".equalsIgnoreCase(s) && !"e".equalsIgnoreCase(s) ? (!"normal".equalsIgnoreCase(s) && !"n".equalsIgnoreCase(s) ? (!"hard".equalsIgnoreCase(s) && !"h".equalsIgnoreCase(s) ? EnumDifficulty.getById(a(s, 0, 3)) : EnumDifficulty.HARD) : EnumDifficulty.NORMAL) : EnumDifficulty.EASY) : EnumDifficulty.PEACEFUL;
    }

    public List<String> tabComplete(MinecraftServer minecraftserver, ICommandListener icommandlistener, String[] astring, @Nullable BlockPosition blockposition) {
        return astring.length == 1 ? a(astring, new String[] { "peaceful", "easy", "normal", "hard"}) : Collections.emptyList();
    }
}
