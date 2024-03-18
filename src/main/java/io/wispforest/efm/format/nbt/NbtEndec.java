package io.wispforest.efm.format.nbt;

import com.google.common.io.ByteStreams;
import io.wispforest.endec.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtTagSizeTracker;

import java.io.IOException;

public final class NbtEndec implements Endec<NbtElement> {

    public static final Endec<NbtElement> ELEMENT = new NbtEndec();
    public static final Endec<NbtCompound> COMPOUND = new NbtEndec().xmap(NbtCompound.class::cast, compound -> compound);

    private NbtEndec() {}

    @Override
    public void encode(Serializer<?> serializer, NbtElement value) {
        if (serializer.has(DataToken.SELF_DESCRIBING)) {
            NbtDeserializer.of(value).readAny(serializer);
            return;
        }

        try {
            var output = ByteStreams.newDataOutput();
            NbtIo.write(value, output);

            serializer.writeBytes(output.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Failed to encode binary NBT in NbtEndec", e);
        }
    }

    @Override
    public NbtElement decode(Deserializer<?> deserializer) {
        if (deserializer instanceof SelfDescribedDeserializer<?> selfDescribedDeserializer) {
            var nbt = NbtSerializer.of();
            selfDescribedDeserializer.readAny(nbt);

            return nbt.result();
        }

        try {
            return NbtIo.read(ByteStreams.newDataInput(deserializer.readBytes()), NbtTagSizeTracker.EMPTY);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse binary NBT in NbtEndec", e);
        }
    }
}
