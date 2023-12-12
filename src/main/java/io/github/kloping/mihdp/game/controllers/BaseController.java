package io.github.kloping.mihdp.game.controllers;

import io.github.kloping.MySpringTool.annotations.*;
import io.github.kloping.MySpringTool.entity.interfaces.Runner;
import io.github.kloping.MySpringTool.exceptions.NoRunException;
import io.github.kloping.MySpringTool.interfaces.QueueExecutor;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.utils.NumberSelector;
import io.github.kloping.mihdp.game.utils.SelectorInvoke;
import io.github.kloping.mihdp.mapper.ConfigMapper;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.mihdp.wss.data.ResDataPack;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.Method;

/**
 * @author github.kloping
 */
@Controller
public class BaseController implements Runner {
    public BaseController(QueueExecutor queueExecutor) {
        queueExecutor.setAfter(this);
    }

    @AutoStand
    DrawController drawController;

    @Override
    public void run(Method method, Object t, Object[] objects) throws NoRunException {
        if (t == null) return;
        GameClient client = (GameClient) objects[3];
        ReqDataPack reqDataPack = (ReqDataPack) objects[2];
        ResDataPack pack = new ResDataPack();
        pack.setId(reqDataPack.getId());
        pack.setBot_id(reqDataPack.getBot_id());
        pack.setEnv_type(reqDataPack.getEnv_type());
        pack.setEnv_id(reqDataPack.getEnv_id());
        pack.setAction(reqDataPack.getAction());
        pack.setTime(reqDataPack.getTime());
        pack.setArgs(reqDataPack.getArgs());
        if (t instanceof GeneralData) {
            pack.setData((GeneralData) t);
        } else {
            pack.setData(new GeneralData.ResDataText(t.toString()));
        }
        if (reqDataPack.getArgValue("draw").toString().equals("true")) {
            GeneralData data = drawController.draw(pack);
            if (data != null) pack.setData(data);
        }
        try {
            client.webSocket.send(pack.toString());
        } catch (Exception e) {
            if (e instanceof WebsocketNotConnectedException) {
                System.err.println(e.getMessage());
            } else e.printStackTrace();
        }
    }

    @AutoStand
    ConfigMapper configMapper;

    @Action("test")
    public Object getInfo(ReqDataPack dataPack, GameClient client) {
        return dataPack.getSender_id() + " 测试通过";
    }

    @Action("s\\d")
    public Object select(@AllMess String msg, ReqDataPack pack) {
        return selectN(Integer.valueOf(msg.substring(1)), pack);
    }

    private static Object selectN(Integer i, ReqDataPack pack) {
        String sid = pack.getSender_id();
        NumberSelector selector = NumberSelector.DATA_MAP.get(sid);
        if (selector == null) return null;
        SelectorInvoke invoke = selector.get(i);
        Object o = invoke.invoke(pack);
        if (o != null) {
            if (NumberSelector.DATA_MAP.containsKey(sid)) {
                NumberSelector selectorNext = selector.getNext();
                if (selectorNext != null) NumberSelector.DATA_MAP.put(sid, selectorNext);
                else NumberSelector.DATA_MAP.remove(sid);
            }
        }
        return o;
    }

    @DefAction
    public void intercept0(ReqDataPack pack) {
        System.out.println("def action: " + pack.getAction());
    }
}
