package data.generation

import PROJECTS_COUNT
import PROJECT_STUDENT_CAPACITY
import PROJECT_MEAN_SKILL_COUNT
import data.model.Project
import data.model.ProjectSkills
import data.model.Skill
import data.model.Supervisor

object GenerateProjects {

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
        val supervisors = GenerateSupervisors.generateSupervisors().toMutableList()
        val map = mutableMapOf<Int, Int>()
        for (supervisor in supervisors) {
            map[supervisor.id] = 0
            println("${supervisor.id} = ${map[supervisor.id]}")
        }
        println("-------------------------")

        for (i in 0 until PROJECTS_COUNT) {
            var supervisor = supervisors[(supervisors.indices).random()]
            while (map[supervisor.id] == 2) {
                supervisor = supervisors[(supervisors.indices).random()]
            }
            println("${supervisor.id} = ${map[supervisor.id]}")
            val index = map[supervisor.id]!!
            map[supervisor.id] = index + 1
            println("${supervisor.id} = ${map[supervisor.id]}")
            if (map[supervisor.id] == 2) {
                map.remove(supervisor.id)
                supervisors.remove(supervisor)
            }

            val project = Project(
                id = i,
                title = titles[i],
                places = PROJECT_STUDENT_CAPACITY,
                supervisor_name = supervisor.fio
            )
            projects.add(project)
        }

        return projects
    }

    fun generateProjectSkills(projects: List<Project>): List<ProjectSkills> {
        val list = mutableListOf<ProjectSkills>()

        for (project in projects) {
            val skills = mutableSetOf<Skill>()
            for (i in 0..PROJECT_MEAN_SKILL_COUNT.random()) {
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