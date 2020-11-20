package com.qihoo.finance.chronus.protocol.api;

import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;

public interface ChronusSdkFacadeFactory {
    ChronusSdkFacade getInstance(SystemGroupEntity systemGroupEntity);
}
