import data.model.Project
import data.model.Speciality
import domain.data.ImportCsvData
import domain.data.ImportExcelData
import domain.distribution.Distribution

fun main() {
    val students = ImportExcelData.getStudentsFromFile("F:/yarmarka_data/stud.xlsx", "E:/exception.xlsx")
    val projects = ImportCsvData.getProjectsFromFile("F:/yarmarka_data/new/projects.csv")
    val participations = ImportCsvData.getParticipationsFromFile("F:/yarmarka_data/new/participations.csv")
    val specialities = Speciality.specialities
//    projects.map { it }.forEach {
//        println(it)
//    }

    for (institute in specialities.keys) {
        //val specs = specialities[institute]!!
        val specs = specialities[institute]!!
        //println("specs = $specs")
        val studs = students.toMutableList()
            .filter {
                specs.contains(it.training_group)
            }
            .toMutableList()
        val projs = projects
            .filter {
                doesProjectContainGroup(it, specs) && it.id != 240
            }
            .toMutableList()
        projs.forEach {
            //println(it)
        }
        val parts = participations
            .filter {
                doesProjectContainGroup(projects.find { proj -> proj.id == it.projectId }!!, specs)
            }
            .toMutableList()
        if (parts.size == 0 || institute == "Байкальский институт БРИКС") continue
        val dis = Distribution(
            students = studs,
            projects = projs,
            participations = parts,
            institute = institute,
            specialities = specs,
            hasSpecialGroups = institute == "Институт информационных технологий и анализа данных"
        )
        //println(dis.participations)
        dis.execute()
    }
}

fun doesProjectContainGroup(project: Project, groups: List<String>): Boolean {
    for (g in project.groups) {
        if (groups.contains(g)) return true
    }
    return false
}