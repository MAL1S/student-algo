package data.model

import domain.data.ImportCsvData

object Speciality {
    var specialities = ImportCsvData.getSpecialitiesFromFile("F:/yarmarka_data/specialities.csv")

    init {
        println(specialities)
    }
}