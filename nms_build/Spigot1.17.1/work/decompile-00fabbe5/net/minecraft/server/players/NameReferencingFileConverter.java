package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.UtilColor;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.storage.SavedFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NameReferencingFileConverter {

    static final Logger LOGGER = LogManager.getLogger();
    public static final File OLD_IPBANLIST = new File("banned-ips.txt");
    public static final File OLD_USERBANLIST = new File("banned-players.txt");
    public static final File OLD_OPLIST = new File("ops.txt");
    public static final File OLD_WHITELIST = new File("white-list.txt");

    public NameReferencingFileConverter() {}

    static List<String> a(File file, Map<String, String[]> map) throws IOException {
        List<String> list = Files.readLines(file, StandardCharsets.UTF_8);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            s = s.trim();
            if (!s.startsWith("#") && s.length() >= 1) {
                String[] astring = s.split("\\|");

                map.put(astring[0].toLowerCase(Locale.ROOT), astring);
            }
        }

        return list;
    }

    private static void a(MinecraftServer minecraftserver, Collection<String> collection, ProfileLookupCallback profilelookupcallback) {
        String[] astring = (String[]) collection.stream().filter((s) -> {
            return !UtilColor.b(s);
        }).toArray((i) -> {
            return new String[i];
        });

        if (minecraftserver.getOnlineMode()) {
            minecraftserver.getGameProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, profilelookupcallback);
        } else {
            String[] astring1 = astring;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s = astring1[j];
                UUID uuid = EntityHuman.a(new GameProfile((UUID) null, s));
                GameProfile gameprofile = new GameProfile(uuid, s);

                profilelookupcallback.onProfileLookupSucceeded(gameprofile);
            }
        }

    }

    public static boolean a(final MinecraftServer minecraftserver) {
        final GameProfileBanList gameprofilebanlist = new GameProfileBanList(PlayerList.USERBANLIST_FILE);

        if (NameReferencingFileConverter.OLD_USERBANLIST.exists() && NameReferencingFileConverter.OLD_USERBANLIST.isFile()) {
            if (gameprofilebanlist.b().exists()) {
                try {
                    gameprofilebanlist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", gameprofilebanlist.b().getName(), ioexception);
                }
            }

            try {
                final Map<String, String[]> map = Maps.newHashMap();

                a(NameReferencingFileConverter.OLD_USERBANLIST, (Map) map);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getUserCache().a(gameprofile);
                        String[] astring = (String[]) map.get(gameprofile.getName().toLowerCase(Locale.ROOT));

                        if (astring == null) {
                            NameReferencingFileConverter.LOGGER.warn("Could not convert user banlist entry for {}", gameprofile.getName());
                            throw new NameReferencingFileConverter.FileConversionException("Profile not in the conversionlist");
                        } else {
                            Date date = astring.length > 1 ? NameReferencingFileConverter.a(astring[1], (Date) null) : null;
                            String s = astring.length > 2 ? astring[2] : null;
                            Date date1 = astring.length > 3 ? NameReferencingFileConverter.a(astring[3], (Date) null) : null;
                            String s1 = astring.length > 4 ? astring[4] : null;

                            gameprofilebanlist.add(new GameProfileBanEntry(gameprofile, date, s, date1, s1));
                        }
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup user banlist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                a(minecraftserver, map.keySet(), profilelookupcallback);
                gameprofilebanlist.save();
                b(NameReferencingFileConverter.OLD_USERBANLIST);
                return true;
            } catch (IOException ioexception1) {
                NameReferencingFileConverter.LOGGER.warn("Could not read old user banlist to convert it!", ioexception1);
                return false;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.LOGGER.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean b(MinecraftServer minecraftserver) {
        IpBanList ipbanlist = new IpBanList(PlayerList.IPBANLIST_FILE);

        if (NameReferencingFileConverter.OLD_IPBANLIST.exists() && NameReferencingFileConverter.OLD_IPBANLIST.isFile()) {
            if (ipbanlist.b().exists()) {
                try {
                    ipbanlist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", ipbanlist.b().getName(), ioexception);
                }
            }

            try {
                Map<String, String[]> map = Maps.newHashMap();

                a(NameReferencingFileConverter.OLD_IPBANLIST, (Map) map);
                Iterator iterator = map.keySet().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    String[] astring = (String[]) map.get(s);
                    Date date = astring.length > 1 ? a(astring[1], (Date) null) : null;
                    String s1 = astring.length > 2 ? astring[2] : null;
                    Date date1 = astring.length > 3 ? a(astring[3], (Date) null) : null;
                    String s2 = astring.length > 4 ? astring[4] : null;

                    ipbanlist.add(new IpBanEntry(s, date, s1, date1, s2));
                }

                ipbanlist.save();
                b(NameReferencingFileConverter.OLD_IPBANLIST);
                return true;
            } catch (IOException ioexception1) {
                NameReferencingFileConverter.LOGGER.warn("Could not parse old ip banlist to convert it!", ioexception1);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean c(final MinecraftServer minecraftserver) {
        final OpList oplist = new OpList(PlayerList.OPLIST_FILE);

        if (NameReferencingFileConverter.OLD_OPLIST.exists() && NameReferencingFileConverter.OLD_OPLIST.isFile()) {
            if (oplist.b().exists()) {
                try {
                    oplist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", oplist.b().getName(), ioexception);
                }
            }

            try {
                List<String> list = Files.readLines(NameReferencingFileConverter.OLD_OPLIST, StandardCharsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getUserCache().a(gameprofile);
                        oplist.add(new OpListEntry(gameprofile, minecraftserver.h(), false));
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup oplist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                a(minecraftserver, list, profilelookupcallback);
                oplist.save();
                b(NameReferencingFileConverter.OLD_OPLIST);
                return true;
            } catch (IOException ioexception1) {
                NameReferencingFileConverter.LOGGER.warn("Could not read old oplist to convert it!", ioexception1);
                return false;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.LOGGER.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean d(final MinecraftServer minecraftserver) {
        final WhiteList whitelist = new WhiteList(PlayerList.WHITELIST_FILE);

        if (NameReferencingFileConverter.OLD_WHITELIST.exists() && NameReferencingFileConverter.OLD_WHITELIST.isFile()) {
            if (whitelist.b().exists()) {
                try {
                    whitelist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", whitelist.b().getName(), ioexception);
                }
            }

            try {
                List<String> list = Files.readLines(NameReferencingFileConverter.OLD_WHITELIST, StandardCharsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getUserCache().a(gameprofile);
                        whitelist.add(new WhiteListEntry(gameprofile));
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                a(minecraftserver, list, profilelookupcallback);
                whitelist.save();
                b(NameReferencingFileConverter.OLD_WHITELIST);
                return true;
            } catch (IOException ioexception1) {
                NameReferencingFileConverter.LOGGER.warn("Could not read old whitelist to convert it!", ioexception1);
                return false;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.LOGGER.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    @Nullable
    public static UUID a(final MinecraftServer minecraftserver, String s) {
        if (!UtilColor.b(s) && s.length() <= 16) {
            Optional<UUID> optional = minecraftserver.getUserCache().getProfile(s).map(GameProfile::getId);

            if (optional.isPresent()) {
                return (UUID) optional.get();
            } else if (!minecraftserver.isEmbeddedServer() && minecraftserver.getOnlineMode()) {
                final List<GameProfile> list = Lists.newArrayList();
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getUserCache().a(gameprofile);
                        list.add(gameprofile);
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", gameprofile.getName(), exception);
                    }
                };

                a(minecraftserver, Lists.newArrayList(new String[]{s}), profilelookupcallback);
                return !list.isEmpty() && ((GameProfile) list.get(0)).getId() != null ? ((GameProfile) list.get(0)).getId() : null;
            } else {
                return EntityHuman.a(new GameProfile((UUID) null, s));
            }
        } else {
            try {
                return UUID.fromString(s);
            } catch (IllegalArgumentException illegalargumentexception) {
                return null;
            }
        }
    }

    public static boolean a(final DedicatedServer dedicatedserver) {
        final File file = getPlayersFolder(dedicatedserver);
        final File file1 = new File(file.getParentFile(), "playerdata");
        final File file2 = new File(file.getParentFile(), "unknownplayers");

        if (file.exists() && file.isDirectory()) {
            File[] afile = file.listFiles();
            List<String> list = Lists.newArrayList();
            File[] afile1 = afile;
            int i = afile.length;

            for (int j = 0; j < i; ++j) {
                File file3 = afile1[j];
                String s = file3.getName();

                if (s.toLowerCase(Locale.ROOT).endsWith(".dat")) {
                    String s1 = s.substring(0, s.length() - ".dat".length());

                    if (!s1.isEmpty()) {
                        list.add(s1);
                    }
                }
            }

            try {
                final String[] astring = (String[]) list.toArray(new String[list.size()]);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        dedicatedserver.getUserCache().a(gameprofile);
                        UUID uuid = gameprofile.getId();

                        if (uuid == null) {
                            throw new NameReferencingFileConverter.FileConversionException("Missing UUID for user profile " + gameprofile.getName());
                        } else {
                            this.a(file1, this.a(gameprofile), uuid.toString());
                        }
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup user uuid for {}", gameprofile.getName(), exception);
                        if (exception instanceof ProfileNotFoundException) {
                            String s2 = this.a(gameprofile);

                            this.a(file2, s2, s2);
                        } else {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }

                    private void a(File file4, String s2, String s3) {
                        File file5 = new File(file, s2 + ".dat");
                        File file6 = new File(file4, s3 + ".dat");

                        NameReferencingFileConverter.a(file4);
                        if (!file5.renameTo(file6)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not convert file for " + s2);
                        }
                    }

                    private String a(GameProfile gameprofile) {
                        String s2 = null;
                        String[] astring1 = astring;
                        int k = astring1.length;

                        for (int l = 0; l < k; ++l) {
                            String s3 = astring1[l];

                            if (s3 != null && s3.equalsIgnoreCase(gameprofile.getName())) {
                                s2 = s3;
                                break;
                            }
                        }

                        if (s2 == null) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not find the filename for " + gameprofile.getName() + " anymore");
                        } else {
                            return s2;
                        }
                    }
                };

                a(dedicatedserver, Lists.newArrayList(astring), profilelookupcallback);
                return true;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.LOGGER.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    static void a(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new NameReferencingFileConverter.FileConversionException("Can't create directory " + file.getName() + " in world save directory.");
            }
        } else if (!file.mkdirs()) {
            throw new NameReferencingFileConverter.FileConversionException("Can't create directory " + file.getName() + " in world save directory.");
        }
    }

    public static boolean e(MinecraftServer minecraftserver) {
        boolean flag = a();

        flag = flag && f(minecraftserver);
        return flag;
    }

    private static boolean a() {
        boolean flag = false;

        if (NameReferencingFileConverter.OLD_USERBANLIST.exists() && NameReferencingFileConverter.OLD_USERBANLIST.isFile()) {
            flag = true;
        }

        boolean flag1 = false;

        if (NameReferencingFileConverter.OLD_IPBANLIST.exists() && NameReferencingFileConverter.OLD_IPBANLIST.isFile()) {
            flag1 = true;
        }

        boolean flag2 = false;

        if (NameReferencingFileConverter.OLD_OPLIST.exists() && NameReferencingFileConverter.OLD_OPLIST.isFile()) {
            flag2 = true;
        }

        boolean flag3 = false;

        if (NameReferencingFileConverter.OLD_WHITELIST.exists() && NameReferencingFileConverter.OLD_WHITELIST.isFile()) {
            flag3 = true;
        }

        if (!flag && !flag1 && !flag2 && !flag3) {
            return true;
        } else {
            NameReferencingFileConverter.LOGGER.warn("**** FAILED TO START THE SERVER AFTER ACCOUNT CONVERSION!");
            NameReferencingFileConverter.LOGGER.warn("** please remove the following files and restart the server:");
            if (flag) {
                NameReferencingFileConverter.LOGGER.warn("* {}", NameReferencingFileConverter.OLD_USERBANLIST.getName());
            }

            if (flag1) {
                NameReferencingFileConverter.LOGGER.warn("* {}", NameReferencingFileConverter.OLD_IPBANLIST.getName());
            }

            if (flag2) {
                NameReferencingFileConverter.LOGGER.warn("* {}", NameReferencingFileConverter.OLD_OPLIST.getName());
            }

            if (flag3) {
                NameReferencingFileConverter.LOGGER.warn("* {}", NameReferencingFileConverter.OLD_WHITELIST.getName());
            }

            return false;
        }
    }

    private static boolean f(MinecraftServer minecraftserver) {
        File file = getPlayersFolder(minecraftserver);

        if (file.exists() && file.isDirectory() && (file.list().length > 0 || !file.delete())) {
            NameReferencingFileConverter.LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            NameReferencingFileConverter.LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            NameReferencingFileConverter.LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", file.getPath());
            return false;
        } else {
            return true;
        }
    }

    private static File getPlayersFolder(MinecraftServer minecraftserver) {
        return minecraftserver.a(SavedFile.PLAYER_OLD_DATA_DIR).toFile();
    }

    private static void b(File file) {
        File file1 = new File(file.getName() + ".converted");

        file.renameTo(file1);
    }

    static Date a(String s, Date date) {
        Date date1;

        try {
            date1 = ExpirableListEntry.DATE_FORMAT.parse(s);
        } catch (ParseException parseexception) {
            date1 = date;
        }

        return date1;
    }

    private static class FileConversionException extends RuntimeException {

        FileConversionException(String s, Throwable throwable) {
            super(s, throwable);
        }

        FileConversionException(String s) {
            super(s);
        }
    }
}
