package unics.okcore.exception

import unics.okcore.exceptionMessageTransformer
import java.io.PrintWriter
import java.io.StringWriter


interface ExceptionMessageTransformer {
    fun getTransformedMessage(e: Throwable): String
}

//默认异常消息转换处理器
internal val defaultExceptionMessageTransformer = object : ExceptionMessageTransformer {
    override fun getTransformedMessage(e: Throwable): String {
        return e.message.orEmpty()
    }
}

/**
 * 扩展友好消息字段，用于将异常转换成对用户比较容易理解的信息。
 */
inline val Throwable.friendlyMessage: String
    get() = exceptionMessageTransformer.getTransformedMessage(
        this
    )

/**
 * 将异常堆栈信息转换成字符串
 */
inline val Throwable.stackTraceMessage: String
    get() {
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        printStackTrace(pw)
        return sw.toString()
    }









