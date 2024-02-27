package unicstar.droid.core

import unicstar.droid.core.util.log.UCSLog

/**
 * 是否是调试模式
 */
var debuggable: Boolean
    get() = libcore.debuggable
    set(value) {
        libcore.debuggable = value
    }

/**
 * 默认的Log
 */
inline var LOGGER: UCSLog
    get() = unicstar.droid.core.util.log.LOGGER
    set(value) {
        unicstar.droid.core.util.log.LOGGER = value
    }

/**
 * 日志等级
 */
inline var logLevel: Int
    @LogLevel
    get() = unicstar.droid.core.util.log.logLevel
    set(@LogLevel value) {
        unicstar.droid.core.util.log.logLevel = value
    }