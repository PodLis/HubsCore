package su.hubs.hubscore.exception;

public class HubsServerPluginMissingException extends Exception {

    public HubsServerPluginMissingException(String msg, Throwable err) {
        super(msg, err);
    }

    public HubsServerPluginMissingException(String msg) {
        super(msg);
    }

}
