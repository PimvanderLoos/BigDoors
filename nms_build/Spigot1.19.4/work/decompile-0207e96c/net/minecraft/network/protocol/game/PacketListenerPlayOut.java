package net.minecraft.network.protocol.game;

import net.minecraft.network.PacketListener;

public interface PacketListenerPlayOut extends PacketListener {

    void handleAddEntity(PacketPlayOutSpawnEntity packetplayoutspawnentity);

    void handleAddExperienceOrb(PacketPlayOutSpawnEntityExperienceOrb packetplayoutspawnentityexperienceorb);

    void handleAddObjective(PacketPlayOutScoreboardObjective packetplayoutscoreboardobjective);

    void handleAddPlayer(PacketPlayOutNamedEntitySpawn packetplayoutnamedentityspawn);

    void handleAnimate(PacketPlayOutAnimation packetplayoutanimation);

    void handleHurtAnimation(ClientboundHurtAnimationPacket clientboundhurtanimationpacket);

    void handleAwardStats(PacketPlayOutStatistic packetplayoutstatistic);

    void handleAddOrRemoveRecipes(PacketPlayOutRecipes packetplayoutrecipes);

    void handleBlockDestruction(PacketPlayOutBlockBreakAnimation packetplayoutblockbreakanimation);

    void handleOpenSignEditor(PacketPlayOutOpenSignEditor packetplayoutopensigneditor);

    void handleBlockEntityData(PacketPlayOutTileEntityData packetplayouttileentitydata);

    void handleBlockEvent(PacketPlayOutBlockAction packetplayoutblockaction);

    void handleBlockUpdate(PacketPlayOutBlockChange packetplayoutblockchange);

    void handleSystemChat(ClientboundSystemChatPacket clientboundsystemchatpacket);

    void handlePlayerChat(ClientboundPlayerChatPacket clientboundplayerchatpacket);

    void handleDisguisedChat(ClientboundDisguisedChatPacket clientbounddisguisedchatpacket);

    void handleDeleteChat(ClientboundDeleteChatPacket clientbounddeletechatpacket);

    void handleChunkBlocksUpdate(PacketPlayOutMultiBlockChange packetplayoutmultiblockchange);

    void handleMapItemData(PacketPlayOutMap packetplayoutmap);

    void handleContainerClose(PacketPlayOutCloseWindow packetplayoutclosewindow);

    void handleContainerContent(PacketPlayOutWindowItems packetplayoutwindowitems);

    void handleHorseScreenOpen(PacketPlayOutOpenWindowHorse packetplayoutopenwindowhorse);

    void handleContainerSetData(PacketPlayOutWindowData packetplayoutwindowdata);

    void handleContainerSetSlot(PacketPlayOutSetSlot packetplayoutsetslot);

    void handleCustomPayload(PacketPlayOutCustomPayload packetplayoutcustompayload);

    void handleDisconnect(PacketPlayOutKickDisconnect packetplayoutkickdisconnect);

    void handleEntityEvent(PacketPlayOutEntityStatus packetplayoutentitystatus);

    void handleEntityLinkPacket(PacketPlayOutAttachEntity packetplayoutattachentity);

    void handleSetEntityPassengersPacket(PacketPlayOutMount packetplayoutmount);

    void handleExplosion(PacketPlayOutExplosion packetplayoutexplosion);

    void handleGameEvent(PacketPlayOutGameStateChange packetplayoutgamestatechange);

    void handleKeepAlive(PacketPlayOutKeepAlive packetplayoutkeepalive);

    void handleLevelChunkWithLight(ClientboundLevelChunkWithLightPacket clientboundlevelchunkwithlightpacket);

    void handleChunksBiomes(ClientboundChunksBiomesPacket clientboundchunksbiomespacket);

    void handleForgetLevelChunk(PacketPlayOutUnloadChunk packetplayoutunloadchunk);

    void handleLevelEvent(PacketPlayOutWorldEvent packetplayoutworldevent);

    void handleLogin(PacketPlayOutLogin packetplayoutlogin);

    void handleMoveEntity(PacketPlayOutEntity packetplayoutentity);

    void handleMovePlayer(PacketPlayOutPosition packetplayoutposition);

    void handleParticleEvent(PacketPlayOutWorldParticles packetplayoutworldparticles);

    void handlePing(ClientboundPingPacket clientboundpingpacket);

    void handlePlayerAbilities(PacketPlayOutAbilities packetplayoutabilities);

    void handlePlayerInfoRemove(ClientboundPlayerInfoRemovePacket clientboundplayerinforemovepacket);

    void handlePlayerInfoUpdate(ClientboundPlayerInfoUpdatePacket clientboundplayerinfoupdatepacket);

    void handleRemoveEntities(PacketPlayOutEntityDestroy packetplayoutentitydestroy);

    void handleRemoveMobEffect(PacketPlayOutRemoveEntityEffect packetplayoutremoveentityeffect);

    void handleRespawn(PacketPlayOutRespawn packetplayoutrespawn);

    void handleRotateMob(PacketPlayOutEntityHeadRotation packetplayoutentityheadrotation);

    void handleSetCarriedItem(PacketPlayOutHeldItemSlot packetplayouthelditemslot);

    void handleSetDisplayObjective(PacketPlayOutScoreboardDisplayObjective packetplayoutscoreboarddisplayobjective);

    void handleSetEntityData(PacketPlayOutEntityMetadata packetplayoutentitymetadata);

    void handleSetEntityMotion(PacketPlayOutEntityVelocity packetplayoutentityvelocity);

    void handleSetEquipment(PacketPlayOutEntityEquipment packetplayoutentityequipment);

    void handleSetExperience(PacketPlayOutExperience packetplayoutexperience);

