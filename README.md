# Chocolate (Velocity Plugin)

Simple player synchronisation solution for servers running multiple Velocity proxies

### Features

---

- Player Data Sync for multiple proxies
- Player Count Sync for multiple proxies
- Alert Sync for multiple proxies
- Hex & ChatColor Support

**Hex Format:** `&#HEX_CODE`

**ChatColor Format:** `&a-zA-Z0-9`

### Commands

---

**Format:** `command - description - permission`

```yaml
/alert <message> - Send a broadcast over all the proxies. - Chocolate.Command.Alert
/chocolate - Sends info about the plugin & the command list. - Chocolate.Command.Chocolate
/find <player> - Find on what server & proxy a player is. - Chocolate.Command.Find
/glist <proxy> - Receive the global list. - Chocolate.Command.Glist
/ip <player> - Get the ip of a player. - Chocolate.Command.IP
/lastseen <player> - Shows when a player has been seen for the last time - Chocolate.Command.LastSeen
/proxy - Get the id of the proxy you are on - Chocolate.Command.Proxy
/proxies - Gets a list with all the proxies id's - Chocolate.Command.ProxyIdList
```

### Configs

---

#### config.json

```json
{
  "proxyName": "proxy01"
}
```

#### lang.json

```json
{
  "generic": {
    "no-permissions": "&c&lChocolate &8» &cYou cannot use this command.",
    "wrong-usage": "&c&lChocolate &8» &cWrong usage, usage: &f%usage%",
    "player-not-found": "&c&lChocolate &8» &f%player% &ccannot be found.",
    "proxy-not-found": "&c&lChocolate &8» &cCannot find a proxy with the name: &f%proxy%&c.",
    "must-be-a-player": "&c&lChocolate &8» &cYou must be a player to use this command"
  },
  "starting": {
    "join": "&c&lChocolate &8» &cCannot join while the server hasn't been fully started yet."
  },
  "command": {
    "find": {
      "found": "&c&lChocolate &8» &f%player% &7is found in &f%server% &7in proxy &f%proxy%&7."
    },
    "glist": {
      "global": "&c&lChocolate &8» &7There are currently &f%online% &7players globally connected.",
      "proxy": "&c&lChocolate &8» &7There are currently &f%online% &7players connected to proxy &f%proxy%."
    },
    "ip": {
      "found": "&c&lChocolate &8» &f%player% &7his ip is &f%ip%"
    },
    "last-seen": {
      "found": "&c&lChocolate &8» &f%player% &7was last seen &f%seen% &7ago"
    },
    "proxy-id": {
      "id": "&c&lChocolate &8» &7You are on proxy &f%proxyId%"
    },
    "proxy-id-list": {
      "list": "&c&lChocolate &8» &7Proxies: &f%proxies%"
    },
    "alert": {
      "format": "&8[&cAlert&8] &f%message%"
    },
    "chocolate": {
      "command-format": "&c%usage% &8- &f%description%",
      "msg": "&8&m--------------&r&8{ &c&lChocolate &8}&m--------------\n &f* &cAuthors: &f%authors%\n &f* &cGithub: &fhttps://github.com/GiansCode/Chocolate\n\n&c&lCommands &8»\n%commands%&8&m--------------&r&8{ &c&lChocolate &8}&m--------------"
    }
  }
}
```

#### redis.yml

```yaml
singleServerConfig:
  idleConnectionTimeout: 10000
  connectTimeout: 10000
  timeout: 3000
  retryAttempts: 3
  retryInterval: 1500
  password: "pswd"
  subscriptionsPerConnection: 5
  clientName: null
  address: "redis://host:6379"
  subscriptionConnectionMinimumIdleSize: 1
  subscriptionConnectionPoolSize: 50
  connectionMinimumIdleSize: 10
  connectionPoolSize: 64
  database: 12
  dnsMonitoringInterval: 5000
threads: 0
nettyThreads: 0
```