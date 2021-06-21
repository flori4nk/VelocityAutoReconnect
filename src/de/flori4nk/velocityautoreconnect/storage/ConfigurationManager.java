package de.flori4nk.velocityautoreconnect.storage;

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
			this.properties.setProperty("log.informational", "true");
			
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
