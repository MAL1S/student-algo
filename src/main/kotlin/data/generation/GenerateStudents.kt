package data.generation

import data.model.Student

object GenerateStudents {

    fun generateStudents(): List<Student> {
        val students = mutableListOf<Student>()

        for (i in 0..300) {
            val student = Student(
                id = i,
                fio = GenerateNames.generateName(),
                skills = GenerateSkills.generateSkills()
            )
            students.add(student)
        }

        return students
    }
}