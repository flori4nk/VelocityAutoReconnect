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

import java.util.regex.Pattern;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.RedirectPlayer;
import com.velocitypowered.api.proxy.Player;
import de.flori4nk.velocityautoreconnect.VelocityAutoReconnect;
import de.flori4nk.velocityautoreconnect.misc.Utility;
import net.kyori.adventure.text.Component;

public class KickListener {

	private Pattern kickFilterWhitelist;
	private Pattern kickFilterBlacklist;
	
	public KickListener() {
		// Compile whitelist / blacklist Patterns from configured expressions
		this.kickFilterBlacklist = Pattern.compile(VelocityAutoReconnect.getConfigurationManager().getProperty("kick-filter.blacklist"), Pattern.DOTALL);
		this.kickFilterWhitelist = Pattern.compile(VelocityAutoReconnect.getConfigurationManager().getProperty("kick-filter.whitelist"), Pattern.DOTALL);
	}
	
	@Subscribe(order = PostOrder.NORMAL)
	public void onPlayerKick(KickedFromServerEvent event) {
		// Check whether the result of the kick actually was a redirection.
		if(event.getResult() instanceof KickedFromServerEvent.RedirectPlayer) {
			KickedFromServerEvent.RedirectPlayer playerRedirection = (RedirectPlayer) event.getResult();
			Player player = event.getPlayer();
			// Get the kick reason, when possible. Use an empty Component if the kick reason isn't present.
			Component kickReason = event.getServerKickReason().isPresent() ? event.getServerKickReason().get() : Component.empty();
			String kickReasonText = kickReason.toString();
			
			if(!Utility.doServerNamesMatch(playerRedirection.getServer(), VelocityAutoReconnect.getLimboServer())) {
				return;
			}
			
			if(VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("kick-filter.whitelist.enabled") 
					&& !this.kickFilterWhitelist.matcher(kickReasonText).matches()) {
				player.disconnect(kickReason);
				return;
			}
			
			if(VelocityAutoReconnect.getConfigurationManager().getBooleanProperty("kick-filter.blacklist.enabled")
					&& this.kickFilterBlacklist.matcher(kickReasonText).matches()) {
				player.disconnect(kickReason);
				return;
			}
			// Add player and previous server to the Map.
			VelocityAutoReconnect.getPlayerManager().addPlayer(event.getPlayer(), event.getServer());
			Utility.sendWelcomeMessage(player);
		}
	}
}
