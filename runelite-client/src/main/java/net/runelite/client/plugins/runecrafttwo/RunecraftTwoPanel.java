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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;


@Singleton
public class RunecraftTwoPanel extends PluginPanel
{
    final JLabel statsLabel = new JLabel();
    private final JPanel runePanel = new JPanel();
    public boolean reset = false;
    public boolean pause = false;

    private RunecraftTwoPlugin runePlugin;
    @Inject
    private EventBus eventBus;

    void init()
    {
        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        final Font smallFont = FontManager.getRunescapeSmallFont();

        runePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        runePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        runePanel.setLayout(new GridLayout(0, 1));

        statsLabel.setFont(smallFont);
        runePanel.add(statsLabel);

        JPanel gap = new JPanel();
        gap.setBackground(ColorScheme.DARK_GRAY_COLOR);
        gap.setBorder(new EmptyBorder(1, 10, 1, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));


        final Color hoverColor = ColorScheme.DARKER_GRAY_HOVER_COLOR;
        final Color pressedColor = ColorScheme.DARKER_GRAY_COLOR.brighter();

        JPanel resetPanel = new JPanel();
        resetPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        resetPanel.setLayout(new BorderLayout());
        resetPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel resetLabel = new JLabel("Reset", SwingConstants.CENTER);
        resetPanel.add(resetLabel);
        resetPanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                resetPanel.setBackground(pressedColor);
                reset = true;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                resetPanel.setBackground(hoverColor);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                resetPanel.setBackground(hoverColor);
                resetPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                resetPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                resetPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        JPanel pausePanel = new JPanel();
        pausePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        pausePanel.setLayout(new BorderLayout());
        pausePanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        JLabel pauseLabel = new JLabel("Pause", SwingConstants.CENTER);
        pausePanel.add(pauseLabel);
        pausePanel.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mousePressed(MouseEvent mouseEvent)
            {
                pausePanel.setBackground(pressedColor);
                pause = true;
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                pausePanel.setBackground(hoverColor);
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                pausePanel.setBackground(hoverColor);
                pausePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                pausePanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
                pausePanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        buttonPanel.add(pausePanel, BorderLayout.NORTH);
        buttonPanel.add(resetPanel, BorderLayout.SOUTH);
        add(runePanel, BorderLayout.NORTH);
        add(gap, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        eventBus.register(this);
    }
}
