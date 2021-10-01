package TheRockYT.DamageReducer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class DamageReducer extends JavaPlugin implements Listener {
	YamlConfiguration cfg;
	HashMap<Material, Double> reducedItems;

	public void load() {
		File f = new File(getDataFolder(), "settings.yml");
		cfg = YamlConfiguration.loadConfiguration(f);
		cfg.options().header("Values in percent. 100.0=Normal damage.");
		reducedItems = new HashMap<Material, Double>();
		cfg.addDefault("GlobalDamageValue", 90.0);
		cfg.addDefault("Debug", false);
		cfg.addDefault("Items.WOOD_SWORD", 95.0);
		cfg.addDefault("Items.IRON_SWORD", 95.0);
		cfg.addDefault("Command.Reload.No_Permission",
				"&4Damage&cReducer &4>> &cYou need more permissions! (DamageReducer.reload)");
		cfg.addDefault("Command.Reload.Reloading", "&4Damage&cReducer &4>> &cReloading...");
		cfg.addDefault("Command.Reload.Reloaded", "&4Damage&cReducer &4>> &aDone!");
		cfg.addDefault("Command.Set.No_Permission",
				"&4Damage&cReducer &4>> &cYou need more permissions! (DamageReducer.set)");
		cfg.addDefault("Command.Set.Done", "&4Damage&cReducer &4>> &aThe Value was set!");
		cfg.addDefault("Command.Set.Not_Valid", "&4Damage&cReducer &4>> &cPlease use a valid item/number!");
		cfg.addDefault("Command.Remove.Not_Valid", "&4Damage&cReducer &4>> &cPlease use a valid item/number!");
		cfg.addDefault("Command.Remove.No_Permission",
				"&4Damage&cReducer &4>> &cYou need more permissions! (DamageReducer.remove)");
		cfg.addDefault("Command.Remove.Done", "&4Damage&cReducer &4>> &aThe Value was removed!");
		cfg.addDefault("Command.Remove.No_CustomValue", "&4Damage&cReducer &4>> &aThis item has no custom value!");
		cfg.addDefault("Command.MustBePlayer", "&4Damage&cReducer &4>> &cYou must be a player!");
		cfg.addDefault("Command.Help",
				"&4Damage&cReducer &4>> &cDamage&4Reducer&c by TheRockYT%nl%" + "&4Damage&cReducer &4>> &c%nl%"
						+ "&4Damage&cReducer &4>> &c/damagerducer reload - Reload the plugin%nl%"
						+ "&4Damage&cReducer &4>> &c/damagerducer <Value> - Set a custom damege value%nl%"
						+ "&4Damage&cReducer &4>> &c/damagerducer remove - Remove a custom damege value%nl%");
		for (String cfgS : cfg.getConfigurationSection("Items").getKeys(false)) {
			try {
				reducedItems.put(Material.valueOf(cfgS), cfg.getDouble("Items." + cfgS));
			} catch (Exception e) {
			}
		}
		Bukkit.getPluginManager().registerEvents(this, this);
		cfg.options().copyDefaults(true);
		try {
			cfg.save(f);
		} catch (IOException e) {
		}
	}

	public String getMessage(String key) {
		if (cfg.contains(key)) {
			String msg = cfg.getString(key);
			msg = msg.replace("%nl%", "\n");
			msg = ChatColor.translateAlternateColorCodes('&', msg);
			return msg;
		}
		return "";
	}

	@Override
	public void onEnable() {

		load();
		getCommand("DamageReducer").setExecutor(new CommandExecutor() {

			@Override
			public boolean onCommand(CommandSender sender, Command cmd, String egal, String[] args) {
				if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
					if (sender.hasPermission("DamageReducer.reload")) {
						sender.sendMessage(getMessage("Command.Reload.Reloading"));
						load();
						sender.sendMessage(getMessage("Command.Reload.Reloaded"));
					} else {
						sender.sendMessage(getMessage("Command.Reload.No_Permission"));
					}
				} else if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
					if (sender instanceof Player) {
						if (sender.hasPermission("DamageReducer.remove")) {
							Player p = (Player) sender;
							ItemStack is = p.getItemInHand();
							if (hasCustomDamage(p.getItemInHand())) {
								if (is != null && is.getItemMeta() != null) {
									if (hasCustomDamage(is)) {

										removeCustomDamage(is);
										sender.sendMessage(getMessage("Command.Remove.Done"));
									} else {
										sender.sendMessage(getMessage("Command.Remove.No_CustomValue"));

									}
								} else {
									sender.sendMessage(getMessage("Command.Remove.Not_Valid"));
								}
							} else {
								sender.sendMessage(getMessage("Command.Remove.No_CustomValue"));
							}
						} else {
							sender.sendMessage(getMessage("Command.Remove.No_Permission"));
						}
					} else {
						sender.sendMessage(getMessage("Command.MustBePlayer"));
					}
				} else if (args.length == 1) {
					if (sender instanceof Player) {
						if (sender.hasPermission("DamageReducer.set")) {
							Player p = (Player) sender;
							ItemStack is = p.getItemInHand();
							if (is != null && is.getItemMeta() != null) {
								try {
									Double d = Double.parseDouble(args[0]);
									if (d != null) {
										setCustomDamage(is, d);
										sender.sendMessage(getMessage("Command.Set.Done"));
									}
								} catch (Exception e) {
									sender.sendMessage(getMessage("Command.Set.Not_Valid"));
								}
							} else {
								sender.sendMessage(getMessage("Command.Set.Not_Valid"));
							}
						} else {
							sender.sendMessage(getMessage("Command.Set.No_Permission"));
						}
					} else {
						sender.sendMessage(getMessage("Command.MustBePlayer"));
					}
				} else {
					sender.sendMessage(getMessage("Command.Help"));
				}
				return false;
			}
		});
	}

	public boolean hasCustomDamage(ItemStack is) {
		if (is != null && is.getItemMeta() != null && is.getItemMeta().getLore() != null) {
			for (String s : is.getItemMeta().getLore()) {
				if (s.startsWith("DamegeValue: ")) {
					return true;
				}
			}
		}

		return false;
	}

	public ItemStack setCustomDamage(ItemStack is, Double damage) {
		if (is != null && is.getItemMeta() != null) {
			ArrayList<String> newLore = new ArrayList<String>();
			if (is.getItemMeta().getLore() != null) {
				for (String s : is.getItemMeta().getLore()) {
					if (!s.startsWith("DamegeValue: ")) {
						newLore.add(s);
					}
				}
			}

			ItemMeta meta = is.getItemMeta();
			newLore.add("DamegeValue: " + damage);
			meta.setLore(newLore);
			is.setItemMeta(meta);
		}

		return is;
	}

	public ItemStack removeCustomDamage(ItemStack is) {
		if (is != null && is.getItemMeta() != null) {
			ArrayList<String> newLore = new ArrayList<String>();
			if (is.getItemMeta().getLore() != null) {
				for (String s : is.getItemMeta().getLore()) {
					if (!s.startsWith("DamegeValue: ")) {
						newLore.add(s);
					}
				}
			}

			ItemMeta meta = is.getItemMeta();
			meta.setLore(newLore);
			is.setItemMeta(meta);
		}

		return is;
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() != null && e.getDamager() instanceof HumanEntity
				&& ((HumanEntity) e.getDamager()).getItemInHand() != null) {
			ItemStack is = ((HumanEntity) e.getDamager()).getItemInHand();
			Material mat = is.getType();

			Double reduceValue = null;
			if (is.getItemMeta() != null && is.getItemMeta().getLore() != null) {
				for (String s : is.getItemMeta().getLore()) {
					if (s.startsWith("DamegeValue: ")) {
						reduceValue = Double.parseDouble(s.replace("DamegeValue: ", ""));
					}
				}
			}

			if (reduceValue != null) {
				double newDamage1 = e.getDamage() / 100 * reduceValue;
				if (cfg.getBoolean("Debug") && e.getEntity() instanceof Player) {
					((Player) e.getEntity()).sendMessage("Reduced damege " + e.getDamage() + " to " + newDamage1 + "!");
					Bukkit.getConsoleSender()
							.sendMessage("Reduced damege " + e.getDamage() + " to " + newDamage1 + "!");
				}
				e.setDamage(newDamage1);
			} else if (reducedItems.containsKey(mat)) {
				double newDamage2 = e.getDamage() / 100 * reducedItems.get(mat);
				if (cfg.getBoolean("Debug") && e.getEntity() instanceof Player) {
					((Player) e.getEntity()).sendMessage("Reduced damege " + e.getDamage() + " to " + newDamage2 + "!");
					Bukkit.getConsoleSender()
							.sendMessage("Reduced damege " + e.getDamage() + " to " + newDamage2 + "!");
				}
				e.setDamage(newDamage2);
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		double newDamage = e.getDamage() / 100 * cfg.getDouble("GlobalDamageValue");
		boolean reduced = false;
		if (!reduced) {
			if (cfg.getBoolean("Debug") && e.getEntity() instanceof Player) {
				((Player) e.getEntity()).sendMessage("Reduced damege " + e.getDamage() + " to " + newDamage + "!");
				Bukkit.getConsoleSender().sendMessage("Reduced damege " + e.getDamage() + " to " + newDamage + "!");
			}
			e.setDamage(newDamage);
		}
	}
}
