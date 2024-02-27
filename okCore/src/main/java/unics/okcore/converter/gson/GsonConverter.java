package unics.okcore.converter.gson;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

import unics.okcore.converter.JsonConverter;
import unics.okcore.date.DateUtils;

/**
 * Created by Lucio on 2021/7/21.
 */
public class GsonConverter implements JsonConverter {

    private final Gson mGson;

    /**
     * 是否内联优化：即如果是kotlin场景，在解析的时候直接使用{@link #mGson}进行解析，而不是用to(@{@link #toObjectList})之类的方法
     */
    private boolean isInlineOptimization = true;

    public GsonConverter() {
        this(newGsonBuilder().create());
    }

    public GsonConverter(Gson gson) {
        this.mGson = gson;
    }

    public Gson getGson() {
        return mGson;
    }

    public boolean isInlineOptimization() {
        return isInlineOptimization;
    }

    public void setInlineOptimization(boolean inlineOptimization) {
        isInlineOptimization = inlineOptimization;
    }

    @Nullable
    @Override
    public <T> T toObject(@Nullable String json, @NotNull Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        return this.mGson.fromJson(json, clazz);
    }

    @Override
    public <T> @Nullable T toObject(@Nullable String json, @NotNull Type type) {
        if (json == null || json.isEmpty()) return null;
        return this.mGson.fromJson(json, type);
    }

    @Nullable
    @Override
    public <T> List<T> toObjectList(@Nullable String json, @NotNull Class<T> tClass) {
//        ParameterizedTypeImpl type = new ParameterizedTypeImpl(List.class, new Class<?>[]{clazz}, null);
//        return this.mGson.fromJson(json, type);
        Type type = TypeBuilder
                .newInstance(List.class)
                .addTypeParam(tClass)
                .build();
        return this.mGson.fromJson(json, type);


        //        /*无法使用下面代码完成反序列化，如果是直接使用而不是封装在方法中是可以的
//        * 具体参考：https://www.jianshu.com/p/d62c2be60617*/
////        if (json.isNullOrEmpty())
////            return null
////        val type: Type = object : TypeToken<List<T>>() {}.type
////        return gson.fromJson(json, type)

//        该方法也可以，但是效率不高
        ////        //Gson不能解析泛型类型，只能用下面方法折中处理：gson可以支持内联的泛型方法
////        if (json.isNullOrEmpty())
////            return null
////        //这种方法则：转换成jsonArray之后再将每一个element转换成对应的object class
////        val results = mutableListOf<T>()
////
////        val arry = JsonParser().parse(json).asJsonArray
////        arry.forEach {
////            results.add(gson.fromJson(it, clazz))
////        }
////        return results
    }

    @Override
    public @Nullable <T> String toJson(@Nullable T obj, @NotNull Class<T> tClass) {
        return toJson(obj);
    }

    @Override
    public @Nullable <T> String toJson(@Nullable T obj, @NotNull Type type) {
        return toJson(obj);
    }

    @Nullable
    public String toJson(@Nullable Object obj) {
        if (obj == null)
            return null;
        return this.mGson.toJson(obj);
    }

    public static GsonBuilder newGsonBuilder() {
        return new GsonBuilder();
    }

    /**
     * 应用本地字段策略
     */
    public static GsonBuilder applyLocalFieldStrategy(GsonBuilder builder) {
        builder.addDeserializationExclusionStrategy(new LocalJsonFieldDeserializationExclusionStrategy())
                .addSerializationExclusionStrategy(new LocalJsonFieldSerializationExclusionStrategy());
        return builder;
    }

    /**
     * 使用UTC日期格式
     */
    public static GsonBuilder applyUTCDateFormat(GsonBuilder builder) {
        builder.setDateFormat(DateUtils.UTC_DATETIME_PATTERN);
        return builder;
    }

    public static GsonBuilder newStrategyGsonBuilder() {
        return applyLocalFieldStrategy(newGsonBuilder());
    }

}
