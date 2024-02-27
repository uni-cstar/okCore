@file:JvmName("OkCoreKt")
@file:JvmMultifileClass

//notice 不要更改包名
package unics.okcore

import unics.okcore.date.DateFormatUseCase
import unics.okcore.date.FriendlyDateFormatUseCase
import java.util.*

/**
 * 友好时间格式化器
 */
var friendlyDateFormat: DateFormatUseCase = FriendlyDateFormatUseCase()

var globalLocale: Locale = Locale.getDefault()
    set(value) {
        field = value
    }