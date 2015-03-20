package de.johni0702.replaystudio.studio.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import org.spacehq.packetlib.Session;
import org.spacehq.packetlib.tcp.TcpPacketCompression;

import java.util.List;

public class StudioCompression extends TcpPacketCompression {

    public StudioCompression(Session session) {
        super(session);
    }

    @Override
    @SneakyThrows
    public void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) {
        super.decode(ctx, buf, out);
    }

}