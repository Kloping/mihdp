package io.github.kloping.mihdp.wss;

import com.google.gson.Gson;
import io.github.kloping.MySpringTool.interfaces.Logger;
import org.fusesource.jansi.Ansi;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketServer extends WebSocketServer {
    public GameWebSocketServer(@Value("${wss.port:6034}") int port) {
        super(new InetSocketAddress(port));

    }

    @Autowired
    Logger logger;

    @Autowired
    Gson gson;

    public Map<String, GameClient> CLIENT_MAP = new ConcurrentHashMap<>();

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String remoteSocketAddress = conn.getRemoteSocketAddress().toString();
        logger.info("New connection from " + remoteSocketAddress);
        if (!CLIENT_MAP.containsKey(remoteSocketAddress)) {
            GameClient client = new GameClient(remoteSocketAddress, conn, gson) {
                @Override
                public void dispose() {
                    CLIENT_MAP.remove(id);
                }

                @Override
                public void authed() {
                    logger.waring("client " + id + " authed");
                }
            };
            CLIENT_MAP.put(remoteSocketAddress, client);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("Closed connection from " + conn.getRemoteSocketAddress());
        CLIENT_MAP.remove(conn.getRemoteSocketAddress().toString());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.info("Received message from " + conn.getRemoteSocketAddress() + ": " + message);
        GameClient client = CLIENT_MAP.get(conn.getRemoteSocketAddress().toString());
        if (client != null) {
            if (client.isAuthed()) {
                client.onMessage(message);
            } else client.auth(message);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.info("Error occurred on connection from " + conn.getRemoteSocketAddress() + ": " + ex.getMessage());
        CLIENT_MAP.remove(conn.getRemoteSocketAddress().toString());
    }

    @Override
    public void onStart() {
        logger.info("WebSocket server started on port:" + Ansi.ansi().fgRgb(Color.RED.getRGB()).a(getPort()).reset().toString());
    }
}