package me.uwu.motentrop.config;

import com.google.gson.*;
import me.uwu.motentrop.api.struct.math.Vec2i;

import java.lang.reflect.Type;

public class Vec2iAdapter implements JsonSerializer<Vec2i>, JsonDeserializer<Vec2i> {
    @Override
    public JsonElement serialize(Vec2i src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(new int[]{src.getX(), src.getY()});
    }

    @Override
    public Vec2i deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Vec2i.valueOf(json.getAsJsonArray().get(0).getAsInt(), json.getAsJsonArray().get(1).getAsInt());
    }
}
