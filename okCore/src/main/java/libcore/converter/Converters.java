package libcore.converter;

import libcore.converter.gson.GsonConverter;
import libcore.converter.jackson.JacksonConverter;
import libcore.lang.ClassesKt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

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
    public static <T> T toObject(@Nullable String json, Class<T> clazz) {
        return mJsonConverter.toObject(json, clazz);
    }

    @Nullable
    public static <T> List<T> toObjectList(@Nullable String json, Class<T> clazz) {
        return mJsonConverter.toObjectList(json, clazz);
    }

    public static <T> T toObject(@Nullable String json, Type type) {
        return mJsonConverter.toObject(json, type);
    }

    /**
     * 不建议直接使用该方法，该方法会直接使用obj.getClass()作为序列化的类型
     * todo 待测试
     * @param obj
     * @return
     */
    @Nullable
    public static String toJson(@Nullable Object obj) {
        if(obj == null)
            return null;
        return mJsonConverter.toJson(obj, obj.getClass());
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
