package unics.okcore.converter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

import unics.okcore.converter.gson.GsonConverter;
import unics.okcore.converter.jackson.JacksonConverter;
import unics.okcore.lang.ClassesKt;

/**
 * Created by Lucio on 2021/7/21.
 */
public class Converters {

    private static JsonConverter mJsonConverter;

    private Converters() {
    }

    static {
        try {
            //初始化默认JsonConverter，优先使用Jackson(Kotlin版本)
            if (ClassesKt.isClassExists("com.fasterxml.jackson.module.kotlin.KotlinModule")) {
                System.out.println("Converters：使用JacksonKotlinConverter");
                mJsonConverter = new JacksonConverter();
            } else if (ClassesKt.isClassExists("com.fasterxml.jackson.databind.ObjectMapper")) {
                System.out.println("Converters：使用JacksonConverter");
                mJsonConverter = new JacksonConverter();
            } else if (ClassesKt.isClassExists("com.google.gson.Gson")) {
                System.out.println("Converters：使用GsonConverter");
                mJsonConverter = new GsonConverter();
            }
//            else if (ClassesKt.isClassExists("com.alibaba.fastjson.JSON")) {
//                System.out.println("Converters：使用FastJsonConverter");
//                mJsonConverter = new FastJsonConverter();
//            }
//
            else {
                System.out.println("Converters：null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static void setJsonConverter(@NotNull JsonConverter converter) {
        mJsonConverter = converter;
    }

    public static JsonConverter getJsonConverter() {
        return mJsonConverter;
    }

    @Nullable
    public static <T> T toObject(@Nullable String json, @NotNull Class<T> clazz) {
        return mJsonConverter.toObject(json, clazz);
    }

    @Nullable
    public static <T> List<T> toObjectList(@Nullable String json, @NotNull Class<T> clazz) {
        return mJsonConverter.toObjectList(json, clazz);
    }

    public static <T> T toObject(@Nullable String json, @NotNull Type type) {
        return mJsonConverter.toObject(json, type);
    }

    @Nullable
    public static String toJson(@Nullable Object obj, @NotNull Class<?> tClass) {
        return mJsonConverter.toJson(obj, tClass);
    }

    @Nullable
    public static String toJson(@Nullable Object obj, @NotNull Type type) {
        return mJsonConverter.toJson(obj, type);
    }

}
