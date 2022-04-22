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
    private lateinit var _encryptSalt: String
    private lateinit var _encryptAESKey: String

    val pythonPath: String get() = _pythonPath
    val scriptPath: String get() = _scriptPath
    val encryptSalt: String get() = _encryptSalt
    val encryptAESKey: String get() = _encryptAESKey

    @PostConstruct
    fun init() {
        _pythonPath = context.environment["dailyset.env.python.path"] ?: ""
        _scriptPath = context.environment["dailyset.env.script.path"] ?: ""
        _encryptSalt = context.environment["dailyset.env.encrypt.salt"] ?: ""
        _encryptAESKey = context.environment["dailyset.env.encrypt.aes.key"] ?: ""
    }
}