package org.delivery.storeadmin.domain.sse.connection;

import lombok.extern.slf4j.Slf4j;
import org.delivery.storeadmin.domain.sse.connection.ifs.ConnectionPoolIfs;
import org.delivery.storeadmin.domain.sse.connection.model.UserSseConnection;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SseConnectionPool implements ConnectionPoolIfs<String, UserSseConnection> {

    private static final Map<String, UserSseConnection> connections = new ConcurrentHashMap<>();

    @Override
    public void addSession(String uniqueKey, UserSseConnection session) {
        connections.put(uniqueKey, session);
    }

    @Override
    public UserSseConnection getSession(String uniqueKey) {
        return connections.get(uniqueKey);
    }

    @Override
    public void onCompletionCallback(UserSseConnection session) {
        log.info("call back connection pool completion : {}", session);
        connections.remove(session.getUniqueKey());
    }
}
