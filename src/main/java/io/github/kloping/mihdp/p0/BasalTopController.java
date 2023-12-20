package io.github.kloping.mihdp.p0;

import io.github.kloping.MySpringTool.annotations.Controller;
import io.github.kloping.MySpringTool.entity.interfaces.Runner;
import io.github.kloping.MySpringTool.exceptions.NoRunException;
import io.github.kloping.MySpringTool.interfaces.QueueExecutor;

import java.lang.reflect.Method;

/**
 * @author github.kloping
 */
@Controller
public class BasalTopController implements Runner {
    public BasalTopController(QueueExecutor queueExecutor) {
        queueExecutor.setBefore(this);
    }

    @Override
    public void run(Method method, Object t, Object[] objects) throws NoRunException {
        //all before
    }

}
