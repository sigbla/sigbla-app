package sigbla.app.internals.serialization;

import java.nio.ByteBuffer;

public final class SerializationUtils {
    public static final int LONG_SIZE = Long.BYTES;
    public static final int INT_SIZE = Integer.BYTES;

    public static final byte[] fromLong(long value) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[LONG_SIZE]);

        buffer.putLong(value);

        return buffer.array();
    }

    public static final long toLong(byte[] value) {
        return ByteBuffer.wrap(value).getLong();
    }

    public static final byte[] fromInt(int value) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[INT_SIZE]);

        buffer.putInt(value);

        return buffer.array();
    }

    public static final int toInt(byte[] value) {
        return ByteBuffer.wrap(value).getInt();
    }
}
