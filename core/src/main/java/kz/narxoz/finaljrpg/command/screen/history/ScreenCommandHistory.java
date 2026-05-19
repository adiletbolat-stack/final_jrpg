package kz.narxoz.finaljrpg.command.screen.history;

import kz.narxoz.finaljrpg.command.screen.ScreenCommand;
import lombok.Getter;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ScreenCommandHistory {
    @Getter
    private List<ScreenCommand> commands;

    private static ScreenCommandHistory history;

    private ScreenCommandHistory(){
        commands = new ArrayList<>();
    }

    public static ScreenCommandHistory getInstance(){
        if (Objects.isNull(history)) history = new ScreenCommandHistory();
        return history;
    }

    public void push(ScreenCommand command){
        commands.add(command);
    }

    public ScreenCommand pop(){
        ScreenCommand command = commands.getLast();
        commands.removeLast();
        return command;
    }
}
