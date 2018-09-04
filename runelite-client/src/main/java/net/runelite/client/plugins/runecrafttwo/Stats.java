package net.runelite.client.plugins.runecrafttwo;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class Stats
{
    @Getter
    @Setter
    public Map<Integer, Rune> RUNES = new HashMap<>();

    @Getter
    @Setter
    public int craftedRunes;

    @Getter
    @Setter
    public int totalProfit;

    @Getter
    @Setter
    public int craftedEssence;

    @Getter
    @Setter
    public int gpPerHour;

    @Getter
    @Setter
    public int laps;

    @Getter
    @Setter
    public long lapTime;

    @Getter
    @Setter
    public long totalLapTime;

    public Stats()
    {
        craftedRunes = 0;
        totalProfit = 0;
        craftedEssence = 0;
        gpPerHour = 0;
        laps = 0;
        lapTime = 0;
        totalLapTime = 0;
    }
}
