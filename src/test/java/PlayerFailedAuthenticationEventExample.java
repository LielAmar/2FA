import com.lielamar.auth.bukkit.events.PlayerFailedAuthenticationEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerFailedAuthenticationEventExample implements Listener {

    @EventHandler
    public void onFail(PlayerFailedAuthenticationEvent event) {
        if(event.getFailedAttempts() >= 3) {
            event.getPlayer().kickPlayer(ChatColor.RED + "You failed the authentication too many times!");
        }
    }
}
