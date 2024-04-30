package org.example;

import com.google.gson.*;
import org.bouncycastle.util.encoders.Hex;

import java.lang.reflect.Type;

public class ByteArrayToHexStringAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    @Override
    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Hex.toHexString(src));
    }

    @Override
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return Hex.decode(json.getAsString());
    }
}

