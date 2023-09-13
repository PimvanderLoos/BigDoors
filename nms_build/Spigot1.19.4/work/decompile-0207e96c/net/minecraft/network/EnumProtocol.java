package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.SystemUtils;
import net.minecraft.network.protocol.BundleDelimiterPacket;
import net.minecraft.network.protocol.BundlePacket;
import net.minecraft.network.protocol.BundlerInfo;
import net.minecraft.network.protocol.EnumProtocolDirection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockChangedAckPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundChunksBiomesPacket;
import net.minecraft.network.protocol.game.ClientboundClearTitlesPacket;
import net.minecraft.network.protocol.game.ClientboundCustomChatCompletionsPacket;
import net.minecraft.network.protocol.game.ClientboundDamageEventPacket;
import net.minecraft.network.protocol.game.ClientboundDeleteChatPacket;
import net.minecraft.network.protocol.game.ClientboundDisguisedChatPacket;
import net.minecraft.network.protocol.game.ClientboundHurtAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundInitializeBorderPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.network.protocol.game.ClientboundPingPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerChatPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEndPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatEnterPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerCombatKillPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundServerDataPacket;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderCenterPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderLerpSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderSizePacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDelayPacket;
import net.minecraft.network.protocol.game.ClientboundSetBorderWarningDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSimulationDistancePacket;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateEnabledFeaturesPacket;
import net.minecraft.network.protocol.game.PacketPlayInAbilities;
import net.minecraft.network.protocol.game.PacketPlayInAdvancements;
import net.minecraft.network.protocol.game.PacketPlayInArmAnimation;
import net.minecraft.network.protocol.game.PacketPlayInAutoRecipe;
import net.minecraft.network.protocol.game.PacketPlayInBEdit;
import net.minecraft.network.protocol.game.PacketPlayInBeacon;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInBlockPlace;
import net.minecraft.network.protocol.game.PacketPlayInBoatMove;
import net.minecraft.network.protocol.game.PacketPlayInChat;
import net.minecraft.network.protocol.game.PacketPlayInClientCommand;
import net.minecraft.network.protocol.game.PacketPlayInCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayInCustomPayload;
import net.minecraft.network.protocol.game.PacketPlayInDifficultyChange;
import net.minecraft.network.protocol.game.PacketPlayInDifficultyLock;
import net.minecraft.network.protocol.game.PacketPlayInEnchantItem;
import net.minecraft.network.protocol.game.PacketPlayInEntityAction;
import net.minecraft.network.protocol.game.PacketPlayInEntityNBTQuery;
import net.minecraft.network.protocol.game.PacketPlayInFlying;
import net.minecraft.network.protocol.game.PacketPlayInHeldItemSlot;
import net.minecraft.network.protocol.game.PacketPlayInItemName;
import net.minecraft.network.protocol.game.PacketPlayInJigsawGenerate;
import net.minecraft.network.protocol.game.PacketPlayInKeepAlive;
import net.minecraft.network.protocol.game.PacketPlayInPickItem;
import net.minecraft.network.protocol.game.PacketPlayInRecipeDisplayed;
import net.minecraft.network.protocol.game.PacketPlayInRecipeSettings;
import net.minecraft.network.protocol.game.PacketPlayInResourcePackStatus;
import net.minecraft.network.protocol.game.PacketPlayInSetCommandBlock;
import net.minecraft.network.protocol.game.PacketPlayInSetCommandMinecart;
import net.minecraft.network.protocol.game.PacketPlayInSetCreativeSlot;
import net.minecraft.network.protocol.game.PacketPlayInSetJigsaw;
import net.minecraft.network.protocol.game.PacketPlayInSettings;
import net.minecraft.network.protocol.game.PacketPlayInSpectate;
import net.minecraft.network.protocol.game.PacketPlayInSteerVehicle;
import net.minecraft.network.protocol.game.PacketPlayInStruct;
import net.minecraft.network.protocol.game.PacketPlayInTabComplete;
import net.minecraft.network.protocol.game.PacketPlayInTeleportAccept;
import net.minecraft.network.protocol.game.PacketPlayInTileNBTQuery;
import net.minecraft.network.protocol.game.PacketPlayInTrSel;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.network.protocol.game.PacketPlayInUseEntity;
import net.minecraft.network.protocol.game.PacketPlayInUseItem;
import net.minecraft.network.protocol.game.PacketPlayInVehicleMove;
import net.minecraft.network.protocol.game.PacketPlayInWindowClick;
import net.minecraft.network.protocol.game.PacketPlayOutAbilities;
import net.minecraft.network.protocol.game.PacketPlayOutAdvancements;
import net.minecraft.network.protocol.game.PacketPlayOutAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutAttachEntity;
import net.minecraft.network.protocol.game.PacketPlayOutAutoRecipe;
import net.minecraft.network.protocol.game.PacketPlayOutBlockAction;
import net.minecraft.network.protocol.game.PacketPlayOutBlockBreakAnimation;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutBoss;
import net.minecraft.network.protocol.game.PacketPlayOutCamera;
import net.minecraft.network.protocol.game.PacketPlayOutCloseWindow;
import net.minecraft.network.protocol.game.PacketPlayOutCollect;
import net.minecraft.network.protocol.game.PacketPlayOutCommands;
import net.minecraft.network.protocol.game.PacketPlayOutCustomPayload;
import net.minecraft.network.protocol.game.PacketPlayOutEntity;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.network.protocol.game.PacketPlayOutEntityHeadRotation;
import net.minecraft.network.protocol.game.PacketPlayOutEntityMetadata;
import net.minecraft.network.protocol.game.PacketPlayOutEntitySound;
import net.minecraft.network.protocol.game.PacketPlayOutEntityStatus;
import net.minecraft.network.protocol.game.PacketPlayOutEntityTeleport;
import net.minecraft.network.protocol.game.PacketPlayOutEntityVelocity;
import net.minecraft.network.protocol.game.PacketPlayOutExperience;
import net.minecraft.network.protocol.game.PacketPlayOutExplosion;
import net.minecraft.network.protocol.game.PacketPlayOutGameStateChange;
import net.minecraft.network.protocol.game.PacketPlayOutHeldItemSlot;
import net.minecraft.network.protocol.game.PacketPlayOutKeepAlive;
import net.minecraft.network.protocol.game.PacketPlayOutKickDisconnect;
import net.minecraft.network.protocol.game.PacketPlayOutLightUpdate;
import net.minecraft.network.protocol.game.PacketPlayOutLogin;
import net.minecraft.network.protocol.game.PacketPlayOutLookAt;
import net.minecraft.network.protocol.game.PacketPlayOutMap;
import net.minecraft.network.protocol.game.PacketPlayOutMount;
import net.minecraft.network.protocol.game.PacketPlayOutMultiBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutNBTQuery;
import net.minecraft.network.protocol.game.PacketPlayOutNamedEntitySpawn;
import net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect;
import net.minecraft.network.protocol.game.PacketPlayOutOpenBook;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindow;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindowHorse;
import net.minecraft.network.protocol.game.PacketPlayOutOpenWindowMerchant;
import net.minecraft.network.protocol.game.PacketPlayOutPlayerListHeaderFooter;
import net.minecraft.network.protocol.game.PacketPlayOutPosition;
import net.minecraft.network.protocol.game.PacketPlayOutRecipeUpdate;
import net.minecraft.network.protocol.game.PacketPlayOutRecipes;
import net.minecraft.network.protocol.game.PacketPlayOutRemoveEntityEffect;
import net.minecraft.network.protocol.game.PacketPlayOutResourcePackSend;
import net.minecraft.network.protocol.game.PacketPlayOutRespawn;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardDisplayObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardObjective;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardScore;
import net.minecraft.network.protocol.game.PacketPlayOutScoreboardTeam;
import net.minecraft.network.protocol.game.PacketPlayOutSelectAdvancementTab;
import net.minecraft.network.protocol.game.PacketPlayOutServerDifficulty;
import net.minecraft.network.protocol.game.PacketPlayOutSetCooldown;
import net.minecraft.network.protocol.game.PacketPlayOutSetSlot;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityExperienceOrb;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnPosition;
import net.minecraft.network.protocol.game.PacketPlayOutStatistic;
import net.minecraft.network.protocol.game.PacketPlayOutStopSound;
import net.minecraft.network.protocol.game.PacketPlayOutTabComplete;
import net.minecraft.network.protocol.game.PacketPlayOutTags;
import net.minecraft.network.protocol.game.PacketPlayOutTileEntityData;
import net.minecraft.network.protocol.game.PacketPlayOutUnloadChunk;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateAttributes;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateHealth;
import net.minecraft.network.protocol.game.PacketPlayOutUpdateTime;
import net.minecraft.network.protocol.game.PacketPlayOutVehicleMove;
import net.minecraft.network.protocol.game.PacketPlayOutViewCentre;
import net.minecraft.network.protocol.game.PacketPlayOutViewDistance;
import net.minecraft.network.protocol.game.PacketPlayOutWindowData;
import net.minecraft.network.protocol.game.PacketPlayOutWindowItems;
import net.minecraft.network.protocol.game.PacketPlayOutWorldEvent;
import net.minecraft.network.protocol.game.PacketPlayOutWorldParticles;
import net.minecraft.network.protocol.game.ServerboundChatAckPacket;
import net.minecraft.network.protocol.game.ServerboundChatCommandPacket;
import net.minecraft.network.protocol.game.ServerboundChatSessionUpdatePacket;
import net.minecraft.network.protocol.game.ServerboundPongPacket;
import net.minecraft.network.protocol.handshake.PacketHandshakingInSetProtocol;
import net.minecraft.network.protocol.login.PacketLoginInCustomPayload;
import net.minecraft.network.protocol.login.PacketLoginInEncryptionBegin;
import net.minecraft.network.protocol.login.PacketLoginInStart;
import net.minecraft.network.protocol.login.PacketLoginOutCustomPayload;
import net.minecraft.network.protocol.login.PacketLoginOutDisconnect;
import net.minecraft.network.protocol.login.PacketLoginOutEncryptionBegin;
import net.minecraft.network.protocol.login.PacketLoginOutSetCompression;
import net.minecraft.network.protocol.login.PacketLoginOutSuccess;
import net.minecraft.network.protocol.status.PacketStatusInPing;
import net.minecraft.network.protocol.status.PacketStatusInStart;
import net.minecraft.network.protocol.status.PacketStatusOutPong;
import net.minecraft.network.protocol.status.PacketStatusOutServerInfo;
import net.minecraft.util.VisibleForDebug;
import org.slf4j.Logger;

