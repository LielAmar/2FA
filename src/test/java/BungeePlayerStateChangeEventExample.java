import com.lielamar.auth.bungee.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.handlers.AuthHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class BungeePlayerStateChangeEventExample implements Listener {

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if(event.getNewAuthState() == AuthHandler.AuthState.AUTHENTICATED)
            event.getPlayer().connect(ProxyServer.getInstance().getServerInfo("lobby"), ServerConnectEvent.Reason.PLUGIN);
    }
}
