package data.generation

import data.model.Project

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
        val supervisors = GenerateSupervisors.generateSupervisors()

        for (i in 0..20) {
            val project = Project(
                id = i,
                title = titles[i],
                places = 15,
                difficulty = (1..3).random(),
                supervisor_name = supervisors[(supervisors.indices).random()].fio
            )
        }

        return projects
    }
}