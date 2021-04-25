package de.flori4nk.velocityautoreconnect;

/*
MIT License

VelocityAutoReconnect
Copyright (c) 2021 Flori4nK <contact@flori4nk.de>

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.RedirectPlayer;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;



@Plugin(id = "velocityautoreconnect", name = "VelocityAutoReconnect", version = "1.2.0", authors = {"Flori4nK"})
public class VelocityAutoReconnect {
	
	private final ProxyServer server;
	private Logger logger;
	private RegisteredServer limboServer;
	private RegisteredServer directConnectServer;
	private Map<Player, RegisteredServer> playerData;
	private File configurationFile;
	private Properties configuration;

	private Pattern kickFilterWhitelist;
	private Pattern kickFilterBlacklist;
	
	@Inject
	public VelocityAutoReconnect(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
		this.server = server;
		this.logger = logger;
		this.playerData = new HashMap<Player, RegisteredServer>();
		this.configurationFile = new File(dataDirectory.toFile(), "velocityautoreconnect.conf");
		this.configuration = new Properties();
		try {
			// Create the configuration file if it doesn't exist.
			if(!this.configurationFile.exists()) {
				this.configurationFile.getParentFile().mkdirs();
				this.configurationFile.createNewFile();
			}
			
			// Set default values
			configuration.setProperty("limbo-name", "limbo");
			configuration.setProperty("directconnect-server", "lobby");
			configuration.setProperty("task-interval-ms", "3500");
			configuration.setProperty("kick-filter.blacklist", ".* ([Bb]anned|[Kk]icked).*");
			configuration.setProperty("kick-filter.blacklist.enabled", "false");
			configuration.setProperty("kick-filter.whitelist", "Server closed");
			configuration.setProperty("kick-filter.whitelist.enabled", "true");
			configuration.setProperty("message", "You will be reconnected soon.");
			configuration.setProperty("message.enabled", "false");
			configuration.setProperty("pingcheck", "true");
			
			// Load saved values
			configuration.load(new FileInputStream(configurationFile));
			
			// Save old values combined with possible new defaults
			configuration.store(new FileOutputStream(configurationFile), "VelocityAutoReconnect configuration -- Documentation: https://github.com/flori4nk/VelocityAutoReconnect/blob/master/README.md#configuration");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Compile whitelist / blacklist Patterns from configured expressions
		this.kickFilterBlacklist = Pattern.compile(configuration.getProperty("kick-filter.blacklist"), Pattern.DOTALL);
		this.kickFilterWhitelist = Pattern.compile(configuration.getProperty("kick-filter.whitelist"), Pattern.DOTALL);
	}
	
	@Subscribe(order = PostOrder.NORMAL)
	public void onInitialize(ProxyInitializeEvent event) {
		// Get Limbo server specified in config
		this.limboServer = getServerByName(configuration.getProperty("limbo-name"));
		
		// Get direct connect fallback server specified in config
		this.directConnectServer = getServerByName(configuration.getProperty("directconnect-server"));
		
		// If either server
		if(this.limboServer == null || this.directConnectServer == null) {
			this.server.getEventManager().unregisterListeners(this);
			return;
		}
		
		// Schedule the reconnector task
		server.getScheduler().buildTask(this, () -> {
			Collection<Player> connectedPlayers = this.limboServer.getPlayersConnected();
			// Prevent NullPointerException when Limbo is empty
			if(connectedPlayers.isEmpty()) return;
			
			// "Choose" the next player.
			Player nextPlayer = connectedPlayers.iterator().next();
			RegisteredServer previousServer = this.playerData.get(nextPlayer);
			
			// Redirect the player, if possible.
			if(previousServer != null) {
				// If enabled, check if a server responds to pings before connecting
				if(Boolean.valueOf(this.configuration.getProperty("pingcheck"))) {
					try {
						previousServer.ping().join();
					} catch(CompletionException exception) {
						return;
					}
				}
				
				this.logger.info(String.format("Connecting %s to %s.", nextPlayer.getUsername(), previousServer.getServerInfo().getName()));
				nextPlayer.createConnectionRequest(previousServer).fireAndForget();
			} else {
				this.logger.severe(String.format("Previous server is null for %s .. disconnecting.", nextPlayer.getUsername()));
				nextPlayer.disconnect(Component.text("Previous server was null.").color(NamedTextColor.YELLOW));
			}
		})
		.repeat(Integer.valueOf(configuration.getProperty("task-interval-ms")), TimeUnit.MILLISECONDS)
		.schedule();
	}
	
	@Subscribe(order = PostOrder.FIRST)
	public void onPlayerPostConnect(ServerPostConnectEvent event) {
		Player player = event.getPlayer();
		RegisteredServer previousServer = event.getPreviousServer();
		
		if(!player.getCurrentServer().isPresent()) {
			this.logger.info(String.format("Current server wasn't present for %s.", player.getUsername()));
			return;
		}
		
		ServerConnection currentServerConnection = player.getCurrentServer().get();
		
		/* Set previousServer to directconnect-server if it's not set
		 * This might happen as a result of direct connection when all other
		 * servers are offline or the usage of plugins that set the initial server,
		 * such as RememberMe.
		 */
		if(previousServer == null) {
			if(!this.playerData.containsKey(player)) {
				previousServer = this.directConnectServer;
			} else {
				previousServer = this.playerData.get(player);
			}
		}
		
		// If a player gets redirected from Limbo to another server, remove them from the Map
		if(doServerNamesMatch(previousServer, limboServer)) {
			this.playerData.remove(player);
			return;
		}
		
		// If a player gets redirected to Limbo from another server, add them to the Map
		if(doServerNamesMatch(currentServerConnection.getServer(), limboServer)) {
			this.playerData.put(player, previousServer);
			this.sendWelcomeMessage(player);
		}
	}
	
	@Subscribe(order = PostOrder.NORMAL)
	public void onPlayerKick(KickedFromServerEvent event) {
		// Check whether the result of the kick actually was a redirection.
		if(event.getResult() instanceof KickedFromServerEvent.RedirectPlayer) {
			KickedFromServerEvent.RedirectPlayer playerRedirection = (RedirectPlayer) event.getResult();
			Player player = event.getPlayer();
			// Get the kick reason, when possible. Use an empty Component if the kick reason isn't present.
			Component kickReason = event.getServerKickReason().isPresent() ? event.getServerKickReason().get() : Component.empty();
			String kickReasonText = kickReason.toString();
			
			if(!doServerNamesMatch(playerRedirection.getServer(), limboServer)) {
				return;
			}
			
			if(Boolean.valueOf(configuration.getProperty("kick-filter.whitelist.enabled")) 
					&& !this.kickFilterWhitelist.matcher(kickReasonText).matches()) {
				player.disconnect(kickReason);
				return;
			}
			
			if(Boolean.valueOf(configuration.getProperty("kick-filter.blacklist.enabled")) 
					&& this.kickFilterBlacklist.matcher(kickReasonText).matches()) {
				player.disconnect(kickReason);
				return;
			}
			// Add player and previous server to the Map.
			this.playerData.put(event.getPlayer(), event.getServer());
			this.sendWelcomeMessage(player);
		}
	}
	
	
	/* Remove anyone who disconnects from the proxy from the playerData Map.
	 * Note that Map.remove is an optional operation, so we don't need to check whether 
	 * there actually was a corresponding mapping.
	 */
	@Subscribe(order = PostOrder.NORMAL)
	public void onDisconnect(DisconnectEvent event) {
		this.playerData.remove(event.getPlayer());
	}
	
	// Returns whether or not the names of the servers match.
	public boolean doServerNamesMatch(RegisteredServer var0, RegisteredServer var1) {
		return var0.getServerInfo().getName().equals(var1.getServerInfo().getName());
	}
	
	// Check whether or not the welcome message should be sent and act accordingly
	public void sendWelcomeMessage(Player player) {
		if(Boolean.valueOf(configuration.getProperty("message.enabled"))) {
			player.sendMessage(Component.text(configuration.getProperty("message")));
		}
	}
	
	public RegisteredServer getServerByName(String serverName) {
		Optional<RegisteredServer> optionalServer = server.getServer(configuration.getProperty("limbo-name"));
		
		if(optionalServer.isPresent()) {
			return optionalServer.get();
		}
		
		this.logger.severe(String.format("Server \"%s\" is invalid, VelocityAutoReconnect will not function!", serverName));
		return null;
	}
	
}
