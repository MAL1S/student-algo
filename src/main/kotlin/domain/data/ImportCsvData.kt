package domain.data

import data.model.Participation
import data.model.Project
import domain.data.ImportCsvData.splitToProject
import java.io.File

object ImportCsvData {

    fun getProjectsFromFile(filePath: String): List<Project> {
        val projects = mutableListOf<Project>()
        val split = splitFileFromCsvFormat(filePath = filePath)
        for ((index, str) in split.withIndex()) {
            if (index == 0) continue
            val project = str.replaceAllExtraSymbolsInProject()
                .splitToProject()
            if (project != null) {
                projects.add(project)
            }
        }
        return projects
    }

    fun getParticipationsFromFile(filePath: String): List<Participation> {
        val participations = mutableListOf<Participation>()
        val split = splitFileFromCsvFormat(filePath = filePath)
        for ((index, str) in split.withIndex()) {
            if (index == 0) continue
            participations.add(
                str.replaceAllExtraSymbolsInParticipation()
                    .splitToParticipation(index)
            )
        }
        return participations
    }

    fun getSpecialitiesFromFile(filePath: String): Map<String, List<String>> {
        val specialities = mutableMapOf<String, MutableList<String>>()
        val split = splitFileFromCsvFormat(filePath)
        for ((index, str) in split.withIndex()) {
            if (index == 0) continue
            val s = str.splitWithQuoteBlocks()
            val spec = s[1]
            val inst = s[2]
            val value = specialities.getOrDefault(inst, emptyList<String>().toMutableList())
            value.add(spec)
            specialities[inst] = value
        }
        return specialities
    }

    private fun String.replaceAllExtraSymbolsInProject(): String {
        return this.replace("&quot", "")
            .replace("&laquo", "")
            .replace("&raquo", "")
            .replace("<span class='label label-success'>", "")
            .replace("</span>", "")
            .replace("&nbsp", "")
            .replace("\n", "")
            .replace("<br>", ";")
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

    private fun String.splitToProject(): Project? {
        val split = this
            .replace("\n", "")
            .splitWithQuoteBlocks()

        val project = Project(
            id = split[0].toInt(),
            title = split[3],
            places = split[4].toInt(),
            freePlaces = split[4].toInt(),
            supervisors = split[15].split(","),
            groups = split[18].replace(" ", "").split(";"),
            skills = split[19].split(";")
        )
        println(project)

        return if (project.places == 100) null
        else project
    }

    private fun String.splitToParticipation(index: Int): Participation {
        val firstQuote = this.indexOfFirst { it == '"' }
        val secondQuote = this.substring(firstQuote + 1).indexOfFirst { it == '"' }
        val split = this
            .replace("\n", "")
            .removeRange(firstQuote..secondQuote + firstQuote)
            .split(",")
        //println(split)

        //old file
//        return Participation(
//            id = split[0].toInt(),
//            priority = split[3].toInt(),
//            projectId = split[4].toInt(),
//            studentId = split[6].toInt(),
//            stateId = 0
//        )

        //new file
        return Participation(
            id = split[0].toInt(),
            priority = split[3].toInt(),
            projectId = split[5].toInt(),
            studentId = split[7].toInt(),
            stateId = 0
        )
    }

    private fun String.splitWithQuoteBlocks(): List<String> {
        val result = mutableListOf<String>()
        var temp = ""
        var wasQuote = false
        for (ch in this) {
            if (ch == '"') {
                wasQuote = !wasQuote
                continue
            }

            if (ch == ',' && !wasQuote) {
                //println(temp)
                result.add(temp)
                temp = ""
            } else {
                temp += ch
            }
        }
        result.add(temp)
        return result
    }

    fun splitFileFromCsvFormat(filePath: String): List<String> {
        val result = mutableListOf<String>()
        var temp = ""
        var wasQuote = false
        File(filePath).bufferedReader().use {
            it.readText().forEach { ch ->
                if (ch == '"') {
                    wasQuote = !wasQuote
                }
                if (ch == '\n' && !wasQuote) {
                    result.add(temp)
                    temp = ""
                } else {
                    temp += ch
                }
            }
        }
        return result
    }
}