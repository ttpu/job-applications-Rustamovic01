package applications;

import java.util.*;

public class Position implements Comparable<Position> {
    private final String name;
    private final Set<Skill> requiredSkills;
    private final SortedSet<String> applicants; // applicant names sorted alphabetically
    private String winner;

    public Position(String name, Collection<Skill> skills) {
        this.name = name;
        this.requiredSkills = new HashSet<>(skills);
        this.applicants = new TreeSet<>();
        this.winner = null;
    }

    public String getName() {
        return name;
    }

    public Set<Skill> getRequiredSkills() {
        return Collections.unmodifiableSet(requiredSkills);
    }

    public void addApplicant(String applicantName) {
        applicants.add(applicantName);
    }

    public List<String> getApplicants() {
        return new ArrayList<>(applicants);
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    @Override
    public int compareTo(Position o) {
        return this.name.compareTo(o.name);
    }
}
