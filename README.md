# VelocityAutoReconnect
[![License: GPLv3](https://img.shields.io/badge/License-GPLv3-brightgreen)](https://github.com/flori4nk/VelocityAutoReconnect/blob/master/LICENSE)
[![Release Build](https://img.shields.io/badge/Download-latest%20release-brightgreen)](https://ci.codemc.io/job/flori4nk/job/VelocityAutoReconnect/lastStableBuild/)
[![Dev Build](https://img.shields.io/badge/Download-latest%20snapshot-yellow)](https://ci.codemc.io/job/flori4nk/job/VelocityAutoReconnect/lastBuild/)
![GitHub contributors](https://img.shields.io/github/contributors/flori4nk/VelocityAutoReconnect?color=brightgreen)

A simple automatic reconnection plugin for Velocity.

VelocityAutoReconnect is licensed under the GPLv3 license.

## Installation
1. Download [the latest release](https://ci.codemc.io/job/flori4nk/job/VelocityAutoReconnect/lastStableBuild/).
1. Upload it into the plugins directory of your instance of Velocity.
1. Restart the proxy to load the plugin and create the configuration files.
1. Configure the proxy, limbo server and plugin as outlined below.
1. Restart the proxy once again

## Configuration
### Proxy / Limbo
VelocityAutoReconnect does not implement a fallback server, only automatic reconnection from a fallback server to the previous server.
Therefore, a fallback server needs to be set up before the plugin can function. 
Using a Limbo server, such as [NanoLimbo](https://www.spigotmc.org/resources/nanolimbo.86198/), for this purpose is recommended.

After setting up the Limbo server and adding it to the servers section of velocity.toml, it needs to be added to the connection order.

Example using the regular try order:
```
[servers]
	...
	try = ["default", "limbo"]
```

Example using the forced hosts order:
```
[forced-hosts]
	"build.example.com" = ["build", "limbo"]
	"pvp.example.com" = ["pvp", "limbo"]
```

### Plugin
After following the installation guide, edit the configuration file: ``plugins/velocityautoreconnect/velocityautoreconnect.conf``.
#### Required
**limbo-name**
* Default: limbo
* Description: Name of the limbo server specified in the servers section of velocity.toml

**directconnect-server**
* Default: default
* Description: Name of the server players should be connected to, if they directly connect to the Limbo server. This should be set to the name of your lobby / main server.

#### Optional
##### Reconnection Task
**task-interval-ms**
* Default: 3500
* Description: Time between attempts to reconnect players

**pingcheck**
* Default: true
* Description: Whether VelocityAutoReconnect should check if a server responds to pings before trying to connect a player to it.

**bypasscheck** (>=1.3.0)
* Default: false
* Description: Whether VelocityAutoReconnect should not reconnect players with the ``velocityautoreconnect.bypass`` permission.

##### Kick Filter
**kick-filter.blacklist**
* Default: .* ([Bb]anned|[Kk]icked).*
* Description: If a kicked player falls back to the Limbo server and the reason matches this regular expression, they are disconnected.

**kick-filter.blacklist.enabled**
* Default: false

**kick-filter.whitelist**
* Default: Server closed
* Description: If a kicked player falls back to the Limbo server and the reason does not match this regular expression, they are disconnected.

**kick-filter.whitelist.enabled**
* Default: true

##### Fallback Message
**message**
* Default: You will be reconnected soon.
* Description: Welcome message sent to players joining the Limbo server

**message.enabled**
* Default: false

##### Logging
**log.informational**
* Default: true
* Description: Whether or not to enable informational log messages, such as the messages printed upon reconnecting a player.

## Note regarding CI
[Jenkins Job](https://ci.codemc.io/job/flori4nk/job/VelocityAutoReconnect/)

All development builds are automatically marked as unstable.

The last stable build is always the latest release.

Thanks to CodeMC for providing the CI.

## Legal Notice
> Copied from LICENSE file

THERE IS NO WARRANTY FOR THE PROGRAM, TO THE EXTENT PERMITTED BY APPLICABLE LAW. EXCEPT WHEN OTHERWISE STATED IN WRITING THE COPYRIGHT HOLDERS AND/OR OTHER PARTIES PROVIDE THE PROGRAM “AS IS” WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE PROGRAM IS WITH YOU. SHOULD THE PROGRAM PROVE DEFECTIVE, YOU ASSUME THE COST OF ALL NECESSARY SERVICING, REPAIR OR CORRECTION.

IN NO EVENT UNLESS REQUIRED BY APPLICABLE LAW OR AGREED TO IN WRITING WILL ANY COPYRIGHT HOLDER, OR ANY OTHER PARTY WHO MODIFIES AND/OR CONVEYS THE PROGRAM AS PERMITTED ABOVE, BE LIABLE TO YOU FOR DAMAGES, INCLUDING ANY GENERAL, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR INABILITY TO USE THE PROGRAM (INCLUDING BUT NOT LIMITED TO LOSS OF DATA OR DATA BEING RENDERED INACCURATE OR LOSSES SUSTAINED BY YOU OR THIRD PARTIES OR A FAILURE OF THE PROGRAM TO OPERATE WITH ANY OTHER PROGRAMS), EVEN IF SUCH HOLDER OR OTHER PARTY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
