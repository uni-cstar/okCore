package unics.okcore.exception

import java.io.PrintWriter
import java.io.StringWriter
import kotlin.stackTraceToString as stackTraceToStringLib

/**
 * 方法存在缺陷：这里应该支持最大堆栈长度限定，避免递归异常的堆栈信息过多问题。
 */
fun Throwable.stackTraceToStringCompat(): String {
    val version = KotlinVersion.CURRENT
    return if (version.isAtLeast(1, 4)) {
        this.stackTraceToStringLib()
    } else {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        printStackTrace(pw)
        pw.flush()
        sw.toString()
    }
}