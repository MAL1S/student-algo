import domain.data.ImportCsvData
import domain.data.ImportExcelData
import domain.distribution.Distribution

fun main() {
    val students = ImportExcelData.getStudentsFromFile("F:/yarmarka_data/stud.xlsx", "E:/exception.xlsx")
    val projects = ImportCsvData.getProjectsFromFile("F:/yarmarka_data/projects.csv")
    val participations = ImportCsvData.getParticipationsFromFile("F:/yarmarka_data/participations.csv")

    Distribution(
        students = students.toMutableList(),
        projects = projects.toMutableList(),
        participations = participations.toMutableList()
    ).execute()
}

