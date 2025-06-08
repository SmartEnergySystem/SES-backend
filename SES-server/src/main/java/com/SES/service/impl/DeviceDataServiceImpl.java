package com.SES.service.impl;

import com.SES.dto.deviceData.DeviceDataQueryDTO;
import com.SES.service.DeviceDataService;
import com.SES.vo.deviceData.DeviceDataVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DeviceDataServiceImpl implements DeviceDataService {

    /**
     * 获取设备当前状态
     * @param deviceDataQueryDTO
     * @return
     */
    @Override
    public List<DeviceDataVO> getDataByDeviceIdList(DeviceDataQueryDTO deviceDataQueryDTO) {
        return null;
    }
}
