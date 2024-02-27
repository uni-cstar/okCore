package unics.okcore.lang

/**
 * 字节数组转16进制字符串
 */
fun ByteArray.toHex(separator: CharSequence = ""): String =
    joinToString(separator = separator) { eachByte -> "%02x".format(eachByte) }

/**
 * As of Kotlin 1.3, we can also use unsigned types to implement the same logic:
 */
@ExperimentalUnsignedTypes
fun ByteArray.toHex2(separator: CharSequence = ""): String =
    asUByteArray().joinToString(separator) { it.toString(radix = 16).padStart(2, '0') }

/**
 * 字节数组转16进制字符串
 * 循环方式
 */
fun ByteArray.toHex3(): String {
    val hexChars = "0123456789abcdef".toCharArray()
    val hex = CharArray(2 * this.size)
    this.forEachIndexed { i, byte ->
        val unsigned = 0xff and byte.toInt()
        hex[2 * i] = hexChars[unsigned / 16]
        hex[2 * i + 1] = hexChars[unsigned % 16]
    }
    return hex.joinToString("")
}