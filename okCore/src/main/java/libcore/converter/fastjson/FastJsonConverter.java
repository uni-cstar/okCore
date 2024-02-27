package libcore.converter.fastjson;

import com.alibaba.fastjson.JSON;
import libcore.converter.JsonConverter;
import libcore.lang.StringUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Lucio on 2021/7/21.
 */
public class FastJsonConverter implements JsonConverter {

    @Nullable
    @Override
    public <T> T toObject(@Nullable String json, Class<T> clazz) {
        if (StringUtils.isNullOrEmpty(json))
            return null;
        return JSON.parseObject(json, clazz);
    }

    @Override
    public <T> @Nullable T toObject(@Nullable String json, Type type) {
        return JSON.parseObject(json,type);
    }

    @Nullable
    @Override
    public <T> List<T> toObjectList(@Nullable String json, Class<T> tClass) {
        if (StringUtils.isNullOrEmpty(json))
            return null;
        return JSON.parseArray(json, tClass);
    }

    @Override
    public @Nullable <T> String toJson(@Nullable T obj, Class<T> tClass) {
        return toJson(obj);
    }

    @Override
    public @Nullable <T> String toJson(@Nullable T obj, @NotNull Type type) {
        return toJson(obj);
    }

    @Nullable
    public String toJson(@Nullable Object obj)  {
        if (obj == null)
            return null;
        return JSON.toJSONString(obj);
    }

}
