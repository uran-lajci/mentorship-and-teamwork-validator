import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ValidatorScript {

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("The validator should be called as follows: python validator.py [instance name] [solution name]");
            System.out.println("Example: python validator.py a.txt output_1_a.txt");
            System.exit(0);
        }

        List<String> fileNames = readFileName(args[0], args[1]);
        String absoluteInputFilePath = fileNames.get(0);
        String absoluteOutputFilePath = fileNames.get(1);

        List<String> fileContents = readFileContent(absoluteInputFilePath);
        List<Contributor> contributors = readContributors(fileContents);
        List<Project> projects = readProjects(fileContents);
        if (checkIfContentIsMissingInOutput(absoluteOutputFilePath)) {
            List<RawAssignments> rawAssignments = readRawAssignments(absoluteOutputFilePath);

            if(areAssignmentsValid(rawAssignments, contributors, projects, absoluteOutputFilePath)) {
                System.out.println("Fitness score: " + getFitnessScore(rawAssignments, contributors, projects));
                System.out.println("The solution is valid!");
            }
            else {
                System.out.println("Wrong solution!");
            }
        } else {
            System.out.println("Error. Wrong or missing content in output file.");
        }

    }

    private static List<String> readFileName(String inputFile, String output) throws Exception {
        String absoluteInputFilePath = "Instances\\";
        String absoluteOutputFilePath = "Solutions\\";

        if(Objects.equals(inputFile, "a")) {
            absoluteInputFilePath += inputFile + ".txt";
            absoluteOutputFilePath += output + ".txt";
        } else if(Objects.equals(inputFile, "b")) {
            absoluteInputFilePath += inputFile + ".txt";
            absoluteOutputFilePath += output + ".txt";
        } else if(Objects.equals(inputFile, "c")) {
            absoluteInputFilePath += inputFile + ".txt";
            absoluteOutputFilePath += output + ".txt";
        } else if(Objects.equals(inputFile, "d")) {
            absoluteInputFilePath += inputFile + ".txt";
            absoluteOutputFilePath += output + ".txt";
        } else if(Objects.equals(inputFile, "e")) {
            absoluteInputFilePath += inputFile + ".txt";
            absoluteOutputFilePath += output + ".txt";
        } else if(Objects.equals(inputFile, "f")) {
            absoluteInputFilePath += inputFile + ".txt";
            absoluteOutputFilePath += output + ".txt";
        } else if(Objects.equals(inputFile, "class")) {
            absoluteInputFilePath += inputFile + ".txt";
            absoluteOutputFilePath += output + ".txt";
        } else {
            throw new Exception("Wrong input/output for file name");
        }

        return List.of(absoluteInputFilePath, absoluteOutputFilePath);
    }

    private static List<String> readFileContent(String fileName) {
        try {
            return Files.readAllLines(Path.of(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    private static List<Contributor> readContributors(List<String> fileContents) {
        int numberOfContributors = Integer.parseInt(fileContents.get(0).split(" ")[0]);
        List<Contributor> contributors = new ArrayList<>();
        int i = 1;
        while (i <= numberOfContributors) {
            Contributor contributor = new Contributor();
            contributor.setId(UUID.randomUUID());
            String [] nameAndNrOfSkills = fileContents.get(i).split(" ");
            contributor.setName(nameAndNrOfSkills[0]);
            int numberOfSkills = Integer.parseInt(nameAndNrOfSkills[1]);

            List<Skill> skills = new ArrayList<>();
            for(int j = 1; j <= numberOfSkills; j++) {
                Skill skill = new Skill();
                String [] skillAndLevel = fileContents.get(i + j).split(" ");
                skill.setId(UUID.randomUUID());
                skill.setName(skillAndLevel[0]);
                skill.setLevel(Integer.parseInt(skillAndLevel[1]));
                skills.add(skill);
            }

            contributor.setSkills(skills);
            contributors.add(contributor);

            numberOfContributors += numberOfSkills;
            i += numberOfSkills + 1;
        }
        return contributors;
    }

    private static List<Project> readProjects(List<String> fileContents) {
        int numberOfProjects = Integer.parseInt(fileContents.get(0).split(" ")[1]);

        int lineStartOfProjectsInContent = 0;
        for(int i = 0; i < fileContents.size(); i++) {
            String[] content = fileContents.get(i).split(" ");
            if(content.length == 5) {
                lineStartOfProjectsInContent = i;
                break;
            }
        }
        List<String> fileContentsForProjects = fileContents.subList(lineStartOfProjectsInContent, fileContents.size());
        List<Project> projectList = new ArrayList<>();
        int i = 0;
        while (i < numberOfProjects) {
            Project project = new Project();
            project.setId(UUID.randomUUID());
            String [] projectInfo = fileContentsForProjects.get(i).split(" ");
            project.setName(projectInfo[0]);
            project.setDaysToComplete(Integer.parseInt(projectInfo[1]));
            project.setScore(Integer.parseInt(projectInfo[2]));
            project.setBestBefore(Integer.parseInt(projectInfo[3]));
            int numberOfSkills = Integer.parseInt(projectInfo[4]);

            List<Skill> skills = new ArrayList<>();
            for(int j = 1; j <= numberOfSkills; j++) {
                Skill skill = new Skill();
                skill.setId(UUID.randomUUID());
                String [] skillAndLevel = fileContentsForProjects.get(i + j).split(" ");
                skill.setName(skillAndLevel[0]);
                skill.setLevel(Integer.parseInt(skillAndLevel[1]));
                skills.add(skill);
            }
            project.setSkills(skills);
            projectList.add(project);
            numberOfProjects += numberOfSkills;
            i += numberOfSkills + 1;
        }
        return projectList;
    }

    private static boolean checkIfContentIsMissingInOutput(String filename) {
        List<String> fileContents = readFileContent(filename);
        for(int i = 1, j = 2; i < fileContents.size(); i = i + 2, j = j + 2) {
            if(fileContents.size() <= j) {
                return false;
            }
        }
        return true;
    }

    private static List<RawAssignments> readRawAssignments(String filename) {
        List<RawAssignments> rawAssignments = new ArrayList<>();
        List<String> fileContents = readFileContent(filename);
        for(int i = 1, j = 2; i < fileContents.size(); i = i + 2, j = j + 2) {
            RawAssignments assignment = new RawAssignments();
            assignment.setProjectName(fileContents.get(i));
            assignment.setId(UUID.randomUUID());
            String [] contributorNames = fileContents.get(j).split(" ");
            List<String> assignmentContributors = new ArrayList<>(Arrays.asList(contributorNames));
            assignment.setContributorNames(assignmentContributors);
            rawAssignments.add(assignment);
        }
        return rawAssignments;
    }

    private static int getFitnessScore(List<RawAssignments> assignments, List<Contributor> contributors, List<Project> projects) {
        Map<String, Integer> contributorsFinalWorkDay = contributors.stream().collect(Collectors.toMap(Contributor::getName, contributor -> 0));
        Map<String, Project> projectMap = projects.stream().collect(Collectors.toMap(Project::getName, project -> project));
        int totalScore = 0;

        for(int i = 0; i < assignments.size(); i++) {
            String projectName = assignments.get(i).getProjectName();
            if(!projectMap.containsKey(projectName)) {
                System.out.println("Error. The project " + projectName + " does not exist in input!");
                System.exit(0);
            }

            Project project = projectMap.get(projectName);
            int nrOfDaysToCompleteProject = project.getDaysToComplete();
            int bestBeforeDaysOfProject = project.getBestBefore();
            int projectScore = project.getScore();

            List<String> contributorNames = assignments.get(i).getContributorNames();

            for(int j = 0; j < contributorNames.size(); j++) {
                String contributorName = contributorNames.get(j);
                int newFinalWorkDay = contributorsFinalWorkDay.get(contributorName) + nrOfDaysToCompleteProject;
                contributorsFinalWorkDay.put(contributorName, newFinalWorkDay);
            }

            int endWorkDayOfProject = updateContributorLastDayToMaxFinalDayAndGetTheMaxFinalDay(contributorNames, contributorsFinalWorkDay);

            if(bestBeforeDaysOfProject > endWorkDayOfProject) {
                totalScore += projectScore;
            }
            else if ((projectScore - (endWorkDayOfProject - bestBeforeDaysOfProject)) >= 0) {
                totalScore += projectScore - (endWorkDayOfProject - bestBeforeDaysOfProject);
            }
            else {
                totalScore += 0;
            }

        }

        return totalScore;
    }

    //    setTheSameFinalDayForAllContributorsInProject
    //    getTheLatestDayOfAllAssignedContributors
    private static int updateContributorLastDayToMaxFinalDayAndGetTheMaxFinalDay(List<String> contributorNames, Map<String, Integer> contributorsFinalWorkDay) {
        int latestFinalDate = 0;
        for(int i = 0; i < contributorNames.size(); i++) {
            if(latestFinalDate < contributorsFinalWorkDay.get(contributorNames.get(i))) {
                latestFinalDate = contributorsFinalWorkDay.get(contributorNames.get(i));
            }
        }
        for(int j = 0; j < contributorNames.size(); j++) {
            contributorsFinalWorkDay.put(contributorNames.get(j), latestFinalDate);
        }
        return latestFinalDate;
    }

    private static boolean areAssignmentsValid(List<RawAssignments> rawAssignments, List<Contributor> contributors, List<Project> projects, String outputFile) {
        List<String> assignmentProjectNames = rawAssignments.stream().map(RawAssignments::getProjectName).collect(Collectors.toList());
        List<String> projectNames = projects.stream().map(Project::getName).collect(Collectors.toList());
        if(!checkIfAssignedProjectsExist(assignmentProjectNames, projectNames)) {
            return false;
        }

        if (!checkIsAssignedProjectHasContributors(rawAssignments)) {
            return false;
        }

        List<String> assignmentContributorNames = rawAssignments.stream().map(RawAssignments::getContributorNames)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        List<String> contributorNames = contributors.stream().map(Contributor::getName).collect(Collectors.toList());
        if(!checkIfAssignedContributorsExist(assignmentContributorNames, contributorNames)) {
            return false;
        }

        if(!checkIfContributorsWorkInOneProjectPerTime(rawAssignments)) {
            return false;
        }

        Map<String, Integer> projectNameAndNrOfSkills = projects.stream().collect(Collectors.toMap(Project::getName, project -> project.getSkills().size()));
        if(!checkIfProjectsHaveTheCorrectNumberOfContributors(rawAssignments, projectNameAndNrOfSkills)) {
            return false;
        }

        if(!checkIfTheNumberOfAssignedProjectsIsValid(rawAssignments.size(), outputFile)) {
            return false;
        }

        if(!areAssignedContributorsToProjectsValid(rawAssignments, contributors, projects)) {
            return false;
        }

        if(!checkIfAssignedContributorsHaveAtLeastOneRequiredProjectSkill(rawAssignments, contributors, projects)) {
            return false;
        }

        return true;
    }

    private static boolean checkIfAssignedProjectsExist(List<String> assignmentProjectNames, List<String> projectNames) {
        for (String assignmentProjectName : assignmentProjectNames) {
            if (!projectNames.contains(assignmentProjectName)) {
                System.out.println("Error. Assigned project " + assignmentProjectName + " does not exist!");
                return false;
            }
        }
        return true;
    }

    private static boolean checkIfAssignedContributorsExist(List<String> assignmentContributorNames, List<String> contributorNames) {
        for (String assignmentContributorName : assignmentContributorNames) {
            if (!contributorNames.contains(assignmentContributorName)) {
                System.out.println("Error. Assigned contributor " + assignmentContributorName + " does not exist!");
                return false;
            }
        }
        return true;
    }

    private static boolean checkIsAssignedProjectHasContributors(List<RawAssignments> rawAssignments) {
        for(RawAssignments assignment : rawAssignments) {
            if (assignment.getContributorNames().size() == 0 || assignment.getContributorNames().contains("")) {
                System.out.println("Error. Assigned project " + assignment.getProjectName() + " does not have contributors!");
                return false;
            }
        }
        return true;
    }

    private static boolean checkIfContributorsWorkInOneProjectPerTime(List<RawAssignments> rawAssignments) {
        for(int i = 0; i < rawAssignments.size(); i++) {
            if(rawAssignments.get(i).getContributorNames().size() != new HashSet<>(rawAssignments.get(i).getContributorNames()).size()) {
                System.out.println("Error. One of contributors in project " + rawAssignments.get(i).getProjectName() + " is working in two positions!");
                return false;
            }
        }
        return true;
    }

    private static boolean checkIfProjectsHaveTheCorrectNumberOfContributors(List<RawAssignments> rawAssignments, Map<String, Integer> projectNameAndNrOfSkills) {
        for(int i = 0; i < rawAssignments.size(); i++) {
            if(rawAssignments.get(i).getContributorNames().size() != projectNameAndNrOfSkills.get(rawAssignments.get(i).getProjectName())) {
                System.out.println("Error. Project " + rawAssignments.get(i).getProjectName() + " has wrong number of contributors!");
                return false;
            }
        }
        return true;
    }

    private static boolean checkIfTheNumberOfAssignedProjectsIsValid(int numberOfAssignedProjects, String outputFile) {
        if(numberOfAssignedProjects != Integer.parseInt(readFileContent(outputFile).get(0))) {
            System.out.println("Error. The number of projects (" + Integer.parseInt(readFileContent(outputFile).get(0)) + ") written on the output file"
                    + " is not the same as the number of assigned projects (" + numberOfAssignedProjects + ")!");
            return false;
        }
        return true;
    }

    private static boolean checkIfAssignedContributorsHaveAtLeastOneRequiredProjectSkill(List<RawAssignments> rawAssignments, List<Contributor> contributors, List<Project> projects) {
        Map<String, Project> projectMap = projects.stream().collect(Collectors.toMap(Project::getName, project -> project));
        Map<String, Contributor> contributorMap = contributors.stream().collect(Collectors.toMap(Contributor::getName, contributor -> contributor));

        for(int i = 0; i < rawAssignments.size(); i++) {
            String projectName = rawAssignments.get(i).getProjectName();
            List<Skill> projectSkills = projectMap.get(projectName).getSkills();
            List<String> projectSkillInfos = projectSkills.stream().map(Skill::getName).collect(Collectors.toList());

            List<String> projectContributors = rawAssignments.get(i).getContributorNames();
            for (int j = 0; j < projectContributors.size(); j++) {
                List<Skill> contributorSkills = contributorMap.get(projectContributors.get(j)).getSkills();
                List<String> contributorSkillInfos = contributorSkills.stream().map(Skill::getName).collect(Collectors.toList());

                boolean hasRequiredSkill = false;
                for (int k = 0; k < projectSkillInfos.size(); k++) {
                    String projectSkill = projectSkillInfos.get(k);

                    if (projectMap.get(projectName).getSkills().get(k).getLevel() == 1) {
                        hasRequiredSkill = true;
                        break;
                    }
                    if (contributorSkillInfos.contains(projectSkill)) {
                        hasRequiredSkill = true;
                        break;
                    }

                }

                if (!hasRequiredSkill) {
                    System.out.println("Error. At least one contributor in project " + rawAssignments.get(i).getProjectName()
                            + " has no skill that is required in the project!");
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean areAssignedContributorsToProjectsValid(List<RawAssignments> rawAssignments, List<Contributor> contributors, List<Project> projects) {
        Map<String, Contributor> contributorMap = contributors.stream().collect(Collectors.toMap(Contributor::getName, contributor -> contributor));
        Map<String, Project> projectMap = projects.stream().collect(Collectors.toMap(Project::getName, project -> project));

        List<CompleteAssignment> completeAssignments = new ArrayList<>();

        for(int i = 0; i < rawAssignments.size(); i++) {
            Project project = projectMap.get(rawAssignments.get(i).getProjectName());
            List<Contributor> assignedContributors = new ArrayList<>();
            for(int j = 0; j < rawAssignments.get(i).getContributorNames().size(); j++) {
                assignedContributors.add(contributorMap.get(rawAssignments.get(i).getContributorNames().get(j)));
            }
            completeAssignments.add(new CompleteAssignment(UUID.randomUUID(), project, assignedContributors));
        }

        for(int i = 0; i < completeAssignments.size(); i++) {
            Project project = completeAssignments.get(i).getProject();
            List<Skill> projectSkills = project.getSkills();
            List<Contributor> contributorList = completeAssignments.get(i).getContributors();

            List<String> checkedContributorIds = new ArrayList<>();
            checkedContributorIds.add("");
            List<String> checkedSkillIds = new ArrayList<>();
            checkedSkillIds.add("");
            List<ContributorAndAssignedSkill> contributorsToIncreaseScore = new ArrayList<>();

            int contributorFulfillsAtLeastOneRequiredSkill = 0;

            for(int j = 0; j < contributorList.size(); j++) {
                Contributor contributor = contributorList.get(j);
                String contributorId = contributor.getId() + "";
                List<Skill> contributorSkills = contributor.getSkills();
                List<String> contributorSkillNames = contributorSkills.stream().map(Skill::getName).collect(Collectors.toList());

                for(int k = 0; k < projectSkills.size(); k++) {
                    Skill projectSkill = projectSkills.get(k);

                    for (int t = 0; t < contributorSkills.size(); t++) {
                        Skill contributorSkill = contributorSkills.get(t);

                        boolean hasSkillWithoutMentor = Objects.equals(projectSkill.getName(), contributorSkill.getName()) && projectSkill.getLevel() <= contributorSkill.getLevel();
                        boolean hasSkillWithMentor = Objects.equals(projectSkill.getName(), contributorSkill.getName()) && projectSkill.getLevel() == contributorSkill.getLevel() + 1
                                && contributorHasMentor(projectSkill.getName(), projectSkill.getLevel(), contributorList, contributorId);

                        boolean hasNoSkillButCanCreateItWithMentor = false;
                        if(!contributorSkillNames.contains(projectSkill.getName())) {
                            if(!Objects.equals(projectSkill.getName(), contributorSkill.getName()) && projectSkill.getLevel() == 1) {
                                List<Contributor> contributorListWithoutCurrentContributor = contributorList.stream()
                                        .filter(contributor1 -> !(contributor1.getId() + "").equals(contributor.getId() + ""))
                                        .collect(Collectors.toList());

                                if(contributorHasMentor(projectSkill.getName(), projectSkill.getLevel(), contributorListWithoutCurrentContributor, contributorId)) {
                                    hasNoSkillButCanCreateItWithMentor = true;
                                }
                            }
                        }

                        if(!checkedContributorIds.contains(contributorId) && !checkedSkillIds.contains(projectSkill.getId() + "") && (hasSkillWithoutMentor || hasSkillWithMentor || hasNoSkillButCanCreateItWithMentor)) {
                            checkedContributorIds.add(contributorId);
                            checkedSkillIds.add(projectSkill.getId() + "");
                            if(hasNoSkillButCanCreateItWithMentor) {
                                if(!contributorSkillNames.contains(projectSkill.getName())) {
                                    contributor.getSkills().add(new Skill(UUID.randomUUID(), projectSkill.getName(), 1));
                                    contributorFulfillsAtLeastOneRequiredSkill++;
                                }
                            }
                            else {
                                contributorFulfillsAtLeastOneRequiredSkill++;
                                if(projectSkill.getLevel() == contributorSkill.getLevel() || projectSkill.getLevel() == contributorSkill.getLevel() + 1) {
                                    contributorsToIncreaseScore.add(new ContributorAndAssignedSkill(contributor, contributorSkill));
                                }
                            }
                        }
                    }
                }
            }
            if(contributorFulfillsAtLeastOneRequiredSkill == 0) {
                System.out.println("Error. Contributor does not have any skill for the project " + project.getName());
                return false;
            } else {
                increaseContributorsScore(contributorsToIncreaseScore);
            }
        }
        return true;
    }

    private static boolean contributorHasMentor(String skill, int level, List<Contributor> possibleMentors, String currentContributorId) {
        for(int i = 0; i < possibleMentors.size(); i++) {
            Contributor mentor = possibleMentors.get(i);
            List<Skill> mentorSkills = mentor.getSkills();
            if (!(mentor.getId() + "").equals(currentContributorId)) {
                for (int j = 0; j < mentorSkills.size(); j++) {
                    if(Objects.equals(mentorSkills.get(j).getName(), skill) && mentorSkills.get(j).getLevel() >= level) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static void increaseContributorsScore(List<ContributorAndAssignedSkill> contributorAndSkills) {
        for (int i = 0; i < contributorAndSkills.size(); i++) {
            List<Skill> contributorSkills = contributorAndSkills.get(i).getContributor().getSkills();
            String assignedSkillId = contributorAndSkills.get(i).getAssignedSkill().getId() + "";
            for(int j = 0; j < contributorSkills.size(); j++) {
                String skillId = contributorSkills.get(j).getId() + "";
                if (assignedSkillId.equals(skillId)) {
                    contributorSkills.get(j).setLevel(contributorSkills.get(j).getLevel() + 1);
                }
            }
        }
    }
}
class Contributor {
    private UUID id;
    private String name;
    private List<Skill> skills;

    public Contributor() {
    }

    public Contributor(UUID id, String name, List<Skill> skills) {
        this.id = id;
        this.name = name;
        this.skills = skills;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "Contributor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", skills=" + skills +
                '}';
    }
}

class Project {
    private UUID id;
    private String name;
    private int daysToComplete;
    private int score;
    private int bestBefore;
    private List<Skill> skills;

    public Project() {
    }

    public Project(UUID id, String name, int daysToComplete, int score, int bestBefore, List<Skill> skills) {
        this.id = id;
        this.name = name;
        this.daysToComplete = daysToComplete;
        this.score = score;
        this.bestBefore = bestBefore;
        this.skills = skills;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDaysToComplete() {
        return daysToComplete;
    }

    public void setDaysToComplete(int daysToComplete) {
        this.daysToComplete = daysToComplete;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getBestBefore() {
        return bestBefore;
    }

    public void setBestBefore(int bestBefore) {
        this.bestBefore = bestBefore;
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", daysToComplete=" + daysToComplete +
                ", score=" + score +
                ", bestBefore=" + bestBefore +
                ", skills=" + skills +
                '}';
    }
}

class RawAssignments {
    private UUID id;
    private String projectName;
    private List<String> contributorNames;

    public RawAssignments() {
    }

    public RawAssignments(UUID id, String projectName, List<String> contributorNames) {
        this.id = id;
        this.projectName = projectName;
        this.contributorNames = contributorNames;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public List<String> getContributorNames() {
        return contributorNames;
    }

    public void setContributorNames(List<String> contributorNames) {
        this.contributorNames = contributorNames;
    }

    @Override
    public String toString() {
        return "RawAssignments{" +
                "id=" + id +
                ", projectName='" + projectName + '\'' +
                ", contributorNames=" + contributorNames +
                '}';
    }
}

class CompleteAssignment {
    private UUID id;
    private Project project;
    private List<Contributor> contributors;

    public CompleteAssignment() {
    }

    public CompleteAssignment(UUID id, Project project, List<Contributor> contributors) {
        this.id = id;
        this.project = project;
        this.contributors = contributors;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<Contributor> getContributors() {
        return contributors;
    }

    public void setContributors(List<Contributor> contributors) {
        this.contributors = contributors;
    }

    @Override
    public String toString() {
        return "CompleteAssignment{" +
                "id=" + id +
                ", project=" + project +
                ", contributors=" + contributors +
                '}';
    }
}

class Skill {
    private UUID id;
    private String name;
    private int level;

    public Skill() {
    }

    public Skill(UUID id, String name, int level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", level=" + level +
                '}';
    }
}

class ContributorAndAssignedSkill {
    private Contributor contributor;
    private Skill assignedSkill;

    public ContributorAndAssignedSkill() {
    }

    public ContributorAndAssignedSkill(Contributor contributor, Skill assignedSkill) {
        this.contributor = contributor;
        this.assignedSkill = assignedSkill;
    }

    public Contributor getContributor() {
        return contributor;
    }

    public void setContributor(Contributor contributor) {
        this.contributor = contributor;
    }

    public Skill getAssignedSkill() {
        return assignedSkill;
    }

    public void setAssignedSkill(Skill assignedSkill) {
        this.assignedSkill = assignedSkill;
    }

    @Override
    public String toString() {
        return "ContributorAndAssignedSkill{" +
                "contributor=" + contributor +
                ", assignedSkill=" + assignedSkill +
                '}';
    }
}
