package austeretony.oxygen_groups.client.chat;

import austeretony.oxygen_core.client.chat.ChatChannelProperties;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import net.minecraft.util.text.TextFormatting;

public class GroupChatChannelProperties implements ChatChannelProperties {

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public TextFormatting getColor() {
        return TextFormatting.GOLD;
    }

    @Override
    public boolean available() {
        return GroupsConfig.ENABLE_GROUP_CHAT.asBoolean();
    }
}
