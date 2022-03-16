package data.model

data class Student(
    var id: Int,
    var fio: String,
//    var about: String,
//    var email: String,
//    var phone: String,
//    var numz: String,
//    var course: Int,
    var training_group:  String,
    var skills: List<Skill>,
    //var experience: List<Int>
)