package io.vn.nguyenduck.nbt.io;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;

import io.vn.nguyenduck.nbt.tags.CompoundTag;

public class LEDataStreamTest {

    @Test
    public void testReadWrite() throws Exception {

        int version;
        CompoundTag<?> tag;

        try (var is = new LEDataInputStream(getClass().getClassLoader().getResourceAsStream("level.dat"));
             var nbtStream = new NBTInputStream(is)) {
            version = is.readInt();
            is.readInt();
            tag = (CompoundTag<?>) nbtStream.readTag();
        }

        byte[] data_write;

        try (var baos = new ByteArrayOutputStream();
             var os = new LEDataOutputStream(baos);
             var byteArrayStream = new ByteArrayOutputStream();
             var nbtStream = new NBTOutputStream(byteArrayStream)) {

            os.writeInt(version);
            nbtStream.writeTag(tag);
            os.writeInt(byteArrayStream.size());
            os.write(byteArrayStream.toByteArray());

            data_write = baos.toByteArray();
        }

        try (var is = getClass().getClassLoader().getResourceAsStream("level.dat")) {
            var data = new byte[is.available()];
            is.read(data);

            Assert.assertArrayEquals(data, data_write);
        }
    }
}