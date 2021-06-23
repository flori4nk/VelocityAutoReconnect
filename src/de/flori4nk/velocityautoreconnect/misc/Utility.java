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
package de.flori4nk.velocityautoreconnect.misc;

import java.util.Optional;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import de.flori4nk.velocityautoreconnect.VelocityAutoReconnect;
import net.kyori.adventure.text.Component;

public class Utility {
	
	// Returns whether or not the names of the servers match.
	public static boolean doServerNamesMatch(RegisteredServer var0, RegisteredServer var1) {
		return var0.getServerInfo().getName().equals(var1.getServerInfo().getName());
	}

	// Check whether or not the welcome message should be sent and act accordingly
	public static void sendWelcomeMessage(Player player) {
		if(VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("message.enabled")) {
			player.sendMessage(Component.text(VelocityAutoReconnect.getConfigurationManager().getProperty("message")));
		}
	}
		
	public static RegisteredServer getServerByName(String serverName) {
		Optional<RegisteredServer> optionalServer = VelocityAutoReconnect.getProxyServer().getServer(VelocityAutoReconnect.getConfigurationManager().getProperty("limbo-name"));
			
		if(optionalServer.isPresent()) {
			return optionalServer.get();
		}
			
		VelocityAutoReconnect.getLogger().severe(String.format("Server \"%s\" is invalid, VelocityAutoReconnect will not function!", serverName));
		return null;
	}
	
	public static void logInformational(String message) {
		if(VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("log.informational")) {
			VelocityAutoReconnect.getLogger().info(message);
		}
	}

}
