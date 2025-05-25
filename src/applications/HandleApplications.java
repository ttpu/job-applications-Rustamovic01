package applications;

import java.util.*;
import java.util.stream.Collectors;

public class HandleApplications {

    private Map<String, Skill> skills = new HashMap<>();
    private Map<String, Position> positions = new HashMap<>();
    private Map<String, Applicant> applicants = new HashMap<>();

    // R1: Skills
    public void addSkills(String... skillNames) throws ApplicationException {
        for (String name : skillNames) {
            if (skills.containsKey(name)) {
                throw new ApplicationException("Duplicate skill: " + name);
            }
            skills.put(name, new Skill(name));
        }
    }

    public void addPosition(String positionName, String... requiredSkillNames) throws ApplicationException {
        if (positions.containsKey(positionName)) {
            throw new ApplicationException("Position already exists: " + positionName);
        }
        Set<Skill> required = new HashSet<>();
        for (String skillName : requiredSkillNames) {
            Skill skill = skills.get(skillName);
            if (skill == null) {
                throw new ApplicationException("Skill not found: " + skillName);
            }
            required.add(skill);
        }
        Position position = new Position(positionName, required);
        positions.put(positionName, position);
        for (Skill skill : required) {
            skill.addPosition(position);
        }
    }

    public Skill getSkill(String name) {
        return skills.get(name);
    }

    public Position getPosition(String name) {
        return positions.get(name);
    }

    // R2: Applicants
    public void addApplicant(String name, String capabilitiesStr) throws ApplicationException {
        if (applicants.containsKey(name)) {
            throw new ApplicationException("Applicant already exists: " + name);
        }

        Map<String, Integer> capabilities = new HashMap<>();
        if (!capabilitiesStr.isEmpty()) {
            String[] parts = capabilitiesStr.split(",");
            for (String part : parts) {
                String[] pair = part.split(":");
                if (pair.length != 2) throw new ApplicationException("Invalid capability format");

                String skillName = pair[0];
                int level = Integer.parseInt(pair[1]);

                if (!skills.containsKey(skillName)) {
                    throw new ApplicationException("Skill not found: " + skillName);
                }

                if (level < 1 || level > 10) {
                    throw new ApplicationException("Invalid level for skill: " + skillName);
                }

                capabilities.put(skillName, level);
            }
        }

        applicants.put(name, new Applicant(name, capabilities));
    }

    public String getCapabilities(String applicantName) throws ApplicationException {
        Applicant applicant = applicants.get(applicantName);
        if (applicant == null) {
            throw new ApplicationException("Applicant not found: " + applicantName);
        }

        return applicant.getFormattedCapabilities();
    }

    // R3: Applications
    public void enterApplication(String applicantName, String positionName) throws ApplicationException {
        Applicant applicant = applicants.get(applicantName);
        Position position = positions.get(positionName);

        if (applicant == null) throw new ApplicationException("Applicant not found");
        if (position == null) throw new ApplicationException("Position not found");
        if (applicant.hasApplied()) throw new ApplicationException("Applicant already applied");

        for (Skill skill : position.getRequiredSkills()) {
            if (!applicant.hasCapability(skill.getName())) {
                throw new ApplicationException("Applicant missing skill: " + skill.getName());
            }
        }

        position.addApplicant(applicant);
        applicant.setApplied(true);
    }

    // R4: Winners
    public int setWinner(String positionName, String applicantName) throws ApplicationException {
        Position position = positions.get(positionName);
        Applicant applicant = applicants.get(applicantName);

        if (position == null || applicant == null)
            throw new ApplicationException("Invalid position or applicant");

        if (!position.getApplicants().contains(applicant))
            throw new ApplicationException("Applicant did not apply");

        if (position.getWinner() != null)
            throw new ApplicationException("Position already has a winner");

        int total = 0;
        for (Skill skill : position.getRequiredSkills()) {
            total += applicant.getLevel(skill.getName());
        }

        if (total <= position.getRequiredSkills().size() * 6)
            throw new ApplicationException("Not enough total skill level");

        position.setWinner(applicant);
        return total;
    }

    // R5: Statistics
    public List<String> skill_nApplicants() {
        return skills.values().stream()
                .sorted(Comparator.comparing(Skill::getName))
                .map(skill -> String.format("%s:%d", skill.getName(), (int) applicants.values().stream()
                        .filter(a -> a.hasCapability(skill.getName()))
                        .count()))
                .collect(Collectors.toList());
    }

    public String maxPosition() {
        return positions.values().stream()
                .max(Comparator.comparingInt(p -> p.getApplicants().size()))
                .map(Position::getName)
                .orElse(null);
    }
}
