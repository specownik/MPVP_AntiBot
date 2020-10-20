package pl.masterpvp.specu.antibot;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class AntiBotPlugin extends Plugin implements Listener {

    private Map<String, Long> map = new LinkedHashMap<>();
    private int activeInSeconds = 60;
    private int maxOnlineFromIp = 2;

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler(priority = -64)
    public void onProxyPing(ProxyPingEvent event){
        String ip = event.getConnection().getAddress().getAddress().getHostAddress(); 
        if(getMap().containsKey(ip)) {
            if (getMap().get(ip) < System.currentTimeMillis()) {
                getMap().put(ip, (System.currentTimeMillis() + (activeInSeconds * 1000L)));
            }
        }else{
            getMap().put(ip, (System.currentTimeMillis() + (activeInSeconds * 1000L)));
        }
    }

    @EventHandler(priority = -64)
    public void onPreLogin(PreLoginEvent event){
        String ip = event.getConnection().getAddress().getAddress().getHostAddress();
        if(!getMap().containsKey(ip) || getMap().get(ip) < System.currentTimeMillis()) {
            event.setCancelled(true);
            event.setCancelReason(color("&cAby dołączyć do serwera, musisz go dodać do listy serwerów\n" +
                    "&cNastępnie odśwież liste i dołącz do serwera!"));
            return;
        }
        int online = getOnline(ip);
        if(online >= maxOnlineFromIp){
            event.setCancelled(true);
            event.setCancelReason(color("&cMaxymalna ilość aktywnych kont na serwerze to: &6" + maxOnlineFromIp));
            return;
        }
    }

    public Map<String, Long> getMap() {
        return map;
    }

    public static String color(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public int getOnline(String ip){
        int i = 0;
        for(ProxiedPlayer proxiedPlayer : getProxy().getPlayers()){
            if(proxiedPlayer.getAddress().getAddress().getHostAddress().equals(ip)){
                i += 1;
            }
        }
        return i;
    }
}
