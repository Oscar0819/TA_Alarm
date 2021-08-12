package com.eunwoo.ta_alarm;

import java.util.concurrent.Executor;

public class BackgroundExecutor implements Executor {
    @Override
    public void execute(Runnable command) {
        command.run();
    }
}