    void handleSetHealth(PacketPlayOutUpdateHealth packetplayoutupdatehealth);

    void handleSetPlayerTeamPacket(PacketPlayOutScoreboardTeam packetplayoutscoreboardteam);

    void handleSetScore(PacketPlayOutScoreboardScore packetplayoutscoreboardscore);

    void handleSetSpawn(PacketPlayOutSpawnPosition packetplayoutspawnposition);

    void handleSetTime(PacketPlayOutUpdateTime packetplayoutupdatetime);

    void handleSoundEvent(PacketPlayOutNamedSoundEffect packetplayoutnamedsoundeffect);

    void handleSoundEntityEvent(PacketPlayOutEntitySound packetplayoutentitysound);

    void handleTakeItemEntity(PacketPlayOutCollect packetplayoutcollect);

    void handleTeleportEntity(PacketPlayOutEntityTeleport packetplayoutentityteleport);

    void handleUpdateAttributes(PacketPlayOutUpdateAttributes packetplayoutupdateattributes);

    void handleUpdateMobEffect(PacketPlayOutEntityEffect packetplayoutentityeffect);

    void handleUpdateTags(PacketPlayOutTags packetplayouttags);

    void handlePlayerCombatEnd(ClientboundPlayerCombatEndPacket clientboundplayercombatendpacket);

    void handlePlayerCombatEnter(ClientboundPlayerCombatEnterPacket clientboundplayercombatenterpacket);

    void handlePlayerCombatKill(ClientboundPlayerCombatKillPacket clientboundplayercombatkillpacket);

    void handleChangeDifficulty(PacketPlayOutServerDifficulty packetplayoutserverdifficulty);

    void handleSetCamera(PacketPlayOutCamera packetplayoutcamera);

    void handleInitializeBorder(ClientboundInitializeBorderPacket clientboundinitializeborderpacket);

    void handleSetBorderLerpSize(ClientboundSetBorderLerpSizePacket clientboundsetborderlerpsizepacket);

    void handleSetBorderSize(ClientboundSetBorderSizePacket clientboundsetbordersizepacket);

    void handleSetBorderWarningDelay(ClientboundSetBorderWarningDelayPacket clientboundsetborderwarningdelaypacket);

    void handleSetBorderWarningDistance(ClientboundSetBorderWarningDistancePacket clientboundsetborderwarningdistancepacket);

    void handleSetBorderCenter(ClientboundSetBorderCenterPacket clientboundsetbordercenterpacket);

    void handleTabListCustomisation(PacketPlayOutPlayerListHeaderFooter packetplayoutplayerlistheaderfooter);

    void handleResourcePack(PacketPlayOutResourcePackSend packetplayoutresourcepacksend);

    void handleBossUpdate(PacketPlayOutBoss packetplayoutboss);

    void handleItemCooldown(PacketPlayOutSetCooldown packetplayoutsetcooldown);

    void handleMoveVehicle(PacketPlayOutVehicleMove packetplayoutvehiclemove);

    void handleUpdateAdvancementsPacket(PacketPlayOutAdvancements packetplayoutadvancements);

    void handleSelectAdvancementsTab(PacketPlayOutSelectAdvancementTab packetplayoutselectadvancementtab);

    void handlePlaceRecipe(PacketPlayOutAutoRecipe packetplayoutautorecipe);

    void handleCommands(PacketPlayOutCommands packetplayoutcommands);

    void handleStopSoundEvent(PacketPlayOutStopSound packetplayoutstopsound);

    void handleCommandSuggestions(PacketPlayOutTabComplete packetplayouttabcomplete);

    void handleUpdateRecipes(PacketPlayOutRecipeUpdate packetplayoutrecipeupdate);

    void handleLookAt(PacketPlayOutLookAt packetplayoutlookat);

    void handleTagQueryPacket(PacketPlayOutNBTQuery packetplayoutnbtquery);

    void handleLightUpdatePacket(PacketPlayOutLightUpdate packetplayoutlightupdate);

    void handleOpenBook(PacketPlayOutOpenBook packetplayoutopenbook);

    void handleOpenScreen(PacketPlayOutOpenWindow packetplayoutopenwindow);

    void handleMerchantOffers(PacketPlayOutOpenWindowMerchant packetplayoutopenwindowmerchant);

    void handleSetChunkCacheRadius(PacketPlayOutViewDistance packetplayoutviewdistance);

    void handleSetSimulationDistance(ClientboundSetSimulationDistancePacket clientboundsetsimulationdistancepacket);

    void handleSetChunkCacheCenter(PacketPlayOutViewCentre packetplayoutviewcentre);

    void handleBlockChangedAck(ClientboundBlockChangedAckPacket clientboundblockchangedackpacket);

    void setActionBarText(ClientboundSetActionBarTextPacket clientboundsetactionbartextpacket);

    void setSubtitleText(ClientboundSetSubtitleTextPacket clientboundsetsubtitletextpacket);

    void setTitleText(ClientboundSetTitleTextPacket clientboundsettitletextpacket);

    void setTitlesAnimation(ClientboundSetTitlesAnimationPacket clientboundsettitlesanimationpacket);

    void handleTitlesClear(ClientboundClearTitlesPacket clientboundcleartitlespacket);

    void handleServerData(ClientboundServerDataPacket clientboundserverdatapacket);

    void handleCustomChatCompletions(ClientboundCustomChatCompletionsPacket clientboundcustomchatcompletionspacket);

    void handleEnabledFeatures(ClientboundUpdateEnabledFeaturesPacket clientboundupdateenabledfeaturespacket);

    void handleBundlePacket(ClientboundBundlePacket clientboundbundlepacket);

    void handleDamageEvent(ClientboundDamageEventPacket clientbounddamageeventpacket);
}
