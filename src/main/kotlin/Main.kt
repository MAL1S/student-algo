import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import data.model.Participation
import data.model.Project
import data.model.Speciality
import domain.data.ImportCsvData
import domain.data.ImportExcelData
import domain.distribution.Distribution
import util.containsGroup
import java.io.File

//fun main() {
//    val students = ImportExcelData.getStudentsFromFile("F:/yarmarka_data/stud_new_new.xlsx", "F:/yarmarka_data/stud_exception.xlsx")
//    val projects = ImportCsvData.getProjectsFromFile("F:/yarmarka_data/new/projects.csv")
//    val participations = ImportCsvData.getParticipationsFromFile("F:/yarmarka_data/new/participations.csv", students.second)
//    val specialities = Speciality.specialities
//    val specialGroups = listOf(
//        "ИИКб"
//    )
//    participations.forEach {
//        if (students.second.map { it.id }.contains(it.studentId)) {
//            println("ALERT ${it.studentId}")
//        }
//    }
//
//    for (institute in specialities.keys) {
//        if (institute == "Байкальский институт БРИКС") continue
//
//        val specs = specialities[institute]!!
//
//        val studs = students.first.toMutableList()
//            .filter {
//                specs.contains(it.training_group)
//            }
//            .toMutableList()
//        val projs = projects
//            .filter {
//                containsGroup(it, specs) && it.id != 240
//            }
//            .toMutableList()
//
//        val parts = participations
//            .filter {
//                containsGroup(projects.find { proj -> proj.id == it.projectId }!!, specs)
//            }
//            .toMutableList()
//
//        if (parts.size == 0) continue
//        val dis = Distribution(
//            students = studs,
//            projects = projs,
//            participations = parts,
//            institute = institute,
//            specialities = specs,
//            specialGroups = specialGroups,
//            hasSpecialGroups = institute == "Институт информационных технологий и анализа данных"
//        )
//        dis.executeUniformly()
//    }
//}

fun main() {
    "INSERT INTO state_participations VALUES(4, 'Отклонена');"
    "UPDATE participations SET state_id=4;"
    "UPDATE projects SET title=title, supervisors=supervisors, customer=customer, places=places, state_id=2 WHERE id=id;"
    "INSERT INTO participations SET created_at='2022-09-01',updated_at='2022-09-01',priority=1,project_id=(proj_id),candidate_id=(stud_id),state_id=2;"

    val projects = mutableListOf<String>()
    csvReader().open("E:/read.csv") {
        readAllWithHeaderAsSequence().forEach {
            projects.add(
                "UPDATE projects SET title='${it["name"]?.replaceAllExtraSymbolsInParticipation()}', " +
                        "supervisors='${it["supervisors"]?.replaceAllExtraSymbolsInParticipation()}', " +
                        "customer='${it["customer"]?.replaceAllExtraSymbolsInParticipation()}', " +
                        "places=${it["places"]?.replaceAllExtraSymbolsInParticipation()}, " +
                        "state_id=2 " +
                        "WHERE id=${it["id"]?.replaceAllExtraSymbolsInParticipation()};"
            )
        }
    }

    val students = mutableListOf<String>()
    val participations = mutableListOf<String>()

    val shit = ImportExcelData.getShit("F:/All.xlsx")

    shit.forEach {
        students.add(
            "INSERT INTO candidates VALUES (" +
                    "123122, " +
                    "curdate(), " +
                    "curdate(), " +
                    "'${it["name"]}', " +
                    "'', " +
                    "'', " +
                    "${it["id"]}, " +
                    "'1232', " +
                    "0, " +
                    "'', " +
                    "'${it["group"]}', " +
                    "NULL);"
        )
        println(it)
    }

//    shit.forEach {
//        participations.add(
//            "INSERT INTO participations SET created_at='2022-09-01'," +
//                    "updated_at='2022-09-01'," +
//                    "priority=1," +
//                    "project_id=," +
//                    "candidate_id=(stud_id)," +
//                    "state_id=2;"
//        )
//    }

//    File("E:/output.txt").printWriter().use { out ->
//        out.println("INSERT INTO state_participations VALUES(4, 'Отклонена');")
//        out.println("UPDATE participations SET state_id=4;")
//        projects.forEach {
//            out.println(it)
//        }
//    }
}

private fun String.replaceAllExtraSymbolsInParticipation(): String {
    return this.replace("&quot;", "")
        .replace("&laquo;", "")
        .replace("&raquo;", "")
        .replace("<span class='label label-success'>", "")
        .replace("</span>", "")
        .replace("&nbsp;", "")
        .replace("\n", "")
}