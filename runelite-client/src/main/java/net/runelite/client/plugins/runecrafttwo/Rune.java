package net.runelite.client.plugins.runecrafttwo;

import lombok.Getter;
import lombok.Setter;

class Rune
{

    @Getter
    double exp;

    @Getter
    String name;

    @Getter
    int id;

    @Getter
    @Setter
    int crafted;

    @Getter
    @Setter
    int carried;

    public Rune(double exp, String name, int id)
    {
        this.exp = exp;
        this.name = name;
        this.id = id;
        this.crafted = 0;
        this.carried = 0;
    }
}
