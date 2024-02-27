/**
 * Created by Lucio on 2021/7/22.
 */
@file:JvmName("ConvertersKt")

package unics.okcore.converter

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import unics.okcore.converter.gson.GsonConverter
import unics.okcore.converter.gson.TypeBuilder
import unics.okcore.converter.gson.LocalJsonField
import unics.okcore.converter.jackson.JacksonConverter
import java.lang.reflect.Type

inline fun <reified T> T?.toJson(): String? {
    return Converters.toJson(this, T::class.java)
}

inline fun <reified T> T?.toJson(type: Type): String? {
    return Converters.toJson(this, type)
}

inline fun <reified T> List<T>?.toJson(): String? {
//        ParameterizedTypeImpl type = new ParameterizedTypeImpl(List.class, new Class<?>[]{clazz}, null);
//        return this.mGson.fromJson(json, type);
    val type = TypeBuilder
        .newInstance(MutableList::class.java)
        .addTypeParam(T::class.java)
        .build()
    return Converters.toJson(this, type)
}

inline fun <reified T> String?.toObject(): T? {
    return Converters.toObject(this, T::class.java)
}

inline fun <reified T> String?.toObject(type: Type): T? {
    return Converters.toObject(this, type)
}

inline fun <reified T> String?.toObjectList(): List<T> {
    if (this.isNullOrEmpty())
        return mutableListOf()
    val convert = Converters.getJsonConverter()
    return if (convert is GsonConverter && convert.isInlineOptimization) {
        //gson不支持泛型方法，所以必须在内联方法中实现；
        val type: Type = object : TypeToken<List<T>>() {}.type
        convert.gson.fromJson(this, type)
    } else {
        convert.toObjectList(this, T::class.java)
    } ?: mutableListOf()
}

/**
 * 使用UTC时间格式
 */
inline fun ObjectMapper.applyUTCDateTimeFormat(): ObjectMapper {
    return JacksonConverter.applyUTCDateFormat(this)
}

/**
 * 使用UTC时间格式
 */
inline fun GsonBuilder.applyUTCDateTimeFormat(): GsonBuilder {
    return GsonConverter.applyUTCDateFormat(this)
}

/**
 * 启用[LocalJsonField]过滤策略
 */
inline fun GsonBuilder.applyLocalFieldStrategy(): GsonBuilder {
    return GsonConverter.applyLocalFieldStrategy(this)
}