public enum EnumProtocol implements BundlerInfo.b {

    HANDSHAKING(-1, protocol().addFlow(EnumProtocolDirection.SERVERBOUND, (new EnumProtocol.a<>()).addPacket(PacketHandshakingInSetProtocol.class, PacketHandshakingInSetProtocol::new))), PLAY(0, protocol().addFlow(EnumProtocolDirection.CLIENTBOUND, (new EnumProtocol.a<>()).withBundlePacket(ClientboundBundlePacket.class, ClientboundBundlePacket::new).addPacket(PacketPlayOutSpawnEntity.class, PacketPlayOutSpawnEntity::new).addPacket(PacketPlayOutSpawnEntityExperienceOrb.class, PacketPlayOutSpawnEntityExperienceOrb::new).addPacket(PacketPlayOutNamedEntitySpawn.class, PacketPlayOutNamedEntitySpawn::new).addPacket(PacketPlayOutAnimation.class, PacketPlayOutAnimation::new).addPacket(PacketPlayOutStatistic.class, PacketPlayOutStatistic::new).addPacket(ClientboundBlockChangedAckPacket.class, ClientboundBlockChangedAckPacket::new).addPacket(PacketPlayOutBlockBreakAnimation.class, PacketPlayOutBlockBreakAnimation::new).addPacket(PacketPlayOutTileEntityData.class, PacketPlayOutTileEntityData::new).addPacket(PacketPlayOutBlockAction.class, PacketPlayOutBlockAction::new).addPacket(PacketPlayOutBlockChange.class, PacketPlayOutBlockChange::new).addPacket(PacketPlayOutBoss.class, PacketPlayOutBoss::new).addPacket(PacketPlayOutServerDifficulty.class, PacketPlayOutServerDifficulty::new).addPacket(ClientboundChunksBiomesPacket.class, ClientboundChunksBiomesPacket::new).addPacket(ClientboundClearTitlesPacket.class, ClientboundClearTitlesPacket::new).addPacket(PacketPlayOutTabComplete.class, PacketPlayOutTabComplete::new).addPacket(PacketPlayOutCommands.class, PacketPlayOutCommands::new).addPacket(PacketPlayOutCloseWindow.class, PacketPlayOutCloseWindow::new).addPacket(PacketPlayOutWindowItems.class, PacketPlayOutWindowItems::new).addPacket(PacketPlayOutWindowData.class, PacketPlayOutWindowData::new).addPacket(PacketPlayOutSetSlot.class, PacketPlayOutSetSlot::new).addPacket(PacketPlayOutSetCooldown.class, PacketPlayOutSetCooldown::new).addPacket(ClientboundCustomChatCompletionsPacket.class, ClientboundCustomChatCompletionsPacket::new).addPacket(PacketPlayOutCustomPayload.class, PacketPlayOutCustomPayload::new).addPacket(ClientboundDamageEventPacket.class, ClientboundDamageEventPacket::new).addPacket(ClientboundDeleteChatPacket.class, ClientboundDeleteChatPacket::new).addPacket(PacketPlayOutKickDisconnect.class, PacketPlayOutKickDisconnect::new).addPacket(ClientboundDisguisedChatPacket.class, ClientboundDisguisedChatPacket::new).addPacket(PacketPlayOutEntityStatus.class, PacketPlayOutEntityStatus::new).addPacket(PacketPlayOutExplosion.class, PacketPlayOutExplosion::new).addPacket(PacketPlayOutUnloadChunk.class, PacketPlayOutUnloadChunk::new).addPacket(PacketPlayOutGameStateChange.class, PacketPlayOutGameStateChange::new).addPacket(PacketPlayOutOpenWindowHorse.class, PacketPlayOutOpenWindowHorse::new).addPacket(ClientboundHurtAnimationPacket.class, ClientboundHurtAnimationPacket::new).addPacket(ClientboundInitializeBorderPacket.class, ClientboundInitializeBorderPacket::new).addPacket(PacketPlayOutKeepAlive.class, PacketPlayOutKeepAlive::new).addPacket(ClientboundLevelChunkWithLightPacket.class, ClientboundLevelChunkWithLightPacket::new).addPacket(PacketPlayOutWorldEvent.class, PacketPlayOutWorldEvent::new).addPacket(PacketPlayOutWorldParticles.class, PacketPlayOutWorldParticles::new).addPacket(PacketPlayOutLightUpdate.class, PacketPlayOutLightUpdate::new).addPacket(PacketPlayOutLogin.class, PacketPlayOutLogin::new).addPacket(PacketPlayOutMap.class, PacketPlayOutMap::new).addPacket(PacketPlayOutOpenWindowMerchant.class, PacketPlayOutOpenWindowMerchant::new).addPacket(PacketPlayOutEntity.PacketPlayOutRelEntityMove.class, PacketPlayOutEntity.PacketPlayOutRelEntityMove::read).addPacket(PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook.class, PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook::read).addPacket(PacketPlayOutEntity.PacketPlayOutEntityLook.class, PacketPlayOutEntity.PacketPlayOutEntityLook::read).addPacket(PacketPlayOutVehicleMove.class, PacketPlayOutVehicleMove::new).addPacket(PacketPlayOutOpenBook.class, PacketPlayOutOpenBook::new).addPacket(PacketPlayOutOpenWindow.class, PacketPlayOutOpenWindow::new).addPacket(PacketPlayOutOpenSignEditor.class, PacketPlayOutOpenSignEditor::new).addPacket(ClientboundPingPacket.class, ClientboundPingPacket::new).addPacket(PacketPlayOutAutoRecipe.class, PacketPlayOutAutoRecipe::new).addPacket(PacketPlayOutAbilities.class, PacketPlayOutAbilities::new).addPacket(ClientboundPlayerChatPacket.class, ClientboundPlayerChatPacket::new).addPacket(ClientboundPlayerCombatEndPacket.class, ClientboundPlayerCombatEndPacket::new).addPacket(ClientboundPlayerCombatEnterPacket.class, ClientboundPlayerCombatEnterPacket::new).addPacket(ClientboundPlayerCombatKillPacket.class, ClientboundPlayerCombatKillPacket::new).addPacket(ClientboundPlayerInfoRemovePacket.class, ClientboundPlayerInfoRemovePacket::new).addPacket(ClientboundPlayerInfoUpdatePacket.class, ClientboundPlayerInfoUpdatePacket::new).addPacket(PacketPlayOutLookAt.class, PacketPlayOutLookAt::new).addPacket(PacketPlayOutPosition.class, PacketPlayOutPosition::new).addPacket(PacketPlayOutRecipes.class, PacketPlayOutRecipes::new).addPacket(PacketPlayOutEntityDestroy.class, PacketPlayOutEntityDestroy::new).addPacket(PacketPlayOutRemoveEntityEffect.class, PacketPlayOutRemoveEntityEffect::new).addPacket(PacketPlayOutResourcePackSend.class, PacketPlayOutResourcePackSend::new).addPacket(PacketPlayOutRespawn.class, PacketPlayOutRespawn::new).addPacket(PacketPlayOutEntityHeadRotation.class, PacketPlayOutEntityHeadRotation::new).addPacket(PacketPlayOutMultiBlockChange.class, PacketPlayOutMultiBlockChange::new).addPacket(PacketPlayOutSelectAdvancementTab.class, PacketPlayOutSelectAdvancementTab::new).addPacket(ClientboundServerDataPacket.class, ClientboundServerDataPacket::new).addPacket(ClientboundSetActionBarTextPacket.class, ClientboundSetActionBarTextPacket::new).addPacket(ClientboundSetBorderCenterPacket.class, ClientboundSetBorderCenterPacket::new).addPacket(ClientboundSetBorderLerpSizePacket.class, ClientboundSetBorderLerpSizePacket::new).addPacket(ClientboundSetBorderSizePacket.class, ClientboundSetBorderSizePacket::new).addPacket(ClientboundSetBorderWarningDelayPacket.class, ClientboundSetBorderWarningDelayPacket::new).addPacket(ClientboundSetBorderWarningDistancePacket.class, ClientboundSetBorderWarningDistancePacket::new).addPacket(PacketPlayOutCamera.class, PacketPlayOutCamera::new).addPacket(PacketPlayOutHeldItemSlot.class, PacketPlayOutHeldItemSlot::new).addPacket(PacketPlayOutViewCentre.class, PacketPlayOutViewCentre::new).addPacket(PacketPlayOutViewDistance.class, PacketPlayOutViewDistance::new).addPacket(PacketPlayOutSpawnPosition.class, PacketPlayOutSpawnPosition::new).addPacket(PacketPlayOutScoreboardDisplayObjective.class, PacketPlayOutScoreboardDisplayObjective::new).addPacket(PacketPlayOutEntityMetadata.class, PacketPlayOutEntityMetadata::new).addPacket(PacketPlayOutAttachEntity.class, PacketPlayOutAttachEntity::new).addPacket(PacketPlayOutEntityVelocity.class, PacketPlayOutEntityVelocity::new).addPacket(PacketPlayOutEntityEquipment.class, PacketPlayOutEntityEquipment::new).addPacket(PacketPlayOutExperience.class, PacketPlayOutExperience::new).addPacket(PacketPlayOutUpdateHealth.class, PacketPlayOutUpdateHealth::new).addPacket(PacketPlayOutScoreboardObjective.class, PacketPlayOutScoreboardObjective::new).addPacket(PacketPlayOutMount.class, PacketPlayOutMount::new).addPacket(PacketPlayOutScoreboardTeam.class, PacketPlayOutScoreboardTeam::new).addPacket(PacketPlayOutScoreboardScore.class, PacketPlayOutScoreboardScore::new).addPacket(ClientboundSetSimulationDistancePacket.class, ClientboundSetSimulationDistancePacket::new).addPacket(ClientboundSetSubtitleTextPacket.class, ClientboundSetSubtitleTextPacket::new).addPacket(PacketPlayOutUpdateTime.class, PacketPlayOutUpdateTime::new).addPacket(ClientboundSetTitleTextPacket.class, ClientboundSetTitleTextPacket::new).addPacket(ClientboundSetTitlesAnimationPacket.class, ClientboundSetTitlesAnimationPacket::new).addPacket(PacketPlayOutEntitySound.class, PacketPlayOutEntitySound::new).addPacket(PacketPlayOutNamedSoundEffect.class, PacketPlayOutNamedSoundEffect::new).addPacket(PacketPlayOutStopSound.class, PacketPlayOutStopSound::new).addPacket(ClientboundSystemChatPacket.class, ClientboundSystemChatPacket::new).addPacket(PacketPlayOutPlayerListHeaderFooter.class, PacketPlayOutPlayerListHeaderFooter::new).addPacket(PacketPlayOutNBTQuery.class, PacketPlayOutNBTQuery::new).addPacket(PacketPlayOutCollect.class, PacketPlayOutCollect::new).addPacket(PacketPlayOutEntityTeleport.class, PacketPlayOutEntityTeleport::new).addPacket(PacketPlayOutAdvancements.class, PacketPlayOutAdvancements::new).addPacket(PacketPlayOutUpdateAttributes.class, PacketPlayOutUpdateAttributes::new).addPacket(ClientboundUpdateEnabledFeaturesPacket.class, ClientboundUpdateEnabledFeaturesPacket::new).addPacket(PacketPlayOutEntityEffect.class, PacketPlayOutEntityEffect::new).addPacket(PacketPlayOutRecipeUpdate.class, PacketPlayOutRecipeUpdate::new).addPacket(PacketPlayOutTags.class, PacketPlayOutTags::new)).addFlow(EnumProtocolDirection.SERVERBOUND, (new EnumProtocol.a<>()).addPacket(PacketPlayInTeleportAccept.class, PacketPlayInTeleportAccept::new).addPacket(PacketPlayInTileNBTQuery.class, PacketPlayInTileNBTQuery::new).addPacket(PacketPlayInDifficultyChange.class, PacketPlayInDifficultyChange::new).addPacket(ServerboundChatAckPacket.class, ServerboundChatAckPacket::new).addPacket(ServerboundChatCommandPacket.class, ServerboundChatCommandPacket::new).addPacket(PacketPlayInChat.class, PacketPlayInChat::new).addPacket(ServerboundChatSessionUpdatePacket.class, ServerboundChatSessionUpdatePacket::new).addPacket(PacketPlayInClientCommand.class, PacketPlayInClientCommand::new).addPacket(PacketPlayInSettings.class, PacketPlayInSettings::new).addPacket(PacketPlayInTabComplete.class, PacketPlayInTabComplete::new).addPacket(PacketPlayInEnchantItem.class, PacketPlayInEnchantItem::new).addPacket(PacketPlayInWindowClick.class, PacketPlayInWindowClick::new).addPacket(PacketPlayInCloseWindow.class, PacketPlayInCloseWindow::new).addPacket(PacketPlayInCustomPayload.class, PacketPlayInCustomPayload::new).addPacket(PacketPlayInBEdit.class, PacketPlayInBEdit::new).addPacket(PacketPlayInEntityNBTQuery.class, PacketPlayInEntityNBTQuery::new).addPacket(PacketPlayInUseEntity.class, PacketPlayInUseEntity::new).addPacket(PacketPlayInJigsawGenerate.class, PacketPlayInJigsawGenerate::new).addPacket(PacketPlayInKeepAlive.class, PacketPlayInKeepAlive::new).addPacket(PacketPlayInDifficultyLock.class, PacketPlayInDifficultyLock::new).addPacket(PacketPlayInFlying.PacketPlayInPosition.class, PacketPlayInFlying.PacketPlayInPosition::read).addPacket(PacketPlayInFlying.PacketPlayInPositionLook.class, PacketPlayInFlying.PacketPlayInPositionLook::read).addPacket(PacketPlayInFlying.PacketPlayInLook.class, PacketPlayInFlying.PacketPlayInLook::read).addPacket(PacketPlayInFlying.d.class, PacketPlayInFlying.d::read).addPacket(PacketPlayInVehicleMove.class, PacketPlayInVehicleMove::new).addPacket(PacketPlayInBoatMove.class, PacketPlayInBoatMove::new).addPacket(PacketPlayInPickItem.class, PacketPlayInPickItem::new).addPacket(PacketPlayInAutoRecipe.class, PacketPlayInAutoRecipe::new).addPacket(PacketPlayInAbilities.class, PacketPlayInAbilities::new).addPacket(PacketPlayInBlockDig.class, PacketPlayInBlockDig::new).addPacket(PacketPlayInEntityAction.class, PacketPlayInEntityAction::new).addPacket(PacketPlayInSteerVehicle.class, PacketPlayInSteerVehicle::new).addPacket(ServerboundPongPacket.class, ServerboundPongPacket::new).addPacket(PacketPlayInRecipeSettings.class, PacketPlayInRecipeSettings::new).addPacket(PacketPlayInRecipeDisplayed.class, PacketPlayInRecipeDisplayed::new).addPacket(PacketPlayInItemName.class, PacketPlayInItemName::new).addPacket(PacketPlayInResourcePackStatus.class, PacketPlayInResourcePackStatus::new).addPacket(PacketPlayInAdvancements.class, PacketPlayInAdvancements::new).addPacket(PacketPlayInTrSel.class, PacketPlayInTrSel::new).addPacket(PacketPlayInBeacon.class, PacketPlayInBeacon::new).addPacket(PacketPlayInHeldItemSlot.class, PacketPlayInHeldItemSlot::new).addPacket(PacketPlayInSetCommandBlock.class, PacketPlayInSetCommandBlock::new).addPacket(PacketPlayInSetCommandMinecart.class, PacketPlayInSetCommandMinecart::new).addPacket(PacketPlayInSetCreativeSlot.class, PacketPlayInSetCreativeSlot::new).addPacket(PacketPlayInSetJigsaw.class, PacketPlayInSetJigsaw::new).addPacket(PacketPlayInStruct.class, PacketPlayInStruct::new).addPacket(PacketPlayInUpdateSign.class, PacketPlayInUpdateSign::new).addPacket(PacketPlayInArmAnimation.class, PacketPlayInArmAnimation::new).addPacket(PacketPlayInSpectate.class, PacketPlayInSpectate::new).addPacket(PacketPlayInUseItem.class, PacketPlayInUseItem::new).addPacket(PacketPlayInBlockPlace.class, PacketPlayInBlockPlace::new))), STATUS(1, protocol().addFlow(EnumProtocolDirection.SERVERBOUND, (new EnumProtocol.a<>()).addPacket(PacketStatusInStart.class, PacketStatusInStart::new).addPacket(PacketStatusInPing.class, PacketStatusInPing::new)).addFlow(EnumProtocolDirection.CLIENTBOUND, (new EnumProtocol.a<>()).addPacket(PacketStatusOutServerInfo.class, PacketStatusOutServerInfo::new).addPacket(PacketStatusOutPong.class, PacketStatusOutPong::new))), LOGIN(2, protocol().addFlow(EnumProtocolDirection.CLIENTBOUND, (new EnumProtocol.a<>()).addPacket(PacketLoginOutDisconnect.class, PacketLoginOutDisconnect::new).addPacket(PacketLoginOutEncryptionBegin.class, PacketLoginOutEncryptionBegin::new).addPacket(PacketLoginOutSuccess.class, PacketLoginOutSuccess::new).addPacket(PacketLoginOutSetCompression.class, PacketLoginOutSetCompression::new).addPacket(PacketLoginOutCustomPayload.class, PacketLoginOutCustomPayload::new)).addFlow(EnumProtocolDirection.SERVERBOUND, (new EnumProtocol.a<>()).addPacket(PacketLoginInStart.class, PacketLoginInStart::new).addPacket(PacketLoginInEncryptionBegin.class, PacketLoginInEncryptionBegin::new).addPacket(PacketLoginInCustomPayload.class, PacketLoginInCustomPayload::new)));

