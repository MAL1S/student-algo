import data.model.Project
import data.model.Speciality
import domain.data.ImportCsvData
import domain.data.ImportExcelData
import domain.distribution.Distribution

fun main() {
    val students = ImportExcelData.getStudentsFromFile("F:/yarmarka_data/stud.xlsx", "E:/exception.xlsx")
    val projects = ImportCsvData.getProjectsFromFile("F:/yarmarka_data/projects.csv")
    val participations = ImportCsvData.getParticipationsFromFile("F:/yarmarka_data/participations.csv")
    val specialities = Speciality.specialities
//    participations.forEach {
//        println(it)
//    }

//    for (institute in specialities.keys) {
        val specs = specialities["Институт информационных технологий и анализа данных"]!!
        Distribution(
            students = students.toMutableList()
                .filter {
                    specs.contains(it.training_group)
                }
                .toMutableList(),
            projects = projects
                .filter {
                    doesProjectContainGroup(it, specs)
                }
                .toMutableList(),
            participations = participations
                .filter {
                    doesProjectContainGroup(projects.find { proj -> proj.id == it.projectId }!!, specs)
                }
                .toMutableList(),
            institute = "Институт информационных технологий и анализа данных",
            specialities = specs
        ).execute()
    //}
}

fun doesProjectContainGroup(project: Project, groups: List<String>): Boolean {
    for (g in project.groups) {
        if (groups.contains(g)) return true
    }
    return false
}

