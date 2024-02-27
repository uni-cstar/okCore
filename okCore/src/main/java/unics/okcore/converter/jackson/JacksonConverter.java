package unics.okcore.converter.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import unics.okcore.converter.JsonConverter;

/**
 * Created by Lucio on 2021/7/21.
 */
public class JacksonConverter implements JsonConverter {

    private final ObjectMapper objectMapper;

    public JacksonConverter() {
        this(createPreferredObjectMapper());
    }

    public JacksonConverter(@NotNull ObjectMapper mapper) {
        this.objectMapper = mapper;
    }

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Nullable
    @Override
    public <T> T toObject(@Nullable String json, @NotNull Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new DeserializeException(e);
        }
    }

    @Override
    public <T> @Nullable T toObject(@Nullable String json, @NotNull Type type) {
        if (json == null || json.isEmpty()) return null;
        try {
            JavaType javaType = TypeFactory.defaultInstance().constructType(type);
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new DeserializeException(e);
        }
    }

    @Nullable
    @Override
    public <T> List<T> toObjectList(@Nullable String json, @NotNull Class<T> tClass) {
        if (json == null || json.isEmpty()) return null;
        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, tClass);
        try {
            return objectMapper.readValue(json, javaType);
        } catch (IOException e) {
            throw new DeserializeException(e);
        }
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
        if (obj == null) return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new SerializeException(e);
        }
    }

    /**
     * 创建推荐的ObjectMapper
     */
    @NotNull
    public static ObjectMapper createPreferredObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        //不存在属性字段时不发生错误
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    /**
     * 使用UTC时间格式
     */
    @NotNull
    public static ObjectMapper applyUTCDateFormat(@NotNull ObjectMapper om) {
        //Jackson本身就是使用的UTC时间格式，在序列化的时候是将时间序列化成时间戳，所以只需要关闭这个开关即可
        om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        //Jackson本身就是使用的UTC时间格式,所以不用在设置对应的dateformat了，设置format的时候要注意时区问题
//        om.setDateFormat(DateUtils.getUTCDateTimeFormat());
        return om;
    }
}
