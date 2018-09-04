/*
 * Copyright (c) 2018 Abex
 * Copyright (c) 2018, Psikoi <https://github.com/psikoi>
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
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;


@Singleton
public class RunecraftTwoPanel extends PluginPanel
{
    public final Map<Integer, Rune> RUNES = new HashMap<>();
    private final JPanel runePanel = new JPanel();
    private final JLabel label1 = new JLabel();
    public RunecraftTwoSession session;
    @Inject
    private EventBus eventBus;
    @Inject
    private Client client;
    @Inject
    private
    ItemManager itemManager;
    private int craftedRunes = 0;
    private int carriedEssence = 0;
    private int craftedEssence = 0;
    private int laps = 0;
    private long totalLapTime = 0;
    private String lapTime = "";
    private Stopwatch timer;
    private int startXp = 0;
    private int currentXp = 0;
    private ItemContainer itemContainer;


    void init()
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        final Font smallFont = FontManager.getRunescapeSmallFont();

        runePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        runePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        runePanel.setLayout(new GridLayout(0, 1));

        label1.setFont(smallFont);
        runePanel.add(label1);
        //versionPanel.add(Box.createGlue());

        add(runePanel, BorderLayout.NORTH);

        eventBus.register(this);
    }


    private void checkInv()
    {
        int lastEssence = this.carriedEssence;
        int essence = 0;
        int totalRunes = 0;

        for (Rune rune : RUNES.values())
        {
            rune.setCount(0);
        }
        if (itemContainer == null)
        {
            return;
        }

        Item[] items = this.itemContainer.getItems();
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
                    essence++;
                }
                if (this.RUNES.containsKey(item.getId()))
                {
                    Rune runeItem = this.RUNES.get(item.getId());
                    runeItem.setCount(item.getQuantity());
                    this.RUNES.put(item.getId(), runeItem);
                    totalRunes += item.getQuantity();
                }
            }
        }

        if (essence == 0 && this.carriedEssence != 0)
        {
            int pouchEssence = 0;
            for (Rune rune : this.RUNES.values())
            {
                if (rune.getCount() > rune.getCarried())
                {
                    this.craftedEssence += lastEssence;
                    this.craftedRunes += rune.getCount() - rune.getCarried();
                    rune.setCrafted(rune.getCrafted() + (rune.getCount() - rune.getCarried()));
                }

                for (Item item : this.itemContainer.getItems())
                {
                    if (ItemID.GIANT_POUCH == item.getId())
                    {
                        pouchEssence += client.getVar(Varbits.POUCH_GIANT);
                    }
                    if (ItemID.LARGE_POUCH == item.getId())
                    {
                        pouchEssence += client.getVar(Varbits.POUCH_LARGE);
                    }
                    if (ItemID.MEDIUM_POUCH == item.getId())
                    {
                        pouchEssence += client.getVar(Varbits.POUCH_MEDIUM);
                    }
                    if (ItemID.SMALL_POUCH == item.getId())
                    {
                        pouchEssence += client.getVar(Varbits.POUCH_SMALL);
                    }
                }

            }

            if (pouchEssence == 0)
            {
                if (this.timer != null)
                {
                    this.timer.stop();
                    long elapsedSeconds = this.timer.elapsed(TimeUnit.SECONDS);
                    long minutes = elapsedSeconds / 60;
                    long seconds = elapsedSeconds % 60;
                    this.totalLapTime += elapsedSeconds;
                    if (seconds < 10)
                    {
                        this.lapTime = Long.toString(minutes) + ":0" + Long.toString(seconds);
                    }
                    else
                    {
                        this.lapTime = Long.toString(minutes) + ":" + Long.toString(seconds);
                    }
                }
                this.timer = Stopwatch.createStarted();
                this.laps += 1;
            }
        }
        for (Rune rune : this.RUNES.values())
        {
            rune.setCarried(rune.getCount());
        }
        this.carriedEssence = essence;
    }


    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
    {
        this.currentXp += client.getSkillExperience(Skill.RUNECRAFT);
        int gainedXp = this.currentXp - this.startXp;
        int totalProfit = 0;
        ItemContainer itemContainer = itemContainerChanged.getItemContainer();
        if (itemContainer == client.getItemContainer(InventoryID.INVENTORY))
        {
            StringBuilder output = new StringBuilder("<html><body>");
            this.itemContainer = itemContainer;
            checkInv();

            for (Rune rune : RUNES.values())
            {
                if (rune.crafted > 0)
                {
                    int profit = itemManager.getItemPrice(rune.id) * rune.crafted;
                    totalProfit += profit;
                    output.append(rune.name).append(" Runes: ").append(Integer.toString(rune.crafted)).append(" / ").append(NumberFormat.getNumberInstance(Locale.US).format(totalProfit)).append("gp<br>");
                }
            }
            output.append("<br>");
            output.append("Total Runes: ").append(Integer.toString(this.craftedRunes)).append(" / ").append(NumberFormat.getNumberInstance(Locale.US).format(totalProfit)).append("gp<br>");
            output.append("Total Essence: ").append(Integer.toString(this.craftedEssence)).append("<br>");
            int gph = (int) (1.0 / (this.totalLapTime / 3600.0)) * totalProfit;
            output.append("gp/h: ").append(NumberFormat.getNumberInstance(Locale.US).format(gph)).append("<br>");
            output.append("<br>");
            output.append("Laps: ").append(Integer.toString(this.laps)).append("<br>");

            if (!Objects.equals(this.lapTime, ""))
            {
                long averageMinutes = (this.totalLapTime / (this.laps - 1)) / 60;
                long averageSeconds = (this.totalLapTime / (this.laps - 1)) % 60;
                output.append("Lap Time: ").append(this.lapTime).append("<br>");
                if (averageSeconds < 10)
                {
                    output.append("Average Lap Time: ").append(Long.toString(averageMinutes)).append(":0").append(Long.toString(averageSeconds)).append("<br>");
                }
                else
                {
                    output.append("Average Lap Time: ").append(Long.toString(averageMinutes)).append(":").append(Long.toString(averageSeconds)).append("<br>");
                }
                long totalMinutes = this.totalLapTime / 60;
                long totalSeconds = this.totalLapTime % 60;
                if (totalSeconds < 10)
                {
                    output.append("Total Time: ").append(Long.toString(totalMinutes)).append(":0").append(Long.toString(totalSeconds)).append("<br>");
                }
                else
                {
                    output.append("Total Time: ").append(Long.toString(totalMinutes)).append(":").append(Long.toString(totalSeconds)).append("<br>");
                }
            }

            output.append("</body></html>");
            label1.setText(output.toString());
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
                    this.startXp = client.getSkillExperience(Skill.RUNECRAFT);
                }
                session.setLastRuneCraft();
            }
        }
    }
}
