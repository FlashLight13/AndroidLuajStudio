package ru.potapov.androidluajstudio

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_script.*
import org.luaj.vm2.LuaError
import org.luaj.vm2.lib.jse.CoerceJavaToLua
import org.luaj.vm2.lib.jse.JsePlatform
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets

class ScriptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_script)

        executeButton.setOnClickListener {
            runLua(scriptInput.text.toString())
        }
    }

    private fun runLua(script: String) {
        val charset = StandardCharsets.UTF_8

        val globals = JsePlatform.standardGlobals()
        globals.set("bubble", CoerceJavaToLua.coerce(Bubble(this)))

        val outStream = ByteArrayOutputStream()
        val outPrintStream = PrintStream(outStream, true, charset.name())

        globals.STDOUT = outPrintStream
        globals.STDERR = outPrintStream

        try {
            globals.load(script).call()

            scriptOutput.setTextColor(Color.BLACK)
            scriptOutput.text = String(outStream.toByteArray(), charset)
        } catch (e: LuaError) {
            scriptOutput.setTextColor(Color.RED)
            scriptOutput.text = e.message
        } finally {
            outPrintStream.close()
        }
    }

    private class Bubble(private val context: Context) {

        // called from lua
        fun show(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
