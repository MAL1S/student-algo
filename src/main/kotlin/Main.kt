import data.model.Participation
import data.model.Project
import data.model.Speciality
import domain.data.ImportCsvData
import domain.data.ImportExcelData
import domain.distribution.Distribution
import util.containsGroup

fun main() {
    val students = ImportExcelData.getStudentsFromFile("F:/yarmarka_data/stud.xlsx", "E:/exception.xlsx")
    val projects = ImportCsvData.getProjectsFromFile("F:/yarmarka_data/new/projects.csv")
    val participations = ImportCsvData.getParticipationsFromFile("F:/yarmarka_data/new/participations.csv")
    val specialities = Speciality.specialities
    val specialGroups = listOf(
        "ИИКб"
    )
//    projects.map { it }.forEach {
//        println(it)
//    }

//    val temp = "Институт экономики, управления и права"
//    //for (institute in specialities.keys) {
//        //val specs = specialities[institute]!!
//        val specs = specialities[temp]!!
//        //println("specs = $specs")
//        val studs = students.toMutableList()
//            .filter {
//                specs.contains(it.training_group)
//            }
//            .toMutableList()
//        val projs = projects
//            .filter {
//                containsGroup(it, specs) && it.id != 240
//            }
//            .toMutableList()
//        projs.forEach {
//            //println(it)
//        }
//        val parts = participations
//            .filter {
//                containsGroup(projects.find { proj -> proj.id == it.projectId }!!, specs)
//            }
//            .toMutableList()
//        //if (parts.size == 0 || "Институт авиамашиностроения и транспорта" == "Байкальский институт БРИКС") continue
//        val dis = Distribution(
//            students = studs,
//            projects = projs,
//            participations = parts,
//            institute = temp,
//            specialities = specs,
//            specialGroups = specialGroups,
//            hasSpecialGroups = temp == "Институт информационных технологий и анализа данных"
//        )
//        //println(dis.participations)
//        dis.executeUniformly()
//    //}

    for (institute in specialities.keys) {
        if (institute == "Байкальский институт БРИКС") continue
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
                containsGroup(it, specs) && it.id != 240
            }
            .toMutableList()
        projs.forEach {
            //println(it)
        }
        val parts = participations
            .filter {
                containsGroup(projects.find { proj -> proj.id == it.projectId }!!, specs)
            }
            .toMutableList()
        if (parts.size == 0 || institute == "Байкальский институт БРИКС") continue
        val dis = Distribution(
            students = studs,
            projects = projs,
            participations = parts,
            institute = institute,
            specialities = specs,
            specialGroups = specialGroups,
            hasSpecialGroups = institute == "Институт информационных технологий и анализа данных"
        )
        //println(dis.participations)
        dis.executeUniformly()
    }
}
