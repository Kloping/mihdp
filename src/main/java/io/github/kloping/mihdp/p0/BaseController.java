package io.github.kloping.mihdp.p0;

import io.github.kloping.MySpringTool.PartUtils;
import io.github.kloping.MySpringTool.annotations.*;
import io.github.kloping.MySpringTool.entity.interfaces.Runner;
import io.github.kloping.MySpringTool.entity.interfaces.RunnerOnThrows;
import io.github.kloping.MySpringTool.exceptions.NoRunException;
import io.github.kloping.MySpringTool.interfaces.Logger;
import io.github.kloping.MySpringTool.interfaces.QueueExecutor;
import io.github.kloping.mihdp.ex.GeneralData;
import io.github.kloping.mihdp.game.v.RedisSource;
import io.github.kloping.mihdp.mapper.ConfigMapper;
import io.github.kloping.mihdp.p0.services.BaseService;
import io.github.kloping.mihdp.p0.utils.NumberSelector;
import io.github.kloping.mihdp.p0.utils.SelectorInvoke;
import io.github.kloping.mihdp.wss.GameClient;
import io.github.kloping.mihdp.wss.data.ReqDataPack;
import io.github.kloping.mihdp.wss.data.ResDataPack;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import java.lang.reflect.Method;

/**
 * @author github.kloping
 */
@Controller
public class BaseController implements Runner, RunnerOnThrows {
    @AutoStand
    DrawController drawController;

    public BaseController(QueueExecutor queueExecutor) {
        queueExecutor.setAfter(this);
        queueExecutor.setException(this);
    }

    private void dispose(Object t, Object[] args) {
        if (t == null) return;
        GameClient client = (GameClient) args[3];
        ReqDataPack reqDataPack = (ReqDataPack) args[2];
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
        if (reqDataPack.containsArgs("draw")) {
            if (reqDataPack.getArgValue("draw").toString().equals("true")) {
                GeneralData data = drawController.draw(pack);
                if (data != null) pack.setData(data);
            }
        }
        try {
            client.webSocket.send(pack.toString());
        } catch (Exception e) {
            if (e instanceof WebsocketNotConnectedException) {
                System.err.println(e.getMessage());
            } else e.printStackTrace();
        }
    }

    @Override
    public void onThrows(Throwable throwable, Object t, Object... args) {
        if (throwable instanceof NoRunException) {
            dispose(t, args);
        } else logger.error(PartUtils.getExceptionLine(throwable));
    }

    @AutoStand
    Logger logger;
    @AutoStand
    ConfigMapper configMapper;

    @Override
    public void run(Method method, Object t, Object[] objects) throws NoRunException {
        dispose(t, objects);
    }

    {
        BaseService.MSG2ACTION.put("测试", "test");
    }

    @AutoStand
    RedisSource redisSource;

    @Action("test")
    public Object getInfo(ReqDataPack dataPack, GameClient client) {
        return GeneralData.GeneralDataBuilder.create("测试按钮发送").append(1, "落日神弓").append(2, "神空剑").build();
//        return redisSource.id2shopMax.setValue("testKey",1234);
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
