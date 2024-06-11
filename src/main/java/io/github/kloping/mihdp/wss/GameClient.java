package io.github.kloping.mihdp.wss;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import io.github.kloping.mihdp.MihDpMain;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.wss.data.BasePack;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.mihdp.wss.data.ResDataPack;
import org.java_websocket.WebSocket;

/**
 * @author github.kloping
 */
public abstract class GameClient {
    public static String PASS_WORD = null;
    public static final String TRANS_ACTION = "msg";
    public static final String ODATA_KEY = "odata-key";

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
        data.set(this);
        GeneralData resData = gson.fromJson(data.getContent(), GeneralData.TYPE_TOKEN);
        data.getArgs().put(ODATA_KEY, resData);
        if (data.getAction().equals(TRANS_ACTION)) {
            data = BaseService.trnasActionOrNull(data);
        }
        MihDpMain.APPLICATION.executeMethod(data.getBot_id(), data.getAction(), data, this, resData);
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

    public void send(ReqDataPack reqDataPack, GeneralData generalData) {
        ResDataPack pack = new ResDataPack();
        pack.setId(reqDataPack.getId());
        pack.setBot_id(reqDataPack.getBot_id());
        pack.setEnv_type(reqDataPack.getEnv_type());
        pack.setEnv_id(reqDataPack.getEnv_id());
        pack.setAction(reqDataPack.getAction());
        pack.setTime(reqDataPack.getTime());
        pack.setArgs(reqDataPack.getArgs());
        pack.setData(generalData);
        webSocket.send(pack.toString());
    }

    public abstract void authed();

    public abstract void dispose();
}
