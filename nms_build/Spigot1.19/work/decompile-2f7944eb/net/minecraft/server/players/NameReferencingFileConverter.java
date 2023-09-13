package net.minecraft.server.players;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.yggdrasil.ProfileNotFoundException;
import com.mojang.logging.LogUtils;
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
import net.minecraft.core.UUIDUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.UtilColor;
import net.minecraft.world.level.storage.SavedFile;
import org.slf4j.Logger;

public class NameReferencingFileConverter {

    static final Logger LOGGER = LogUtils.getLogger();
    public static final File OLD_IPBANLIST = new File("banned-ips.txt");
    public static final File OLD_USERBANLIST = new File("banned-players.txt");
    public static final File OLD_OPLIST = new File("ops.txt");
    public static final File OLD_WHITELIST = new File("white-list.txt");

    public NameReferencingFileConverter() {}

    static List<String> readOldListFormat(File file, Map<String, String[]> map) throws IOException {
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

    private static void lookupPlayers(MinecraftServer minecraftserver, Collection<String> collection, ProfileLookupCallback profilelookupcallback) {
        String[] astring = (String[]) collection.stream().filter((s) -> {
            return !UtilColor.isNullOrEmpty(s);
        }).toArray((i) -> {
            return new String[i];
        });

        if (minecraftserver.usesAuthentication()) {
            minecraftserver.getProfileRepository().findProfilesByNames(astring, Agent.MINECRAFT, profilelookupcallback);
        } else {
            String[] astring1 = astring;
            int i = astring.length;

            for (int j = 0; j < i; ++j) {
                String s = astring1[j];
                UUID uuid = UUIDUtil.getOrCreatePlayerUUID(new GameProfile((UUID) null, s));
                GameProfile gameprofile = new GameProfile(uuid, s);

                profilelookupcallback.onProfileLookupSucceeded(gameprofile);
            }
        }

    }

    public static boolean convertUserBanlist(final MinecraftServer minecraftserver) {
        final GameProfileBanList gameprofilebanlist = new GameProfileBanList(PlayerList.USERBANLIST_FILE);

        if (NameReferencingFileConverter.OLD_USERBANLIST.exists() && NameReferencingFileConverter.OLD_USERBANLIST.isFile()) {
            if (gameprofilebanlist.getFile().exists()) {
                try {
                    gameprofilebanlist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", gameprofilebanlist.getFile().getName(), ioexception);
                }
            }

            try {
                final Map<String, String[]> map = Maps.newHashMap();

                readOldListFormat(NameReferencingFileConverter.OLD_USERBANLIST, map);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getProfileCache().add(gameprofile);
                        String[] astring = (String[]) map.get(gameprofile.getName().toLowerCase(Locale.ROOT));

                        if (astring == null) {
                            NameReferencingFileConverter.LOGGER.warn("Could not convert user banlist entry for {}", gameprofile.getName());
                            throw new NameReferencingFileConverter.FileConversionException("Profile not in the conversionlist");
                        } else {
                            Date date = astring.length > 1 ? NameReferencingFileConverter.parseDate(astring[1], (Date) null) : null;
                            String s = astring.length > 2 ? astring[2] : null;
                            Date date1 = astring.length > 3 ? NameReferencingFileConverter.parseDate(astring[3], (Date) null) : null;
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

                lookupPlayers(minecraftserver, map.keySet(), profilelookupcallback);
                gameprofilebanlist.save();
                renameOldFile(NameReferencingFileConverter.OLD_USERBANLIST);
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

    public static boolean convertIpBanlist(MinecraftServer minecraftserver) {
        IpBanList ipbanlist = new IpBanList(PlayerList.IPBANLIST_FILE);

        if (NameReferencingFileConverter.OLD_IPBANLIST.exists() && NameReferencingFileConverter.OLD_IPBANLIST.isFile()) {
            if (ipbanlist.getFile().exists()) {
                try {
                    ipbanlist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", ipbanlist.getFile().getName(), ioexception);
                }
            }

            try {
                Map<String, String[]> map = Maps.newHashMap();

                readOldListFormat(NameReferencingFileConverter.OLD_IPBANLIST, map);
                Iterator iterator = map.keySet().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();
                    String[] astring = (String[]) map.get(s);
                    Date date = astring.length > 1 ? parseDate(astring[1], (Date) null) : null;
                    String s1 = astring.length > 2 ? astring[2] : null;
                    Date date1 = astring.length > 3 ? parseDate(astring[3], (Date) null) : null;
                    String s2 = astring.length > 4 ? astring[4] : null;

                    ipbanlist.add(new IpBanEntry(s, date, s1, date1, s2));
                }

                ipbanlist.save();
                renameOldFile(NameReferencingFileConverter.OLD_IPBANLIST);
                return true;
            } catch (IOException ioexception1) {
                NameReferencingFileConverter.LOGGER.warn("Could not parse old ip banlist to convert it!", ioexception1);
                return false;
            }
        } else {
            return true;
        }
    }

    public static boolean convertOpsList(final MinecraftServer minecraftserver) {
        final OpList oplist = new OpList(PlayerList.OPLIST_FILE);

        if (NameReferencingFileConverter.OLD_OPLIST.exists() && NameReferencingFileConverter.OLD_OPLIST.isFile()) {
            if (oplist.getFile().exists()) {
                try {
                    oplist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", oplist.getFile().getName(), ioexception);
                }
            }

            try {
                List<String> list = Files.readLines(NameReferencingFileConverter.OLD_OPLIST, StandardCharsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getProfileCache().add(gameprofile);
                        oplist.add(new OpListEntry(gameprofile, minecraftserver.getOperatorUserPermissionLevel(), false));
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup oplist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                lookupPlayers(minecraftserver, list, profilelookupcallback);
                oplist.save();
                renameOldFile(NameReferencingFileConverter.OLD_OPLIST);
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

    public static boolean convertWhiteList(final MinecraftServer minecraftserver) {
        final WhiteList whitelist = new WhiteList(PlayerList.WHITELIST_FILE);

        if (NameReferencingFileConverter.OLD_WHITELIST.exists() && NameReferencingFileConverter.OLD_WHITELIST.isFile()) {
            if (whitelist.getFile().exists()) {
                try {
                    whitelist.load();
                } catch (IOException ioexception) {
                    NameReferencingFileConverter.LOGGER.warn("Could not load existing file {}", whitelist.getFile().getName(), ioexception);
                }
            }

            try {
                List<String> list = Files.readLines(NameReferencingFileConverter.OLD_WHITELIST, StandardCharsets.UTF_8);
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getProfileCache().add(gameprofile);
                        whitelist.add(new WhiteListEntry(gameprofile));
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", gameprofile.getName(), exception);
                        if (!(exception instanceof ProfileNotFoundException)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }
                };

                lookupPlayers(minecraftserver, list, profilelookupcallback);
                whitelist.save();
                renameOldFile(NameReferencingFileConverter.OLD_WHITELIST);
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
    public static UUID convertMobOwnerIfNecessary(final MinecraftServer minecraftserver, String s) {
        if (!UtilColor.isNullOrEmpty(s) && s.length() <= 16) {
            Optional<UUID> optional = minecraftserver.getProfileCache().get(s).map(GameProfile::getId);

            if (optional.isPresent()) {
                return (UUID) optional.get();
            } else if (!minecraftserver.isSingleplayer() && minecraftserver.usesAuthentication()) {
                final List<GameProfile> list = Lists.newArrayList();
                ProfileLookupCallback profilelookupcallback = new ProfileLookupCallback() {
                    public void onProfileLookupSucceeded(GameProfile gameprofile) {
                        minecraftserver.getProfileCache().add(gameprofile);
                        list.add(gameprofile);
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup user whitelist entry for {}", gameprofile.getName(), exception);
                    }
                };

                lookupPlayers(minecraftserver, Lists.newArrayList(new String[]{s}), profilelookupcallback);
                return !list.isEmpty() && ((GameProfile) list.get(0)).getId() != null ? ((GameProfile) list.get(0)).getId() : null;
            } else {
                return UUIDUtil.getOrCreatePlayerUUID(new GameProfile((UUID) null, s));
            }
        } else {
            try {
                return UUID.fromString(s);
            } catch (IllegalArgumentException illegalargumentexception) {
                return null;
            }
        }
    }

    public static boolean convertPlayers(final DedicatedServer dedicatedserver) {
        final File file = getWorldPlayersDirectory(dedicatedserver);
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
                        dedicatedserver.getProfileCache().add(gameprofile);
                        UUID uuid = gameprofile.getId();

                        if (uuid == null) {
                            throw new NameReferencingFileConverter.FileConversionException("Missing UUID for user profile " + gameprofile.getName());
                        } else {
                            this.movePlayerFile(file1, this.getFileNameForProfile(gameprofile), uuid.toString());
                        }
                    }

                    public void onProfileLookupFailed(GameProfile gameprofile, Exception exception) {
                        NameReferencingFileConverter.LOGGER.warn("Could not lookup user uuid for {}", gameprofile.getName(), exception);
                        if (exception instanceof ProfileNotFoundException) {
                            String s2 = this.getFileNameForProfile(gameprofile);

                            this.movePlayerFile(file2, s2, s2);
                        } else {
                            throw new NameReferencingFileConverter.FileConversionException("Could not request user " + gameprofile.getName() + " from backend systems", exception);
                        }
                    }

                    private void movePlayerFile(File file4, String s2, String s3) {
                        File file5 = new File(file, s2 + ".dat");
                        File file6 = new File(file4, s3 + ".dat");

                        NameReferencingFileConverter.ensureDirectoryExists(file4);
                        if (!file5.renameTo(file6)) {
                            throw new NameReferencingFileConverter.FileConversionException("Could not convert file for " + s2);
                        }
                    }

                    private String getFileNameForProfile(GameProfile gameprofile) {
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

                lookupPlayers(dedicatedserver, Lists.newArrayList(astring), profilelookupcallback);
                return true;
            } catch (NameReferencingFileConverter.FileConversionException namereferencingfileconverter_fileconversionexception) {
                NameReferencingFileConverter.LOGGER.error("Conversion failed, please try again later", namereferencingfileconverter_fileconversionexception);
                return false;
            }
        } else {
            return true;
        }
    }

    static void ensureDirectoryExists(File file) {
        if (file.exists()) {
            if (!file.isDirectory()) {
                throw new NameReferencingFileConverter.FileConversionException("Can't create directory " + file.getName() + " in world save directory.");
            }
        } else if (!file.mkdirs()) {
            throw new NameReferencingFileConverter.FileConversionException("Can't create directory " + file.getName() + " in world save directory.");
        }
    }

    public static boolean serverReadyAfterUserconversion(MinecraftServer minecraftserver) {
        boolean flag = areOldUserlistsRemoved();

        flag = flag && areOldPlayersConverted(minecraftserver);
        return flag;
    }

    private static boolean areOldUserlistsRemoved() {
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

    private static boolean areOldPlayersConverted(MinecraftServer minecraftserver) {
        File file = getWorldPlayersDirectory(minecraftserver);

        if (file.exists() && file.isDirectory() && (file.list().length > 0 || !file.delete())) {
            NameReferencingFileConverter.LOGGER.warn("**** DETECTED OLD PLAYER DIRECTORY IN THE WORLD SAVE");
            NameReferencingFileConverter.LOGGER.warn("**** THIS USUALLY HAPPENS WHEN THE AUTOMATIC CONVERSION FAILED IN SOME WAY");
            NameReferencingFileConverter.LOGGER.warn("** please restart the server and if the problem persists, remove the directory '{}'", file.getPath());
            return false;
        } else {
            return true;
        }
    }

    private static File getWorldPlayersDirectory(MinecraftServer minecraftserver) {
        return minecraftserver.getWorldPath(SavedFile.PLAYER_OLD_DATA_DIR).toFile();
    }

    private static void renameOldFile(File file) {
        File file1 = new File(file.getName() + ".converted");

        file.renameTo(file1);
    }

    static Date parseDate(String s, Date date) {
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
