package at.agd.gulag.commands;

import at.agd.gulag.world.dimension.GulagTeleportHelper;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class GulagVisitCommand
{
    public GulagVisitCommand(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("gulag").then(Commands.literal("visit").executes(command -> {
            return visitGulag(command.getSource());
        })));
    }

    private int visitGulag(CommandSource source) throws CommandSyntaxException
    {
        return GulagTeleportHelper.visitGulag(source);
    }
}
