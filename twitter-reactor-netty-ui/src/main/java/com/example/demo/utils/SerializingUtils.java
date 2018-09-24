package com.example.demo.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

final class SerializingUtils {
    static final ObjectMapper MAPPER = new ObjectMapper();

    static ByteBuf toByteBuffer(Object any) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("data: ".getBytes(Charset.defaultCharset()));
            MAPPER.writeValue(out, any);
            out.write("\n\n".getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ByteBufAllocator.DEFAULT
                               .buffer()
                               .writeBytes(out.toByteArray());
    }
}
