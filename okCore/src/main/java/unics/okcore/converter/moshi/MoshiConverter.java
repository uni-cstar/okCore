package unics.okcore.converter.moshi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import unics.okcore.converter.JsonConverter;

/**
 * Created by Lucio on 2022/1/12.
 */
public class MoshiConverter implements JsonConverter {

    private final Moshi moshi;

    public MoshiConverter(Moshi moshi) {
        this.moshi = moshi;
    }

    public MoshiConverter() {
        this(createPreferredMoshi());
    }

    @Nullable
    @Override
    public <T> T toObject(@Nullable String json, @NotNull Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        try {
            return moshi.adapter(clazz).fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public <T> T toObject(@Nullable String json, @NotNull Type type) {
        if (json == null || json.isEmpty()) return null;
        try {
            JsonAdapter<T> ja = moshi.adapter(type);
            return ja.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> toObjectList(@Nullable String json, @NotNull Class<T> tClass) {
        if (json == null || json.isEmpty()) return null;
        try {
            Type type = Types.newParameterizedType(List.class, tClass);
            JsonAdapter<List<T>> ja = moshi.adapter(type);
            return ja.fromJson(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> String toJson(T obj, @NotNull Class<T> tClass) {
        return moshi.adapter(tClass).toJson(obj);
    }

    @Override
    public <T> String toJson(T obj, @NotNull Type type) {
        return moshi.adapter(type).toJson(obj);
    }

    public static Moshi createPreferredMoshi() {
        return new Moshi.Builder().build();
    }

    public static Moshi.Builder applyUtcDate(Moshi.Builder builder, boolean isSupportTimestamp) {
        builder.add(new UTCDateJsonAdapter(isSupportTimestamp));
        return builder;
    }
}
