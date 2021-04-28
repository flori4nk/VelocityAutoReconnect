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
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import com.google.inject.Inject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import de.flori4nk.velocityautoreconnect.listeners.ConnectionListener;
import de.flori4nk.velocityautoreconnect.listeners.KickListener;
import de.flori4nk.velocityautoreconnect.misc.Utility;
import de.flori4nk.velocityautoreconnect.storage.ConfigurationManager;
import de.flori4nk.velocityautoreconnect.storage.PlayerManager;



@Plugin(id = "velocityautoreconnect", name = "VelocityAutoReconnect", version = "1.2.1", authors = {"Flori4nK"})
public class VelocityAutoReconnect {
	
	private static ProxyServer proxyServer;
	private static Logger logger;
	private static RegisteredServer limboServer;
	private static RegisteredServer directConnectServer;
	
	private static ConfigurationManager configurationManager;
	private static PlayerManager playerManager;
	
	@Inject
	public VelocityAutoReconnect(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
		VelocityAutoReconnect.proxyServer = server;
		VelocityAutoReconnect.logger = logger;
		VelocityAutoReconnect.configurationManager = new ConfigurationManager(new File(dataDirectory.toFile(), "velocityautoreconnect.conf"));
		VelocityAutoReconnect.playerManager = new PlayerManager();
	}
	
	@Subscribe(order = PostOrder.NORMAL)
	public void onInitialize(ProxyInitializeEvent event) {
		// Get Limbo server specified in config
		limboServer = Utility.getServerByName(configurationManager.getProperty("limbo-name"));
		
		// Get direct connect fallback server specified in config
		directConnectServer = Utility.getServerByName(configurationManager.getProperty("directconnect-server"));
		
		// If either server is null, self-destruct
		if(limboServer == null || directConnectServer == null) {
			logger.severe("At least one of the specified servers is invalid, VelocityAutoReconnect will not function!");
			proxyServer.getEventManager().unregisterListeners(this);
			return;
		}
		
		// Register listeners
		proxyServer.getEventManager().register(this, new ConnectionListener());
		proxyServer.getEventManager().register(this, new KickListener());
		
		// Schedule the reconnector task
		proxyServer.getScheduler().buildTask(this, () -> {
			Collection<Player> connectedPlayers = limboServer.getPlayersConnected();
			// Prevent NullPointerException when Limbo is empty
			if(connectedPlayers.isEmpty()) return;
			// "Choose" the next player.
			Player nextPlayer = connectedPlayers.iterator().next();
			RegisteredServer previousServer = playerManager.getPreviousServer(nextPlayer);
			
			// Redirect the player, if possible.
			// If enabled, check if a server responds to pings before connecting
			try {
				if(configurationManager.getBooleanProperty("pingcheck")) {
					// The nested try-catch is probably a horrible solution
					try {
						previousServer.ping().join();
					} catch(CompletionException completionException) {
						// Server failed to respond to ping request, return to prevent spam
						return;
					}
				}
				logger.info(String.format("Connecting %s to %s.", nextPlayer.getUsername(), previousServer.getServerInfo().getName()));
				nextPlayer.createConnectionRequest(previousServer).connect();
			} catch(CompletionException exception) {
				// Prevent console from being spammed when a server is offline and ping-check is disabled
			}
		})
		.repeat(configurationManager.getIntegerProperty("task-interval-ms"), TimeUnit.MILLISECONDS)
		.schedule();
	}

	public static RegisteredServer getLimboServer() {
		return limboServer;
	}

	public static RegisteredServer getDirectConnectServer() {
		return directConnectServer;
	}

	public static ProxyServer getProxyServer() {
		return proxyServer;
	}

	public static Logger getLogger() {
		return logger;
	}

	public static ConfigurationManager getConfigurationManager() {
		return configurationManager;
	}

	public static PlayerManager getPlayerManager() {
		return playerManager;
	}
	
	
}
