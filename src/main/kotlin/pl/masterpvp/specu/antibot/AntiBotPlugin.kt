package pl.masterpvp.specu.antibot

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.event.PreLoginEvent
import net.md_5.bungee.api.event.ProxyPingEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.event.EventHandler
import java.util.*

class AntiBotPlugin : Plugin(), Listener {
    val map: MutableMap<String, Long?> = LinkedHashMap()
    private val activeInSeconds = 60
    private val maxOnlineFromIp = 2
    override fun onEnable() {
        proxy.pluginManager.registerListener(this, this)
    }

    @EventHandler(priority = -64)
    fun onProxyPing(event: ProxyPingEvent) {
        val ip = event.connection.address.address.hostAddress
        if (map.containsKey(ip)) {
            if (map[ip]!! < System.currentTimeMillis()) {
                map[ip] = System.currentTimeMillis() + activeInSeconds * 1000L
            }
        } else {
            map[ip] = System.currentTimeMillis() + activeInSeconds * 1000L
        }
    }

    @EventHandler(priority = -64)
    fun onPreLogin(event: PreLoginEvent) {
        val ip = event.connection.address.address.hostAddress
        if (!map.containsKey(ip) || map[ip]!! < System.currentTimeMillis()) {
            event.isCancelled = true
            event.cancelReason = color("""
    &cAby dołączyć do serwera, musisz go dodać do listy serwerów
    &cNastępnie odśwież liste i dołącz do serwera!
    """.trimIndent())
            return
        }
        val online = getOnline(ip)
        if (online >= maxOnlineFromIp) {
            event.isCancelled = true
            event.cancelReason = color("&cMaxymalna ilość aktywnych kont na serwerze to: &6$maxOnlineFromIp")
            return
        }
    }

    fun getOnline(ip: String): Int {
        var i = 0
        for (proxiedPlayer in proxy.players) {
            if (proxiedPlayer.address.address.hostAddress == ip) {
                i += 1
            }
        }
        return i
    }

    companion object {
        fun color(text: String?): String {
            return ChatColor.translateAlternateColorCodes('&', text)
        }
    }
}
