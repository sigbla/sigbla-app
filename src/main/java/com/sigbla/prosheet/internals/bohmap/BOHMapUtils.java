package com.sigbla.prosheet.internals.bohmap;

import java.nio.ByteBuffer;

public final class BOHMapUtils {
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final int LONG_SIZE = Long.SIZE / Byte.SIZE;

    public static final int tableSizeFor(int cap) {
        int n = -1 >>> Integer.numberOfLeadingZeros(cap - 1);
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    public static final Binary fromLong(long value) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[LONG_SIZE]);

        buffer.putLong(value);

        return new Binary(buffer.array());
    }

    public static final long toLong(Binary value) {
        return ByteBuffer.wrap(value.getValue()).getLong();
    }
}
