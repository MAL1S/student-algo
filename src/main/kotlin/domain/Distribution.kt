package domain

import PROJECT_MIN_CAPACITY
import PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY
import PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY
import data.generation.*
import data.model.Participation
import data.model.Project
import data.model.Skill
import kotlin.math.ceil

class Distribution {

    private val students = GenerateParticipations.students
    private val projects = GenerateParticipations.projects
    private val participations = GenerateParticipations.generateParticipations()
    private val projectsSkills = GenerateProjects.generateProjectSkills(projects)

    private var notApplied = mutableListOf<Int>()

    private var participationIndex = participations.size

    fun execute() {
        firstDistribute()
        secondDistribution()
        distributeExcessStudents()
        showFinalParticipations()
    }

    private fun firstDistribute() {
        for (priority in (1..3)) {
            for (project in projects) {
                if (project.freePlaces != 0) {
                    val participationsForCurrentProject =
                        participations.filter { it.projectId == project.id && it.priority == priority && it.stateId == 0 }
                            .toMutableList()

//                    println("${project.groups} ")
//                    for (p in participationsForCurrentProject) {
//                        println("${students[p.studentId].training_group}")
//                    }
//                    println("-------------------")

                    if (participationsForCurrentProject.isNotEmpty()) {
                        for (i in participationsForCurrentProject) {
                            if (project.freePlaces == 0) {
                                //println("empty ${project.id} = $a")
                                break
                            }
                            val participationIndex = participations.indexOf(i)
                            participations[participationIndex].stateId = 1

                            val participationsToDelete =
                                participations.filter { it.studentId == participations[participationIndex].studentId && it.priority != priority }
                            for (j in participationsToDelete) {
                                participations[participations.indexOf(j)].stateId = 2
                            }
                            project.freePlaces--
                        }
                    }
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

    private fun sortProjectList(): List<Project> {
        val list = mutableListOf<Project>()

        for (project in projects) {
            if (project.freePlaces != 0) {
                list.add(project)
            }
        }

        val firstlySorted = list.sortedBy { it.freePlaces }
        val highDemandProjects =
            firstlySorted.filter { it.freePlaces <= ceil(PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY * 0.8) }
        val lowDemandProjects = firstlySorted.subtract(highDemandProjects.toSet()).toMutableList()
        val toEnd = mutableListOf<Project>()

        for (project in lowDemandProjects) {
            if (highDemandProjects.find { it.supervisor_name == project.supervisor_name } != null) {
                toEnd.add(project)
            }
        }
        lowDemandProjects.removeAll(toEnd)
        lowDemandProjects.addAll(toEnd)

        return highDemandProjects + lowDemandProjects
    }

    private fun secondDistribution() {
        for (project in sortProjectList()) {
            val places =
                project.freePlaces - (PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY)
            for (i in 0 until places) {
                val bestMatchingStudentId: Int? = findBestMatch(project = project)

                if (bestMatchingStudentId != null) {
                    participations.add(
                        Participation(
                            id = participationIndex++,
                            priority = 0,
                            projectId = project.id,
                            studentId = bestMatchingStudentId,
                            stateId = 1
                        )
                    )
                    projects[project.id].freePlaces--
                } else {
                    break
                }
            }
        }
    }

    private fun distributeExcessStudents() {
        val excessProjects = projects.filter { it.freePlaces > (it.placesUpper - PROJECT_MIN_CAPACITY) }.reversed()
        val sortedProjects = projects.sortedBy { it.freePlaces }

        for (project in excessProjects) {

            println("${project.id}")
            val excessParticipations = participations.filter { it.projectId == project.id }

            for (i in excessParticipations) {
                val student = students[i.studentId]
                val suitedProjects =
                    sortedProjects.filter { it.id != project.id &&
                            it.groups.contains(student.training_group) &&
                            it.freePlaces <= (project.placesUpper - PROJECT_MIN_CAPACITY) }
                val suitedProject = suitedProjects.maxByOrNull { it.freePlaces }
                participations.remove(i)
                participations.add(
                    Participation(
                        id = participationIndex++,
                        priority = -1,
                        projectId = suitedProject!!.id,
                        studentId = student.id,
                        stateId = 1
                    )
                )

                projects[project.id].freePlaces++
//                if (suitedProject.freePlaces > 0) {
                    projects[suitedProject.id].freePlaces--
                //}
            }
        }
    }

    private fun findBestMatch(project: Project): Int? {
        var bestMatching = 0
        var bestMatchingEmpty = 0
        var bestMatchingStudent: Int? = null
        var bestMatchingEmptyStudent: Int? = null

        val projectSkills = projectsSkills.filter { it.projectId == project.id }
        val notAppliedForThisProject = notApplied.filter { project.groups.contains(students[it].training_group) }

        if (notAppliedForThisProject.isEmpty()) {
            return null
        }

        for (student in notAppliedForThisProject) {
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

    private fun getSimilarSkillsCount(projectSkills: List<Skill>, studentSkills: List<Skill>): Int {
        var count = 0
        for (skill in projectSkills) {
            if (studentSkills.contains(skill)) {
                count++
            }
        }

        return count
    }

    private fun showFinalParticipations() {
        var count = 0
        for (project in projects) {
            val projectParticipations = participations.filter { it.projectId == project.id && it.stateId == 1 }
            println("\n${project.id} occupied places = ${projectParticipations.count()} free places = ${project.freePlaces} ${project.groups}")
            for (participation in projectParticipations) {
                var empty: String? = null
                if (students[participation.studentId].skills.isEmpty()) {
                    count++
                    if (participation.priority !in (1..3)) {
                        empty = "- empty skills"
                    }
                }
                println("=== $participation ${empty ?: ""} ${(students[participation.studentId].training_group)}")
            }
        }
        //println("${GenerateStudents.emptyCount} == $count")
    }
}