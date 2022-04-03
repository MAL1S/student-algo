package data.generation

import FIRST_FREQUENCY
import FIRST_ITERATION_DEMAND
import FIRST_ITERATION_SKIP_COUNT
import SECOND_ITERATION_DEMAND
import SKIP_SECOND_FREQUENCY
import SKIP_THIRD_FREQUENCY
import data.model.Participation
import data.model.Project
import data.model.Student

object GenerateParticipations {

    val students = GenerateStudents.generateStudents()
    val projects = GenerateProjects.generateProjects()
    val freeStudents = mutableSetOf<Int>()

    fun generateParticipations(): MutableList<Participation> {
        val participations = mutableListOf<Participation>()
        val usedStudentToProjectCombinations = mutableMapOf<Int, MutableList<Project>>()

        var participationIndex = 0

        for (priority in (1..3)) {
            var i = 0
            for (student in students) {
                if (i++ <  FIRST_ITERATION_SKIP_COUNT) {
                    if (priority == 1) freeStudents.add(student.id)
                    continue
                }

                if (priority == 1) {
                    var ifFirst: Int? = null
                    if (FIRST_FREQUENCY.random() == 0) {
                        ifFirst = FIRST_ITERATION_DEMAND.random()
                    }
                    val project = projects[ifFirst ?: (projects.indices).random()]
                    val participation = Participation(
                        id = participationIndex++,
                        priority = priority,
                        projectId = project.id,
                        studentId = student.id,
                        stateId = States.states[0].id
                    )
                    participations.add(participation)
                    usedStudentToProjectCombinations[student.id] = mutableListOf(project)
                } else {
                    if (priority == 2 && SKIP_SECOND_FREQUENCY.random() == 0) continue

                    if (priority == 3) {
                        if (usedStudentToProjectCombinations[student.id]!!.size == 1) {
                            continue
                        } else if (SKIP_THIRD_FREQUENCY.random() == 0) {
                            continue
                        }
                    }

                    var ifFirst: Int? = null
                    if (FIRST_FREQUENCY.random() == 0) {
                        ifFirst = SECOND_ITERATION_DEMAND.random()
                    }

                    var project = projects[ifFirst ?: (projects.indices).random()]
                    while (usedStudentToProjectCombinations[student.id]!!.contains(project)) {
                        project = projects[(projects.indices).random()]
                    }

                    val participation = Participation(
                        id = participationIndex++,
                        priority = priority,
                        projectId = project.id,
                        studentId = student.id,
                        stateId = States.states[0].id
                    )
                    participations.add(participation)
                    usedStudentToProjectCombinations[student.id]!!.add(project)
                }
            }
        }

//        for (i in participations) {
//            println(i)
//        }
        println("total 1 priority = " + participations.count { it.priority == 1 })
        println("total 2 priority = " + participations.count { it.priority == 2 })
        println("total 3 priority = " + participations.count { it.priority == 3 })
        println("---------------")
        return participations
    }
}