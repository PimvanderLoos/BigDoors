package net.minecraft.server;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import net.minecraft.server.players.UserCache;
import net.minecraft.util.SignatureValidator;

public record Services(MinecraftSessionService sessionService, SignatureValidator serviceSignatureValidator, GameProfileRepository profileRepository, UserCache profileCache) {

    private static final String USERID_CACHE_FILE = "usercache.json";

    public static Services create(YggdrasilAuthenticationService yggdrasilauthenticationservice, File file) {
        MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
        GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
        UserCache usercache = new UserCache(gameprofilerepository, new File(file, "usercache.json"));
        SignatureValidator signaturevalidator = SignatureValidator.from(yggdrasilauthenticationservice.getServicesKey());

        return new Services(minecraftsessionservice, signaturevalidator, gameprofilerepository, usercache);
    }
}
