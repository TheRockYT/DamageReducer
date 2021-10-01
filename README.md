# DamageReducer
## A damage change system for Bukkit/Spigot.
### Installation
1. Download the latest version from the "Releases" tab.
2. Now move the file into the plugins folder of your spigot / bukkit server.
3. Restart your server.
5. Done!
### Configuration ("plugins/DamageReducer/settings.yml")
1. Change "GlobalDamageValue" to 100.0=100%=Full damage;50.0=50%=Half damage;0.0=0%=No damage;....
2. Change "Items.<Type>" to 100.0=100%=Full damage;50.0=50%=Half damage;0.0=0%=No damage;....
3. Change some messages.
### Commands
/damgereducer <Value> - Set custom damage value. - Permission: *DamageReducer.set*\
/damgereducer remove - Remove custom damage value. - Permission: *DamageReducer.remove*\
/damgereducer reload - Reload the plugin. - Permission: *DamageReducer.reload*
