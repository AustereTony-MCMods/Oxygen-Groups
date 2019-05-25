package austeretony.oxygen_groups.common.main;

import austeretony.oxygen.client.core.api.ClientReference;
import net.minecraft.util.text.TextComponentTranslation;

public enum EnumGroupsChatMessages {

    GROUP_REQUEST_ACCEPTED_SENDER,
    GROUP_REQUEST_ACCEPTED_TARGET,
    GROUP_REQUEST_REJECTED_SENDER,
    GROUP_REQUEST_REJECTED_TARGET,
    GROUP_READINESS_CHECK_STARTED,
    GROUP_READY,
    GROUP_NOT_READY,
    KICK_PLAYER_VOTING_STARTED,
    PLAYER_KICKED,
    PLAYER_NOT_KICKED;
    
    public void show(String... args) {
        switch (this) {
        case GROUP_REQUEST_ACCEPTED_SENDER:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.groupRequest.sender.accepted"));
            break;
        case GROUP_REQUEST_ACCEPTED_TARGET:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.groupRequest.target.accepted"));
            break;
        case GROUP_REQUEST_REJECTED_SENDER:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.groupRequest.sender.rejected"));
            break;
        case GROUP_REQUEST_REJECTED_TARGET:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.groupRequest.target.rejected"));
            break;
        case GROUP_READINESS_CHECK_STARTED:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.readinessCheck.started"));
            break;
        case GROUP_READY:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.readinessCheck.ready"));
            break;
        case GROUP_NOT_READY:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.readinessCheck.notReady"));
            break;
        case KICK_PLAYER_VOTING_STARTED:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.playerKickVoting.started", args[0]));
            break;
        case PLAYER_KICKED:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.playerKickVoting.kicked", args[0]));
            break;
        case PLAYER_NOT_KICKED:
            ClientReference.showMessage(new TextComponentTranslation("groups.message.playerKickVoting.notKicked", args[0]));
            break;
        }
    }
}
