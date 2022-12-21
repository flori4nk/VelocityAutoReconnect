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

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.flori4nk.velocityautoreconnect.VelocityAutoReconnect;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.title.Title;

import java.util.Optional;

public class Utility {

    // Returns whether or not the names of the servers match.
    public static boolean doServerNamesMatch(RegisteredServer var0, RegisteredServer var1) {
        return var0.getServerInfo().getName().equals(var1.getServerInfo().getName());
    }

    // Check whether or not the welcome message should be sent and act accordingly
    public static void sendWelcomeMessage(Player player) {
        if (VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("message.enabled")) {
            player.sendMessage(Component.text(VelocityAutoReconnect.getConfigurationManager().getProperty("message")));
        }
    }

    /**
     * Sends a Title message to a player, if configured.
     * @param player Player that will see the Title message.
     */
    public static void sendWelcomeTitleMessage(Player player) {
        if (!VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("title.enabled")) return;

		// Get the title text values from config
        var cfgManager = VelocityAutoReconnect.getConfigurationManager();
        var titleText = cfgManager.getProperty("title-text");
        var subtitleText = cfgManager.getProperty("title-subtitle");

		// Deserialize the title text values
        Component mainTitle = deserializeAsJson(titleText);
        Component subtitle = deserializeAsJson(subtitleText);

		// Show a title to a player
        player.showTitle(Title.title(mainTitle, subtitle));
    }

    public static RegisteredServer getServerByName(String serverName) {
        Optional<RegisteredServer> optionalServer = VelocityAutoReconnect.getProxyServer().getServer(serverName);

        if (optionalServer.isPresent()) {
            return optionalServer.get();
        }

        VelocityAutoReconnect.getLogger().severe(String.format("Server \"%s\" is invalid, VelocityAutoReconnect will not function!", serverName));
        return null;
    }

    public static RegisteredServer getServerFromProperty(String propertyName) {
        return getServerByName(VelocityAutoReconnect.getConfigurationManager().getProperty(propertyName));
    }

    public static void logInformational(String message) {
        if (VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("log.informational")) {
            VelocityAutoReconnect.getLogger().info(message);
        }
    }

	/**
	 * Attempts to deserialize a string as a JSON component. If it is not a valid JSON component, it will be returned as a plain text component.
	 * @param input The input string to deserialize.
	 * @return The deserialized component.
	 */
	public static Component deserializeAsJson(String input) {
		try {
			return GsonComponentSerializer.gson().deserialize(input);
		} catch (Exception e) {
			return Component.text(input);
		}
	}

}
