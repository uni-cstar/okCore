package unics.okcore.converter;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Lucio on 2021/7/21.
 */
public interface JsonConverter {

    @Nullable <T> T toObject(@Nullable String json, @NotNull Class<T> clazz);

    @Nullable <T> T toObject(@Nullable String json, @NotNull Type type);

    /**
     * 转换成列表
     *
     * @param tClass item class类型，比如List<String>,则 tClass为String.class
     * @param <T>    List item的class类型
     */
    @Nullable <T> List<T> toObjectList(@Nullable String json, @NotNull Class<T> tClass);

    @Nullable <T> String toJson(@Nullable T obj, @NotNull Class<T> tClass);

    @Nullable <T> String toJson(@Nullable T obj, @NotNull Type type);
}
