package lab6.common.requests;

public class UnknownCommandRequest implements CommandRequest{
    private final String commandName;
    private final String argument;

    public UnknownCommandRequest(String commandName, String argument){
        this.commandName = commandName;
        this.argument = argument;
    }

    public String getCommandName(){
        return commandName;
    }

    public String getArgument(){
        return argument;
    }
}
