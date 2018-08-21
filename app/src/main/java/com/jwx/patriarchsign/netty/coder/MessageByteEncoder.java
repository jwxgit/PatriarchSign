package com.jwx.patriarchsign.netty.coder;

import android.text.TextUtils;

import com.jwx.patriarchsign.utils.FormatUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

public class MessageByteEncoder extends MessageToByteEncoder<CharSequence> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, CharSequence msg, ByteBuf byteBuf) throws Exception {
        if (TextUtils.isEmpty(msg))
            return;
        // 报文长度
        byte[] bytes = msg.toString().getBytes("UTF-8");
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }

}
