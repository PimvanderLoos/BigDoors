package net.minecraft.server.level;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.BlockUtil;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportSystemDetails;
import net.minecraft.EnumChatFormat;
import net.minecraft.ReportedException;
import net.minecraft.SystemUtils;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.commands.arguments.ArgumentAnchor;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.NonNullList;
import net.minecraft.core.SectionPosition;
import net.minecraft.nbt.DynamicOpsNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatHoverable;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.chat.IChatMutableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.PacketPlayInSettings;
import net.minecraft.network.protocol.game.PacketPlayOutAbilities;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutCamera;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutExperience;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutLookAt;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.network.protocol.game.PacketPlayOutOpenBook;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindowHorse;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindowMerchant;
import net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutResourcePackSend;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.network.protocol.game.PacketPlayOutServerDifficulty;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth;
import net.minecraft.network.protocol.game.PacketPlayOutWindowData;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;
import net.minecraft.network.protocol.game.PacketPlayOutWorldEvent;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ITextFilter;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundCategory;
import net.minecraft.sounds.SoundEffect;
import net.minecraft.stats.RecipeBookServer;
import net.minecraft.stats.ServerStatisticManager;
import net.minecraft.stats.Statistic;
import net.minecraft.stats.StatisticList;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Unit;
import net.minecraft.world.EnumHand;
import net.minecraft.world.IInventory;
import net.minecraft.world.ITileInventory;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.IEntityAngerable;
import net.minecraft.world.entity.IEntitySelector;
import net.minecraft.world.entity.animal.horse.EntityHorseAbstract;
import net.minecraft.world.entity.item.EntityItem;
import net.minecraft.world.entity.monster.EntityMonster;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerHorse;
import net.minecraft.world.inventory.ContainerSynchronizer;
import net.minecraft.world.inventory.ICrafting;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SlotResult;
import net.minecraft.world.item.ItemCooldown;
import net.minecraft.world.item.ItemCooldownPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemWorldMapBase;
import net.minecraft.world.item.ItemWrittenBook;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.IRecipe;
import net.minecraft.world.item.trading.MerchantRecipeList;
import net.minecraft.world.level.ChunkCoordIntPair;
import net.minecraft.world.level.EnumGamemode;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.BlockFacingHorizontal;
import net.minecraft.world.level.block.BlockPortal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityCommand;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.ShapeDetectorShape;
import net.minecraft.world.level.storage.WorldData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.ScoreboardScore;
import net.minecraft.world.scores.ScoreboardTeam;
import net.minecraft.world.scores.ScoreboardTeamBase;
import net.minecraft.world.scores.criteria.IScoreboardCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityPlayer extends EntityHuman {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_XZ = 32;
    private static final int NEUTRAL_MOB_DEATH_NOTIFICATION_RADII_Y = 10;
    public PlayerConnection connection;
    public final MinecraftServer server;
    public final PlayerInteractManager gameMode;
    private final AdvancementDataPlayer advancements;
    private final ServerStatisticManager stats;
    private float lastRecordedHealthAndAbsorption = Float.MIN_VALUE;
    private int lastRecordedFoodLevel = Integer.MIN_VALUE;
    private int lastRecordedAirLevel = Integer.MIN_VALUE;
    private int lastRecordedArmor = Integer.MIN_VALUE;
    private int lastRecordedLevel = Integer.MIN_VALUE;
    private int lastRecordedExperience = Integer.MIN_VALUE;
    private float lastSentHealth = -1.0E8F;
    private int lastSentFood = -99999999;
    private boolean lastFoodSaturationZero = true;
    public int lastSentExp = -99999999;
    public int spawnInvulnerableTime = 60;
    private EnumChatVisibility chatVisibility;
    private boolean canChatColor;
    private long lastActionTime;
    @Nullable
    private Entity camera;
    public boolean isChangingDimension;
    private boolean seenCredits;
    private final RecipeBookServer recipeBook;
    @Nullable
    private Vec3D levitationStartPos;
    private int levitationStartTime;
    private boolean disconnected;
    @Nullable
    private Vec3D startingToFallPosition;
    @Nullable
    private Vec3D enteredNetherPosition;
    @Nullable
    private Vec3D enteredLavaOnVehiclePosition;
    private SectionPosition lastSectionPos;
    private ResourceKey<World> respawnDimension;
    @Nullable
    private BlockPosition respawnPosition;
    private boolean respawnForced;
    private float respawnAngle;
    private final ITextFilter textFilter;
    private boolean textFilteringEnabled;
    private boolean allowsListing;
    private final ContainerSynchronizer containerSynchronizer;
    private final ICrafting containerListener;
    private int containerCounter;
    public int latency;
    public boolean wonGame;

    public EntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile) {
        super(worldserver, worldserver.getSharedSpawnPos(), worldserver.getSharedSpawnAngle(), gameprofile);
        this.chatVisibility = EnumChatVisibility.FULL;
        this.canChatColor = true;
        this.lastActionTime = SystemUtils.getMillis();
        this.recipeBook = new RecipeBookServer();
        this.lastSectionPos = SectionPosition.of(0, 0, 0);
        this.respawnDimension = World.OVERWORLD;
        this.allowsListing = true;
        this.containerSynchronizer = new ContainerSynchronizer() {
            @Override
            public void sendInitialData(Container container, NonNullList<ItemStack> nonnulllist, ItemStack itemstack, int[] aint) {
                EntityPlayer.this.connection.send(new PacketPlayOutWindowItems(container.containerId, container.incrementStateId(), nonnulllist, itemstack));

                for (int i = 0; i < aint.length; ++i) {
                    this.broadcastDataValue(container, i, aint[i]);
                }

            }

            @Override
            public void sendSlotChange(Container container, int i, ItemStack itemstack) {
                EntityPlayer.this.connection.send(new PacketPlayOutSetSlot(container.containerId, container.incrementStateId(), i, itemstack));
            }

            @Override
            public void sendCarriedChange(Container container, ItemStack itemstack) {
                EntityPlayer.this.connection.send(new PacketPlayOutSetSlot(-1, container.incrementStateId(), -1, itemstack));
            }

            @Override
            public void sendDataChange(Container container, int i, int j) {
                this.broadcastDataValue(container, i, j);
            }

            private void broadcastDataValue(Container container, int i, int j) {
                EntityPlayer.this.connection.send(new PacketPlayOutWindowData(container.containerId, i, j));
            }
        };
        this.containerListener = new ICrafting() {
            @Override
            public void slotChanged(Container container, int i, ItemStack itemstack) {
                Slot slot = container.getSlot(i);

                if (!(slot instanceof SlotResult)) {
                    if (slot.container == EntityPlayer.this.getInventory()) {
                        CriterionTriggers.INVENTORY_CHANGED.trigger(EntityPlayer.this, EntityPlayer.this.getInventory(), itemstack);
                    }

                }
            }

            @Override
            public void dataChanged(Container container, int i, int j) {}
        };
        this.textFilter = minecraftserver.createTextFilterForPlayer(this);
        this.gameMode = minecraftserver.createGameModeForPlayer(this);
        this.server = minecraftserver;
        this.stats = minecraftserver.getPlayerList().getPlayerStats(this);
        this.advancements = minecraftserver.getPlayerList().getPlayerAdvancements(this);
        this.maxUpStep = 1.0F;
        this.fudgeSpawnLocation(worldserver);
    }

    private void fudgeSpawnLocation(WorldServer worldserver) {
        BlockPosition blockposition = worldserver.getSharedSpawnPos();

        if (worldserver.dimensionType().hasSkyLight() && worldserver.getServer().getWorldData().getGameType() != EnumGamemode.ADVENTURE) {
            int i = Math.max(0, this.server.getSpawnRadius(worldserver));
            int j = MathHelper.floor(worldserver.getWorldBorder().getDistanceToBorder((double) blockposition.getX(), (double) blockposition.getZ()));

            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            long k = (long) (i * 2 + 1);
            long l = k * k;
            int i1 = l > 2147483647L ? Integer.MAX_VALUE : (int) l;
            int j1 = this.getCoprime(i1);
            int k1 = (new Random()).nextInt(i1);

            for (int l1 = 0; l1 < i1; ++l1) {
                int i2 = (k1 + j1 * l1) % i1;
                int j2 = i2 % (i * 2 + 1);
                int k2 = i2 / (i * 2 + 1);
                BlockPosition blockposition1 = WorldProviderNormal.getOverworldRespawnPos(worldserver, blockposition.getX() + j2 - i, blockposition.getZ() + k2 - i);

                if (blockposition1 != null) {
                    this.moveTo(blockposition1, 0.0F, 0.0F);
                    if (worldserver.noCollision((Entity) this)) {
                        break;
                    }
                }
            }
        } else {
            this.moveTo(blockposition, 0.0F, 0.0F);

            while (!worldserver.noCollision((Entity) this) && this.getY() < (double) (worldserver.getMaxBuildHeight() - 1)) {
                this.setPos(this.getX(), this.getY() + 1.0D, this.getZ());
            }
        }

    }

    private int getCoprime(int i) {
        return i <= 16 ? i - 1 : 17;
    }

    @Override
    public void readAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.readAdditionalSaveData(nbttagcompound);
        if (nbttagcompound.contains("enteredNetherPosition", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("enteredNetherPosition");

            this.enteredNetherPosition = new Vec3D(nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("y"), nbttagcompound1.getDouble("z"));
        }

        this.seenCredits = nbttagcompound.getBoolean("seenCredits");
        if (nbttagcompound.contains("recipeBook", 10)) {
            this.recipeBook.fromNbt(nbttagcompound.getCompound("recipeBook"), this.server.getRecipeManager());
        }

        if (this.isSleeping()) {
            this.stopSleeping();
        }

        if (nbttagcompound.contains("SpawnX", 99) && nbttagcompound.contains("SpawnY", 99) && nbttagcompound.contains("SpawnZ", 99)) {
            this.respawnPosition = new BlockPosition(nbttagcompound.getInt("SpawnX"), nbttagcompound.getInt("SpawnY"), nbttagcompound.getInt("SpawnZ"));
            this.respawnForced = nbttagcompound.getBoolean("SpawnForced");
            this.respawnAngle = nbttagcompound.getFloat("SpawnAngle");
            if (nbttagcompound.contains("SpawnDimension")) {
                DataResult dataresult = World.RESOURCE_KEY_CODEC.parse(DynamicOpsNBT.INSTANCE, nbttagcompound.get("SpawnDimension"));
                Logger logger = EntityPlayer.LOGGER;

                Objects.requireNonNull(logger);
                this.respawnDimension = (ResourceKey) dataresult.resultOrPartial(logger::error).orElse(World.OVERWORLD);
            }
        }

    }

    @Override
    public void addAdditionalSaveData(NBTTagCompound nbttagcompound) {
        super.addAdditionalSaveData(nbttagcompound);
        this.storeGameTypes(nbttagcompound);
        nbttagcompound.putBoolean("seenCredits", this.seenCredits);
        if (this.enteredNetherPosition != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.putDouble("x", this.enteredNetherPosition.x);
            nbttagcompound1.putDouble("y", this.enteredNetherPosition.y);
            nbttagcompound1.putDouble("z", this.enteredNetherPosition.z);
            nbttagcompound.put("enteredNetherPosition", nbttagcompound1);
        }

        Entity entity = this.getRootVehicle();
        Entity entity1 = this.getVehicle();

        if (entity1 != null && entity != this && entity.hasExactlyOnePlayerPassenger()) {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();

            entity.save(nbttagcompound3);
            nbttagcompound2.putUUID("Attach", entity1.getUUID());
            nbttagcompound2.put("Entity", nbttagcompound3);
            nbttagcompound.put("RootVehicle", nbttagcompound2);
        }

        nbttagcompound.put("recipeBook", this.recipeBook.toNbt());
        nbttagcompound.putString("Dimension", this.level.dimension().location().toString());
        if (this.respawnPosition != null) {
            nbttagcompound.putInt("SpawnX", this.respawnPosition.getX());
            nbttagcompound.putInt("SpawnY", this.respawnPosition.getY());
            nbttagcompound.putInt("SpawnZ", this.respawnPosition.getZ());
            nbttagcompound.putBoolean("SpawnForced", this.respawnForced);
            nbttagcompound.putFloat("SpawnAngle", this.respawnAngle);
            DataResult dataresult = MinecraftKey.CODEC.encodeStart(DynamicOpsNBT.INSTANCE, this.respawnDimension.location());
            Logger logger = EntityPlayer.LOGGER;

            Objects.requireNonNull(logger);
            dataresult.resultOrPartial(logger::error).ifPresent((nbtbase) -> {
                nbttagcompound.put("SpawnDimension", nbtbase);
            });
        }

    }

    public void setExperiencePoints(int i) {
        float f = (float) this.getXpNeededForNextLevel();
        float f1 = (f - 1.0F) / f;

        this.experienceProgress = MathHelper.clamp((float) i / f, 0.0F, f1);
        this.lastSentExp = -1;
    }

    public void setExperienceLevels(int i) {
        this.experienceLevel = i;
        this.lastSentExp = -1;
    }

    @Override
    public void giveExperienceLevels(int i) {
        super.giveExperienceLevels(i);
        this.lastSentExp = -1;
    }

    @Override
    public void onEnchantmentPerformed(ItemStack itemstack, int i) {
        super.onEnchantmentPerformed(itemstack, i);
        this.lastSentExp = -1;
    }

    public void initMenu(Container container) {
        container.addSlotListener(this.containerListener);
        container.setSynchronizer(this.containerSynchronizer);
    }

    public void initInventoryMenu() {
        this.initMenu(this.inventoryMenu);
    }

    @Override
    public void onEnterCombat() {
        super.onEnterCombat();
        this.connection.send(new ClientboundPlayerCombatEnterPacket());
    }

    @Override
    public void onLeaveCombat() {
        super.onLeaveCombat();
        this.connection.send(new ClientboundPlayerCombatEndPacket(this.getCombatTracker()));
    }

    @Override
    protected void onInsideBlock(IBlockData iblockdata) {
        CriterionTriggers.ENTER_BLOCK.trigger(this, iblockdata);
    }

    @Override
    protected ItemCooldown createItemCooldowns() {
        return new ItemCooldownPlayer(this);
    }

    @Override
    public void tick() {
        this.gameMode.tick();
        --this.spawnInvulnerableTime;
        if (this.invulnerableTime > 0) {
            --this.invulnerableTime;
        }

        this.containerMenu.broadcastChanges();
        if (!this.level.isClientSide && !this.containerMenu.stillValid(this)) {
            this.closeContainer();
            this.containerMenu = this.inventoryMenu;
        }

        Entity entity = this.getCamera();

        if (entity != this) {
            if (entity.isAlive()) {
                this.absMoveTo(entity.getX(), entity.getY(), entity.getZ(), entity.getYRot(), entity.getXRot());
                this.getLevel().getChunkSource().move(this);
                if (this.wantsToStopRiding()) {
                    this.setCamera(this);
                }
            } else {
                this.setCamera(this);
            }
        }

        CriterionTriggers.TICK.trigger(this);
        if (this.levitationStartPos != null) {
            CriterionTriggers.LEVITATION.trigger(this, this.levitationStartPos, this.tickCount - this.levitationStartTime);
        }

        this.trackStartFallingPosition();
        this.trackEnteredOrExitedLavaOnVehicle();
        this.advancements.flushDirty(this);
    }

    public void doTick() {
        try {
            if (!this.isSpectator() || !this.touchingUnloadedChunk()) {
                super.tick();
            }

            for (int i = 0; i < this.getInventory().getContainerSize(); ++i) {
                ItemStack itemstack = this.getInventory().getItem(i);

                if (itemstack.getItem().isComplex()) {
                    Packet<?> packet = ((ItemWorldMapBase) itemstack.getItem()).getUpdatePacket(itemstack, this.level, this);

                    if (packet != null) {
                        this.connection.send(packet);
                    }
                }
            }

            if (this.getHealth() != this.lastSentHealth || this.lastSentFood != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.lastFoodSaturationZero) {
                this.connection.send(new PacketPlayOutUpdateHealth(this.getHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel()));
                this.lastSentHealth = this.getHealth();
                this.lastSentFood = this.foodData.getFoodLevel();
                this.lastFoodSaturationZero = this.foodData.getSaturationLevel() == 0.0F;
            }

            if (this.getHealth() + this.getAbsorptionAmount() != this.lastRecordedHealthAndAbsorption) {
                this.lastRecordedHealthAndAbsorption = this.getHealth() + this.getAbsorptionAmount();
                this.updateScoreForCriteria(IScoreboardCriteria.HEALTH, MathHelper.ceil(this.lastRecordedHealthAndAbsorption));
            }

            if (this.foodData.getFoodLevel() != this.lastRecordedFoodLevel) {
                this.lastRecordedFoodLevel = this.foodData.getFoodLevel();
                this.updateScoreForCriteria(IScoreboardCriteria.FOOD, MathHelper.ceil((float) this.lastRecordedFoodLevel));
            }

            if (this.getAirSupply() != this.lastRecordedAirLevel) {
                this.lastRecordedAirLevel = this.getAirSupply();
                this.updateScoreForCriteria(IScoreboardCriteria.AIR, MathHelper.ceil((float) this.lastRecordedAirLevel));
            }

            if (this.getArmorValue() != this.lastRecordedArmor) {
                this.lastRecordedArmor = this.getArmorValue();
                this.updateScoreForCriteria(IScoreboardCriteria.ARMOR, MathHelper.ceil((float) this.lastRecordedArmor));
            }

            if (this.totalExperience != this.lastRecordedExperience) {
                this.lastRecordedExperience = this.totalExperience;
                this.updateScoreForCriteria(IScoreboardCriteria.EXPERIENCE, MathHelper.ceil((float) this.lastRecordedExperience));
            }

            if (this.experienceLevel != this.lastRecordedLevel) {
                this.lastRecordedLevel = this.experienceLevel;
                this.updateScoreForCriteria(IScoreboardCriteria.LEVEL, MathHelper.ceil((float) this.lastRecordedLevel));
            }

            if (this.totalExperience != this.lastSentExp) {
                this.lastSentExp = this.totalExperience;
                this.connection.send(new PacketPlayOutExperience(this.experienceProgress, this.totalExperience, this.experienceLevel));
            }

            if (this.tickCount % 20 == 0) {
                CriterionTriggers.LOCATION.trigger(this);
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Ticking player");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.addCategory("Player being ticked");

            this.fillCrashReportCategory(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    @Override
    public void resetFallDistance() {
        if (this.getHealth() > 0.0F && this.startingToFallPosition != null) {
            CriterionTriggers.FALL_FROM_HEIGHT.trigger(this, this.startingToFallPosition);
        }

        this.startingToFallPosition = null;
        super.resetFallDistance();
    }

    public void trackStartFallingPosition() {
        if (this.fallDistance > 0.0F && this.startingToFallPosition == null) {
            this.startingToFallPosition = this.position();
        }

    }

    public void trackEnteredOrExitedLavaOnVehicle() {
        if (this.getVehicle() != null && this.getVehicle().isInLava()) {
            if (this.enteredLavaOnVehiclePosition == null) {
                this.enteredLavaOnVehiclePosition = this.position();
            } else {
                CriterionTriggers.RIDE_ENTITY_IN_LAVA_TRIGGER.trigger(this, this.enteredLavaOnVehiclePosition);
            }
        }

        if (this.enteredLavaOnVehiclePosition != null && (this.getVehicle() == null || !this.getVehicle().isInLava())) {
            this.enteredLavaOnVehiclePosition = null;
        }

    }

    private void updateScoreForCriteria(IScoreboardCriteria iscoreboardcriteria, int i) {
        this.getScoreboard().forAllObjectives(iscoreboardcriteria, this.getScoreboardName(), (scoreboardscore) -> {
            scoreboardscore.setScore(i);
        });
    }

    @Override
    public void die(DamageSource damagesource) {
        boolean flag = this.level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES);

        if (flag) {
            IChatBaseComponent ichatbasecomponent = this.getCombatTracker().getDeathMessage();

            this.connection.send(new ClientboundPlayerCombatKillPacket(this.getCombatTracker(), ichatbasecomponent), (future) -> {
                if (!future.isSuccess()) {
                    boolean flag1 = true;
                    String s = ichatbasecomponent.getString(256);
                    ChatMessage chatmessage = new ChatMessage("death.attack.message_too_long", new Object[]{(new ChatComponentText(s)).withStyle(EnumChatFormat.YELLOW)});
                    IChatMutableComponent ichatmutablecomponent = (new ChatMessage("death.attack.even_more_magic", new Object[]{this.getDisplayName()})).withStyle((chatmodifier) -> {
                        return chatmodifier.withHoverEvent(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, chatmessage));
                    });

                    this.connection.send(new ClientboundPlayerCombatKillPacket(this.getCombatTracker(), ichatmutablecomponent));
                }

            });
            ScoreboardTeamBase scoreboardteambase = this.getTeam();

            if (scoreboardteambase != null && scoreboardteambase.getDeathMessageVisibility() != ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS) {
                if (scoreboardteambase.getDeathMessageVisibility() == ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS) {
                    this.server.getPlayerList().broadcastToTeam(this, ichatbasecomponent);
                } else if (scoreboardteambase.getDeathMessageVisibility() == ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM) {
                    this.server.getPlayerList().broadcastToAllExceptTeam(this, ichatbasecomponent);
                }
            } else {
                this.server.getPlayerList().broadcastMessage(ichatbasecomponent, ChatMessageType.SYSTEM, SystemUtils.NIL_UUID);
            }
        } else {
            this.connection.send(new ClientboundPlayerCombatKillPacket(this.getCombatTracker(), ChatComponentText.EMPTY));
        }

        this.removeEntitiesOnShoulder();
        if (this.level.getGameRules().getBoolean(GameRules.RULE_FORGIVE_DEAD_PLAYERS)) {
            this.tellNeutralMobsThatIDied();
        }

        if (!this.isSpectator()) {
            this.dropAllDeathLoot(damagesource);
        }

        this.getScoreboard().forAllObjectives(IScoreboardCriteria.DEATH_COUNT, this.getScoreboardName(), ScoreboardScore::increment);
        EntityLiving entityliving = this.getKillCredit();

        if (entityliving != null) {
            this.awardStat(StatisticList.ENTITY_KILLED_BY.get(entityliving.getType()));
            entityliving.awardKillScore(this, this.deathScore, damagesource);
            this.createWitherRose(entityliving);
        }

        this.level.broadcastEntityEvent(this, (byte) 3);
        this.awardStat(StatisticList.DEATHS);
        this.resetStat(StatisticList.CUSTOM.get(StatisticList.TIME_SINCE_DEATH));
        this.resetStat(StatisticList.CUSTOM.get(StatisticList.TIME_SINCE_REST));
        this.clearFire();
        this.setTicksFrozen(0);
        this.setSharedFlagOnFire(false);
        this.getCombatTracker().recheckStatus();
    }

    private void tellNeutralMobsThatIDied() {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(this.blockPosition())).inflate(32.0D, 10.0D, 32.0D);

        this.level.getEntitiesOfClass(EntityInsentient.class, axisalignedbb, IEntitySelector.NO_SPECTATORS).stream().filter((entityinsentient) -> {
            return entityinsentient instanceof IEntityAngerable;
        }).forEach((entityinsentient) -> {
            ((IEntityAngerable) entityinsentient).playerDied(this);
        });
    }

    @Override
    public void awardKillScore(Entity entity, int i, DamageSource damagesource) {
        if (entity != this) {
            super.awardKillScore(entity, i, damagesource);
            this.increaseScore(i);
            String s = this.getScoreboardName();
            String s1 = entity.getScoreboardName();

            this.getScoreboard().forAllObjectives(IScoreboardCriteria.KILL_COUNT_ALL, s, ScoreboardScore::increment);
            if (entity instanceof EntityHuman) {
                this.awardStat(StatisticList.PLAYER_KILLS);
                this.getScoreboard().forAllObjectives(IScoreboardCriteria.KILL_COUNT_PLAYERS, s, ScoreboardScore::increment);
            } else {
                this.awardStat(StatisticList.MOB_KILLS);
            }

            this.handleTeamKill(s, s1, IScoreboardCriteria.TEAM_KILL);
            this.handleTeamKill(s1, s, IScoreboardCriteria.KILLED_BY_TEAM);
            CriterionTriggers.PLAYER_KILLED_ENTITY.trigger(this, entity, damagesource);
        }
    }

    private void handleTeamKill(String s, String s1, IScoreboardCriteria[] aiscoreboardcriteria) {
        ScoreboardTeam scoreboardteam = this.getScoreboard().getPlayersTeam(s1);

        if (scoreboardteam != null) {
            int i = scoreboardteam.getColor().getId();

            if (i >= 0 && i < aiscoreboardcriteria.length) {
                this.getScoreboard().forAllObjectives(aiscoreboardcriteria[i], s, ScoreboardScore::increment);
            }
        }

    }

    @Override
    public boolean hurt(DamageSource damagesource, float f) {
        if (this.isInvulnerableTo(damagesource)) {
            return false;
        } else {
            boolean flag = this.server.isDedicatedServer() && this.isPvpAllowed() && "fall".equals(damagesource.msgId);

            if (!flag && this.spawnInvulnerableTime > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (damagesource instanceof EntityDamageSource) {
                    Entity entity = damagesource.getEntity();

                    if (entity instanceof EntityHuman && !this.canHarmPlayer((EntityHuman) entity)) {
                        return false;
                    }

                    if (entity instanceof EntityArrow) {
                        EntityArrow entityarrow = (EntityArrow) entity;
                        Entity entity1 = entityarrow.getOwner();

                        if (entity1 instanceof EntityHuman && !this.canHarmPlayer((EntityHuman) entity1)) {
                            return false;
                        }
                    }
                }

                return super.hurt(damagesource, f);
            }
        }
    }

    @Override
    public boolean canHarmPlayer(EntityHuman entityhuman) {
        return !this.isPvpAllowed() ? false : super.canHarmPlayer(entityhuman);
    }

    private boolean isPvpAllowed() {
        return this.server.isPvpAllowed();
    }

    @Nullable
    @Override
    protected ShapeDetectorShape findDimensionEntryPoint(WorldServer worldserver) {
        ShapeDetectorShape shapedetectorshape = super.findDimensionEntryPoint(worldserver);

        if (shapedetectorshape != null && this.level.dimension() == World.OVERWORLD && worldserver.dimension() == World.END) {
            Vec3D vec3d = shapedetectorshape.pos.add(0.0D, -1.0D, 0.0D);

            return new ShapeDetectorShape(vec3d, Vec3D.ZERO, 90.0F, 0.0F);
        } else {
            return shapedetectorshape;
        }
    }

    @Nullable
    @Override
    public Entity changeDimension(WorldServer worldserver) {
        this.isChangingDimension = true;
        WorldServer worldserver1 = this.getLevel();
        ResourceKey<World> resourcekey = worldserver1.dimension();

        if (resourcekey == World.END && worldserver.dimension() == World.OVERWORLD) {
            this.unRide();
            this.getLevel().removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
            if (!this.wonGame) {
                this.wonGame = true;
                this.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.WIN_GAME, this.seenCredits ? 0.0F : 1.0F));
                this.seenCredits = true;
            }

            return this;
        } else {
            WorldData worlddata = worldserver.getLevelData();

            this.connection.send(new PacketPlayOutRespawn(worldserver.dimensionType(), worldserver.dimension(), BiomeManager.obfuscateSeed(worldserver.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), worldserver.isDebug(), worldserver.isFlat(), true));
            this.connection.send(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
            PlayerList playerlist = this.server.getPlayerList();

            playerlist.sendPlayerPermissionLevel(this);
            worldserver1.removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
            this.unsetRemoved();
            ShapeDetectorShape shapedetectorshape = this.findDimensionEntryPoint(worldserver);

            if (shapedetectorshape != null) {
                worldserver1.getProfiler().push("moving");
                if (resourcekey == World.OVERWORLD && worldserver.dimension() == World.NETHER) {
                    this.enteredNetherPosition = this.position();
                } else if (worldserver.dimension() == World.END) {
                    this.createEndPlatform(worldserver, new BlockPosition(shapedetectorshape.pos));
                }

                worldserver1.getProfiler().pop();
                worldserver1.getProfiler().push("placing");
                this.setLevel(worldserver);
                worldserver.addDuringPortalTeleport(this);
                this.setRot(shapedetectorshape.yRot, shapedetectorshape.xRot);
                this.moveTo(shapedetectorshape.pos.x, shapedetectorshape.pos.y, shapedetectorshape.pos.z);
                worldserver1.getProfiler().pop();
                this.triggerDimensionChangeTriggers(worldserver1);
                this.connection.send(new PacketPlayOutAbilities(this.getAbilities()));
                playerlist.sendLevelInfo(this, worldserver);
                playerlist.sendAllPlayerInfo(this);
                Iterator iterator = this.getActiveEffects().iterator();

                while (iterator.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator.next();

                    this.connection.send(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
                }

                this.connection.send(new PacketPlayOutWorldEvent(1032, BlockPosition.ZERO, 0, false));
                this.lastSentExp = -1;
                this.lastSentHealth = -1.0F;
                this.lastSentFood = -1;
            }

            return this;
        }
    }

    private void createEndPlatform(WorldServer worldserver, BlockPosition blockposition) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = blockposition.mutable();

        for (int i = -2; i <= 2; ++i) {
            for (int j = -2; j <= 2; ++j) {
                for (int k = -1; k < 3; ++k) {
                    IBlockData iblockdata = k == -1 ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.AIR.defaultBlockState();

                    worldserver.setBlockAndUpdate(blockposition_mutableblockposition.set(blockposition).move(j, k, i), iblockdata);
                }
            }
        }

    }

    @Override
    protected Optional<BlockUtil.Rectangle> getExitPortal(WorldServer worldserver, BlockPosition blockposition, boolean flag, WorldBorder worldborder) {
        Optional<BlockUtil.Rectangle> optional = super.getExitPortal(worldserver, blockposition, flag, worldborder);

        if (optional.isPresent()) {
            return optional;
        } else {
            EnumDirection.EnumAxis enumdirection_enumaxis = (EnumDirection.EnumAxis) this.level.getBlockState(this.portalEntrancePos).getOptionalValue(BlockPortal.AXIS).orElse(EnumDirection.EnumAxis.X);
            Optional<BlockUtil.Rectangle> optional1 = worldserver.getPortalForcer().createPortal(blockposition, enumdirection_enumaxis);

            if (!optional1.isPresent()) {
                EntityPlayer.LOGGER.error("Unable to create a portal, likely target out of worldborder");
            }

            return optional1;
        }
    }

    public void triggerDimensionChangeTriggers(WorldServer worldserver) {
        ResourceKey<World> resourcekey = worldserver.dimension();
        ResourceKey<World> resourcekey1 = this.level.dimension();

        CriterionTriggers.CHANGED_DIMENSION.trigger(this, resourcekey, resourcekey1);
        if (resourcekey == World.NETHER && resourcekey1 == World.OVERWORLD && this.enteredNetherPosition != null) {
            CriterionTriggers.NETHER_TRAVEL.trigger(this, this.enteredNetherPosition);
        }

        if (resourcekey1 != World.NETHER) {
            this.enteredNetherPosition = null;
        }

    }

    @Override
    public boolean broadcastToPlayer(EntityPlayer entityplayer) {
        return entityplayer.isSpectator() ? this.getCamera() == this : (this.isSpectator() ? false : super.broadcastToPlayer(entityplayer));
    }

    @Override
    public void take(Entity entity, int i) {
        super.take(entity, i);
        this.containerMenu.broadcastChanges();
    }

    @Override
    public Either<EntityHuman.EnumBedResult, Unit> startSleepInBed(BlockPosition blockposition) {
        EnumDirection enumdirection = (EnumDirection) this.level.getBlockState(blockposition).getValue(BlockFacingHorizontal.FACING);

        if (!this.isSleeping() && this.isAlive()) {
            if (!this.level.dimensionType().natural()) {
                return Either.left(EntityHuman.EnumBedResult.NOT_POSSIBLE_HERE);
            } else if (!this.bedInRange(blockposition, enumdirection)) {
                return Either.left(EntityHuman.EnumBedResult.TOO_FAR_AWAY);
            } else if (this.bedBlocked(blockposition, enumdirection)) {
                return Either.left(EntityHuman.EnumBedResult.OBSTRUCTED);
            } else {
                this.setRespawnPosition(this.level.dimension(), blockposition, this.getYRot(), false, true);
                if (this.level.isDay()) {
                    return Either.left(EntityHuman.EnumBedResult.NOT_POSSIBLE_NOW);
                } else {
                    if (!this.isCreative()) {
                        double d0 = 8.0D;
                        double d1 = 5.0D;
                        Vec3D vec3d = Vec3D.atBottomCenterOf(blockposition);
                        List<EntityMonster> list = this.level.getEntitiesOfClass(EntityMonster.class, new AxisAlignedBB(vec3d.x() - 8.0D, vec3d.y() - 5.0D, vec3d.z() - 8.0D, vec3d.x() + 8.0D, vec3d.y() + 5.0D, vec3d.z() + 8.0D), (entitymonster) -> {
                            return entitymonster.isPreventingPlayerRest(this);
                        });

                        if (!list.isEmpty()) {
                            return Either.left(EntityHuman.EnumBedResult.NOT_SAFE);
                        }
                    }

                    Either<EntityHuman.EnumBedResult, Unit> either = super.startSleepInBed(blockposition).ifRight((unit) -> {
                        this.awardStat(StatisticList.SLEEP_IN_BED);
                        CriterionTriggers.SLEPT_IN_BED.trigger(this);
                    });

                    if (!this.getLevel().canSleepThroughNights()) {
                        this.displayClientMessage(new ChatMessage("sleep.not_possible"), true);
                    }

                    ((WorldServer) this.level).updateSleepingPlayerList();
                    return either;
                }
            }
        } else {
            return Either.left(EntityHuman.EnumBedResult.OTHER_PROBLEM);
        }
    }

    @Override
    public void startSleeping(BlockPosition blockposition) {
        this.resetStat(StatisticList.CUSTOM.get(StatisticList.TIME_SINCE_REST));
        super.startSleeping(blockposition);
    }

    private boolean bedInRange(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.isReachableBedBlock(blockposition) || this.isReachableBedBlock(blockposition.relative(enumdirection.getOpposite()));
    }

    private boolean isReachableBedBlock(BlockPosition blockposition) {
        Vec3D vec3d = Vec3D.atBottomCenterOf(blockposition);

        return Math.abs(this.getX() - vec3d.x()) <= 3.0D && Math.abs(this.getY() - vec3d.y()) <= 2.0D && Math.abs(this.getZ() - vec3d.z()) <= 3.0D;
    }

    private boolean bedBlocked(BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition blockposition1 = blockposition.above();

        return !this.freeAt(blockposition1) || !this.freeAt(blockposition1.relative(enumdirection.getOpposite()));
    }

    @Override
    public void stopSleepInBed(boolean flag, boolean flag1) {
        if (this.isSleeping()) {
            this.getLevel().getChunkSource().broadcastAndSend(this, new PacketPlayOutAnimation(this, 2));
        }

        super.stopSleepInBed(flag, flag1);
        if (this.connection != null) {
            this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    @Override
    public boolean startRiding(Entity entity, boolean flag) {
        Entity entity1 = this.getVehicle();

        if (!super.startRiding(entity, flag)) {
            return false;
        } else {
            Entity entity2 = this.getVehicle();

            if (entity2 != entity1 && this.connection != null) {
                this.connection.teleport(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
            }

            return true;
        }
    }

    @Override
    public void stopRiding() {
        Entity entity = this.getVehicle();

        super.stopRiding();
        Entity entity1 = this.getVehicle();

        if (entity1 != entity && this.connection != null) {
            this.connection.dismount(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    @Override
    public void dismountTo(double d0, double d1, double d2) {
        this.removeVehicle();
        if (this.connection != null) {
            this.connection.dismount(d0, d1, d2, this.getYRot(), this.getXRot());
        }

    }

    @Override
    public boolean isInvulnerableTo(DamageSource damagesource) {
        return super.isInvulnerableTo(damagesource) || this.isChangingDimension() || this.getAbilities().invulnerable && damagesource == DamageSource.WITHER;
    }

    @Override
    protected void checkFallDamage(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    @Override
    protected void onChangedBlock(BlockPosition blockposition) {
        if (!this.isSpectator()) {
            super.onChangedBlock(blockposition);
        }

    }

    public void doCheckFallDamage(double d0, boolean flag) {
        if (!this.touchingUnloadedChunk()) {
            BlockPosition blockposition = this.getOnPos();

            super.checkFallDamage(d0, flag, this.level.getBlockState(blockposition), blockposition);
        }
    }

    @Override
    public void openTextEdit(TileEntitySign tileentitysign) {
        tileentitysign.setAllowedPlayerEditor(this.getUUID());
        this.connection.send(new PacketPlayOutBlockChange(this.level, tileentitysign.getBlockPos()));
        this.connection.send(new PacketPlayOutOpenSignEditor(tileentitysign.getBlockPos()));
    }

    public void nextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }

    @Override
    public OptionalInt openMenu(@Nullable ITileInventory itileinventory) {
        if (itileinventory == null) {
            return OptionalInt.empty();
        } else {
            if (this.containerMenu != this.inventoryMenu) {
                this.closeContainer();
            }

            this.nextContainerCounter();
            Container container = itileinventory.createMenu(this.containerCounter, this.getInventory(), this);

            if (container == null) {
                if (this.isSpectator()) {
                    this.displayClientMessage((new ChatMessage("container.spectatorCantOpen")).withStyle(EnumChatFormat.RED), true);
                }

                return OptionalInt.empty();
            } else {
                this.connection.send(new PacketPlayOutOpenWindow(container.containerId, container.getType(), itileinventory.getDisplayName()));
                this.initMenu(container);
                this.containerMenu = container;
                return OptionalInt.of(this.containerCounter);
            }
        }
    }

    @Override
    public void sendMerchantOffers(int i, MerchantRecipeList merchantrecipelist, int j, int k, boolean flag, boolean flag1) {
        this.connection.send(new PacketPlayOutOpenWindowMerchant(i, merchantrecipelist, j, k, flag, flag1));
    }

    @Override
    public void openHorseInventory(EntityHorseAbstract entityhorseabstract, IInventory iinventory) {
        if (this.containerMenu != this.inventoryMenu) {
            this.closeContainer();
        }

        this.nextContainerCounter();
        this.connection.send(new PacketPlayOutOpenWindowHorse(this.containerCounter, iinventory.getContainerSize(), entityhorseabstract.getId()));
        this.containerMenu = new ContainerHorse(this.containerCounter, this.getInventory(), iinventory, entityhorseabstract);
        this.initMenu(this.containerMenu);
    }

    @Override
    public void openItemGui(ItemStack itemstack, EnumHand enumhand) {
        if (itemstack.is(Items.WRITTEN_BOOK)) {
            if (ItemWrittenBook.resolveBookComponents(itemstack, this.createCommandSourceStack(), this)) {
                this.containerMenu.broadcastChanges();
            }

            this.connection.send(new PacketPlayOutOpenBook(enumhand));
        }

    }

    @Override
    public void openCommandBlock(TileEntityCommand tileentitycommand) {
        this.connection.send(PacketPlayOutTileEntityData.create(tileentitycommand, TileEntity::saveWithoutMetadata));
    }

    @Override
    public void closeContainer() {
        this.connection.send(new PacketPlayOutCloseWindow(this.containerMenu.containerId));
        this.doCloseContainer();
    }

    public void doCloseContainer() {
        this.containerMenu.removed(this);
        this.inventoryMenu.transferState(this.containerMenu);
        this.containerMenu = this.inventoryMenu;
    }

    public void setPlayerInput(float f, float f1, boolean flag, boolean flag1) {
        if (this.isPassenger()) {
            if (f >= -1.0F && f <= 1.0F) {
                this.xxa = f;
            }

            if (f1 >= -1.0F && f1 <= 1.0F) {
                this.zza = f1;
            }

            this.jumping = flag;
            this.setShiftKeyDown(flag1);
        }

    }

    @Override
    public void awardStat(Statistic<?> statistic, int i) {
        this.stats.increment(this, statistic, i);
        this.getScoreboard().forAllObjectives(statistic, this.getScoreboardName(), (scoreboardscore) -> {
            scoreboardscore.add(i);
        });
    }

    @Override
    public void resetStat(Statistic<?> statistic) {
        this.stats.setValue(this, statistic, 0);
        this.getScoreboard().forAllObjectives(statistic, this.getScoreboardName(), ScoreboardScore::reset);
    }

    @Override
    public int awardRecipes(Collection<IRecipe<?>> collection) {
        return this.recipeBook.addRecipes(collection, this);
    }

    @Override
    public void awardRecipesByKey(MinecraftKey[] aminecraftkey) {
        List<IRecipe<?>> list = Lists.newArrayList();
        MinecraftKey[] aminecraftkey1 = aminecraftkey;
        int i = aminecraftkey.length;

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = aminecraftkey1[j];
            Optional optional = this.server.getRecipeManager().byKey(minecraftkey);

            Objects.requireNonNull(list);
            optional.ifPresent(list::add);
        }

        this.awardRecipes(list);
    }

    @Override
    public int resetRecipes(Collection<IRecipe<?>> collection) {
        return this.recipeBook.removeRecipes(collection, this);
    }

    @Override
    public void giveExperiencePoints(int i) {
        super.giveExperiencePoints(i);
        this.lastSentExp = -1;
    }

    public void disconnect() {
        this.disconnected = true;
        this.ejectPassengers();
        if (this.isSleeping()) {
            this.stopSleepInBed(true, false);
        }

    }

    public boolean hasDisconnected() {
        return this.disconnected;
    }

    public void resetSentInfo() {
        this.lastSentHealth = -1.0E8F;
    }

    @Override
    public void displayClientMessage(IChatBaseComponent ichatbasecomponent, boolean flag) {
        this.sendMessage(ichatbasecomponent, flag ? ChatMessageType.GAME_INFO : ChatMessageType.CHAT, SystemUtils.NIL_UUID);
    }

    @Override
    protected void completeUsingItem() {
        if (!this.useItem.isEmpty() && this.isUsingItem()) {
            this.connection.send(new PacketPlayOutEntityStatus(this, (byte) 9));
            super.completeUsingItem();
        }

    }

    @Override
    public void lookAt(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        super.lookAt(argumentanchor_anchor, vec3d);
        this.connection.send(new PacketPlayOutLookAt(argumentanchor_anchor, vec3d.x, vec3d.y, vec3d.z));
    }

    public void lookAt(ArgumentAnchor.Anchor argumentanchor_anchor, Entity entity, ArgumentAnchor.Anchor argumentanchor_anchor1) {
        Vec3D vec3d = argumentanchor_anchor1.apply(entity);

        super.lookAt(argumentanchor_anchor, vec3d);
        this.connection.send(new PacketPlayOutLookAt(argumentanchor_anchor, entity, argumentanchor_anchor1));
    }

    public void restoreFrom(EntityPlayer entityplayer, boolean flag) {
        this.textFilteringEnabled = entityplayer.textFilteringEnabled;
        this.gameMode.setGameModeForPlayer(entityplayer.gameMode.getGameModeForPlayer(), entityplayer.gameMode.getPreviousGameModeForPlayer());
        if (flag) {
            this.getInventory().replaceWith(entityplayer.getInventory());
            this.setHealth(entityplayer.getHealth());
            this.foodData = entityplayer.foodData;
            this.experienceLevel = entityplayer.experienceLevel;
            this.totalExperience = entityplayer.totalExperience;
            this.experienceProgress = entityplayer.experienceProgress;
            this.setScore(entityplayer.getScore());
            this.portalEntrancePos = entityplayer.portalEntrancePos;
        } else if (this.level.getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY) || entityplayer.isSpectator()) {
            this.getInventory().replaceWith(entityplayer.getInventory());
            this.experienceLevel = entityplayer.experienceLevel;
            this.totalExperience = entityplayer.totalExperience;
            this.experienceProgress = entityplayer.experienceProgress;
            this.setScore(entityplayer.getScore());
        }

        this.enchantmentSeed = entityplayer.enchantmentSeed;
        this.enderChestInventory = entityplayer.enderChestInventory;
        this.getEntityData().set(EntityPlayer.DATA_PLAYER_MODE_CUSTOMISATION, (Byte) entityplayer.getEntityData().get(EntityPlayer.DATA_PLAYER_MODE_CUSTOMISATION));
        this.lastSentExp = -1;
        this.lastSentHealth = -1.0F;
        this.lastSentFood = -1;
        this.recipeBook.copyOverData(entityplayer.recipeBook);
        this.seenCredits = entityplayer.seenCredits;
        this.enteredNetherPosition = entityplayer.enteredNetherPosition;
        this.setShoulderEntityLeft(entityplayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(entityplayer.getShoulderEntityRight());
    }

    @Override
    protected void onEffectAdded(MobEffect mobeffect, @Nullable Entity entity) {
        super.onEffectAdded(mobeffect, entity);
        this.connection.send(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
        if (mobeffect.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartTime = this.tickCount;
            this.levitationStartPos = this.position();
        }

        CriterionTriggers.EFFECTS_CHANGED.trigger(this, entity);
    }

    @Override
    protected void onEffectUpdated(MobEffect mobeffect, boolean flag, @Nullable Entity entity) {
        super.onEffectUpdated(mobeffect, flag, entity);
        this.connection.send(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
        CriterionTriggers.EFFECTS_CHANGED.trigger(this, entity);
    }

    @Override
    protected void onEffectRemoved(MobEffect mobeffect) {
        super.onEffectRemoved(mobeffect);
        this.connection.send(new PacketPlayOutRemoveEntityEffect(this.getId(), mobeffect.getEffect()));
        if (mobeffect.getEffect() == MobEffects.LEVITATION) {
            this.levitationStartPos = null;
        }

        CriterionTriggers.EFFECTS_CHANGED.trigger(this, (Entity) null);
    }

    @Override
    public void teleportTo(double d0, double d1, double d2) {
        this.connection.teleport(d0, d1, d2, this.getYRot(), this.getXRot());
    }

    @Override
    public void moveTo(double d0, double d1, double d2) {
        this.teleportTo(d0, d1, d2);
        this.connection.resetPosition();
    }

    @Override
    public void crit(Entity entity) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new PacketPlayOutAnimation(entity, 4));
    }

    @Override
    public void magicCrit(Entity entity) {
        this.getLevel().getChunkSource().broadcastAndSend(this, new PacketPlayOutAnimation(entity, 5));
    }

    @Override
    public void onUpdateAbilities() {
        if (this.connection != null) {
            this.connection.send(new PacketPlayOutAbilities(this.getAbilities()));
            this.updateInvisibilityStatus();
        }
    }

    @Override
    public WorldServer getLevel() {
        return (WorldServer) this.level;
    }

    public boolean setGameMode(EnumGamemode enumgamemode) {
        if (!this.gameMode.changeGameModeForPlayer(enumgamemode)) {
            return false;
        } else {
            this.connection.send(new PacketPlayOutGameStateChange(PacketPlayOutGameStateChange.CHANGE_GAME_MODE, (float) enumgamemode.getId()));
            if (enumgamemode == EnumGamemode.SPECTATOR) {
                this.removeEntitiesOnShoulder();
                this.stopRiding();
            } else {
                this.setCamera(this);
            }

            this.onUpdateAbilities();
            this.updateEffectVisibility();
            return true;
        }
    }

    @Override
    public boolean isSpectator() {
        return this.gameMode.getGameModeForPlayer() == EnumGamemode.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        return this.gameMode.getGameModeForPlayer() == EnumGamemode.CREATIVE;
    }

    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent, UUID uuid) {
        this.sendMessage(ichatbasecomponent, ChatMessageType.SYSTEM, uuid);
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent, ChatMessageType chatmessagetype, UUID uuid) {
        if (this.acceptsChat(chatmessagetype)) {
            this.connection.send(new PacketPlayOutChat(ichatbasecomponent, chatmessagetype, uuid), (future) -> {
                if (!future.isSuccess() && (chatmessagetype == ChatMessageType.GAME_INFO || chatmessagetype == ChatMessageType.SYSTEM) && this.acceptsChat(ChatMessageType.SYSTEM)) {
                    boolean flag = true;
                    String s = ichatbasecomponent.getString(256);
                    IChatMutableComponent ichatmutablecomponent = (new ChatComponentText(s)).withStyle(EnumChatFormat.YELLOW);

                    this.connection.send(new PacketPlayOutChat((new ChatMessage("multiplayer.message_not_delivered", new Object[]{ichatmutablecomponent})).withStyle(EnumChatFormat.RED), ChatMessageType.SYSTEM, uuid));
                }

            });
        }
    }

    public String getIpAddress() {
        String s = this.connection.connection.getRemoteAddress().toString();

        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        return s;
    }

    public void updateOptions(PacketPlayInSettings packetplayinsettings) {
        this.chatVisibility = packetplayinsettings.chatVisibility();
        this.canChatColor = packetplayinsettings.chatColors();
        this.textFilteringEnabled = packetplayinsettings.textFilteringEnabled();
        this.allowsListing = packetplayinsettings.allowsListing();
        this.getEntityData().set(EntityPlayer.DATA_PLAYER_MODE_CUSTOMISATION, (byte) packetplayinsettings.modelCustomisation());
        this.getEntityData().set(EntityPlayer.DATA_PLAYER_MAIN_HAND, (byte) (packetplayinsettings.mainHand() == EnumMainHand.LEFT ? 0 : 1));
    }

    public boolean canChatInColor() {
        return this.canChatColor;
    }

    public EnumChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    private boolean acceptsChat(ChatMessageType chatmessagetype) {
        switch (this.chatVisibility) {
            case HIDDEN:
                return chatmessagetype == ChatMessageType.GAME_INFO;
            case SYSTEM:
                return chatmessagetype == ChatMessageType.SYSTEM || chatmessagetype == ChatMessageType.GAME_INFO;
            case FULL:
            default:
                return true;
        }
    }

    public void sendTexturePack(String s, String s1, boolean flag, @Nullable IChatBaseComponent ichatbasecomponent) {
        this.connection.send(new PacketPlayOutResourcePackSend(s, s1, flag, ichatbasecomponent));
    }

    @Override
    protected int getPermissionLevel() {
        return this.server.getProfilePermissions(this.getGameProfile());
    }

    public void resetLastActionTime() {
        this.lastActionTime = SystemUtils.getMillis();
    }

    public ServerStatisticManager getStats() {
        return this.stats;
    }

    public RecipeBookServer getRecipeBook() {
        return this.recipeBook;
    }

    @Override
    protected void updateInvisibilityStatus() {
        if (this.isSpectator()) {
            this.removeEffectParticles();
            this.setInvisible(true);
        } else {
            super.updateInvisibilityStatus();
        }

    }

    public Entity getCamera() {
        return (Entity) (this.camera == null ? this : this.camera);
    }

    public void setCamera(@Nullable Entity entity) {
        Entity entity1 = this.getCamera();

        this.camera = (Entity) (entity == null ? this : entity);
        if (entity1 != this.camera) {
            this.connection.send(new PacketPlayOutCamera(this.camera));
            this.teleportTo(this.camera.getX(), this.camera.getY(), this.camera.getZ());
        }

    }

    @Override
    protected void processPortalCooldown() {
        if (!this.isChangingDimension) {
            super.processPortalCooldown();
        }

    }

    @Override
    public void attack(Entity entity) {
        if (this.gameMode.getGameModeForPlayer() == EnumGamemode.SPECTATOR) {
            this.setCamera(entity);
        } else {
            super.attack(entity);
        }

    }

    public long getLastActionTime() {
        return this.lastActionTime;
    }

    @Nullable
    public IChatBaseComponent getTabListDisplayName() {
        return null;
    }

    @Override
    public void swing(EnumHand enumhand) {
        super.swing(enumhand);
        this.resetAttackStrengthTicker();
    }

    public boolean isChangingDimension() {
        return this.isChangingDimension;
    }

    public void hasChangedDimension() {
        this.isChangingDimension = false;
    }

    public AdvancementDataPlayer getAdvancements() {
        return this.advancements;
    }

    public void teleportTo(WorldServer worldserver, double d0, double d1, double d2, float f, float f1) {
        this.setCamera(this);
        this.stopRiding();
        if (worldserver == this.level) {
            this.connection.teleport(d0, d1, d2, f, f1);
        } else {
            WorldServer worldserver1 = this.getLevel();
            WorldData worlddata = worldserver.getLevelData();

            this.connection.send(new PacketPlayOutRespawn(worldserver.dimensionType(), worldserver.dimension(), BiomeManager.obfuscateSeed(worldserver.getSeed()), this.gameMode.getGameModeForPlayer(), this.gameMode.getPreviousGameModeForPlayer(), worldserver.isDebug(), worldserver.isFlat(), true));
            this.connection.send(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
            this.server.getPlayerList().sendPlayerPermissionLevel(this);
            worldserver1.removePlayerImmediately(this, Entity.RemovalReason.CHANGED_DIMENSION);
            this.unsetRemoved();
            this.moveTo(d0, d1, d2, f, f1);
            this.setLevel(worldserver);
            worldserver.addDuringCommandTeleport(this);
            this.triggerDimensionChangeTriggers(worldserver1);
            this.connection.teleport(d0, d1, d2, f, f1);
            this.server.getPlayerList().sendLevelInfo(this, worldserver);
            this.server.getPlayerList().sendAllPlayerInfo(this);
        }

    }

    @Nullable
    public BlockPosition getRespawnPosition() {
        return this.respawnPosition;
    }

    public float getRespawnAngle() {
        return this.respawnAngle;
    }

    public ResourceKey<World> getRespawnDimension() {
        return this.respawnDimension;
    }

    public boolean isRespawnForced() {
        return this.respawnForced;
    }

    public void setRespawnPosition(ResourceKey<World> resourcekey, @Nullable BlockPosition blockposition, float f, boolean flag, boolean flag1) {
        if (blockposition != null) {
            boolean flag2 = blockposition.equals(this.respawnPosition) && resourcekey.equals(this.respawnDimension);

            if (flag1 && !flag2) {
                this.sendMessage(new ChatMessage("block.minecraft.set_spawn"), SystemUtils.NIL_UUID);
            }

            this.respawnPosition = blockposition;
            this.respawnDimension = resourcekey;
            this.respawnAngle = f;
            this.respawnForced = flag;
        } else {
            this.respawnPosition = null;
            this.respawnDimension = World.OVERWORLD;
            this.respawnAngle = 0.0F;
            this.respawnForced = false;
        }

    }

    public void trackChunk(ChunkCoordIntPair chunkcoordintpair, Packet<?> packet) {
        this.connection.send(packet);
    }

    public void untrackChunk(ChunkCoordIntPair chunkcoordintpair) {
        if (this.isAlive()) {
            this.connection.send(new PacketPlayOutUnloadChunk(chunkcoordintpair.x, chunkcoordintpair.z));
        }

    }

    public SectionPosition getLastSectionPos() {
        return this.lastSectionPos;
    }

    public void setLastSectionPos(SectionPosition sectionposition) {
        this.lastSectionPos = sectionposition;
    }

    @Override
    public void playNotifySound(SoundEffect soundeffect, SoundCategory soundcategory, float f, float f1) {
        this.connection.send(new PacketPlayOutNamedSoundEffect(soundeffect, soundcategory, this.getX(), this.getY(), this.getZ(), f, f1));
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return new PacketPlayOutNamedEntitySpawn(this);
    }

    @Override
    public EntityItem drop(ItemStack itemstack, boolean flag, boolean flag1) {
        EntityItem entityitem = super.drop(itemstack, flag, flag1);

        if (entityitem == null) {
            return null;
        } else {
            this.level.addFreshEntity(entityitem);
            ItemStack itemstack1 = entityitem.getItem();

            if (flag1) {
                if (!itemstack1.isEmpty()) {
                    this.awardStat(StatisticList.ITEM_DROPPED.get(itemstack1.getItem()), itemstack.getCount());
                }

                this.awardStat(StatisticList.DROP);
            }

            return entityitem;
        }
    }

    public ITextFilter getTextFilter() {
        return this.textFilter;
    }

    public void setLevel(WorldServer worldserver) {
        this.level = worldserver;
        this.gameMode.setLevel(worldserver);
    }

    @Nullable
    private static EnumGamemode readPlayerMode(@Nullable NBTTagCompound nbttagcompound, String s) {
        return nbttagcompound != null && nbttagcompound.contains(s, 99) ? EnumGamemode.byId(nbttagcompound.getInt(s)) : null;
    }

    private EnumGamemode calculateGameModeForNewPlayer(@Nullable EnumGamemode enumgamemode) {
        EnumGamemode enumgamemode1 = this.server.getForcedGameType();

        return enumgamemode1 != null ? enumgamemode1 : (enumgamemode != null ? enumgamemode : this.server.getDefaultGameType());
    }

    public void loadGameTypes(@Nullable NBTTagCompound nbttagcompound) {
        this.gameMode.setGameModeForPlayer(this.calculateGameModeForNewPlayer(readPlayerMode(nbttagcompound, "playerGameType")), readPlayerMode(nbttagcompound, "previousPlayerGameType"));
    }

    private void storeGameTypes(NBTTagCompound nbttagcompound) {
        nbttagcompound.putInt("playerGameType", this.gameMode.getGameModeForPlayer().getId());
        EnumGamemode enumgamemode = this.gameMode.getPreviousGameModeForPlayer();

        if (enumgamemode != null) {
            nbttagcompound.putInt("previousPlayerGameType", enumgamemode.getId());
        }

    }

    public boolean isTextFilteringEnabled() {
        return this.textFilteringEnabled;
    }

    public boolean shouldFilterMessageTo(EntityPlayer entityplayer) {
        return entityplayer == this ? false : this.textFilteringEnabled || entityplayer.textFilteringEnabled;
    }

    @Override
    public boolean mayInteract(World world, BlockPosition blockposition) {
        return super.mayInteract(world, blockposition) && world.mayInteract(this, blockposition);
    }

    @Override
    protected void updateUsingItem(ItemStack itemstack) {
        CriterionTriggers.USING_ITEM.trigger(this, itemstack);
        super.updateUsingItem(itemstack);
    }

    public boolean drop(boolean flag) {
        PlayerInventory playerinventory = this.getInventory();
        ItemStack itemstack = playerinventory.removeFromSelected(flag);

        this.containerMenu.findSlot(playerinventory, playerinventory.selected).ifPresent((i) -> {
            this.containerMenu.setRemoteSlot(i, playerinventory.getSelected());
        });
        return this.drop(itemstack, false, true) != null;
    }

    public boolean allowsListing() {
        return this.allowsListing;
    }
}
