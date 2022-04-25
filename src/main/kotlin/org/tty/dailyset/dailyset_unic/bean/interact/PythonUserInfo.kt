package org.tty.dailyset.dailyset_unic.bean.interact

data class PythonUserInfo(
    /**
     * **student_number**, like (201806061201)
     */
    val studentNumber: String,
    /**
     * **name**, like (张三)
     */
    val name: String,
    /**
     * **department_name**, like (计算机科学与技术学院)
     */
    val departmentName: String,
    /**
     * **class_name**, like (2018软件工程)
     */
    val className: String,
    /**
     * **grade**, like (2018)
     */
    val grade: String,
    /**
     * **graduation_school**, like (杭州市...)
     */
    val graduationSchool: String,
    /**
     * **major**, like (软件工程)
     */
    val major: String,
    /**
     * **gender**, like (男)
     */
    val gender: String
)