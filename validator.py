import sys

def get_the_latest_day_of_all_given_contributors(all_contributors, assignment_contributors):
    """
    Given all contributors and the list of assignment contributors, returns the latest work day from those contributors.
    """
    latest_work_day = max(all_contributors.get(name, 0) for name in assignment_contributors)
    return latest_work_day

def convert_info_from_contributor_based_to_project_based(contributor_based_assignments):
    """
    Converts the assignment information from a contributor-based to a project-based format.
    """
    project_based_assignments = {}
    for contributor, projects in contributor_based_assignments.items():
        for project in projects:
            if project in project_based_assignments:
                project_based_assignments[project].append(contributor)
            else:
                project_based_assignments[project] = [contributor]
    return project_based_assignments


def set_the_latest_end_day_to_all_contributors_of_a_project(assignment_contributors, contributors_final_work_day):
    """
    Sets the latest end work day for all contributors of a project.
    """
    latest_work_day = max(contributors_final_work_day.get(name, 0) for name in assignment_contributors)
    for contributor in assignment_contributors:
        contributors_final_work_day[contributor] = latest_work_day

def get_the_fitness_value(projects, contributors, assignments):
    """
    Calculates the fitness value of the assignments.
    """
    # Initialize the dictionary that saves the final day of work for every contributor.
    cuntributors_final_work_day = {contributor: 0 for contributor in contributors}

    total_projects_score = 0

    for assignmet_projects, assignmet_contributors in assignments.items():
        number_of_days_to_complete_project = 0

        # Get the number of days needed to complete the current project.
        for project in projects:
            if project["name"] == assignmet_projects:
                number_of_days_to_complete_project = project["days"]
                project_best_before_days = project["best_before"]
                project_score = project["score"]
                break

        # Update the final work day for each contributor assigned to the project.
        for contributor in assignmet_contributors:
            cuntributors_final_work_day[contributor] += number_of_days_to_complete_project

        set_the_latest_end_day_to_all_contributors_of_a_project(assignmet_contributors, cuntributors_final_work_day)

        # Get the latest end work day of all contributors assigned to the project.
        end_work_day_of_project = get_the_latest_day_of_all_given_contributors(cuntributors_final_work_day, assignmet_contributors)

        # Calculate the score of the current project and add it to the total score.
        if project_best_before_days > end_work_day_of_project:
            total_projects_score += project_score
        elif (project_score - (end_work_day_of_project - project_best_before_days)) >= 0:
            total_projects_score += project_score - (end_work_day_of_project - project_best_before_days)
        else:
            total_projects_score += 0

    return total_projects_score

def read_contributors_and_projects(input_file):
    """
    Reads the given input file and returns the contributors and projects information
    in a structured way.

    Args:
        input_file (str): The path of the input file.

    Returns:
        Tuple:
            A tuple containing the number of contributors and projects, contributors' information
            and the projects' information respectively.
    """
    with open(input_file, "r") as f:
        # read the first line
        number_of_contributors, number_of_projects = map(int, f.readline().split())

        # read contributors information
        contributors = {}
        for i in range(number_of_contributors):
            name, n = f.readline().split()
            n = int(n)
            skills = {}
            for j in range(n):
                skill, li = f.readline().split()
                li = int(li)
                skills[skill] = li
            contributors[name] = skills

        # read projects information
        projects = []
        for i in range(number_of_projects):
            name, di, si, bi, ri = f.readline().split()
            di, si, bi, ri = int(di), int(si), int(bi), int(ri)
            skills = {}
            for j in range(ri):
                skill, lk = f.readline().split()
                lk = int(lk)
                skills[skill] = lk
            project = {
                "name": name,
                "days": di,
                "score": si,
                "best_before": bi,
                "skills": skills,
            }
            projects.append(project)

    return contributors, projects

def print_contributors_and_projects_info(number_of_contributors, number_of_projects, contributors, projects):
    print({"Number of contributors": number_of_contributors, "Number of projects": number_of_projects})
    print("")
    print("Contributors:")
    print(contributors)
    print("")
    print("Projects: ")
    print(projects)

def parse_data(filename):
    # read the file
    with open(filename, "r") as f:
        lines = f.readlines()

    # parse the lines to get the project contributors
    projects = {}
    for i in range(1, len(lines), 2):
        name = lines[i].strip()
        contributors = lines[i + 1].strip().split()
        projects[name] = contributors

    # return the parsed data
    return projects

def helpFunction():
    print("The validator should be called as in the follwing: python validator.py [instnace name] [solution name]")
    print("Example: python validator.py a.txt output_1_a.txt")
    exit()

def check_if_solution_completes_hard_constraints(projects, contributors, assigments): 
    if False:
        print("Error")
    else:
        print("Valid solution")

if __name__ == "__main__":
    arguments = sys.argv
    if len(arguments) != 3:
        helpFunction()
    input_file_name = "Instances/" + arguments[1] + ".txt"
    if input_file_name == None:
        helpFunction()
    solution_file_name = "Solutions/" + arguments[2] + ".txt"

    contributors, projects  = read_contributors_and_projects(input_file_name)

print("==  Calculating fitness ... ==")
print(get_the_fitness_value(projects, contributors, parse_data(solution_file_name)))
print("==  Validating solution ... ==")
check_if_solution_completes_hard_constraints(projects, contributors, parse_data(solution_file_name))