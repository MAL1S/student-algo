package domain.distribution

import data.model.Participation
import data.model.Student

class DistributionPreparation(
    students: List<Student>,
    private val participations: List<Participation>
) {

    val freeStudents = students.toMutableList()

    fun prepare() {
        freeStudents.removeIf { participations.find { p -> it.id == p.studentId } == null }
    }
}