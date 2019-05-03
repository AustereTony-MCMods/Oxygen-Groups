package austeretony.groups.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.groups.client.GroupsManagerClient;
import austeretony.groups.client.event.GroupsEventsClient;
import austeretony.groups.client.gui.InviteToGroupContextAction;
import austeretony.groups.client.gui.InviteToGroupInteractionExecutor;
import austeretony.groups.client.gui.overlay.GroupGUIOverlay;
import austeretony.groups.client.input.GroupsKeyHandler;
import austeretony.groups.common.GroupsManagerServer;
import austeretony.groups.common.command.CommandGroupMessage;
import austeretony.groups.common.config.GroupsConfig;
import austeretony.groups.common.event.GroupsEventsServer;
import austeretony.groups.common.network.client.CPAddPlayerToGroup;
import austeretony.groups.common.network.client.CPGroupsCommand;
import austeretony.groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.groups.common.network.client.CPSyncGroup;
import austeretony.groups.common.network.client.CPSyncPlayersHealth;
import austeretony.groups.common.network.client.CPUpdateLeader;
import austeretony.groups.common.network.server.SPGroupsRequest;
import austeretony.groups.common.network.server.SPInviteToGroup;
import austeretony.groups.common.network.server.SPPromoteToLeader;
import austeretony.groups.common.network.server.SPStartKickPlayerVoting;
import austeretony.oxygen.client.gui.OxygenGUITextures;
import austeretony.oxygen.client.gui.playerlist.context.AddToFriendsContextAction;
import austeretony.oxygen.client.gui.playerlist.context.IgnoreContextAction;
import austeretony.oxygen.common.api.OxygenGUIHelper;
import austeretony.oxygen.common.api.OxygenHelperClient;
import austeretony.oxygen.common.api.OxygenHelperServer;
import austeretony.oxygen.common.api.network.OxygenNetwork;
import austeretony.oxygen.common.core.api.CommonReference;
import austeretony.oxygen.common.main.OxygenMain;
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
        dependencies = "required-after:oxygen@[0.4.1,);",//TODO Always check required Oxygen version before build
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = GroupsMain.VERSIONS_FORGE_URL)
public class GroupsMain {

    public static final String 
    MODID = "groups", 
    NAME = "Groups", 
    VERSION = "0.1.0", 
    VERSION_CUSTOM = VERSION + ":alpha:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Groups/info/mod_versions_forge.json";

    public static final int 
    GROUPS_MOD_INDEX = 2,//Oxygen - 0, Teleportation - 1

    GROUP_REQUEST_ID = 20,
    READINESS_CHECK_REQUEST_ID = 21,
    VOTE_KICK_REQUEST_ID = 22,

    GROUP_MENU_SCREEN_ID = 20;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    private static OxygenNetwork network, routineNetwork;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperServer.registerConfig(new GroupsConfig());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();

        GroupsManagerServer.create();

        CommonReference.registerEvent(new GroupsEventsServer());

        OxygenHelperServer.registerSharedDataIdentifierForScreen(GROUP_MENU_SCREEN_ID, OxygenMain.STATUS_DATA_ID);
        OxygenHelperServer.registerSharedDataIdentifierForScreen(GROUP_MENU_SCREEN_ID, OxygenMain.DIMENSION_DATA_ID);

        if (event.getSide() == Side.CLIENT) {
            GroupsManagerClient.create();

            CommonReference.registerEvent(new GroupsEventsClient());
            CommonReference.registerEvent(new GroupsKeyHandler());
            CommonReference.registerEvent(new GroupGUIOverlay());

            OxygenGUIHelper.registerScreenId(GROUP_MENU_SCREEN_ID);

            OxygenGUIHelper.registerSharedDataListenerScreen(GROUP_MENU_SCREEN_ID);

            OxygenHelperClient.registerInteractionMenuAction(new InviteToGroupInteractionExecutor());

            OxygenGUIHelper.registerContextAction(OxygenMain.PLAYER_LIST_SCREEN_ID, new InviteToGroupContextAction());
            OxygenGUIHelper.registerContextAction(OxygenMain.FRIEND_LIST_SCREEN_ID, new InviteToGroupContextAction());
            OxygenGUIHelper.registerContextAction(GROUP_MENU_SCREEN_ID, new AddToFriendsContextAction());
            OxygenGUIHelper.registerContextAction(GROUP_MENU_SCREEN_ID, new IgnoreContextAction());

            OxygenHelperClient.registerNotificationIcon(GROUP_REQUEST_ID, OxygenGUITextures.REQUEST_ICON);
            OxygenHelperClient.registerNotificationIcon(READINESS_CHECK_REQUEST_ID, OxygenGUITextures.REQUEST_ICON);
            OxygenHelperClient.registerNotificationIcon(VOTE_KICK_REQUEST_ID, OxygenGUITextures.REQUEST_ICON);
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) { 
        CommonReference.registerCommand(event, new CommandGroupMessage());
        GroupsManagerServer.instance().reset();
        OxygenHelperServer.loadWorldDataDelegated(GroupsManagerServer.instance());
    }

    private void initNetwork() {
        network = OxygenHelperServer.createNetworkHandler("oxygen:" + MODID);

        network.registerPacket(CPGroupsCommand.class);
        network.registerPacket(CPSyncGroup.class);
        network.registerPacket(CPAddPlayerToGroup.class);
        network.registerPacket(CPRemovePlayerFromGroup.class);
        network.registerPacket(CPUpdateLeader.class);

        network.registerPacket(SPGroupsRequest.class);
        network.registerPacket(SPInviteToGroup.class);
        network.registerPacket(SPStartKickPlayerVoting.class);
        network.registerPacket(SPPromoteToLeader.class);

        routineNetwork = OxygenHelperServer.createNetworkHandler("oxygen:" + MODID + ":r");

        routineNetwork.registerPacket(CPSyncPlayersHealth.class);
    }

    public static OxygenNetwork network() {
        return network;
    }

    public static OxygenNetwork routineNetwork() {
        return routineNetwork;
    }
}
