package austeretony.oxygen_groups.common.command;

import java.util.UUID;

import austeretony.oxygen_core.common.api.CommonReference;
import austeretony.oxygen_core.server.api.OxygenHelperServer;
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

public class CommandGroupMessage extends CommandBase {

    @Override
    public String getName() {
        return "group";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/group <message>, available while in group only.";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0)
            throw new WrongUsageException(this.getUsage(sender));   
        EntityPlayerMP playerMP = getCommandSenderAsPlayer(sender);
        UUID playerUUID = CommonReference.getPersistentUUID(playerMP);
        if (GroupsManagerServer.instance().getGroupsDataContainer().haveGroup(playerUUID)) {
            StringBuilder builder = new StringBuilder()
                    .append("<")
                    .append(CommonReference.getName(playerMP))
                    .append("> ");
            for (String s : args)
                builder.append(s).append(" ");
            ITextComponent msg = new TextComponentString(builder.toString());
            msg.getStyle().setColor(TextFormatting.YELLOW);
            for (UUID uuid : GroupsManagerServer.instance().getGroupsDataContainer().getGroup(playerUUID).getPlayers())
                if (OxygenHelperServer.isPlayerOnline(uuid))
                    CommonReference.playerByUUID(uuid).sendMessage(msg);
        } else
            throw new WrongUsageException(this.getUsage(sender));   
    }
}
