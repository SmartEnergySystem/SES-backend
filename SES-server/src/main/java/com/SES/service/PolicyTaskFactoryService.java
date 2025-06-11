package com.SES.service;

import java.time.LocalTime;

public interface PolicyTaskFactoryService {
    Runnable create(Long deviceId, LocalTime timePoint);
}
