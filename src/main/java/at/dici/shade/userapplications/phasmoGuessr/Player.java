package at.dici.shade.userapplications.phasmoGuessr;

import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Player implements Comparable<Player>, Serializable {
    @Serial
    private static final long serialVersionUID = 200001L;
    private final String id;
    private String name;
    private int points;
    private int fails;

    public Player(String id, String name, int points, int fails) {
        this.id = id;
        this.name = name;
        this.points = points;
        this.fails = fails;
    }

    public Player(Member member) {
        id = member.getId();
        name = member.getEffectiveName();
        points = 0;
        fails = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }

    public int getFails() {
        return fails;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int addPoints(int points) {
        this.points += points;
        return this.points;
    }

    public int failed() {
        return ++fails;
    }

    public void resetFails() {
        fails = 0;
    }

    @Override
    public int compareTo(@NotNull Player otherPlayer) {
        return Integer.compare(getPoints(), otherPlayer.getPoints());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof Player p) {
            return getId().equals(p.getId());
        }
        else return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
