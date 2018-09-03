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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import lombok.Getter;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;

@Singleton
public class RunecraftTwoPanel extends PluginPanel
{
    public JPanel runePanel = new JPanel();
    @Inject
    private EventBus eventBus;
    @Inject
    private Client client;
    @Getter
    private Map<Integer, Rune> RUNES = new HashMap<>();
    private JLabel label1 = new JLabel();

    private boolean started = false;
    private int carriedEssence = 0;
    private int craftedEssence = 0;
    private ItemContainer itemContainer;

    private Item[] items;

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

    @Subscribe
    public void onGameTick(GameTick gameTick)

    {
        if (this.RUNES.isEmpty())
        {
            this.RUNES.put(ItemID.AIR_RUNE, new Rune(5, "Air", ItemID.AIR_RUNE));
            this.RUNES.put(ItemID.MIND_RUNE, new Rune(5.5, "Mind", ItemID.MIND_RUNE));
            this.RUNES.put(ItemID.WATER_RUNE, new Rune(6, "Water", ItemID.WATER_RUNE));
            this.RUNES.put(ItemID.EARTH_RUNE, new Rune(6.5, "Earth", ItemID.EARTH_RUNE));
            this.RUNES.put(ItemID.FIRE_RUNE, new Rune(7, "Fire", ItemID.FIRE_RUNE));
            this.RUNES.put(ItemID.BODY_RUNE, new Rune(7.5, "Body", ItemID.BODY_RUNE));
            this.RUNES.put(ItemID.COSMIC_RUNE, new Rune(8, "Cosmic", ItemID.COSMIC_RUNE));
            this.RUNES.put(ItemID.CHAOS_RUNE, new Rune(8.5, "Chaos", ItemID.CHAOS_RUNE));
            this.RUNES.put(ItemID.ASTRAL_RUNE, new Rune(8.7, "Astral", ItemID.ASTRAL_RUNE));
            this.RUNES.put(ItemID.NATURE_RUNE, new Rune(9, "Nature", ItemID.NATURE_RUNE));
            this.RUNES.put(ItemID.LAW_RUNE, new Rune(9.5, "Law", ItemID.LAW_RUNE));
            this.RUNES.put(ItemID.DEATH_RUNE, new Rune(10, "Death", ItemID.DEATH_RUNE));
            this.started = true;
        }
        checkInv();
    }

    public void checkInv()
    {
        int count = 0;
        Item[] items = this.itemContainer.getItems();
        for (int i = 0; i < 28; i++)
        {
            if (i < items.length)
            {
                switch (items[i].getId())
                {
                    case ItemID.AIR_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.MIND_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.WATER_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.EARTH_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.FIRE_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.BODY_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.COSMIC_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.CHAOS_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.ASTRAL_RUNE:
                        if (this.RUNES.get(items[i].getId()).carried < items[i].getQuantity())
                        {
                            this.RUNES.get(items[i].getId()).crafted = items[i].getQuantity() - this.RUNES.get(items[i].getId()).carried;
                            this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                        }
                    case ItemID.NATURE_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.LAW_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.DEATH_RUNE:
                        this.RUNES.get(items[i].getId()).carried = items[i].getQuantity();
                    case ItemID.PURE_ESSENCE:
                        count++;
                }
            }
        }
        this.carriedEssence = count;
    }


    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
    {

        ItemContainer itemContainer = itemContainerChanged.getItemContainer();
        if (itemContainer == client.getItemContainer(InventoryID.INVENTORY))
        {
            this.itemContainer = itemContainer;
            if (!this.started)
            {
                return;
            }
            checkInv();
            label1.setText(Integer.toString(this.craftedEssence) + " " + Integer.toString(this.RUNES.get(ItemID.ASTRAL_RUNE).crafted));
        }
    }


    @Subscribe
    public void onChatMessage(ChatMessage event)
    {
        if (event.getType() == ChatMessageType.FILTERED || event.getType() == ChatMessageType.SERVER)
        {
            if (event.getMessage().startsWith("You bind the temple") && event.getMessage().endsWith("runes."))
            {
                this.craftedEssence += this.carriedEssence;
            }
        }
    }
}
