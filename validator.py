import sys
import os

def help_function():
    print("The validator should be called as follows: python validator.py [instance name] [solution name]")
    print("Example: python validator.py a.txt output_1_a.txt")


def read_contributors_and_projects(filename):
    try:
        with open(filename, "r") as f:
            if not f.read():
                print(f"Error: The file {filename} is empty.")
                exit()

        with open(filename, "r") as f:
            number_of_contributors, number_of_projects = map(int, f.readline().split())

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

            projects = []
            for i in range(number_of_projects):
                name, di, si, bi, ri = f.readline().split()
                di, si, bi, ri = int(di), int(si), int(bi), int(ri)
                skills = []
                for j in range(ri):
                    skill, lk = f.readline().split()
                    lk = int(lk)
                    skills.append({skill:lk})

                project = {
                    "name": name,
                    "days": di,
                    "score": si,
                    "best_before": bi,
                    "skills": skills,
                }
                projects.append(project)

        return contributors, projects

    except IOError:
        print(f"Error: Could not open file '{filename}'.")
        exit()


def read_assignments(filename):
    try:
        with open(filename, "r") as f:
            if not f.read():
                print(f"Error: The file {filename} is empty.")
                exit()
        
        with open(filename, "r") as f:
            lines = f.readlines()

        projects = {}
        for i in range(1, len(lines), 2):
            name = lines[i].strip()
            contributors = lines[i + 1].strip().split()
            projects[name] = contributors

        return projects

    except IOError:
        print(f"Error: Could not open file '{filename}'.")
        exit()


def set_latest_end_day_to_all_contributors_of_a_project(assignment_contributors, contributors_final_work_day):
    """
    Sets the latest end work day for all contributors of a project.
    """
    latest_work_day = max(contributors_final_work_day.get(name, 0) for name in assignment_contributors)
    for contributor in assignment_contributors:
        contributors_final_work_day[contributor] = latest_work_day


def get_latest_day_of_all_given_contributors(all_contributors, assignment_contributors):
    """
    Given all contributors and the list of assignment contributors, returns the latest work day from those contributors.
    """
    latest_work_day = max(all_contributors.get(name, 0) for name in assignment_contributors)
    return latest_work_day


def convert_project_list_to_dictionary(projects):
    projects_in_dict = {}
    for project in projects:
        name = project['name']
        project_data = project.copy()
        del project_data['name']
        projects_in_dict[name] = project_data
    return projects_in_dict


def get_the_fitness_value(projects, contributors, assignments):
    contributors_final_work_day = {contributor: 0 for contributor in contributors}
    total_projects_score = 0

    projects_in_dict = convert_project_list_to_dictionary(projects)

    for assignment_project, assignment_contributors in assignments.items():
        num_days_to_complete_project = 0

        if not assignment_project in projects_in_dict:
            print(f"Error. The project {assignment_project} is not correct.")
            print("Check if the input and output files correspond with each other.")
            exit()

        project = projects_in_dict[assignment_project]

        num_days_to_complete_project = project["days"]
        project_best_before_days = project["best_before"]
        project_score = project["score"]

        # Update the final work day for each contributor assigned to the project.
        for contributor in assignment_contributors:
            contributors_final_work_day[contributor] += num_days_to_complete_project

        set_latest_end_day_to_all_contributors_of_a_project(assignment_contributors, contributors_final_work_day)

        # Get the latest end work day of all contributors assigned to the project.
        end_work_day_of_project = get_latest_day_of_all_given_contributors(contributors_final_work_day, assignment_contributors)

        # Calculate the score of the current project and add it to the total score.
        if project_best_before_days > end_work_day_of_project:
            total_projects_score += project_score
        elif (project_score - (end_work_day_of_project - project_best_before_days)) >= 0:
            total_projects_score += project_score - (end_work_day_of_project - project_best_before_days)
        else:
            total_projects_score += 0

    return total_projects_score


def check_if_assigned_projects_exist(projects, assignments):
    project_names = [project["name"] for project in projects]
    assigned_projects = list(assignments.keys())
    for assigned_project in assigned_projects:
        if assigned_project not in project_names:
            print(f"The assigned project {assigned_project} does not exist in the input file.")
            return False
    return True


