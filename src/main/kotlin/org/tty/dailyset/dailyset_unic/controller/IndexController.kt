package org.tty.dailyset.dailyset_unic.controller

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.tty.dailyset.dailyset_unic.bean.LineResult
import org.tty.dailyset.dailyset_unic.bean.req.GetCourseReq
import org.tty.dailyset.dailyset_unic.service.PythonCallTestService

@RestController
class IndexController {

    @Autowired
    private lateinit var pythonCallTestService: PythonCallTestService

    @RequestMapping("/")
    fun index(): String {
        return "hello ?dailyset_unic?"
    }

    @RequestMapping("/get_course")
    fun testGetCourse(getCourseReq: GetCourseReq): LineResult {
        val uid = getCourseReq.uid!!
        val password = getCourseReq.password!!
        val year = getCourseReq.year!!
        val term = getCourseReq.term!!

        return pythonCallTestService.getCourse(uid, password, year, term)
    }

}