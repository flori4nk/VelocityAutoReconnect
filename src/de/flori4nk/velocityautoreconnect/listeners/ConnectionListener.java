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
package de.flori4nk.velocityautoreconnect.listeners;

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
        if (VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("bypasscheck")
                && player.hasPermission("velocityautoreconnect.bypass")) {
            return;
        }

        if (player.getCurrentServer().isEmpty()) {
            VelocityAutoReconnect.getLogger().severe(String.format("Current server wasn't present for %s.", player.getUsername()));
            return;
        }
        ServerConnection currentServerConnection = player.getCurrentServer().get();

        /* Set previousServer to directconnect-server if it's not set.
         * This might happen as a result of direct connection when all other
         * servers are offline or the usage of plugins that set the initial server,
         * such as RememberMe.
         */
        RegisteredServer previousServer = event.getPreviousServer();
        if (previousServer == null) {
            if (VelocityAutoReconnect.getPlayerManager().isPlayerRegistered(player)) {
                previousServer = VelocityAutoReconnect.getPlayerManager().getPreviousServer(player);
            } else {
                previousServer = VelocityAutoReconnect.getDirectConnectServer();
            }
        }

        // If a player gets redirected from Limbo to another server, remove them from the Map
        if (Utility.doServerNamesMatch(previousServer, VelocityAutoReconnect.getLimboServer())) {
            VelocityAutoReconnect.getPlayerManager().removePlayer(player);
            return;
        }

        // If a player gets redirected to Limbo from another server, add them to the Map
        if (Utility.doServerNamesMatch(currentServerConnection.getServer(), VelocityAutoReconnect.getLimboServer())) {
            VelocityAutoReconnect.getPlayerManager().addPlayer(player, previousServer);
            Utility.sendWelcomeMessage(player);
            Utility.sendWelcomeTitleMessage(player);
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
