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
package de.flori4nk.velocityautoreconnect.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationManager {

    private final Properties properties;

    public ConfigurationManager(File configurationFile) {
        this.properties = new Properties();

        try {
            // Create the configuration file if it doesn't exist.
            if (!configurationFile.exists()) {
                configurationFile.getParentFile().mkdirs();
                configurationFile.createNewFile();
            }

            // Set default values
            this.properties.setProperty("limbo-name", "limbo");
            this.properties.setProperty("directconnect-server", "lobby");
            this.properties.setProperty("task-interval-ms", "3500");
            this.properties.setProperty("pingcheck", "true");
            this.properties.setProperty("ping-worldloaded-check", "true");
            this.properties.setProperty("bypasscheck", "false");
            this.properties.setProperty("kick-filter.blacklist", ".* ([Bb]anned|[Kk]icked|[Ww]hitelist).*");
            this.properties.setProperty("kick-filter.blacklist.enabled", "true");
            this.properties.setProperty("kick-filter.whitelist", "Server closed");
            this.properties.setProperty("kick-filter.whitelist.enabled", "false");
            this.properties.setProperty("message", "You will be reconnected soon.");
            this.properties.setProperty("message.enabled", "false");
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
        return Boolean.parseBoolean(this.getProperty(key));
    }

    public int getIntegerProperty(String key) {
        return Integer.parseInt(this.getProperty(key));
    }

}
