package data.generation

import data.model.Supervisor

object GenerateSupervisors {

    fun generateSupervisors(): List<Supervisor> {
        val supervisors = mutableListOf<Supervisor>()

        for (i in 0..10) {
            supervisors.add(
                Supervisor(id = i, fio = GenerateNames.generateName())
            )
        }

        return supervisors
    }
}