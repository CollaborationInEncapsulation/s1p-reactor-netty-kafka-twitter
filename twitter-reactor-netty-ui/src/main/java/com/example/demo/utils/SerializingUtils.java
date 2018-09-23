package com.example.demo.utils;

import java.io.ByteArrayOutputStream;
import java.util.function.Function;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class SerializingUtils {
    static final ObjectMapper MAPPER = new ObjectMapper();

    static ByteBuf toByteBuffer(Object any) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write("data: ".getBytes());
            MAPPER.writeValue(out, any);
            out.write("\n\n".getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ByteBufAllocator.DEFAULT.buffer().writeBytes(out.toByteArray());
    }
}
