package austeretony.oxygen_groups.common.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import austeretony.oxygen_core.client.api.OxygenGUIHelper;
import austeretony.oxygen_core.client.api.OxygenHelperClient;
import austeretony.oxygen_core.client.command.CommandOxygenClient;
import austeretony.oxygen_core.client.interaction.InteractionHelperClient;
import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.api.OxygenHelperCommon;
import austeretony.oxygen_core.common.main.OxygenMain;
import austeretony.oxygen_core.server.api.RequestsFilterHelper;
import austeretony.oxygen_groups.client.GroupsManagerClient;
import austeretony.oxygen_groups.client.GroupsStatusMessagesHandler;
import austeretony.oxygen_groups.client.command.GroupsArgumentExecutorClient;
import austeretony.oxygen_groups.client.event.GroupsEventsClient;
import austeretony.oxygen_groups.client.gui.context.InviteToGroupContextAction;
import austeretony.oxygen_groups.client.gui.group.GroupMenuGUIScreen;
import austeretony.oxygen_groups.client.gui.interaction.InviteToGroupInteractionExecutor;
import austeretony.oxygen_groups.client.gui.overlay.GroupMarkRenderer;
import austeretony.oxygen_groups.client.gui.overlay.GroupOverlay;
import austeretony.oxygen_groups.client.input.GroupsKeyHandler;
import austeretony.oxygen_groups.common.command.CommandGroupMessage;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.common.network.client.CPAddPlayerToGroup;
import austeretony.oxygen_groups.common.network.client.CPLeaveGroup;
import austeretony.oxygen_groups.common.network.client.CPRemovePlayerFromGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroup;
import austeretony.oxygen_groups.common.network.client.CPSyncGroupData;
import austeretony.oxygen_groups.common.network.client.CPUpdateLeader;
import austeretony.oxygen_groups.common.network.server.SPInviteToGroup;
import austeretony.oxygen_groups.common.network.server.SPLeaveGroup;
import austeretony.oxygen_groups.common.network.server.SPPromoteToLeader;
import austeretony.oxygen_groups.common.network.server.SPStartKickPlayerVoting;
import austeretony.oxygen_groups.common.network.server.SPStartReadinessCheck;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import austeretony.oxygen_groups.server.event.GroupsEventsServer;
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
        dependencies = "required-after:oxygen_core@[0.9.3,);",
        certificateFingerprint = "@FINGERPRINT@",
        updateJSON = GroupsMain.VERSIONS_FORGE_URL)
public class GroupsMain {

    public static final String 
    MODID = "oxygen_groups", 
    NAME = "Oxygen: Groups", 
    VERSION = "0.9.1", 
    VERSION_CUSTOM = VERSION + ":beta:0",
    GAME_VERSION = "1.12.2",
    VERSIONS_FORGE_URL = "https://raw.githubusercontent.com/AustereTony-MCMods/Oxygen-Groups/info/mod_versions_forge.json";

    public static final int 
    GROUPS_MOD_INDEX = 2,

    GROUP_REQUEST_ID = 20,
    READINESS_CHECK_REQUEST_ID = 21,
    VOTE_KICK_REQUEST_ID = 22,

    GROUP_MENU_SCREEN_ID = 20,

    INVITE_TO_GROUP_REQUEST_ID = 25, 
    LEAVE_GROUP_REQUEST_ID = 26, 
    START_READINESS_CHECK_REQUEST_ID = 27,
    START_KICK_PLAYER_VOTING_REQUEST_ID = 28,
    PROMOTE_TO_LEADER_REQUEST_ID = 29,

    HIDE_GROUP_OVERLAY_SETTING_ID = 20,
    AUTO_ACCEPT_GROUP_INVITE_SETTING_ID = 21;

    public static final Logger LOGGER = LogManager.getLogger(NAME);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        OxygenHelperCommon.registerConfig(new GroupsConfig());
        if (event.getSide() == Side.CLIENT)
            CommandOxygenClient.registerArgumentExecutor(new GroupsArgumentExecutorClient("groups", true));
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        this.initNetwork();
        GroupsManagerServer.create();
        CommonReference.registerEvent(new GroupsEventsServer());
        RequestsFilterHelper.registerNetworkRequest(INVITE_TO_GROUP_REQUEST_ID, 1);
        RequestsFilterHelper.registerNetworkRequest(LEAVE_GROUP_REQUEST_ID, 5);
        RequestsFilterHelper.registerNetworkRequest(START_READINESS_CHECK_REQUEST_ID, 5);
        RequestsFilterHelper.registerNetworkRequest(START_KICK_PLAYER_VOTING_REQUEST_ID, 5);
        RequestsFilterHelper.registerNetworkRequest(PROMOTE_TO_LEADER_REQUEST_ID, 5);
        if (event.getSide() == Side.CLIENT) {
            GroupsManagerClient.create();       
            CommonReference.registerEvent(new GroupsEventsClient());
            CommonReference.registerEvent(new GroupMarkRenderer());
            if (!OxygenGUIHelper.isOxygenMenuEnabled())
                CommonReference.registerEvent(new GroupsKeyHandler());
            OxygenGUIHelper.registerScreenId(GROUP_MENU_SCREEN_ID);
            InteractionHelperClient.registerInteractionMenuEntry(new InviteToGroupInteractionExecutor());
            OxygenGUIHelper.registerContextAction(50, new InviteToGroupContextAction());//50 - players list menu id
            OxygenGUIHelper.registerContextAction(60, new InviteToGroupContextAction());//60 - friends list menu id
            OxygenHelperClient.registerClientSetting(HIDE_GROUP_OVERLAY_SETTING_ID);
            OxygenHelperClient.registerClientSetting(AUTO_ACCEPT_GROUP_INVITE_SETTING_ID);
            OxygenGUIHelper.registerOverlay(new GroupOverlay());
            OxygenGUIHelper.registerOxygenMenuEntry(GroupMenuGUIScreen.GROUP_MENU_ENTRY);
            OxygenHelperClient.registerStatusMessagesHandler(new GroupsStatusMessagesHandler());
            OxygenHelperClient.registerSharedDataSyncListener(GROUP_MENU_SCREEN_ID, 
                    ()->GroupsManagerClient.instance().getGroupMenuManager().sharedDataSynchronized());
        }
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) { 
        CommonReference.registerCommand(event, new CommandGroupMessage());
    }

    private void initNetwork() {
        OxygenMain.network().registerPacket(CPLeaveGroup.class);
        OxygenMain.network().registerPacket(CPSyncGroup.class);
        OxygenMain.network().registerPacket(CPAddPlayerToGroup.class);
        OxygenMain.network().registerPacket(CPRemovePlayerFromGroup.class);
        OxygenMain.network().registerPacket(CPUpdateLeader.class);
        OxygenMain.network().registerPacket(CPSyncGroupData.class);

        OxygenMain.network().registerPacket(SPInviteToGroup.class);
        OxygenMain.network().registerPacket(SPLeaveGroup.class);
        OxygenMain.network().registerPacket(SPStartReadinessCheck.class);
        OxygenMain.network().registerPacket(SPStartKickPlayerVoting.class);
        OxygenMain.network().registerPacket(SPPromoteToLeader.class);
    }
}
