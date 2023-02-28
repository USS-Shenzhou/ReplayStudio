/*
 * Copyright (c) 2021
 *
 * This file is part of ReplayStudio.
 *
 * ReplayStudio is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ReplayStudio is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ReplayStudio.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.replaymod.replaystudio.util;

import io.netty.buffer.ByteBuf;
import com.github.steveice10.packetlib.tcp.io.ByteBufNetOutput;

public class ByteBufExtNetOutput extends ByteBufNetOutput {
    private final ByteBuf buf;

    public ByteBufExtNetOutput(ByteBuf buf) {
        super(buf);

        this.buf = buf;
    }

    public ByteBuf getBuf() {
        return buf;
    }
}
