package fi.methics.musap.sdk.internal.util;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import android.util.Base64;

/**
 * Handle byte[] &lt;-> Base64String conversion.
 */
public class ByteaMarshaller extends TypeAdapter<byte[]> {

    @Override
    public byte[] read(final JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            return null;
        }
        String v = null;
        try {
            v = reader.nextString();
            return Base64.decode(v, Base64.NO_WRAP);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException("Value \"" + v + "\" is not a legal Base64 encoded value", e);
        }
    }
    @SuppressWarnings("resource")
    @Override
    public void write(JsonWriter writer, final byte[] value) throws IOException {
        if (value == null) {
            writer.nullValue();
        } else {
            writer.value(Base64.encodeToString(value, Base64.NO_WRAP));
        }
    }
}
