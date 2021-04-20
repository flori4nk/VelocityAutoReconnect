# VelocityAutoReconnect
Automatic reconnection for Velocity

## Installation
1. Download [the latest release](https://github.com/flori4nk/VelocityAutoReconnect/releases/latest).
1. Upload it into the plugins directory of your instance of Velocity.
1. Restart the proxy to load the plugin and create the configuration files.
1. Configure the proxy and plugin as explained below.
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

#### Optional
**task-interval-ms**
* Default: 2500
* Description: Time between attempts to reconnect players

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

**message**
* Default: You will be reconnected soon.
* Description: Welcome message sent to players joining the Limbo server

**message.enabled**
* Default: false

## Legal Notice
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.