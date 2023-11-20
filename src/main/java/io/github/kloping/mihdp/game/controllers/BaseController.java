package io.github.kloping.mihdp.game.controllers;

import io.github.kloping.MySpringTool.annotations.Action;
import io.github.kloping.MySpringTool.annotations.AutoStand;
import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.MySpringTool.annotations.DefAction;
import io.github.kloping.MySpringTool.entity.interfaces.Runner;
import io.github.kloping.MySpringTool.exceptions.NoRunException;
import io.github.kloping.MySpringTool.interfaces.QueueExecutor;
import io.github.kloping.mihdp.ex.GeneralData;
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

    @Override
    public void run(Method method, Object t, Object[] objects) throws NoRunException {
        if (t == null) return;
        Class cla = method.getDeclaringClass();
        GameClient client = (GameClient) objects[3];
        ReqDataPack reqDataPack = (ReqDataPack) objects[2];
        ResDataPack pack = new ResDataPack();
        pack.setId(reqDataPack.getId());
        pack.setAction(reqDataPack.getAction());
        if (t instanceof GeneralData) {
            pack.setData((GeneralData) t);
        } else {
            pack.setData(new GeneralData.ResDataText(t.toString()));
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

    @Action("get")
    public Object getInfo(ReqDataPack dataPack) {
        return dataPack.getSender_id() + " 测试通过";
    }

    @DefAction
    public void intercept0(ReqDataPack pack) {
        System.out.println("def action: " + pack.getAction());
    }
}
