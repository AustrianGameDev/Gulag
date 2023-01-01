package at.agd.gulag.commands;

import at.agd.gulag.world.dimension.GulagTeleportHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class GulagLeaveCommand
{
    public GulagLeaveCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("gulag").then(Commands.literal("leave").executes(command -> {
            return leaveGulag(command.getSource());
        })));
    }

    private int leaveGulag(CommandSource source) throws CommandSyntaxException
    {
        return GulagTeleportHelper.leaveGulag(source);
    }
}
