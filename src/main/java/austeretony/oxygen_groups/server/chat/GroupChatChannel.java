package austeretony.oxygen_groups.server.chat;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.common.config.PrivilegesConfig;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
import austeretony.oxygen_core.server.chat.AbstractChatChannel;
import austeretony.oxygen_groups.common.config.GroupsConfig;
import austeretony.oxygen_groups.server.GroupsManagerServer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;

public class GroupChatChannel extends AbstractChatChannel {

    @Override
    public TextFormatting getColor() {
        return TextFormatting.GOLD;
    }

    @Override
    public boolean isEnabled() {
        return GroupsConfig.ENABLE_GROUP_CHAT.asBoolean();
    }

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0)
            throw new WrongUsageException(this.getUsage(sender));   
        EntityPlayerMP playerMP = CommandBase.getCommandSenderAsPlayer(sender);
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (PrivilegesConfig.ENABLE_CUSTOM_FORMATTED_CHAT.asBoolean()) {
            String username = PrivilegesConfig.FORMATTED_CHAT_PATTERN.asString().replace("@username", CommonReference.getName(playerMP));   

            StringBuilder builder = new StringBuilder();
            for (String word : args)
                builder.append(word).append(" ");

            ITextComponent message = new TextComponentString(username);
            message.getStyle().setColor(this.getColor());

            message.appendSibling(ForgeHooks.newChatWithLinks(builder.toString()));

            for (UUID memberUUID : GroupsManagerServer.instance().getGroupsDataContainer().getGroup(playerUUID).getMembers())
                if (OxygenHelperServer.isPlayerOnline(memberUUID))
                    CommonReference.playerByUUID(memberUUID).sendMessage(message);
        } else {
            String username = String.format("<%s> ", CommonReference.getName(playerMP));

            StringBuilder builder = new StringBuilder();
            for (String word : args)
                builder.append(word).append(" ");

            ITextComponent message = new TextComponentString(username);
            message.getStyle().setColor(this.getColor());

            message.appendSibling(ForgeHooks.newChatWithLinks(builder.toString()));

            for (UUID memberUUID : GroupsManagerServer.instance().getGroupsDataContainer().getGroup(playerUUID).getMembers())
                if (OxygenHelperServer.isPlayerOnline(memberUUID))
                    CommonReference.playerByUUID(memberUUID).sendMessage(message);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP 
                && GroupsManagerServer.instance().getGroupsDataContainer().getGroup(CommonReference.getPersistentUUID((EntityPlayerMP) sender)) != null;
    }
}