def get_assignment_contributors(assignments):
    assignment_contributors = list(assignments.values())
    flat_list = [item for sublist in assignment_contributors for item in sublist]
    unique_list = list(set(flat_list))
    return unique_list


def check_if_assigned_contributors_exist(contributors, assignments):
    assignment_contributors = get_assignment_contributors(assignments)
    for assignment_contributor in assignment_contributors:
        if assignment_contributor not in contributors:
            print(f"The assigned contributor {assignment_contributor} does not exist in the input file.")
            return False
    return True


def check_if_assigned_projects_have_contributors(assigments):
    for key in assigments:
        if assigments[key] is None or assigments[key] == "" or not assigments[key]:
            print(f"The assigned project {assigments[key]} does not have contributors.")
            return False
    return True


def get_the_number_of_contributors_and_project_skills_for_projects(projects, assignments):
    result = []
    assigned_projects = list(assignments.keys())

    for project in projects:
        if project in assigned_projects:
            project_name = project['name']
            contributors_list = assignments[project_name]
            num_contributors = len(contributors_list)
            num_project_skills = len(project['skills'])
            result.append({project_name: {'NumberOfContributors': num_contributors, 'NumberOfSkills': num_project_skills}})

    return result


def check_if_contributors_work_in_one_project_per_time(projects, assignments):
    list_of_dicts = get_the_number_of_contributors_and_project_skills_for_projects(projects, assignments)
    for dictionary in list_of_dicts:
        values = dictionary.values()
        num_contributors = list(values)[0]['NumberOfContributors']
        num_skills = list(values)[0]['NumberOfSkills']
        if num_contributors != num_skills:
            print(f"There are contributors that work in multiple projects.")
            return False
    return True


def check_if_number_of_contributors_in_project_is_the_same_as_project_required(projects, contributors, assignments):
    for project in projects:
        if project["name"] in assignments:
            number_of_skills = len(project["skills"])
            number_of_project_contributors = len(assignments[project["name"]])
            if number_of_skills != number_of_project_contributors:
                print(f"Error. The number of contributors in project {project['name']} is wrong.")
                return False
    return True


def read_the_number_of_assigned_projects(filename):
    with open(filename, 'r') as f:
        first_line = f.readline().strip()
        if not first_line.isnumeric():
            print(f"The first line of the file {filename} is not a number")
            return False
        else:
            return int(first_line)


def check_if_the_number_of_assigned_projects_is_valid(solution_file_name, assignments):
    number_of_assigned_projects = read_the_number_of_assigned_projects(solution_file_name)
    if not number_of_assigned_projects:
        return False
    elif number_of_assigned_projects != len(assignments):
        print(f"The number of projects written ({number_of_assigned_projects}) is not the same as the number of assigned projects ({len(assignments)}).")
        return False
    else:
        return True


def get_project_details(assignments, contributors, projects):
    project_details = []
    for project in projects:
        project_name = project['name']
        project_skills = project['skills']
        project_contributors = []
        if project_name in assignments:
            for contributor in assignments[project_name]:
                contributor_skills = contributors[contributor]
                project_contributors.append({contributor: contributor_skills})
            project_details.append({'name': project_name, 'skills': project_skills, 'contributors': project_contributors})
    return project_details


def assign_numbers(project_list, assignments_dict):
    project_order = {project_name: i for i, project_name in enumerate(assignments_dict.keys())}
    for project in project_list:
        project['number'] = project_order[project['name']]
    return project_list


def possible_mentors(current_contributor, current_skill, current_level, contributors, contributor_names):
    possible_mentors = []
    for name in contributor_names:
        if name != current_contributor:
            possible_mentors.append({name:contributors[name]})
    
    for p_mentor in possible_mentors:
        p_mentor_skills = list(p_mentor.values())
        for s in p_mentor_skills:
            if current_skill in s and s[current_skill] >= current_level:
                return True
               
    return False


