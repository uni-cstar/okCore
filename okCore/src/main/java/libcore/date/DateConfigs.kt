@file:JvmName("DatesKt")
@file:JvmMultifileClass

//不要更改包名
package libcore.date

import java.util.*

/**
 * 友好时间格式化器
 */
var friendlyDateFormat: DateFormatUseCase = FriendlyDateFormatUseCase()


var globalLocale: Locale = Locale.getDefault()
    set(value) {
        field = value
    }