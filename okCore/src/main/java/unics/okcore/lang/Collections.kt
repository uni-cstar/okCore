/**
 * Created by Lucio on 2020/12/13.
 */
@file:JvmName("CollectionsKt")

package unics.okcore.lang

import kotlin.collections.isNullOrEmpty as isNullOrEmptyLib

/**
 * For Java
 */
inline fun <T> Collection<T>?.isNullOrEmpty(): Boolean {
    return this == null || this.isEmpty()
}

inline fun <reified T> Collection<*>.loopWhen(block: (T) -> Unit) {
    forEach {
        if (it is T) {
            block.invoke(it)
        }
    }
}

inline fun <K> MutableMap<K, *>.removeAllByKey(block: (K) -> Boolean) {
    val it: MutableIterator<Map.Entry<K, *>> = this.iterator()
    while (it.hasNext()) {
        val (key, _) = it.next()
        if (block(key)) {
            it.remove()
        }
    }
}

inline fun <V> MutableMap<*, V>.removeAllByValue(filter: (V) -> Boolean) {
    val it: MutableIterator<Map.Entry<*, V>> = this.iterator()
    while (it.hasNext()) {
        val (_, value) = it.next()
        if (filter(value)) {
            it.remove()
        }
    }
}

inline fun <reified K> Map<*, *>.loopKeyWhen(block: (K) -> Unit) {
    for ((key) in this) {
        if (key is K) {
            block(key)
        }
    }
}

inline fun <reified V> Map<*, *>.loopValueWhen(block: (V) -> Unit) {
    for ((_, value) in this) {
        if (value is V) {
            block(value)
        }
    }
}

/**
 * 拼接所有非空元素
 */
inline fun <E> MutableCollection<E>.appendAllNotNulls(
    elements: Collection<E>?
): MutableCollection<E> = this.appendAll(elements, true)

/**
 * 拼接元素
 * @param filterNotNulls 是否只过滤非空元素，即是否不添加null元素
 */
inline fun <E> MutableCollection<E>.appendAll(
    elements: Collection<E>?,
    filterNotNulls: Boolean = false
): MutableCollection<E> {
    unics.okcore.lang.CollectionUtils.mergeTo(this, elements, filterNotNulls)
    return this
}

/**
 *
 * @return `true` if any of the specified elements was added to the collection, `false` if the collection was not modified.
 */
inline fun <E> MutableCollection<E>.addAllNotNulls(
    elements: Collection<E>?
): Boolean {
    if (!elements.isNullOrEmptyLib()) {
        return this.addAll(elements)
    }
    return false
}

inline fun <E> Collection<E>?.areItemsEqual(other: Collection<E>?) =
    unics.okcore.lang.CollectionUtils.areItemsEqual(this, other)

/**
 * 累加 elements.hashCode()
 */
inline fun <T> Collection<T>?.hashCodeAccumulate(): Int {
    var result = 0
    this?.forEach {
        result = 31 * result + it.hashCode()
    }
    return result
}