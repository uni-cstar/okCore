@file:JvmName("FilesKt")

package unics.okcore.io

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.io.deleteRecursively as deleteRecursivelyLib

inline fun File.deleteSafely(): Boolean {
    return if (this.isDirectory)
        deleteRecursively()
    else delete()
}

/**
 * 递归删除目录下的所有文件及子目录下所有文件
 * 如果目录文件可能比较多，也可以尝试放在线程中去执行
 *
 * @param dir 将要删除的文件目录
 * @return boolean Returns "true" if all deletions were successful.
 * If a deletion fails, the method stops attempting to
 * delete and returns "false".
 *
 * for Java
 */
inline fun File.deleteRecursively(): Boolean {
    return deleteRecursivelyLib()
}

/**
 * 获取文件的md5
 */
@JvmOverloads
inline fun fileMd5(path: String, bufferSize: Int = FilesUtil.DEFAULT_BUFFER_SIZE): String {
    return FilesUtil.md5(path, bufferSize)
}

/**
 * 获取文件的md5
 */
@JvmOverloads
inline fun File.md5(bufferSize: Int = FilesUtil.DEFAULT_BUFFER_SIZE): String {
    return fileMd5(this.absolutePath, bufferSize)
}

/**
 * 根据byte数组，生成文件
 * @param bfile
 * @param filePath 文件路径
 * @param fileName 文件名
 */
fun File(bfile: ByteArray, filePath: String, fileName: String): File {
    var bos: BufferedOutputStream? = null
    var fos: FileOutputStream? = null
    val file: File
    try {
        val dir = File(filePath)
        if (!dir.exists() && dir.isDirectory) { //判断文件目录是否存在
            dir.mkdirs()
        }
        file = File("${filePath}${File.pathSeparator}${fileName}")
        fos = FileOutputStream(file)
        bos = BufferedOutputStream(fos)
        bos.write(bfile)
        return file
    } finally {
        try {
            bos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}