package de.flori4nk.velocityautoreconnect.misc;

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
