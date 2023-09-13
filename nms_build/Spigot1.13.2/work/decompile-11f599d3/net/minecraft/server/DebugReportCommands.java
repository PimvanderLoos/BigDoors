package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.util.UUID;

public class DebugReportCommands implements DebugReportProvider {

    private final DebugReportGenerator b;

    public DebugReportCommands(DebugReportGenerator debugreportgenerator) {
        this.b = debugreportgenerator;
    }

    public void a(HashCache hashcache) throws IOException {
        YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
        File file = new File(this.b.b().toFile(), "tmp");
        UserCache usercache = new UserCache(gameprofilerepository, new File(file, MinecraftServer.a.getName()));
        DedicatedServer dedicatedserver = new DedicatedServer(file, DataConverterRegistry.a(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, usercache);

        dedicatedserver.getCommandDispatcher().a(this.b.b().resolve("reports/commands.json").toFile());
    }

    public String a() {
        return "Command Syntax";
    }
}
