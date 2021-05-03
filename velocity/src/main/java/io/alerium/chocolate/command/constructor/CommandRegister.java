package io.alerium.chocolate.command.constructor;

import io.alerium.chocolate.ChocolatePlugin;
import io.alerium.chocolate.command.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CommandRegister {

    private final ChocolatePlugin chocolatePlugin;
    @Getter
    private final List<ChocolateCommand> chocolateCommands = new ArrayList<>();

    public void initialize() {
        this.loadCommand(new FindCommand());
        this.loadCommand(new GlistCommand());
        this.loadCommand(new IPCommand());
        this.loadCommand(new LastSeenCommand());
        this.loadCommand(new ProxyIdCommand());
        this.loadCommand(new ProxyIdListCommand());
        this.loadCommand(new AlertCommand());
        this.loadCommand(new ChocolateInfoCommand());
    }

    private void loadCommand(ChocolateCommand chocolateCommand) {
        this.chocolatePlugin.getServer().getCommandManager().register(chocolateCommand, chocolateCommand);
        this.chocolateCommands.add(chocolateCommand);
    }

}
