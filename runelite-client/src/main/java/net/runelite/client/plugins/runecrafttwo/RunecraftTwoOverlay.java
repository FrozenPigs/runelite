package net.runelite.client.plugins.runecrafttwo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.Query;
import net.runelite.api.queries.InventoryWidgetItemQuery;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.util.QueryRunner;


public class RunecraftTwoOverlay extends Overlay
{
    private final QueryRunner queryRunner;
    private final RunecraftTwoPlugin plugin;

    @Inject
    private RunecraftTwoOverlay(RunecraftTwoPlugin plugin, QueryRunner queryRunner)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
        this.queryRunner = queryRunner;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Query query = new InventoryWidgetItemQuery().idEquals(
            ItemID.GIANT_POUCH,
            ItemID.LARGE_POUCH,
            ItemID.MEDIUM_POUCH
        );
        WidgetItem[] items = queryRunner.runQuery(query);
        graphics.setFont(FontManager.getRunescapeSmallFont());
        Color backgroundColor = new Color(255, 255, 0, 50);
        for (WidgetItem item : items)
        {
            if (plugin.stats.degrades <= 0)
            {
                if (plugin.stats.pouch.equals("giant") && item.getId() == ItemID.GIANT_POUCH)
                {
                    graphics.setColor(backgroundColor);
                    graphics.fill(item.getCanvasBounds());
                }
                else if (plugin.stats.pouch.equals("small") && item.getId() == ItemID.LARGE_POUCH)
                {
                    graphics.setColor(backgroundColor);
                    graphics.fill(item.getCanvasBounds());

                }
                else if (plugin.stats.pouch.equals("medium") && item.getId() == ItemID.MEDIUM_POUCH)
                {
                    graphics.setColor(backgroundColor);
                    graphics.fill(item.getCanvasBounds());

                }
            }

        }
        return null;
    }
}
