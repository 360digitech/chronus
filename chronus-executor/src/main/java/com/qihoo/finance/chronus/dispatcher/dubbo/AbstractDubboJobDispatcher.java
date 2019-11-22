package com.qihoo.finance.chronus.dispatcher.dubbo;

import com.qihoo.finance.chronus.dispatcher.AbstractJobDispatcher;
import com.qihoo.finance.chronus.sdk.ChronusSdkProcessor;

/**
 * Created by xiongpu on 2019/1/9.
 */
public abstract class AbstractDubboJobDispatcher extends AbstractJobDispatcher {
    protected ChronusSdkProcessor chronusSdkFacade;

    public AbstractDubboJobDispatcher(ChronusSdkProcessor chronusSdkFacade) {
        super();
        this.chronusSdkFacade = chronusSdkFacade;
    }
}
