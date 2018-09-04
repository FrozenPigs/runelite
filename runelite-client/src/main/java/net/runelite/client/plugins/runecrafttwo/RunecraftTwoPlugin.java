/*
 * Copyright (c) 2018 Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.runecrafttwo;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@PluginDescriptor(
    name = "Runecrafting Panel",
    description = "Enable the Runecrafting Panel",
    loadWhenOutdated = true
)
public class RunecraftTwoPlugin extends Plugin
{
    @Inject
    private ClientToolbar clientToolbar;

    private RunecraftTwoPanel panel;
    @Inject
    private
    RunecraftTwoConfig config;
    private NavigationButton navButton;

    @Provides
    RunecraftTwoConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(RunecraftTwoConfig.class);
    }

    @Override
    protected void startUp()
    {

        this.panel = injector.getInstance(RunecraftTwoPanel.class);
        this.panel.init();

        final BufferedImage icon = ImageUtil.getResourceStreamFromClass(getClass(), "rc_icon.png");

        navButton = NavigationButton.builder()
            .tooltip("Runecrafting")
            .icon(icon)
            .priority(9)
            .panel(panel)
            .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown()
    {
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onGameTick(GameTick gameTick)

    {
        if (this.panel.RUNES.isEmpty())
        {
            this.panel.RUNES.put(ItemID.AIR_RUNE, new Rune(5, "Air", ItemID.AIR_RUNE));
            this.panel.RUNES.put(ItemID.MIND_RUNE, new Rune(5.5, "Mind", ItemID.MIND_RUNE));
            this.panel.RUNES.put(ItemID.WATER_RUNE, new Rune(6, "Water", ItemID.WATER_RUNE));
            this.panel.RUNES.put(ItemID.EARTH_RUNE, new Rune(6.5, "Earth", ItemID.EARTH_RUNE));
            this.panel.RUNES.put(ItemID.FIRE_RUNE, new Rune(7, "Fire", ItemID.FIRE_RUNE));
            this.panel.RUNES.put(ItemID.BODY_RUNE, new Rune(7.5, "Body", ItemID.BODY_RUNE));
            this.panel.RUNES.put(ItemID.COSMIC_RUNE, new Rune(8, "Cosmic", ItemID.COSMIC_RUNE));
            this.panel.RUNES.put(ItemID.CHAOS_RUNE, new Rune(8.5, "Chaos", ItemID.CHAOS_RUNE));
            this.panel.RUNES.put(ItemID.ASTRAL_RUNE, new Rune(8.7, "Astral", ItemID.ASTRAL_RUNE));
            this.panel.RUNES.put(ItemID.NATURE_RUNE, new Rune(9, "Nature", ItemID.NATURE_RUNE));
            this.panel.RUNES.put(ItemID.LAW_RUNE, new Rune(9.5, "Law", ItemID.LAW_RUNE));
            this.panel.RUNES.put(ItemID.DEATH_RUNE, new Rune(10, "Death", ItemID.DEATH_RUNE));
        }
        if (this.panel.session == null)
        {
            return;
        }
        Duration statTimeout = Duration.ofMinutes(config.statTimeout());
        Duration sinceCut = Duration.between(this.panel.session.getLastRuneCraft(), Instant.now());

        if (sinceCut.compareTo(statTimeout) >= 0)
        {
            this.panel.session = null;
        }
    }
}
