package domain

import data.generation.GenerateParticipations
import data.generation.GenerateProjects
import data.model.Participation
import data.model.Project
import data.model.Student

class FirstDistribution {

    private val students = GenerateParticipations.students
    private val projects = GenerateProjects.generateProjects()
    private val participations = GenerateParticipations.generateParticipations()
    private var notApplied = mutableListOf<Int>()

    private val log = mutableListOf<String>(
        "0)",
        "1)",
        "2)",
        "3)",
        "4)",
        "5)",
        "6)",
        "7)",
        "8)",
        "9)",
        "10)",
        "11)",
        "12)",
        "13)",
        "14)",
        "15)",
        "16)",
        "17)",
        "18)",
        "19)"
    )

    fun firstDistribute() {
        for (priority in (1..3)) {
            for (project in projects) {
                if (project.places != 0) {
                    val participationsForCurrentProject =
                        participations.filter { it.projectId == project.id && it.priority == priority && it.stateId == 0 }
                            .toMutableList()
                    //println("$priority vacancies = ${participationsForCurrentProject.size}")
                    if (participationsForCurrentProject.isNotEmpty()) {
                        for (i in participationsForCurrentProject) {
                            if (project.places == 0) {
                                //println("empty ${project.id} = $a")
                                break
                            }
                            val participationIndex = participations.indexOf(i)
                            participations[participationIndex].stateId = 1
                            //a.remove(i)
                            //participations.removeAll { it.studentId == participations[participationIndex].studentId && it.priority != priority }
                            val participationsToDelete =
                                participations.filter { it.studentId == participations[participationIndex].studentId && it.priority != priority }
                            for (j in participationsToDelete) {
                                participations[participations.indexOf(j)].stateId = 2
                            }
                            project.places--
                        }
                    }
                    log[project.id] += " - ${project.places}"
                }
            }
        }
        println(participations.count { it.priority == 1 && it.stateId == 1 })
        println(participations.count { it.priority == 2 && it.stateId == 1 })
        println(participations.count { it.priority == 3 && it.stateId == 1 })
        println(participations.count { it.stateId == 0 })
        findNotAppliedStudents()
        println(notApplied.size)
        sortProjectList().forEach { println(it) }

        secondDistribution()

        showFinalParticipations()
//        showFirstDistributionParticipations()
//        println("---------------")
////        participations.forEach {
////            println(it)
////        }
////        val list = participations.map { it.studentId }
////        println(list.size)
////        println(HashSet<Int>(list).size)
//
//        println("----------")
//        log.forEach { println(it) }
    }

    fun findNotAppliedStudents() {
        val list = participations.filter { it.stateId == 0 }
        val set = HashSet(participations.filter { it.stateId == 0 }.map { it.studentId })
//        set.forEach {
//            println(it)
//        }
        notApplied.addAll(set)
        println(GenerateParticipations.freeStudents.size)
        notApplied.addAll(GenerateParticipations.freeStudents)

        for (i in list) {
            participations[participations.indexOf(i)].stateId = 2
        }
    }

    fun sortProjectList(): List<Project> {
        val list = mutableListOf<Project>()

        for (project in projects) {
            if (project.places != 0) list.add(project)
        }

        return list.sortedBy { it.places }
    }

    fun secondDistribution() {
        var index = 0
        var participationIndex = participations.size
        for (project in sortProjectList()) {
            val places = project.places
            for (i in 0 until places) {
                participations.add(
                    Participation(
                        id = participationIndex++,
                        priority = 0,
                        projectId = project.id,
                        studentId = GenerateParticipations.freeStudents.elementAt(index++),
                        stateId = 1
                    )
                )
                project.places--
            }
        }
    }

    fun showFinalParticipations() {
        for (project in projects) {
            println("${project.id} ${project.places}")
            for (participation in participations.filter { it.projectId == project.id && it.stateId == 1 }) {
                println("=== $participation")
            }
        }
    }
}