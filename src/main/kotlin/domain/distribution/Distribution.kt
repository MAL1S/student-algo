package domain.distribution

import PROJECT_LOWER_DEMAND_COEFFICIENT
import PROJECT_MIN_CAPACITY
import PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY
import PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY
import data.model.Participation
import data.model.Project
import data.model.Skill
import data.model.Student
import kotlin.math.ceil
import com.grapecity.documents.excel.Workbook
import domain.data.ExportDataToExcel

class Distribution(
    private val students: MutableList<Student>,
    private val projects: MutableList<Project>,
    private val participations: MutableList<Participation>,
    private val institute: String,
    private val specialities: List<String>
) {

    private val distributionPreparation = DistributionPreparation(
        students = students,
        participations = participations
    )

    private var notApplied = mutableListOf<Student>()
    var applied = 0

    private var participationIndex = participations.size

    init {
        distributionPreparation.prepare()
    }

    fun execute() {
        distributeParticipations()
        findNotAppliedStudents()
        distributeSilentStudents()
        //distributeSilentStudentsToFull()
//        distributeExcessStudents()
        ExportDataToExcel.writeProjectsWithStudents(
            students = students,
            projects = projects,
            participations = participations,
            institute = institute
        )
        ExportDataToExcel.writeFreeStudents(
            students = notApplied,
            institute = institute
        )
        projects.forEach {
            println("${it.freePlaces} ${it.groups}")
        }
    }

    private fun distributeParticipations() {
        for (priority in (1..3)) {
            for (project in projects) {
                if (project.freePlaces != 0) {
                    val participationsForCurrentProject =
                        participations.filter {
                            it.projectId == project.id &&
                                    it.priority == priority &&
                                    it.stateId == 0 &&
                                    students.map { stud -> stud.id }.contains(it.studentId)
                        }
                            .toMutableList()

                    if (participationsForCurrentProject.isNotEmpty()) {
                        for (i in participationsForCurrentProject) {
                            if (project.freePlaces == 0) {
                                break
                            }
                            val currentParticipationIndex = participations.indexOf(i)
                            participations[currentParticipationIndex].stateId = 1
                            applied++

                            val participationsToDelete =
                                participations.filter { it.studentId == participations[currentParticipationIndex].studentId && it.priority != priority }
                            for (j in participationsToDelete) {
                                participations[participations.indexOf(j)].stateId = 2
                            }
                            project.freePlaces--
                        }
                    }
                }
            }
        }
//        participations.forEach {
//            println(it)
//        }
//        println("1 priority = " + participations.count { it.priority == 1 && it.stateId == 1 })
//        println("2 priority = " + participations.count { it.priority == 2 && it.stateId == 1 })
//        println("3 priority = " + participations.count { it.priority == 3 && it.stateId == 1 })
//        println("not applied count = " + participations.count { it.stateId == 0 })
//        findNotAppliedStudents()
//        println("not applied size = " + notApplied.size)
//        println("-------------")
//        sortProjectList().forEach { println(it) }
    }

    private fun findNotAppliedStudents() {
        val notAppliedParticipations = participations.filter { it.stateId == 0 }
        val notAppliedStudents =
            participations.filter { it.stateId == 0 && students.map { stud -> stud.id }.contains(it.studentId) }
                .map { it.studentId }.toSet()

        notApplied.addAll(notAppliedStudents.map { students.find { stud -> stud.id == it }!! })

        println("free students due to not applied = ${notApplied.size}")
        println("free students count = " + distributionPreparation.freeStudents.size)

        notApplied.addAll(distributionPreparation.freeStudents)

//        println(students.count())
//        println(participations.map { it.studentId }.count())
//        println(participations.map { it.studentId }.toSet().count())
//        println(notApplied.toSet().count())
//        println(applied)

        for (i in notAppliedParticipations.map { it.id }) {
            participations.find { it.id == i }!!.stateId = 2
        }
        println("not applied = ${notApplied.size}")
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
            firstlySorted.filter { it.freePlaces <= ceil(PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY * PROJECT_LOWER_DEMAND_COEFFICIENT) }
        val lowDemandProjects = firstlySorted.subtract(highDemandProjects.toSet()).toMutableList()
        val toEnd = mutableListOf<Project>()

        for (project in lowDemandProjects) {
            if (highDemandProjects.find { it.supervisors[0] == project.supervisors[0] } != null) {
                toEnd.add(project)
            }
        }
        lowDemandProjects.removeAll(toEnd)
        lowDemandProjects.addAll(toEnd)

//        (highDemandProjects + lowDemandProjects).forEach {
//            println(it.freePlaces)
//        }

        return highDemandProjects + lowDemandProjects
    }

    private fun distributeSilentStudents() {
        for (project in sortProjectList()) {
            var places = project.freePlaces
            //project.freePlaces - (PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY)
            if (project.groups.contains("ИИКб")) places = 10000
            for (i in 0 until places) {
                val bestMatchingStudent: Student? = findBestMatch(project = project)

                if (bestMatchingStudent != null) {
                    participations.add(
                        Participation(
                            id = participationIndex++,
                            priority = 4,
                            projectId = project.id,
                            studentId = bestMatchingStudent.id,
                            stateId = 1
                        )
                    )
                    projects[projects.indexOfFirst { it.id == project.id }].freePlaces--
                } else {
                    break
                }
            }
        }
    }

    private fun distributeSilentStudentsToFull() {
        for (project in sortProjectList()) {
            val places = PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - project.freePlaces
            for (i in 0 until places) {
                val bestMatchingStudent: Student? = findBestMatch(project = project)

                if (bestMatchingStudent != null) {
                    participations.add(
                        Participation(
                            id = participationIndex++,
                            priority = 4,
                            projectId = project.id,
                            studentId = bestMatchingStudent.id,
                            stateId = 1
                        )
                    )
                    projects[projects.indexOfFirst { it.id == project.id }].freePlaces--
                } else {
                    break
                }
            }
        }
    }

    private fun distributeExcessStudents() {
        val excessProjects =
            projects.filter { it.freePlaces > (PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_MIN_CAPACITY) }
                .reversed()
        val sortedProjects = projects.sortedBy { it.freePlaces }

        for (project in excessProjects) {

            //println("${project.id}")
            val excessParticipations = participations.filter { it.projectId == project.id }

            for (i in excessParticipations) {
                val student = students.find { it.id == i.studentId }!!
                val suitedProjects =
                    sortedProjects.filter {
                        it.id != project.id &&
                                it.groups.contains(student.training_group) &&
                                it.freePlaces <= (PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY - PROJECT_MIN_CAPACITY)
                    }
                val suitedProject = suitedProjects.maxByOrNull { it.freePlaces }
                participations.remove(i)
                participations.add(
                    Participation(
                        id = participationIndex++,
                        priority = 5,
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

    private fun findBestMatch(project: Project): Student? {
        var bestMatchingStudent: Student? = null
        var special = project.groups.contains("ИИКб")

//        val notAppliedForThisProject =
//            notApplied.filter { project.groups.contains(it.training_group) }
        //println("${project.freePlaces} ${project.groups} $notAppliedForThisProject")

        if (notApplied.isEmpty()) {
            return null
        }

        var groupStudent: Student? = null
        var toFillStudent: Student? = null

        for (student in notApplied) {
            if (special) {
                if (student.training_group == "ИИКб") {
                    println(student)
                    bestMatchingStudent = student
                    break
                }
            } else {
                if (groupStudent == null && project.groups.contains(student.training_group)) {
                    groupStudent = student
                } else if (toFillStudent == null) {
                    toFillStudent = student
                }
            }

//            var isEmpty: Boolean
//            var similarities: Int
//
//            if (students[student].skills.isEmpty()) {
//                similarities = getSimilarSkillsCount(
//                    projectSkills = projectSkills.map { it.skill },
//                    studentSkills = GenerateSkills.groupSkills[students[student].training_group]!!
//                )
//                isEmpty = true
//            } else {
//                similarities = getSimilarSkillsCount(
//                    projectSkills = projectSkills.map { it.skill },
//                    studentSkills = students[student].skills
//                )
//                isEmpty = false
//            }

//            if (isEmpty) {
//                if (similarities >= bestMatchingEmpty) {
//                    bestMatchingEmpty = similarities
//                    bestMatchingEmptyStudent = student
//                }
//            } else {
//                if (similarities > bestMatching) {
//                    bestMatching = similarities
//                    bestMatchingStudent = student
//                    if (bestMatching == projectSkills.size) {
//                        notApplied.remove(bestMatchingStudent)
//                        return bestMatchingStudent
//                    }
//                } else if (bestMatchingStudent == null) {
//                    bestMatchingStudent = student
//                }
//            }
        }

        if (!special) bestMatchingStudent = groupStudent ?: toFillStudent

//        if (bestMatchingStudent == null) {
//            notApplied.remove(bestMatchingEmptyStudent!!)
//
//        } else {
//            notApplied.remove(bestMatchingStudent)
//        }
        notApplied.removeIf { it.id == bestMatchingStudent?.id }
        return bestMatchingStudent
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
}