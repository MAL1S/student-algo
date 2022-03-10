package domain

import data.generation.GenerateProjects

class FirstDistribution {

    private val projects = GenerateProjects.generateProjects()

    fun distribute() {
        for (i in (1..3)) {
            for (project in projects) {

            }
        }
    }
}