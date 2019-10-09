package austeretony.oxygen_groups.common.main;

import austeretony.oxygen_core.client.api.ClientReference;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumGroupsChatMessage {

    GROUP_READINESS_CHECK_STARTED,
    GROUP_READY,
    GROUP_NOT_READY,
    KICK_PLAYER_VOTING_STARTED,
    PLAYER_KICKED,
    PLAYER_NOT_KICKED;

    public void show(String... args) {
        switch (this) {
        case GROUP_READINESS_CHECK_STARTED:
            ClientReference.showChatMessage(new TextComponentTranslation("oxygen_groups.message.readinessCheck.started"));
            break;
        case GROUP_READY:
            ClientReference.showChatMessage(new TextComponentTranslation("oxygen_groups.message.readinessCheck.ready"));
            break;
        case GROUP_NOT_READY:
            ClientReference.showChatMessage(new TextComponentTranslation("oxygen_groups.message.readinessCheck.notReady"));
            break;
        case KICK_PLAYER_VOTING_STARTED:
            ClientReference.showChatMessage(new TextComponentTranslation("oxygen_groups.message.playerKickVoting.started", args[0]));
            break;
        case PLAYER_KICKED:
            ClientReference.showChatMessage(new TextComponentTranslation("oxygen_groups.message.playerKickVoting.kicked", args[0]));
            break;
        case PLAYER_NOT_KICKED:
            ClientReference.showChatMessage(new TextComponentTranslation("oxygen_groups.message.playerKickVoting.notKicked", args[0]));
            break;
        }
    }
}
