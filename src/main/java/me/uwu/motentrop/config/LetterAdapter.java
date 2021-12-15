package me.uwu.motentrop.config;

import com.google.gson.*;
import me.uwu.motentrop.api.struct.game.Letter;
import me.uwu.motentrop.api.struct.math.Vec2i;

import java.lang.reflect.Type;

public class LetterAdapter implements JsonSerializer<Letter>, JsonDeserializer<Letter> {
    @Override
    public JsonElement serialize(Letter src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject object = new JsonObject();
        object.add("position", context.serialize(src.getPosition()));
        object.add("character", context.serialize(src.getCharacter()));
        return object;
    }

    @SuppressWarnings("deprecation")
    @Override
    public Letter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        JsonElement position = object.get("position");
        JsonElement character = object.get("character");
        return new Letter(context.deserialize(position, Vec2i.class), character.getAsCharacter());
    }
}
