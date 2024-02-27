@file:JvmName("LibCoreKt")
@file:JvmMultifileClass

//notice 不要更改包名
package libcore

import libcore.exception.ExceptionHandler
import libcore.exception.ExceptionMessageTransformer
import libcore.exception.defaultExceptionHandler
import libcore.exception.defaultExceptionMessageTransformer


/**
 * 设置异常消息处理器
 */
var exceptionHandler: ExceptionHandler = defaultExceptionHandler

/**
 * 异常消息转换器
 */
var exceptionMessageTransformer: ExceptionMessageTransformer =
    defaultExceptionMessageTransformer
