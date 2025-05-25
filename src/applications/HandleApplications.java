package applications;

import java.util.*;

public class HandleApplications {

    private final Map<String, Skill> skills = new HashMap<>();
    private final Map<String, Position> positions = new HashMap<>();
    private final Map<String, Applicant> applicants = new HashMap<>();
    private final Set<String> appliedApplicants = new HashSet<>(); // track applicants who have applied

    // R1: Add skills (throws if duplicate)
    public void addSkills(String... skillNames) throws ApplicationException {
        Set<String> seen = new HashSet<>();
        for (String skillName : skillNames) {
            if (skills.containsKey(skillName)) {
                throw new ApplicationException("Duplicate skill: " + skillName);
            }
            if (!seen.add(skillName)) {
                throw new ApplicationException("Duplicate skill in input: " + skillName);
            }
        }
        for (String skillName : skillNames) {
            skills.put(skillName, new Skill(skillName));
        }
    }

    // R1: Add position with required skills (throws if position exists or skills missing)
    public void addPosition(String positionName, String... requiredSkillNames) throws ApplicationException {
        if (positions.containsKey(positionName)) {
            throw new ApplicationException("Position already exists: " + positionName);
        }

        List<Skill> requiredSkills = new ArrayList<>();
        for (String skillName : requiredSkillNames) {
            Skill skill = skills.get(skillName);
            if (skill == null) {
                throw new ApplicationException("Required skill not found: " + skillName);
            }
            requiredSkills.add(skill);
        }

        Position position = new Position(positionName, requiredSkills);
        positions.put(positionName, position);

        // Link skills to this position
        for (Skill skill : requiredSkills) {
            skill.addPosition(position);
        }
    }

    // R1: Get skill by name or null
    public Skill getSkill(String name) {
        return skills.get(name);
    }

    // R1: Get position by name or null
    public Position getPosition(String name) {
        return positions.get(name);
    }

    // R2: Add applicant with capabilities string e.g. "java:9,sql:7"
    public void addApplicant(String applicantName, String capabilitiesStr) throws ApplicationException {
        if (applicants.containsKey(applicantName)) {
            throw new ApplicationException("Applicant already exists: " + applicantName);
        }

        Map<String, Integer> capabilities = new HashMap<>();
        if (!capabilitiesStr.trim().isEmpty()) {
            String[] pairs = capabilitiesStr.split(",");
            for (String pair : pairs) {
                String[] parts = pair.trim().split(":");
                if (parts.length != 2) {
                    throw new ApplicationException("Invalid capability format for: " + pair);
                }
                String skillName = parts[0].trim();
                Skill skill = skills.get(skillName);
                if (skill == null) {
                    throw new ApplicationException("Capability skill not found: " + skillName);
                }
                int level;
                try {
                    level = Integer.parseInt(parts[1].trim());
                } catch (NumberFormatException e) {
                    throw new ApplicationException("Invalid level for skill " + skillName);
                }
                if (level < 1 || level > 10) {
                    throw new ApplicationException("Level out of range (1-10) for skill " + skillName);
                }
                capabilities.put(skillName, level);
            }
        }

        applicants.put(applicantName, new Applicant(applicantName, capabilities));
    }

    // R2: Get applicant capabilities string sorted alphabetically
    public String getCapabilities(String applicantName) throws ApplicationException {
        Applicant applicant = applicants.get(applicantName);
        if (applicant == null) {
            throw new ApplicationException("Applicant not found: " + applicantName);
        }
        return applicant.getCapabilitiesString();
    }

    // R3: Applicant applies for a position
    public void enterApplication(String applicantName, String positionName) throws ApplicationException {
        Applicant applicant = applicants.get(applicantName);
        if (applicant == null) {
            throw new ApplicationException("Applicant not found: " + applicantName);
        }
        Position position = positions.get(positionName);
        if (position == null) {
            throw new ApplicationException("Position not found: " + positionName);
        }
        if (appliedApplicants.contains(applicantName)) {
            throw new ApplicationException("Applicant already applied for a position: " + applicantName);
        }

        // Check applicant has required capabilities
        Map<String, Integer> capabilities = applicant.getCapabilities();
        for (Skill requiredSkill : position.getRequiredSkills()) {
            if (!capabilities.containsKey(requiredSkill.getName())) {
                throw new ApplicationException("Applicant missing required skill: " + requiredSkill.getName());
            }
        }

        appliedApplicants.add(applicantName);
        position.addApplicant(applicantName);
    }

    // R4: Set winner for a position
    public int setWinner(String positionName, String applicantName) throws ApplicationException {
        Position position = positions.get(positionName);
        if (position == null) {
            throw new ApplicationException("Position not found: " + positionName);
        }
        Applicant applicant = applicants.get(applicantName);
        if (applicant == null) {
            throw new ApplicationException("Applicant not found: " + applicantName);
        }
        if (!position.getApplicants().contains(applicantName)) {
            throw new ApplicationException("Applicant did not apply for position");
        }
        if (position.getWinner() != null) {
            throw new ApplicationException("Position already has a winner");
        }

        Map<String, Integer> capabilities = applicant.getCapabilities();
        int sumLevels = 0;
        for (Skill requiredSkill : position.getRequiredSkills()) {
            Integer level = capabilities.get(requiredSkill.getName());
            if (level == null) {
                throw new ApplicationException("Applicant missing required skill: " + requiredSkill.getName());
            }
            sumLevels += level;
        }

        int minSum = position.getRequiredSkills().size() * 6;
        if (sumLevels <= minSum) {
            throw new ApplicationException("Sum of levels does not exceed six times the number of required skills");
        }

        position.setWinner(applicantName);
        return sumLevels;
    }

    // R5: Return Map skill -> number of applicants who have it, sorted by skill name
    public Map<String, Integer> skill_nApplicants() {
        Map<String, Integer> counts = new TreeMap<>();
        for (String skillName : skills.keySet()) {
            int count = 0;
            for (Applicant a : applicants.values()) {
                if (a.getCapabilities().containsKey(skillName)) count++;
            }
            counts.put(skillName, count);
        }
        return counts;
    }

    // R5: Position with highest number of applicants
    public String maxPosition() {
        String maxPos = null;
        int maxCount = -1;
        for (Position p : positions.values()) {
            int c = p.getApplicants().size();
            if (c > maxCount) {
                maxCount = c;
                maxPos = p.getName();
            }
        }
        return maxPos;
    }
}
