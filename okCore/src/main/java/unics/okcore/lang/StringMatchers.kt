@file:JvmName("StringsKt")
@file:JvmMultifileClass

/**
 * 正则
 * Copyright (C) 2018 Lucio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package unics.okcore.lang

import kotlin.text.isNullOrEmpty as libIsNullOrEmpty

/**
 * 是否仅包含数字
 */
inline fun CharSequence.isDigitsOnly(): Boolean = unics.okcore.lang.StringUtils.isDigitsOnly(this)

inline val String?.isIPV4: Boolean
    get() {
        if (this.libIsNullOrEmpty())
            return false
        return this.matches(Regex("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\$"))
    }

inline val String?.isValidIPV4: Boolean get() = this.isIPV4 && this != "0.0.0.0"

/**
 * 是否是Mac地址
 */
inline val String?.isMacAddress: Boolean
    get() {
        if (this.libIsNullOrEmpty())
            return false
        return this.matches(Regex("([A-Fa-f0-9]{2}[-,:]){5}[A-Fa-f0-9]{2}"))
    }

/**
 * 是否是版本数字；eg. like 1 or 1.0  or 3.1.25
 */
fun String.isVersionNumber(content: String): Boolean {
    return content.matches(Regex("[\\d.]+"))
}

/**
 * 是否是网络地址
 *
 * @return
 */
fun String?.isUrl(): Boolean {
    //TODO 此正则估计不是很完善，有待解决
    return this?.matches(
        "^(http://|https://)?((?:[A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/\\?\\:]?.*$".toRegex(
            RegexOption.IGNORE_CASE
        )
    )
        .orDefault(false)
}

/**
 * 是否是电话号码
 *
 * @param tel
 * @return
 */
fun String?.isMobileNumber(): Boolean {
    return this?.matches(
        "^[1][3,4,5,6,7,8][0-9]{9}$".toRegex()
    )
        .orDefault(false)
}