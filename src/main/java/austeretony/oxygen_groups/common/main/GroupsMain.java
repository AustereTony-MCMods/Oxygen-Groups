package austeretony.oxygen_groups.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen.client.api.OxygenGUIHelper;
import austeretony.oxygen.client.api.OxygenHelperClient;
import austeretony.oxygen.client.command.CommandOxygenClient;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.interaction.InteractionHelperClient;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.OxygenMain;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.command.GroupsArgumentExecutorClient;
import austeretony.oxygen_groups.client.event.GroupsEventsClient;
import austeretony.oxygen_groups.client.gui.InviteToGroupContextAction;
import austeretony.oxygen_groups.client.gui.InviteToGroupInteractionExecutor;
import austeretony.oxygen_groups.client.gui.group.GroupMenuGUIHandler;
import austeretony.oxygen_groups.client.gui.overlay.GroupMarkRenderer;
import austeretony.oxygen_groups.client.gui.overlay.GroupOverlay;
import austeretony.oxygen_groups.client.input.GroupsKeyHandler;
import austeretony.oxygen_groups.common.GroupsManagerServer;
import austeretony.oxygen_groups.common.command.CommandGroupMessage;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.event.GroupsEventsServer;
import austeretony.oxygen_groups.common.network.client.CPAddPlayerToGroup;
import austeretony.oxygen_groups.common.network.client.CPGroupsCommand;
import austeretony.oxygen_groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroupOnLoad;
import austeretony.oxygen_groups.common.network.client.CPSyncPlayersHealth;
import austeretony.oxygen_groups.common.network.client.CPUpdateLeader;
import austeretony.oxygen_groups.common.network.server.SPGroupsRequest;
import austeretony.oxygen_groups.common.network.server.SPInviteToGroup;
import austeretony.oxygen_groups.common.network.server.SPPromoteToLeader;
import austeretony.oxygen_groups.common.network.server.SPStartKickPlayerVoting;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(
        modid = GroupsMain.MODID, 
        name = GroupsMain.NAME, 
        version = GroupsMain.VERSION,
        dependencies = "required-after:oxygen@[0.8.0,);",//TODO Always check required Oxygen version before build
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = GroupsMain.VERSIONS_FORGE_URL)
public class GroupsMain {

    public static final String 
    MODID = "oxygen_groups", 
    NAME = "Oxygen: Groups", 
    VERSION = "0.8.1", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Groups/info/mod_versions_forge.json";

    public static final int 
    GROUPS_MOD_INDEX = 2,//Oxygen - 0, Teleportation - 1, Exchange - 3, Merchants - 4, Players List - 5, Friends List - 6, Interaction - 7, Mail - 8, Chat - 9

    GROUP_REQUEST_ID = 20,
    READINESS_CHECK_REQUEST_ID = 21,
    VOTE_KICK_REQUEST_ID = 22,

    GROUP_MENU_SCREEN_ID = 20,

    HIDE_GROUP_OVERLAY_SETTING_ID = 20,
    AUTO_ACCEPT_GROUP_INVITE_SETTING_ID = 21;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static OxygenNetwork network, routineNetwork;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperServer.registerConfig(new GroupsConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgumentExecutor(new GroupsArgumentExecutorClient("groups", true));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        GroupsManagerServer.create();
        CommonReference.registerEvent(new GroupsEventsServer());
        OxygenHelperServer.registerSharedDataIdentifierForScreen(GROUP_MENU_SCREEN_ID, OxygenMain.ACTIVITY_STATUS_SHARED_DATA_ID);
        OxygenHelperServer.registerSharedDataIdentifierForScreen(GROUP_MENU_SCREEN_ID, OxygenMain.DIMENSION_SHARED_DATA_ID);
        OxygenHelperServer.addPersistentServiceProcess(new GroupDataSyncProcess());
        if (event.getSide() == Side.CLIENT) {
            GroupsManagerClient.create();       
            CommonReference.registerEvent(new GroupsEventsClient());
            CommonReference.registerEvent(new GroupsKeyHandler());
            CommonReference.registerEvent(new GroupMarkRenderer());       
            OxygenGUIHelper.registerScreenId(GROUP_MENU_SCREEN_ID);
            OxygenGUIHelper.registerSharedDataListenerScreen(GROUP_MENU_SCREEN_ID, new GroupMenuGUIHandler());
            InteractionHelperClient.registerInteractionMenuAction(new InviteToGroupInteractionExecutor());
            OxygenGUIHelper.registerContextAction(50, new InviteToGroupContextAction());//50 - players list menu id
            OxygenGUIHelper.registerContextAction(60, new InviteToGroupContextAction());//60 - friends list menu id
            OxygenHelperClient.registerNotificationIcon(GROUP_REQUEST_ID, OxygenGUITextures.REQUEST_ICONS);
            OxygenHelperClient.registerNotificationIcon(READINESS_CHECK_REQUEST_ID, OxygenGUITextures.REQUEST_ICONS);
            OxygenHelperClient.registerNotificationIcon(VOTE_KICK_REQUEST_ID, OxygenGUITextures.REQUEST_ICONS);
            OxygenHelperClient.registerClientSetting(HIDE_GROUP_OVERLAY_SETTING_ID);
            OxygenHelperClient.registerClientSetting(AUTO_ACCEPT_GROUP_INVITE_SETTING_ID);
            OxygenGUIHelper.registerOverlay(new GroupOverlay());
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) { 
        CommonReference.registerCommand(event, new CommandGroupMessage());
        GroupsManagerServer.instance().reset();
        OxygenHelperServer.loadPersistentDataDelegated(GroupsManagerServer.instance());
    }

    private void initNetwork() {
        network = OxygenHelperServer.createNetworkHandler(MODID);

        network.registerPacket(CPGroupsCommand.class);
        network.registerPacket(CPSyncGroupOnLoad.class);
        network.registerPacket(CPSyncGroup.class);
        network.registerPacket(CPAddPlayerToGroup.class);
        network.registerPacket(CPRemovePlayerFromGroup.class);
        network.registerPacket(CPUpdateLeader.class);

        network.registerPacket(SPGroupsRequest.class);
        network.registerPacket(SPInviteToGroup.class);
        network.registerPacket(SPStartKickPlayerVoting.class);
        network.registerPacket(SPPromoteToLeader.class);

        routineNetwork = OxygenHelperServer.createNetworkHandler(MODID + "_r");

        routineNetwork.registerPacket(CPSyncPlayersHealth.class);
    }

    public static OxygenNetwork network() {
        return network;
    }

    public static OxygenNetwork routineNetwork() {
        return routineNetwork;
    }
}