    public static final int NOT_REGISTERED = -1;
    private static final int MIN_PROTOCOL_ID = -1;
    private static final int MAX_PROTOCOL_ID = 2;
    private static final EnumProtocol[] LOOKUP = new EnumProtocol[4];
    private static final Map<Class<? extends Packet<?>>, EnumProtocol> PROTOCOL_BY_PACKET = Maps.newHashMap();
    private final int id;
    private final Map<EnumProtocolDirection, ? extends EnumProtocol.a<?>> flows;

    private static EnumProtocol.b protocol() {
        return new EnumProtocol.b();
    }

    private EnumProtocol(int i, EnumProtocol.b enumprotocol_b) {
        this.id = i;
        this.flows = enumprotocol_b.flows;
    }

    public int getPacketId(EnumProtocolDirection enumprotocoldirection, Packet<?> packet) {
        return ((EnumProtocol.a) this.flows.get(enumprotocoldirection)).getId(packet.getClass());
    }

    @Override
    public BundlerInfo getBundlerInfo(EnumProtocolDirection enumprotocoldirection) {
        return ((EnumProtocol.a) this.flows.get(enumprotocoldirection)).bundlerInfo();
    }

    @VisibleForDebug
    public Int2ObjectMap<Class<? extends Packet<?>>> getPacketsByIds(EnumProtocolDirection enumprotocoldirection) {
        Int2ObjectMap<Class<? extends Packet<?>>> int2objectmap = new Int2ObjectOpenHashMap();
        EnumProtocol.a<?> enumprotocol_a = (EnumProtocol.a) this.flows.get(enumprotocoldirection);

        if (enumprotocol_a == null) {
            return Int2ObjectMaps.emptyMap();
        } else {
            enumprotocol_a.classToId.forEach((oclass, integer) -> {
                int2objectmap.put(integer, oclass);
            });
            return int2objectmap;
        }
    }

