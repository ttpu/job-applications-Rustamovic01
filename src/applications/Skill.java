package applications;

import java.util.*;

public class Skill {
    private String name;
    private Set<Position> positions = new TreeSet<>(Comparator.comparing(Position::getName));

    public Skill(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void addPosition(Position position) {
        positions.add(position);
    }

    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }
}
