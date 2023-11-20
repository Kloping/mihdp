package io.github.kloping.mihdp.wss;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import io.github.kloping.mihdp.Main;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.services.BaseService;
import io.github.kloping.mihdp.wss.data.BasePack;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import org.java_websocket.WebSocket;

/**
 * @author github.kloping
 */
public abstract class GameClient {
    public static String PASS_WORD = null;
    public static final String TRANS_ACTION = "msg";

    public final String id;
    public final WebSocket webSocket;
    private final Gson gson;
    private String passwd;
    private Boolean authed = false;

    public GameClient(String id, WebSocket socket, Gson gson) {
        this.id = id;
        this.webSocket = socket;
        this.gson = gson;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void onMessage(String msg) {
        ReqDataPack data = JSON.parseObject(msg, ReqDataPack.class);
        if (data == null) return;
        GeneralData resData = gson.fromJson(data.getContent(), GeneralData.TYPE_TOKEN);
        if (data.getAction().equals(TRANS_ACTION)) {
            String action = BaseService.trnasActionOrNull(resData);
            if (action != null) {
                System.err.println("conversion action: " + action);
                data.setAction(action);
            }
        }
        Main.APPLICATION.executeMethod(data.getBot_id(), data.getAction(), data, this, resData);
    }

    private Long auth_cd = 0L;
    private Integer auth_times = 0;

    public void auth(String message) {
        if (System.currentTimeMillis() < auth_cd) {
            webSocket.send(new BasePack("event_auth", "auth failed;Verify that it is cooling(MAX 30s)").toString());
        } else if (message.trim().equals(PASS_WORD)) {
            authed = true;
            webSocket.send(new BasePack("event_auth", "auth success").toString());
            authed();
        } else {
            auth_cd = System.currentTimeMillis() + 30000;
            webSocket.send(new BasePack("event_auth", "auth failed;Wait 30 seconds and try again").toString());
            auth_times++;
            if (auth_times >= 3) {
                webSocket.send(new BasePack("event_auth", "The maximum number of verifications").toString());
                webSocket.close();
                dispose();
            }
        }
    }

    public abstract void authed();

    public abstract void dispose();
}
