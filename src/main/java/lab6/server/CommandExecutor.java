package lab6.server;

import lab6.common.Response;
import lab6.common.requests.CommandRequest;

public class CommandExecutor {
    public Response execute(CommandRequest request) {
        return new Response(false, "Not implemented", null);
    }
}