    @Nullable
    public Packet<?> createPacket(EnumProtocolDirection enumprotocoldirection, int i, PacketDataSerializer packetdataserializer) {
        return ((EnumProtocol.a) this.flows.get(enumprotocoldirection)).createPacket(i, packetdataserializer);
    }

    public int getId() {
        return this.id;
    }

    @Nullable
    public static EnumProtocol getById(int i) {
        return i >= -1 && i <= 2 ? EnumProtocol.LOOKUP[i - -1] : null;
    }

    @Nullable
    public static EnumProtocol getProtocolForPacket(Packet<?> packet) {
        return (EnumProtocol) EnumProtocol.PROTOCOL_BY_PACKET.get(packet.getClass());
    }

    static {
        EnumProtocol[] aenumprotocol = values();
        int i = aenumprotocol.length;

        for (int j = 0; j < i; ++j) {
            EnumProtocol enumprotocol = aenumprotocol[j];
            int k = enumprotocol.getId();

            if (k < -1 || k > 2) {
                throw new Error("Invalid protocol ID " + k);
            }

            EnumProtocol.LOOKUP[k - -1] = enumprotocol;
            enumprotocol.flows.forEach((enumprotocoldirection, enumprotocol_a) -> {
                enumprotocol_a.listAllPackets((oclass) -> {
                    if (EnumProtocol.PROTOCOL_BY_PACKET.containsKey(oclass) && EnumProtocol.PROTOCOL_BY_PACKET.get(oclass) != enumprotocol) {
                        throw new IllegalStateException("Packet " + oclass + " is already assigned to protocol " + EnumProtocol.PROTOCOL_BY_PACKET.get(oclass) + " - can't reassign to " + enumprotocol);
                    } else {
                        EnumProtocol.PROTOCOL_BY_PACKET.put(oclass, enumprotocol);
                    }
                });
            });
        }

    }

