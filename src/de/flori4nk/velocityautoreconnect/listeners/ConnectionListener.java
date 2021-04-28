package de.flori4nk.velocityautoreconnect.listeners;

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

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import de.flori4nk.velocityautoreconnect.VelocityAutoReconnect;
import de.flori4nk.velocityautoreconnect.misc.Utility;

public class ConnectionListener {
	
	@Subscribe(order = PostOrder.FIRST)
	public void onPlayerPostConnect(ServerPostConnectEvent event) {
		Player player = event.getPlayer();
		RegisteredServer previousServer = event.getPreviousServer();
		
		if(!player.getCurrentServer().isPresent()) {
			VelocityAutoReconnect.getLogger().info(String.format("Current server wasn't present for %s.", player.getUsername()));
			return;
		}
		
		ServerConnection currentServerConnection = player.getCurrentServer().get();
		
		/* Set previousServer to directconnect-server if it's not set
		 * This might happen as a result of direct connection when all other
		 * servers are offline or the usage of plugins that set the initial server,
		 * such as RememberMe.
		 */
		if(previousServer == null) {
			if(VelocityAutoReconnect.getPlayerManager().isPlayerRegistered(player)) {
				previousServer = VelocityAutoReconnect.getPlayerManager().getPreviousServer(player);
			} else {
				previousServer = VelocityAutoReconnect.getDirectConnectServer();
			}
		}
		
		// If a player gets redirected from Limbo to another server, remove them from the Map
		if(Utility.doServerNamesMatch(previousServer, VelocityAutoReconnect.getLimboServer())) {
			VelocityAutoReconnect.getPlayerManager().removePlayer(player);
			return;
		}
		
		// If a player gets redirected to Limbo from another server, add them to the Map
		if(Utility.doServerNamesMatch(currentServerConnection.getServer(), VelocityAutoReconnect.getLimboServer())) {
			VelocityAutoReconnect.getPlayerManager().addPlayer(player, previousServer);
			Utility.sendWelcomeMessage(player);
		}
	}
		
	/* Remove anyone who disconnects from the proxy from the playerData Map.
	 * Note that Map.remove is an optional operation, so we don't need to check whether 
	 * there actually was a corresponding mapping.
	 */
	@Subscribe(order = PostOrder.NORMAL)
	public void onDisconnect(DisconnectEvent event) {
		VelocityAutoReconnect.getPlayerManager().removePlayer(event.getPlayer());
	}
	
}
