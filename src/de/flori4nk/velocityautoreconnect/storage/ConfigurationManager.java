package de.flori4nk.velocityautoreconnect.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationManager {
	
	private Properties properties;
	
	public ConfigurationManager(File configurationFile) {
		this.properties = new Properties();
		
		try {
			// Create the configuration file if it doesn't exist.
			if(!configurationFile.exists()) {
				configurationFile.getParentFile().mkdirs();
				configurationFile.createNewFile();
			}
			
			// Set default values
			this.properties.setProperty("limbo-name", "limbo");
			this.properties.setProperty("directconnect-server", "lobby");
			this.properties.setProperty("task-interval-ms", "3500");
			this.properties.setProperty("kick-filter.blacklist", ".* ([Bb]anned|[Kk]icked).*");
			this.properties.setProperty("kick-filter.blacklist.enabled", "false");
			this.properties.setProperty("kick-filter.whitelist", "Server closed");
			this.properties.setProperty("kick-filter.whitelist.enabled", "true");
			this.properties.setProperty("message", "You will be reconnected soon.");
			this.properties.setProperty("message.enabled", "false");
			this.properties.setProperty("pingcheck", "true");
			
			// Load saved values
			this.properties.load(new FileInputStream(configurationFile));
			
			// Save old values combined with possible new defaults
			this.properties.store(new FileOutputStream(configurationFile), "VelocityAutoReconnect configuration -- Documentation: https://github.com/flori4nk/VelocityAutoReconnect/blob/master/README.md#configuration");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key) {
		return this.properties.getProperty(key, "");
	}
	
	public boolean getBooleanProperty(String key) {
		return Boolean.valueOf(this.getProperty(key));
	}
	
	public int getIntegerProperty(String key) {
		return Integer.valueOf(this.getProperty(key));
	}
	
}
