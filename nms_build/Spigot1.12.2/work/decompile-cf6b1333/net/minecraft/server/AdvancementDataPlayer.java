package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AdvancementDataPlayer {

    private static final Logger a = LogManager.getLogger();
    private static final Gson b = (new GsonBuilder()).registerTypeAdapter(AdvancementProgress.class, new AdvancementProgress.a()).registerTypeAdapter(MinecraftKey.class, new MinecraftKey.a()).setPrettyPrinting().create();
    private static final TypeToken<Map<MinecraftKey, AdvancementProgress>> c = new TypeToken() {
    };
    private final MinecraftServer d;
    private final File e;
    public final Map<Advancement, AdvancementProgress> data = Maps.newLinkedHashMap();
    private final Set<Advancement> g = Sets.newLinkedHashSet();
    private final Set<Advancement> h = Sets.newLinkedHashSet();
    private final Set<Advancement> i = Sets.newLinkedHashSet();
    private EntityPlayer player;
    @Nullable
    private Advancement k;
    private boolean l = true;

    public AdvancementDataPlayer(MinecraftServer minecraftserver, File file, EntityPlayer entityplayer) {
        this.d = minecraftserver;
        this.e = file;
        this.player = entityplayer;
        this.g();
    }

    public void a(EntityPlayer entityplayer) {
        this.player = entityplayer;
    }

    public void a() {
        Iterator iterator = CriterionTriggers.a().iterator();

        while (iterator.hasNext()) {
            CriterionTrigger criteriontrigger = (CriterionTrigger) iterator.next();

            criteriontrigger.a(this);
        }

    }

    public void b() {
        this.a();
        this.data.clear();
        this.g.clear();
        this.h.clear();
        this.i.clear();
        this.l = true;
        this.k = null;
        this.g();
    }

    private void d() {
        Iterator iterator = this.d.getAdvancementData().c().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            this.c(advancement);
        }

    }

    private void e() {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.data.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            if (((AdvancementProgress) entry.getValue()).isDone()) {
                arraylist.add(entry.getKey());
                this.i.add(entry.getKey());
            }
        }

        iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            this.e(advancement);
        }

    }

    private void f() {
        Iterator iterator = this.d.getAdvancementData().c().iterator();

        while (iterator.hasNext()) {
            Advancement advancement = (Advancement) iterator.next();

            if (advancement.getCriteria().isEmpty()) {
                this.grantCriteria(advancement, "");
                advancement.d().a(this.player);
            }
        }

    }

    private void g() {
        if (this.e.isFile()) {
            try {
                String s = Files.toString(this.e, StandardCharsets.UTF_8);
                Map map = (Map) ChatDeserializer.a(AdvancementDataPlayer.b, s, AdvancementDataPlayer.c.getType());

                if (map == null) {
                    throw new JsonParseException("Found null for advancements");
                }

                Stream stream = map.entrySet().stream().sorted(Comparator.comparing(Entry::getValue));
                Iterator iterator = ((List) stream.collect(Collectors.toList())).iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry) iterator.next();
                    Advancement advancement = this.d.getAdvancementData().a((MinecraftKey) entry.getKey());

                    if (advancement == null) {
                        AdvancementDataPlayer.a.warn("Ignored advancement \'" + entry.getKey() + "\' in progress file " + this.e + " - it doesn\'t exist anymore?");
                    } else {
                        this.a(advancement, (AdvancementProgress) entry.getValue());
                    }
                }
            } catch (JsonParseException jsonparseexception) {
                AdvancementDataPlayer.a.error("Couldn\'t parse player advancements in " + this.e, jsonparseexception);
            } catch (IOException ioexception) {
                AdvancementDataPlayer.a.error("Couldn\'t access player advancements in " + this.e, ioexception);
            }
        }

        this.f();
        this.e();
        this.d();
    }

    public void c() {
        HashMap hashmap = Maps.newHashMap();
        Iterator iterator = this.data.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            AdvancementProgress advancementprogress = (AdvancementProgress) entry.getValue();

            if (advancementprogress.b()) {
                hashmap.put(((Advancement) entry.getKey()).getName(), advancementprogress);
            }
        }

        if (this.e.getParentFile() != null) {
            this.e.getParentFile().mkdirs();
        }

        try {
            Files.write(AdvancementDataPlayer.b.toJson(hashmap), this.e, StandardCharsets.UTF_8);
        } catch (IOException ioexception) {
            AdvancementDataPlayer.a.error("Couldn\'t save player advancements to " + this.e, ioexception);
        }

    }

    public boolean grantCriteria(Advancement advancement, String s) {
        boolean flag = false;
        AdvancementProgress advancementprogress = this.getProgress(advancement);
        boolean flag1 = advancementprogress.isDone();

        if (advancementprogress.a(s)) {
            this.d(advancement);
            this.i.add(advancement);
            flag = true;
            if (!flag1 && advancementprogress.isDone()) {
                advancement.d().a(this.player);
                if (advancement.c() != null && advancement.c().i() && this.player.world.getGameRules().getBoolean("announceAdvancements")) {
                    this.d.getPlayerList().sendMessage(new ChatMessage("chat.type.advancement." + advancement.c().e().a(), new Object[] { this.player.getScoreboardDisplayName(), advancement.j()}));
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
            this.i.add(advancement);
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
                Entry entry = (Entry) iterator.next();
                CriterionProgress criterionprogress = advancementprogress.getCriterionProgress((String) entry.getKey());

                if (criterionprogress != null && !criterionprogress.a()) {
                    CriterionInstance criterioninstance = ((Criterion) entry.getValue()).a();

                    if (criterioninstance != null) {
                        CriterionTrigger criteriontrigger = CriterionTriggers.a(criterioninstance.a());

                        if (criteriontrigger != null) {
                            criteriontrigger.a(this, new CriterionTrigger.a(criterioninstance, advancement, (String) entry.getKey()));
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
            Entry entry = (Entry) iterator.next();
            CriterionProgress criterionprogress = advancementprogress.getCriterionProgress((String) entry.getKey());

            if (criterionprogress != null && (criterionprogress.a() || advancementprogress.isDone())) {
                CriterionInstance criterioninstance = ((Criterion) entry.getValue()).a();

                if (criterioninstance != null) {
                    CriterionTrigger criteriontrigger = CriterionTriggers.a(criterioninstance.a());

                    if (criteriontrigger != null) {
                        criteriontrigger.b(this, new CriterionTrigger.a(criterioninstance, advancement, (String) entry.getKey()));
                    }
                }
            }
        }

    }

    public void b(EntityPlayer entityplayer) {
        if (!this.h.isEmpty() || !this.i.isEmpty()) {
            HashMap hashmap = Maps.newHashMap();
            LinkedHashSet linkedhashset = Sets.newLinkedHashSet();
            LinkedHashSet linkedhashset1 = Sets.newLinkedHashSet();
            Iterator iterator = this.i.iterator();

            Advancement advancement;

            while (iterator.hasNext()) {
                advancement = (Advancement) iterator.next();
                if (this.g.contains(advancement)) {
                    hashmap.put(advancement.getName(), this.data.get(advancement));
                }
            }

            iterator = this.h.iterator();

            while (iterator.hasNext()) {
                advancement = (Advancement) iterator.next();
                if (this.g.contains(advancement)) {
                    linkedhashset.add(advancement);
                } else {
                    linkedhashset1.add(advancement.getName());
                }
            }

            if (!hashmap.isEmpty() || !linkedhashset.isEmpty() || !linkedhashset1.isEmpty()) {
                entityplayer.playerConnection.sendPacket(new PacketPlayOutAdvancements(this.l, linkedhashset, linkedhashset1, hashmap));
                this.h.clear();
                this.i.clear();
            }
        }

        this.l = false;
    }

    public void a(@Nullable Advancement advancement) {
        Advancement advancement1 = this.k;

        if (advancement != null && advancement.b() == null && advancement.c() != null) {
            this.k = advancement;
        } else {
            this.k = null;
        }

        if (advancement1 != this.k) {
            this.player.playerConnection.sendPacket(new PacketPlayOutSelectAdvancementTab(this.k == null ? null : this.k.getName()));
        }

    }

    public AdvancementProgress getProgress(Advancement advancement) {
        AdvancementProgress advancementprogress = (AdvancementProgress) this.data.get(advancement);

        if (advancementprogress == null) {
            advancementprogress = new AdvancementProgress();
            this.a(advancement, advancementprogress);
        }

        return advancementprogress;
    }

    private void a(Advancement advancement, AdvancementProgress advancementprogress) {
        advancementprogress.a(advancement.getCriteria(), advancement.i());
        this.data.put(advancement, advancementprogress);
    }

    private void e(Advancement advancement) {
        boolean flag = this.f(advancement);
        boolean flag1 = this.g.contains(advancement);

        if (flag && !flag1) {
            this.g.add(advancement);
            this.h.add(advancement);
            if (this.data.containsKey(advancement)) {
                this.i.add(advancement);
            }
        } else if (!flag && flag1) {
            this.g.remove(advancement);
            this.h.add(advancement);
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
