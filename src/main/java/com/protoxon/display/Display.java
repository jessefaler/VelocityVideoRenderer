package com.protoxon.display;

import com.google.inject.Inject;
import com.protoxon.display.command.DisplayCommand;
import com.protoxon.display.utils.Color;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;

@Plugin(id = "display", name = "display", version = "1.0", description = "video player using text displays", url = "protoxon.com", authors = {"protoxon"})
public class Display {

    public static Logger logger;
    public static Display plugin;
    public static ProxyServer proxy;
    public static DisplayManager DISPLAY_MANAGER;

    @Inject //injects the proxy server and logger into the plugin class (dependency injection)
    public Display(ProxyServer proxy, Logger logger) {
        Display.logger = logger;
        Display.proxy = proxy;
        plugin = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        logger.info("{} Display Plugin Enabled {}", Color.CYAN, Color.RESET);
        DISPLAY_MANAGER = new DisplayManager();
        DisplayCommand.register();
    }
}
