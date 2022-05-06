package org.tty.dailyset.dailyset_unic.controller

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController {

    @RequestMapping("/")
    fun index(): String {
        return "hello ?dailyset_unic?"
    }
}