    private static class b {

        final Map<EnumProtocolDirection, EnumProtocol.a<?>> flows = Maps.newEnumMap(EnumProtocolDirection.class);

        b() {}

        public <T extends PacketListener> EnumProtocol.b addFlow(EnumProtocolDirection enumprotocoldirection, EnumProtocol.a<T> enumprotocol_a) {
            this.flows.put(enumprotocoldirection, enumprotocol_a);
            return this;
        }
    }

    private static class a<T extends PacketListener> {

        private static final Logger LOGGER = LogUtils.getLogger();
        final Object2IntMap<Class<? extends Packet<T>>> classToId = (Object2IntMap) SystemUtils.make(new Object2IntOpenHashMap(), (object2intopenhashmap) -> {
            object2intopenhashmap.defaultReturnValue(-1);
        });
        private final List<Function<PacketDataSerializer, ? extends Packet<T>>> idToDeserializer = Lists.newArrayList();
        private BundlerInfo bundlerInfo;
        private final Set<Class<? extends Packet<T>>> extraClasses;

        a() {
            this.bundlerInfo = BundlerInfo.EMPTY;
            this.extraClasses = new HashSet();
        }

        public <P extends Packet<T>> EnumProtocol.a<T> addPacket(Class<P> oclass, Function<PacketDataSerializer, P> function) {
            int i = this.idToDeserializer.size();
            int j = this.classToId.put(oclass, i);

            if (j != -1) {
                String s = "Packet " + oclass + " is already registered to ID " + j;

                EnumProtocol.a.LOGGER.error(LogUtils.FATAL_MARKER, s);
                throw new IllegalArgumentException(s);
            } else {
                this.idToDeserializer.add(function);
                return this;
            }
        }

