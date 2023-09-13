package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionInstance;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.protocol.game.PacketPlayOutAdvancements;
import net.minecraft.network.protocol.game.PacketPlayOutSelectAdvancementTab;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.GameRules;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementDataPlayer {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int VISIBILITY_DEPTH = 2;
    private static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.a()).registerTypeAdapter(MinecraftKey.class, new MinecraftKey.a()).setPrettyPrinting().create();
    private static final TypeToken<Map<MinecraftKey, AdvancementProgress>> TYPE_TOKEN = new TypeToken<Map<MinecraftKey, AdvancementProgress>>() {
    };
    private final DataFixer dataFixer;
    private final PlayerList playerList;
    private final File file;
    public final Map<Advancement, AdvancementProgress> advancements = Maps.newLinkedHashMap();
    private final Set<Advancement> visible = Sets.newLinkedHashSet();
    private final Set<Advancement> visibilityChanged = Sets.newLinkedHashSet();
    private final Set<Advancement> progressChanged = Sets.newLinkedHashSet();
    private EntityPlayer player;
    @Nullable
    private Advancement lastSelectedTab;
    private boolean isFirstPacket = true;

    public AdvancementDataPlayer(DataFixer datafixer, PlayerList playerlist, AdvancementDataWorld advancementdataworld, File file, EntityPlayer entityplayer) {
        this.dataFixer = datafixer;
        this.playerList = playerlist;
        this.file = file;
        this.player = entityplayer;
        this.d(advancementdataworld);
    }

    public void a(EntityPlayer entityplayer) {
        this.player = entityplayer;
    }

    public void a() {
        Iterator iterator = CriterionTriggers.a().iterator();

        while (iterator.hasNext()) {
            CriterionTrigger<?> criteriontrigger = (CriterionTrigger) iterator.next();

            criteriontrigger.a(this);
        }

    }

    public void a(AdvancementDataWorld advancementdataworld) {
        this.a();
        this.advancements.clear();
        this.visible.clear();
        this.visibilityChanged.clear();
        this.progressChanged.clear();
        this.isFirstPacket = true;
        this.lastSelectedTab = null;
        this.d(advancementdataworld);
    }

    private void b(AdvancementDataWorld advancementdataworld) {
        Iterator iterator = advancementdataworld.getAdvancements().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            this.c(advancement);
        }

    }

    private void c() {
        List<Advancement> list = Lists.newArrayList();
        Iterator iterator = this.advancements.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Advancement, AdvancementProgress> entry = (Entry) iterator.next();

            if (((AdvancementProgress) entry.getValue()).isDone()) {
                list.add((Advancement) entry.getKey());
                this.progressChanged.add((Advancement) entry.getKey());
            }
        }

        iterator = list.iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            this.e(advancement);
        }

    }

    private void c(AdvancementDataWorld advancementdataworld) {
        Iterator iterator = advancementdataworld.getAdvancements().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            if (advancement.getCriteria().isEmpty()) {
                this.grantCriteria(advancement, "");
                advancement.d().a(this.player);
            }
        }

    }

    private void d(AdvancementDataWorld advancementdataworld) {
        if (this.file.isFile()) {
            try {
                JsonReader jsonreader = new JsonReader(new StringReader(Files.toString(this.file, StandardCharsets.UTF_8)));

                try {
                    jsonreader.setLenient(false);
                    Dynamic<JsonElement> dynamic = new Dynamic(JsonOps.INSTANCE, Streams.parse(jsonreader));

                    if (!dynamic.get("DataVersion").asNumber().result().isPresent()) {
                        dynamic = dynamic.set("DataVersion", dynamic.createInt(1343));
                    }

                    dynamic = this.dataFixer.update(DataFixTypes.ADVANCEMENTS.a(), dynamic, dynamic.get("DataVersion").asInt(0), SharedConstants.getGameVersion().getWorldVersion());
                    dynamic = dynamic.remove("DataVersion");
                    Map<MinecraftKey, AdvancementProgress> map = (Map) AdvancementDataPlayer.GSON.getAdapter(AdvancementDataPlayer.TYPE_TOKEN).fromJsonTree((JsonElement) dynamic.getValue());

                    if (map == null) {
                        throw new JsonParseException("Found null for advancements");
                    }

                    Stream<Entry<MinecraftKey, AdvancementProgress>> stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));
                    Iterator iterator = ((List) stream.collect(Collectors.toList())).iterator();

                    while (iterator.hasNext()) {
                        Entry<MinecraftKey, AdvancementProgress> entry = (Entry) iterator.next();
                        Advancement advancement = advancementdataworld.a((MinecraftKey) entry.getKey());

                        if (advancement == null) {
                            AdvancementDataPlayer.LOGGER.warn("Ignored advancement '{}' in progress file {} - it doesn't exist anymore?", entry.getKey(), this.file);
                        } else {
                            this.a(advancement, (AdvancementProgress) entry.getValue());
                        }
                    }
                } catch (Throwable throwable) {
                    try {
                        jsonreader.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                jsonreader.close();
            } catch (JsonParseException jsonparseexception) {
                AdvancementDataPlayer.LOGGER.error("Couldn't parse player advancements in {}", this.file, jsonparseexception);
            } catch (IOException ioexception) {
                AdvancementDataPlayer.LOGGER.error("Couldn't access player advancements in {}", this.file, ioexception);
            }
        }

        this.c(advancementdataworld);
        this.c();
        this.b(advancementdataworld);
    }

    public void b() {
        Map<MinecraftKey, AdvancementProgress> map = Maps.newHashMap();
        Iterator iterator = this.advancements.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Advancement, AdvancementProgress> entry = (Entry) iterator.next();
            AdvancementProgress advancementprogress = (AdvancementProgress) entry.getValue();

            if (advancementprogress.b()) {
                map.put(((Advancement) entry.getKey()).getName(), advancementprogress);
            }
        }

        if (this.file.getParentFile() != null) {
            this.file.getParentFile().mkdirs();
        }

        JsonElement jsonelement = AdvancementDataPlayer.GSON.toJsonTree(map);

        jsonelement.getAsJsonObject().addProperty("DataVersion", SharedConstants.getGameVersion().getWorldVersion());

        try {
            FileOutputStream fileoutputstream = new FileOutputStream(this.file);

            try {
                OutputStreamWriter outputstreamwriter = new OutputStreamWriter(fileoutputstream, Charsets.UTF_8.newEncoder());

                try {
                    AdvancementDataPlayer.GSON.toJson(jsonelement, outputstreamwriter);
                } catch (Throwable throwable) {
                    try {
                        outputstreamwriter.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }

                    throw throwable;
                }

                outputstreamwriter.close();
            } catch (Throwable throwable2) {
                try {
                    fileoutputstream.close();
                } catch (Throwable throwable3) {
                    throwable2.addSuppressed(throwable3);
                }

                throw throwable2;
            }

            fileoutputstream.close();
        } catch (IOException ioexception) {
            AdvancementDataPlayer.LOGGER.error("Couldn't save player advancements to {}", this.file, ioexception);
        }

    }

    public boolean grantCriteria(Advancement advancement, String s) {
        boolean flag = false;
        AdvancementProgress advancementprogress = this.getProgress(advancement);
        boolean flag1 = advancementprogress.isDone();

        if (advancementprogress.a(s)) {
            this.d(advancement);
            this.progressChanged.add(advancement);
            flag = true;
            if (!flag1 && advancementprogress.isDone()) {
                advancement.d().a(this.player);
                if (advancement.c() != null && advancement.c().i() && this.player.level.getGameRules().getBoolean(GameRules.RULE_ANNOUNCE_ADVANCEMENTS)) {
                    this.playerList.sendMessage(new ChatMessage("chat.type.advancement." + advancement.c().e().a(), new Object[]{this.player.getScoreboardDisplayName(), advancement.j()}), ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
                }
            }
        }

        if (advancementprogress.isDone()) {
            this.e(advancement);
        }

        return flag;
    }

    public boolean revokeCritera(Advancement advancement, String s) {
        boolean flag = false;
        AdvancementProgress advancementprogress = this.getProgress(advancement);

        if (advancementprogress.b(s)) {
            this.c(advancement);
            this.progressChanged.add(advancement);
            flag = true;
        }

        if (!advancementprogress.b()) {
            this.e(advancement);
        }

        return flag;
    }

    private void c(Advancement advancement) {
        AdvancementProgress advancementprogress = this.getProgress(advancement);

        if (!advancementprogress.isDone()) {
            Iterator iterator = advancement.getCriteria().entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<String, Criterion> entry = (Entry) iterator.next();
                CriterionProgress criterionprogress = advancementprogress.getCriterionProgress((String) entry.getKey());

                if (criterionprogress != null && !criterionprogress.a()) {
                    CriterionInstance criterioninstance = ((Criterion) entry.getValue()).a();

                    if (criterioninstance != null) {
                        CriterionTrigger<CriterionInstance> criteriontrigger = CriterionTriggers.a(criterioninstance.a());

                        if (criteriontrigger != null) {
                            criteriontrigger.a(this, new CriterionTrigger.a<>(criterioninstance, advancement, (String) entry.getKey()));
                        }
                    }
                }
            }

        }
    }

    private void d(Advancement advancement) {
        AdvancementProgress advancementprogress = this.getProgress(advancement);
        Iterator iterator = advancement.getCriteria().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, Criterion> entry = (Entry) iterator.next();
            CriterionProgress criterionprogress = advancementprogress.getCriterionProgress((String) entry.getKey());

            if (criterionprogress != null && (criterionprogress.a() || advancementprogress.isDone())) {
                CriterionInstance criterioninstance = ((Criterion) entry.getValue()).a();

                if (criterioninstance != null) {
                    CriterionTrigger<CriterionInstance> criteriontrigger = CriterionTriggers.a(criterioninstance.a());

                    if (criteriontrigger != null) {
                        criteriontrigger.b(this, new CriterionTrigger.a<>(criterioninstance, advancement, (String) entry.getKey()));
                    }
                }
            }
        }

    }

    public void b(EntityPlayer entityplayer) {
        if (this.isFirstPacket || !this.visibilityChanged.isEmpty() || !this.progressChanged.isEmpty()) {
            Map<MinecraftKey, AdvancementProgress> map = Maps.newHashMap();
            Set<Advancement> set = Sets.newLinkedHashSet();
            Set<MinecraftKey> set1 = Sets.newLinkedHashSet();
            Iterator iterator = this.progressChanged.iterator();

            Advancement advancement;

            while (iterator.hasNext()) {
                advancement = (Advancement) iterator.next();
                if (this.visible.contains(advancement)) {
                    map.put(advancement.getName(), (AdvancementProgress) this.advancements.get(advancement));
                }
            }

            iterator = this.visibilityChanged.iterator();

            while (iterator.hasNext()) {
                advancement = (Advancement) iterator.next();
                if (this.visible.contains(advancement)) {
                    set.add(advancement);
                } else {
                    set1.add(advancement.getName());
                }
            }

            if (this.isFirstPacket || !map.isEmpty() || !set.isEmpty() || !set1.isEmpty()) {
                entityplayer.connection.sendPacket(new PacketPlayOutAdvancements(this.isFirstPacket, set, set1, map));
                this.visibilityChanged.clear();
                this.progressChanged.clear();
            }
        }

        this.isFirstPacket = false;
    }

    public void a(@Nullable Advancement advancement) {
        Advancement advancement1 = this.lastSelectedTab;

        if (advancement != null && advancement.b() == null && advancement.c() != null) {
            this.lastSelectedTab = advancement;
        } else {
            this.lastSelectedTab = null;
        }

        if (advancement1 != this.lastSelectedTab) {
            this.player.connection.sendPacket(new PacketPlayOutSelectAdvancementTab(this.lastSelectedTab == null ? null : this.lastSelectedTab.getName()));
        }

    }

    public AdvancementProgress getProgress(Advancement advancement) {
        AdvancementProgress advancementprogress = (AdvancementProgress) this.advancements.get(advancement);

        if (advancementprogress == null) {
            advancementprogress = new AdvancementProgress();
            this.a(advancement, advancementprogress);
        }

        return advancementprogress;
    }

    private void a(Advancement advancement, AdvancementProgress advancementprogress) {
        advancementprogress.a(advancement.getCriteria(), advancement.i());
        this.advancements.put(advancement, advancementprogress);
    }

    private void e(Advancement advancement) {
        boolean flag = this.f(advancement);
        boolean flag1 = this.visible.contains(advancement);

        if (flag && !flag1) {
            this.visible.add(advancement);
            this.visibilityChanged.add(advancement);
            if (this.advancements.containsKey(advancement)) {
                this.progressChanged.add(advancement);
            }
        } else if (!flag && flag1) {
            this.visible.remove(advancement);
            this.visibilityChanged.add(advancement);
        }

        if (flag != flag1 && advancement.b() != null) {
            this.e(advancement.b());
        }

        Iterator iterator = advancement.e().iterator();

        while (iterator.hasNext()) {
            Advancement advancement1 = (Advancement) iterator.next();

            this.e(advancement1);
        }

    }

    private boolean f(Advancement advancement) {
        for (int i = 0; advancement != null && i <= 2; ++i) {
            if (i == 0 && this.g(advancement)) {
                return true;
            }

            if (advancement.c() == null) {
                return false;
            }

            AdvancementProgress advancementprogress = this.getProgress(advancement);

            if (advancementprogress.isDone()) {
                return true;
            }

            if (advancement.c().j()) {
                return false;
            }

            advancement = advancement.b();
        }

        return false;
    }

    private boolean g(Advancement advancement) {
        AdvancementProgress advancementprogress = this.getProgress(advancement);

        if (advancementprogress.isDone()) {
            return true;
        } else {
            Iterator iterator = advancement.e().iterator();

            Advancement advancement1;

            do {
                if (!iterator.hasNext()) {
                    return false;
                }

                advancement1 = (Advancement) iterator.next();
            } while (!this.g(advancement1));

            return true;
        }
    }
}
