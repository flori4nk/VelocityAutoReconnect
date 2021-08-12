/*
 * VelocityAutoReconnect
 * Copyright (C) 2021 Flori4nK <contact@flori4nk.de>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.flori4nk.velocityautoreconnect.storage;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.flori4nk.velocityautoreconnect.VelocityAutoReconnect;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerManager {

    private final Map<Player, RegisteredServer> playerData;

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
