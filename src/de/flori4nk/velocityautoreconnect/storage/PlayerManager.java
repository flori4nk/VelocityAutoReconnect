package de.flori4nk.velocityautoreconnect.storage;

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
