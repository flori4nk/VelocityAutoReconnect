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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;


public class PlayerManager {

    private Deque<PlayerServerPair> playerQueue;

    public PlayerManager() {
        this.playerQueue = new ArrayDeque<>();
    }

    public void queuePlayer(Player player, RegisteredServer registeredServer) {
        this.playerQueue.add(new PlayerServerPair(player.getUniqueId(), registeredServer.getServerInfo().getName()));
    }

    public void checkPriorityAndQueuePlayer(Player player, RegisteredServer registeredServer) {
        if (player.hasPermission("velocityautoreconnect.priority")) {
            this.playerQueue.addFirst(new PlayerServerPair(player.getUniqueId(), registeredServer.getServerInfo().getName()));
        } else {
            this.queuePlayer(player, registeredServer);
        }
    }

    public PlayerServerPair next() {
        return this.playerQueue.pollFirst();
    }

    public boolean isPlayerRegistered(Player player) {
        for (PlayerServerPair pair : this.playerQueue) {
            if (pair.uuid.equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    static class PlayerServerPair {
        private final UUID uuid;
        private final String serverID;

        private PlayerServerPair(UUID uuid, String serverID) {
            this.uuid = uuid;
            this.serverID = serverID;
        }
    }

}
