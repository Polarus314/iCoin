package org.example;

import com.google.gson.*;
import org.bouncycastle.util.encoders.Hex;

import java.lang.reflect.Type;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class PublicKeySerializer implements JsonSerializer<PublicKey>, JsonDeserializer<PublicKey> {
    @Override
    public JsonElement serialize(PublicKey src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Hex.toHexString(src.getEncoded()));
    }

    @Override
    public PublicKey deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            byte[] encodedKey = Hex.decode(json.getAsString());
            X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("ECDSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new JsonParseException(e);
        }
    }
}
