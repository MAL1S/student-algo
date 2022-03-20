package domain

import data.generation.*
import data.model.Participation
import data.model.Project
import data.model.Skill
import data.model.State

class Distribution {

    private val students = GenerateParticipations.students
    private val projects = GenerateProjects.generateProjects()
    private val participations = GenerateParticipations.generateParticipations()
    private val projectsSkills = GenerateProjects.generateProjectSkills(projects)

    private var notApplied = mutableListOf<Int>()

    private var participationIndex = participations.size

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
        println("1 priority = " + participations.count { it.priority == 1 && it.stateId == 1 })
        println("2 priority = " + participations.count { it.priority == 2 && it.stateId == 1 })
        println("3 priority = " + participations.count { it.priority == 3 && it.stateId == 1 })
        println("not applied count = " + participations.count { it.stateId == 0 })
        findNotAppliedStudents()
        println("not applied size = " + notApplied.size)
        println("-------------")
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
        println("free students count = " + GenerateParticipations.freeStudents.size)
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
        for (project in sortProjectList()) {
            val places = project.places
            for (i in 0 until places) {
                participations.add(
                    Participation(
                        id = participationIndex++,
                        priority = 0,
                        projectId = project.id,
                        studentId = findBestMatch(project),
                        stateId = 1
                    )
                )

                project.places--
            }
        }
    }

    fun findBestMatch(project: Project): Int {
        var bestMatching = 0
        var bestMatchingEmpty = 0
        var bestMatchingStudent: Int? = null
        var bestMatchingEmptyStudent: Int? = null

        val projectSkills = projectsSkills.filter { it.projectId == project.id }
        for (student in notApplied) {
            var isEmpty: Boolean
            var similarities: Int

            if (students[student].skills.isEmpty()) {
                similarities = getSimilarSkillsCount(
                    projectSkills = projectSkills.map { it.skill },
                    studentSkills = GenerateSkills.groupSkills[students[student].training_group]!!
                )
                isEmpty = true
            } else {
                similarities = getSimilarSkillsCount(
                    projectSkills = projectSkills.map { it.skill },
                    studentSkills = students[student].skills
                )
                isEmpty = false
            }

            if (isEmpty) {
                if (similarities >= bestMatchingEmpty) {
                    bestMatchingEmpty = similarities
                    bestMatchingEmptyStudent = student
                }
            } else {
                if (similarities > bestMatching) {
                    bestMatching = similarities
                    bestMatchingStudent = student
                    if (bestMatching == projectSkills.size) {
                        notApplied.remove(bestMatchingStudent)
                        return bestMatchingStudent
                    }
                } else if (bestMatchingStudent == null) {
                    bestMatchingStudent = student
                }
            }
        }

        if (bestMatchingStudent == null) {
            notApplied.remove(bestMatchingEmptyStudent!!)

        } else {
            notApplied.remove(bestMatchingStudent)
        }
        return bestMatchingStudent ?: bestMatchingEmptyStudent!!
    }

    fun getSimilarSkillsCount(projectSkills: List<Skill>, studentSkills: List<Skill>): Int {
        var count = 0
        for (skill in projectSkills) {
            if (studentSkills.contains(skill)) {
                count++
            }
        }

        return count
    }

    fun showFinalParticipations() {
        var count = 0
        for (project in projects) {
            println("${project.id} ${project.places}")
            for (participation in participations.filter { it.projectId == project.id && it.stateId == 1 }) {
                var empty: String? = null
                if (students[participation.studentId].skills.isEmpty()) {
                    count++
                    if (participation.priority !in (1..3)) {
                        empty = "- empty skills"
                    }
                }
                println("=== $participation ${empty ?: ""}")
            }
        }
        println("${GenerateStudents.emptyCount} == $count")
    }
}