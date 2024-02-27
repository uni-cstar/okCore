@file:JvmName("OkCoreKt")
@file:JvmMultifileClass

//notice 不要更改包名
package unics.okcore

import unics.okcore.exception.ExceptionHandler
import unics.okcore.exception.ExceptionMessageTransformer
import unics.okcore.exception.defaultExceptionHandler
import unics.okcore.exception.defaultExceptionMessageTransformer


/**
 * 设置异常消息处理器
 */
var exceptionHandler: ExceptionHandler = defaultExceptionHandler

/**
 * 异常消息转换器
 */
var exceptionMessageTransformer: ExceptionMessageTransformer =
    defaultExceptionMessageTransformer
