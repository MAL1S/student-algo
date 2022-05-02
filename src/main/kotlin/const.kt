val FIRST_FREQUENCY = (0..1) //частота спроса на первые проекты в списке для каждого приоритета
val FIRST_ITERATION_DEMAND = (0..1) //на какие проекты в основно будет спрос заявок 1-го приоритета
val SECOND_ITERATION_DEMAND = (2..4) //на какие проекты в основном будет спрос заявок 2-го и 3-го приоритета
const val FIRST_ITERATION_SKIP_COUNT = 150 //количество студентов, не подавших заявки вообще
val SKIP_SECOND_FREQUENCY = (0..10) //шанс у студента не сгенерировать заявку 2-го приоритета
val SKIP_THIRD_FREQUENCY = (0..10) //шанс у студента не сгенерировать заявку 3-го приоритета
val PROJECT_MEAN_SKILL_COUNT = (3..6) //количество сгенерированных навыков у проекта в среднем
val STUDENT_MEAN_SKILL_COUNT = (0..4) //количество сгенерированных навыков у студента в среднем
val STUDENT_WITH_NULL_SKILLS_CHANCE = (0..3) //шанс генерации студента без определенных навыков
val PROJECT_GROUPS_COUNT = (1..3) //количество групп студентов, допущенных для проекта

const val STUDENT_COUNT = 300 //количесто студентов
const val SUPERVISOR_COUNT = 15 //количество преподавателей
const val PROJECTS_COUNT = 30 //количество проектов
//const val PROJECT_STUDENT_CAPACITY = 15 //вместительность проектов
const val PROJECT_STUDENT_CAPACITY_LOWER_BOUNDARY = 11 //граница "от" для вместимости проектов
const val PROJECT_STUDENT_CAPACITY_UPPER_BOUNDARY = 12 //граница "до" для вместимости проектов
const val PROJECT_MIN_CAPACITY = 8 //минимальная вместимость проекта