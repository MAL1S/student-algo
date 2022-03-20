package data.generation

import data.model.Project
import data.model.ProjectSkills
import data.model.Skill

object GenerateProjects {

    const val PROJECT_SIZE = 15

    val titles = listOf<String>(
        "1",
        "2",
        "3",
        "4",
        "5",
        "6",
        "7",
        "8",
        "9",
        "10",
        "11",
        "12",
        "13",
        "14",
        "15",
        "16",
        "17",
        "18",
        "19",
        "20"
    )

    fun generateProjects(): List<Project> {
        val projects = mutableListOf<Project>()
        val supervisors = GenerateSupervisors.generateSupervisors()

        for (i in 0..19) {
            val project = Project(
                id = i,
                title = titles[i],
                places = PROJECT_SIZE,
                supervisor_name = supervisors[(supervisors.indices).random()].fio
            )
            projects.add(project)
        }

        return projects
    }

    fun generateProjectSkills(projects: List<Project>): List<ProjectSkills> {
        val list = mutableListOf<ProjectSkills>()

        for (project in projects) {
            val skills = mutableSetOf<Skill>()
            for (i in 0..(3..6).random()) {
                val skill = GenerateSkills.getRandomSkill()
                if (!skills.contains(skill)) {
                    list.add(
                        ProjectSkills(
                            projectId = project.id,
                            skill = skill
                        )
                    )
                }
                skills.add(skill)
            }
        }

        return list
    }
}