import com.lielamar.auth.bukkit.events.PlayerStateChangeEvent;
import com.lielamar.auth.shared.handlers.AuthHandler;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerStateChangeEventExample implements Listener {

    @EventHandler
    public void onStateChange(PlayerStateChangeEvent event) {
        if(event.getAuthState() == AuthHandler.AuthState.PENDING_LOGIN) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "We are waiting for you to authenticate!");
        }
    }
}
