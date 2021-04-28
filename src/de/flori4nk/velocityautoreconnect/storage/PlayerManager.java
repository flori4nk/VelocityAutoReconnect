package de.flori4nk.velocityautoreconnect.storage;

import java.util.LinkedHashMap;
import java.util.Map;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import de.flori4nk.velocityautoreconnect.VelocityAutoReconnect;

public class PlayerManager {
	
	private Map<Player, RegisteredServer> playerData;
	
	public PlayerManager() {
		this.playerData = new LinkedHashMap<>();
	}
	
	public void addPlayer(Player player, RegisteredServer registeredServer) {
		this.playerData.put(player, registeredServer);
	}
	
	public void removePlayer(Player player) {
		this.playerData.remove(player);
	}
	
	public RegisteredServer getPreviousServer(Player player) {
		return this.playerData.getOrDefault(player, VelocityAutoReconnect.getDirectConnectServer());
	}
	
	public boolean isPlayerRegistered(Player player) {
		return playerData.containsKey(player);
	}

}
