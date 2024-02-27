package unics.okcore.converter.fastjson;

import com.alibaba.fastjson.JSON;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

import unics.okcore.converter.JsonConverter;

/**
 * Created by Lucio on 2021/7/21.
 */
public class FastJsonConverter implements JsonConverter {

    @Nullable
    @Override
    public <T> T toObject(@Nullable String json, @NotNull Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        return JSON.parseObject(json, clazz);
    }

    @Override
    public <T> @Nullable T toObject(@Nullable String json, @NotNull Type type) {
        return JSON.parseObject(json, type);
    }

    @Nullable
    @Override
    public <T> List<T> toObjectList(@Nullable String json, @NotNull Class<T> tClass) {
        if (json == null || json.isEmpty()) return null;
        return JSON.parseArray(json, tClass);
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
        return JSON.toJSONString(obj);
    }

}
