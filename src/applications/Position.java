package applications;

import java.util.*;

public class Position {
    private String name;
    private Set<Skill> requiredSkills;
    private Set<Applicant> applicants = new TreeSet<>(Comparator.comparing(Applicant::getName));
    private Applicant winner;

    public Position(String name, Set<Skill> skills) {
        this.name = name;
        this.requiredSkills = skills;
    }

    public String getName() {
        return name;
    }

    public void addApplicant(Applicant applicant) {
        applicants.add(applicant);
    }

    public List<Applicant> getApplicants() {
        return new ArrayList<>(applicants);
    }

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public String getWinner() {
        return winner != null ? winner.getName() : null;
    }

    public void setWinner(Applicant applicant) throws ApplicationException {
        if (winner != null) throw new ApplicationException("Winner already set");

        int sum = 0;
        for (Skill skill : requiredSkills) {
            Integer level = applicant.getSkillLevel(skill.getName());
            if (level == null)
                throw new ApplicationException("Missing required skill: " + skill.getName());
            sum += level;
        }

        if (sum <= 6 * requiredSkills.size())
            throw new ApplicationException("Skill level sum is too low");

        this.winner = applicant;
    }
}