        public <P extends BundlePacket<T>> EnumProtocol.a<T> withBundlePacket(Class<P> oclass, Function<Iterable<Packet<T>>, P> function) {
            if (this.bundlerInfo != BundlerInfo.EMPTY) {
                throw new IllegalStateException("Bundle packet already configured");
            } else {
                BundleDelimiterPacket<T> bundledelimiterpacket = new BundleDelimiterPacket<>();

                this.addPacket(BundleDelimiterPacket.class, (packetdataserializer) -> {
                    return bundledelimiterpacket;
                });
                this.bundlerInfo = BundlerInfo.createForPacket(oclass, function, bundledelimiterpacket);
                this.extraClasses.add(oclass);
                return this;
            }
        }

        public int getId(Class<?> oclass) {
            return this.classToId.getInt(oclass);
        }

        @Nullable
        public Packet<?> createPacket(int i, PacketDataSerializer packetdataserializer) {
            Function<PacketDataSerializer, ? extends Packet<T>> function = (Function) this.idToDeserializer.get(i);

            return function != null ? (Packet) function.apply(packetdataserializer) : null;
        }

        public void listAllPackets(Consumer<Class<? extends Packet<?>>> consumer) {
            this.classToId.keySet().stream().filter((oclass) -> {
                return oclass != BundleDelimiterPacket.class;
            }).forEach(consumer);
            this.extraClasses.forEach(consumer);
        }

        public BundlerInfo bundlerInfo() {
            return this.bundlerInfo;
        }
    }
}
