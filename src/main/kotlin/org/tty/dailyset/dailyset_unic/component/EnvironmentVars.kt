package org.tty.dailyset.dailyset_unic.component

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.env.get
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class EnvironmentVars {

    @Autowired
    private lateinit var context: ConfigurableApplicationContext

    private lateinit var _pythonPath: String
    private lateinit var _scriptPath: String

    val pythonPath: String get() = _pythonPath
    val scriptPath: String get() = _scriptPath

    @PostConstruct
    fun init() {
        _pythonPath = context.environment["dailyset.env.python.path"] ?: ""
        _scriptPath = context.environment["dailyset.env.script.path"] ?: ""
    }
}