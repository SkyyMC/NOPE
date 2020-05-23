package xyz.msws.anticheat.actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import xyz.msws.anticheat.NOPE;
import xyz.msws.anticheat.checks.Check;
import xyz.msws.anticheat.utils.MSG;

/**
 * Action Manager is the primary class for loading and executing actions for
 * when a player flags a specific check
 * 
 * @author imodm
 *
 */
public class ActionManager {
	private ConfigurationSection section;

	private File file;

	private ActionFactory factory;

	private Map<String, List<ActionGroup>> actions;

	public ActionManager(NOPE plugin, File file) {
		this.file = file;
		this.factory = new ActionFactory(plugin, this);

		ConfigurationSection s = YamlConfiguration.loadConfiguration(file);
		if (!s.isConfigurationSection("Actions")) {
			MSG.error("No actions are defined for ActionManager");
			return;
		}

		this.section = s.getConfigurationSection("Actions");
	}

	/**
	 * Returns all actions
	 * 
	 * @return
	 */
	@Nonnull
	public Map<String, List<ActionGroup>> getAllActions() {
		return actions;
	}

	/**
	 * Returns the list of actions related to a specific type of check
	 * 
	 * @param type
	 * @return List of actions or empty arraylist if none
	 */
	@Nonnull
	public List<ActionGroup> getActions(String type) {
		return actions.getOrDefault(type, new ArrayList<>());
	}

	public boolean hasAction(String type) {
		return actions.containsKey(type);
	}

	/**
	 * Manually reload actions from the file
	 */
	@SuppressWarnings("unchecked")
	public void loadActions() {
		actions = new HashMap<>();
		loadCustomActions();
		if (section == null) {
			MSG.error("Actions section is null.");
			return;
		}
		for (Entry<String, Object> entry : section.getValues(false).entrySet()) {
			if (!(entry.getValue() instanceof List)) {
				MSG.warn("Invalid value set for " + MSG.FORMAT_INFO + entry.getKey() + MSG.ERROR + " skipping.");
				continue;
			}

			List<String> commands = (List<String>) entry.getValue();
			ActionGroup group = new ActionGroup(new ArrayList<>());
			List<ActionGroup> groups = new ArrayList<>();
			for (String commandLine : commands) {
				for (String cmd : commandLine.split("\\|")) {
					AbstractAction action = factory.createAction(cmd);
					if (action == null)
						MSG.error(entry.getKey() + "." + commandLine + " (" + cmd + ") is an invalid action");
					else
						group.add(factory.createAction(cmd));
				}
				groups.add(group);
			}
			actions.put(entry.getKey(), groups);
		}
	}

	public void setActions(String category, List<ActionGroup> actions) {
		this.actions.put(category, actions);
	}

	public void addAction(String category, ActionGroup action) {
		this.actions.getOrDefault(category, new ArrayList<>()).add(action);
	}

	public boolean runActions(OfflinePlayer player, String category, Check check) {
		if (!actions.containsKey(category)) {
			for (ActionGroup group : actions.get("Default"))
				group.activate(player, check);
			return false;
		}
		for (ActionGroup group : actions.get(category))
			group.activate(player, check);
		return true;
	}

	public ActionFactory getActionFactory() {
		return factory;
	}

	/**
	 * Manually loads custom defined commands so loading of new actions can
	 * reference them
	 */
	private void loadCustomActions() {
		ConfigurationSection conf = YamlConfiguration.loadConfiguration(file);
		if (!conf.isConfigurationSection("Commands"))
			return;
		conf = conf.getConfigurationSection("Commands");
		for (Entry<String, Object> entry : conf.getValues(false).entrySet()) {
			if (actions.containsKey(entry.getKey()))
				continue;
			ActionGroup group = new ActionGroup();
			for (String action : ((String) entry.getValue()).split("\\|")) {
				AbstractAction act = factory.createAction(action);
				if (act == null)
					MSG.error(entry.getKey() + "." + action + " is an invalid action.");
				else
					group.add(factory.createAction(action));
			}
			actions.put(entry.getKey(), Arrays.asList(group));
//			actions.put(entry.getKey(),
//					Arrays.asList(new ActionGroup(Arrays.asList(new CustomAction(plugin, (String) entry.getValue())))));
		}
	}
}