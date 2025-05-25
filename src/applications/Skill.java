package applications;

import java.util.*;

public class Skill {
    private final String name;
    private final Set<Position> positions;

    public Skill(String name) {
        this.name = name;
        this.positions = new TreeSet<>(Comparator.comparing(Position::getName));
    }

    public String getName() {
        return name;
    }

    public void addPosition(Position position) {
        positions.add(position);
    }

    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }
}
