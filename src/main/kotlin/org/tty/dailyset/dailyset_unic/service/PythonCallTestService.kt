package org.tty.dailyset.dailyset_unic.service

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.tty.dailyset.dailyset_unic.bean.LineResult
import org.tty.dailyset.dailyset_unic.component.EnvironmentVars
import java.io.BufferedReader
import java.nio.charset.Charset
import java.util.*

@Service
class PythonCallTestService {

    @Autowired
    private lateinit var environmentVars: EnvironmentVars

    private val os = System.getProperty("os.name").lowercase(Locale.getDefault())

    @Deprecated("not support yet")
    fun getCourse(uid: String, password: String, year: Int, term: Int): LineResult {
        var reader: BufferedReader ?= null

        val result: LineResult = try {
            val inputArgs = arrayOf(
                environmentVars.pythonPath,
                environmentVars.scriptPath,
                uid,
                password,
                year.toString(),
                term.toString()
            )
            val process = Runtime.getRuntime().exec(inputArgs)
            val charset = if (os.indexOf("windows") >= 0) {
                Charset.forName("gbk")
            } else {
                Charset.forName("utf-8")
            }
            reader = process.inputStream.bufferedReader(charset)
            val resultEntity = Json.parseToJsonElement( reader.readText())
            LineResult(success = true, result = resultEntity)
        } catch (e: Exception) {
            LineResult(success = false, result =  e.message?:"")
        } finally {
            reader?.close()
        }

        return result
    }
}