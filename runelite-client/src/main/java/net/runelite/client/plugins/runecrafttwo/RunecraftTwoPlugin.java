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

import com.google.common.base.Stopwatch;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.ItemManager;
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
    private RunecraftTwoSession session;
    private Stopwatch timer;
    private Stats stats = new Stats();
    @Inject
    private
    ItemManager itemManager;
    @Inject
    private Client client;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private RunecraftTwoConfig config;
    private RunecraftTwoPanel panel;
    private NavigationButton navButton;
    private int carriedEssence = 0;

    @Provides
    RunecraftTwoConfig getConfig(ConfigManager configManager)
    {
        return configManager.getConfig(RunecraftTwoConfig.class);
    }

    @Override
    protected void startUp()
    {
        panel = injector.getInstance(RunecraftTwoPanel.class);
        panel.init();

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
        if (stats.RUNES.isEmpty())
        {
            stats.RUNES.put(ItemID.AIR_RUNE, new Rune(5, "Air", ItemID.AIR_RUNE));
            stats.RUNES.put(ItemID.MIND_RUNE, new Rune(5.5, "Mind", ItemID.MIND_RUNE));
            stats.RUNES.put(ItemID.WATER_RUNE, new Rune(6, "Water", ItemID.WATER_RUNE));
            stats.RUNES.put(ItemID.EARTH_RUNE, new Rune(6.5, "Earth", ItemID.EARTH_RUNE));
            stats.RUNES.put(ItemID.FIRE_RUNE, new Rune(7, "Fire", ItemID.FIRE_RUNE));
            stats.RUNES.put(ItemID.BODY_RUNE, new Rune(7.5, "Body", ItemID.BODY_RUNE));
            stats.RUNES.put(ItemID.COSMIC_RUNE, new Rune(8, "Cosmic", ItemID.COSMIC_RUNE));
            stats.RUNES.put(ItemID.CHAOS_RUNE, new Rune(8.5, "Chaos", ItemID.CHAOS_RUNE));
            stats.RUNES.put(ItemID.ASTRAL_RUNE, new Rune(8.7, "Astral", ItemID.ASTRAL_RUNE));
            stats.RUNES.put(ItemID.NATURE_RUNE, new Rune(9, "Nature", ItemID.NATURE_RUNE));
            stats.RUNES.put(ItemID.LAW_RUNE, new Rune(9.5, "Law", ItemID.LAW_RUNE));
            stats.RUNES.put(ItemID.DEATH_RUNE, new Rune(10, "Death", ItemID.DEATH_RUNE));
        }
        if (session == null)
        {
            return;
        }
        Duration statTimeout = Duration.ofMinutes(config.statTimeout());
        Duration sinceCut = Duration.between(session.getLastRuneCraft(), Instant.now());

        if (sinceCut.compareTo(statTimeout) >= 0 || panel.pause)
        {
            session = null;
            timer = null;
            panel.pause = false;
        }
        if (panel.reset)
        {
            session = null;
            stats = new Stats();
            timer = null;
            panel.reset = false;
            panel.statsLabel.setText("");

        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() == ChatMessageType.FILTERED || event.getType() == ChatMessageType.SERVER)
        {
            if (event.getMessage().startsWith("You bind the temple") && event.getMessage().endsWith("runes."))
            {
                if (session == null)
                {
                    session = new RunecraftTwoSession();
                    if (stats.totalLapTime != 0)
                    {
                        stats.totalLapTime += stats.totalLapTime / stats.laps;
                    }
                }
                session.setLastRuneCraft();
            }
        }
    }


    private void checkInv(ItemContainer itemContainer)
    {
        int lastCarriedEssence = carriedEssence;
        int tmpCarriedEssence = 0;
        for (Rune rune : stats.RUNES.values())
        {
            rune.setTmpCarried(0);
        }
        // Stop if empty inv
        if (itemContainer == null)
        {
            return;
        }

        // Count pure ess and current carried runes
        Item[] items = itemContainer.getItems();
        if (items != null)
        {
            for (Item item : items)
            {
                if (item == null)
                {
                    continue;
                }
                if (item.getId() == ItemID.PURE_ESSENCE)
                {
                    tmpCarriedEssence++;
                }
                if (stats.RUNES.containsKey(item.getId()))
                {
                    Rune runeItem = stats.RUNES.get(item.getId());
                    runeItem.setTmpCarried(item.getQuantity());
                    stats.RUNES.put(item.getId(), runeItem);
                }
            }
        }

        // if no carried essence updated runes and essence crafted, check pouches for lap timer
        if (tmpCarriedEssence == 0 && carriedEssence != 0)
        {
            int pouchEssence = 0;
            for (Rune rune : stats.RUNES.values())
            {
                if (rune.getTmpCarried() > rune.getCarried())
                {
                    stats.craftedEssence += lastCarriedEssence;
                    stats.craftedRunes += rune.getTmpCarried() - rune.getCarried();
                    rune.setCrafted(rune.getCrafted() + (rune.getTmpCarried() - rune.getCarried()));
                }

                for (Item item : itemContainer.getItems())
                {
                    switch (item.getId())
                    {
                        case ItemID.GIANT_POUCH:
                            pouchEssence += client.getVar(Varbits.POUCH_GIANT);
                        case ItemID.LARGE_POUCH:
                            pouchEssence += client.getVar(Varbits.POUCH_LARGE);
                        case ItemID.MEDIUM_POUCH:
                            pouchEssence += client.getVar(Varbits.POUCH_MEDIUM);
                        case ItemID.SMALL_POUCH:
                            pouchEssence += client.getVar(Varbits.POUCH_SMALL);

                    }
                }

            }

            // if no essence start new lap
            if (pouchEssence == 0)
            {
                if (timer != null)
                {
                    timer.stop();
                    stats.lapTime = timer.elapsed(TimeUnit.SECONDS);
                    stats.totalLapTime += stats.lapTime;
                    if (stats.laps == 0)
                    {
                        stats.laps = 1;
                        stats.totalLapTime += stats.lapTime;
                    }
                    stats.laps += 1;
                }
                timer = Stopwatch.createStarted();
            }
        }

        // update carried ess and runes
        for (Rune rune : stats.RUNES.values())
        {
            rune.setCarried(rune.getTmpCarried());
        }
        carriedEssence = tmpCarriedEssence;
    }

    private String formatTime(long minutes, long seconds)
    {
        String output;
        if ((seconds % 60) < 10)
        {
            output = Long.toString(minutes) + ":0" + Long.toString(seconds);
        }
        else
        {
            output = Long.toString(minutes) + ":" + Long.toString(seconds);
        }
        return output;
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
    {
        ItemContainer itemContainer = itemContainerChanged.getItemContainer();
        if (itemContainer == client.getItemContainer(InventoryID.INVENTORY))
        {
            StringBuilder output = new StringBuilder("<html><body>");
            checkInv(itemContainer);
            int tmpProfit = 0;
            for (Rune rune : stats.RUNES.values())
            {
                if (rune.getCrafted() > 0)
                {
                    int profit = itemManager.getItemPrice(rune.getId()) * rune.getCrafted();
                    tmpProfit += profit;
                    output.append(rune.name).append(" Runes: ").append(Integer.toString(rune.getCrafted()));
                    output.append(" / ").append(NumberFormat.getNumberInstance(Locale.US).format(profit)).append("gp<br>");
                }
            }
            stats.totalProfit = tmpProfit;
            output.append("<br>");
            output.append("Crafted Runes: ").append(Integer.toString(stats.craftedRunes)).append(" / ").append(NumberFormat.getNumberInstance(Locale.US).format(stats.totalProfit)).append("gp<br>");
            output.append("Crafted Essence: ").append(Integer.toString(stats.craftedEssence)).append("<br>");
            if (stats.lapTime != 0)
            {
                int gph = (int) (1.0 / (stats.totalLapTime / 3600.0)) * stats.totalProfit;
                output.append("gp/h: ").append(NumberFormat.getNumberInstance(Locale.US).format(gph)).append("<br>");
                output.append("<br>");
                output.append("Laps: ").append(Integer.toString(stats.laps)).append("<br>");
                output.append("Lap Time: ").append(formatTime(stats.lapTime / 60, stats.lapTime % 60)).append("<br>");
                long averageMinutes = (stats.totalLapTime / (stats.laps)) / 60;
                long averageSeconds = (stats.totalLapTime / (stats.laps)) % 60;
                output.append("Average Lap Time: ").append(formatTime(averageMinutes, averageSeconds)).append("<br>");
                output.append("Total Time: ").append(formatTime(stats.totalLapTime / 60, stats.totalLapTime % 60));

            }

            output.append("</body></html>");
            panel.statsLabel.setText(output.toString());
        }
    }
}