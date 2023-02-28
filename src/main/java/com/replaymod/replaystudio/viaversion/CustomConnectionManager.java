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

package com.replaymod.replaystudio.viaversion;

import com.viaversion.viaversion.api.connection.ConnectionManager;
import com.viaversion.viaversion.api.connection.UserConnection;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CustomConnectionManager implements ConnectionManager {

    @Override
    public boolean isClientConnected(UUID uuid) {
        return getConnectedClient(uuid) != null;
    }

    @Override
    public UserConnection getConnectedClient(UUID uuid) {
        UserConnection user = CustomViaAPI.INSTANCE.get().user();
        if (uuid.equals(user.getProtocolInfo().getUuid())) {
            return user;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public UUID getConnectedClientId(UserConnection userConnection) {
        return userConnection.getProtocolInfo().getUuid();
    }

    @Override
    public Set<UserConnection> getConnections() {
        return Collections.singleton(CustomViaAPI.INSTANCE.get().user());
    }

    @Override
    public Map<UUID, UserConnection> getConnectedClients() {
        UserConnection user = CustomViaAPI.INSTANCE.get().user();
        UUID uuid = user.getProtocolInfo().getUuid();
        return Collections.singletonMap(uuid, user);
    }

    @Override
    public void onLoginSuccess(UserConnection userConnection) {
    }

    @Override
    public void onDisconnect(UserConnection userConnection) {
    }
}