# convertd data to make it easier to validate the project and contributor combination
def transformed_data(assignments, projects):
    trasformed_data = []
    for assignment in assignments:
        assigment_information = {}
        assigment_information["name"] = assignment
        for project in projects:
            if assignment == project["name"]:
                assigment_information["skills"] = project["skills"]
                trasformed_data.append(assigment_information)
        assigment_information["contributors"] = assignments[assignment]

    return trasformed_data


def convert_assigments(information):
    skills = information["skills"]
    contributors = information["contributors"]
    new_info = {"name":information["name"], "contributors":{}}

    i = 0
    for skill in skills:
        new_info["contributors"][contributors[i]] = skill
        i += 1

    return new_info


def check_if_contributors_have_the_correct_skills_for_the_assigned_projects(projects, contributors, assignments):
    for assignments in transformed_data(assignments, projects):
        name = assignments["name"]    
        new_assignments = convert_assigments(assignments)
        new_contributors = new_assignments["contributors"]

        for new_c, info in new_contributors.items():
            for skill, level in contributors[new_c].items():
                for s_skill, l_level in info.items():
                    if s_skill == skill and level >= l_level:
                        if l_level == level or l_level - 1 == level:
                            contributors[new_c][skill] += 1
                            
                    elif s_skill == skill and contributors[new_c][skill] == l_level - 1: 
                        if possible_mentors(new_c, skill, l_level, contributors, assignments['contributors']):
                            contributors[new_c][skill] += 1    
                        else:
                            print(f"Error. Could not find a mentor for this contributor {new_c} in project {name} for the skill {skill} with level {l_level}\n")
                            failure_reason = input("Write y if you want to see the full failure reason: ")

                            if failure_reason == "y":
                                print(f"Contributor {new_c} information {contributors[new_c]}")
                                print(f"Contributers that are in the same project {assignments['contributors']}\n")
                                project_contributors = [] 
                                for name in assignments['contributors']:
                                    if name != new_c:
                                        project_contributors.append({name:contributors[name]})
                                print(f"The skills and levels of those contributors {project_contributors}")
                            return False 
    
        for contributor_name, skill_and_level in new_contributors.items():
            for contributor_skill in contributors[contributor_name]:
                skills = list(skill_and_level.keys())
                for skill in skills:
                    if skill == contributor_skill and skill_and_level[skill] > contributors[contributor_name][contributor_skill]:
                        return False
    return True


def check_if_solution_completes_hard_constraints(solution_file_name, projects, contributors, assignments): 
    if not check_if_assigned_projects_exist(projects, assignments):
        return False
    elif not check_if_assigned_contributors_exist(contributors, assignments):
        return False
    elif not check_if_assigned_projects_have_contributors(assignments):
        return False
    elif not check_if_contributors_work_in_one_project_per_time(projects, assignments):
        return False
    elif not check_if_number_of_contributors_in_project_is_the_same_as_project_required(projects, contributors, assignments):
        return False
    elif not check_if_the_number_of_assigned_projects_is_valid(solution_file_name, assignments):
        return False
    elif not check_if_contributors_have_the_correct_skills_for_the_assigned_projects(projects, contributors, assignments):
        return False
    else:
        return True


if __name__ == "__main__":
    arguments = sys.argv

    if len(arguments) != 3:
        help_function()
        exit()

    input_file_name = f"Instances/{arguments[1]}.txt"

    if not os.path.exists(input_file_name):
        print(f"The file {input_file_name} does not exist.")
        exit()

    solution_file_name = f"Solutions/{arguments[2]}.txt"

    if not os.path.exists(solution_file_name):
        print(f"The file {solution_file_name} does not exist.")
        exit()

    contributors, projects = read_contributors_and_projects(input_file_name)
    assignments = read_assignments(solution_file_name)

    print("== Calculating fitness... ==")
    print(get_the_fitness_value(projects, contributors, assignments))

    print("== Validating solution... ==")
    if check_if_solution_completes_hard_constraints(solution_file_name, projects, contributors, assignments):
        print("The solution is valid.")
    else:
        print("Invalid solution.")