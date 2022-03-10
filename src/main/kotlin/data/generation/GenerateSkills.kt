package data.generation

import data.model.Skill

object GenerateSkills {

    private const val IST = "ИСТ"
    private const val ISM = "ИСМ"
    private const val ASU = "АСУ"
    private const val EVM = "ЭВМ"

    private val IST_SKILLS = listOf<String>(

    )
    private val ISM_SKILLS = listOf<String>(

    )
    private val ASU_SKILLS = listOf<String>(

    )
    private val EVM_SKILLS = listOf<String>(

    )

    val groupSkills = mutableMapOf<String, List<String>>(
        IST to IST_SKILLS,
        ISM to ISM_SKILLS,
        ASU to ASU_SKILLS,
        EVM to EVM_SKILLS
    )

    private val skills = listOf<String>(
        "html",
        "css",
        "java",
        "kotlin",
        "android",
        "web",
        "vue",
        "c#",
        "wpf",
        "sql",
        ".net",
        "unit tests",
        "python",
        "машинное обучение",
        "big data",
        "статьи",
        "анализ предметной области",
        "cisco",
        "конфигурирование сетей",
    )

    fun generateSkills(): List<Skill> {
        val resultSkills = mutableListOf<Skill>()

        val randomInt = (0..10).random()
        for (i in 0..randomInt) {
            resultSkills.add(
                Skill(id = i, skill = skills[(skills.indices).random()])
            )
        }

        return resultSkills
    }
}