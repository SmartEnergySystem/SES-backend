package com.SES.service.impl;

import com.SES.dto.deviceData.AlertReportResultDTO;
import com.SES.dto.deviceData.DeviceDataQueryDTO;
import com.SES.dto.deviceData.ReportQueryDTO;
import com.SES.dto.deviceData.DeviceReportResultDTO;
import com.SES.dto.deviceMonitor.DeviceDataRedisDTO;
import com.SES.dto.log.AlertLogDataDTO;
import com.SES.dto.log.DeviceLogDataDTO;
import com.SES.dto.log.LogCommonDTO;
import com.SES.mapper.AlertLogMapper;
import com.SES.mapper.DeviceLogMapper;
import com.SES.service.DeviceDataService;
import com.SES.service.LogCommonCacheService;
import com.SES.service.LogService;
import com.SES.vo.deviceData.AlertReportVO;
import com.SES.vo.deviceData.DeviceDataVO;
import com.SES.vo.deviceData.DeviceReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.SES.constant.DeviceMonitorConstant.DEVICE_DATA_EXPIRATION_TTL;
import static com.SES.constant.DeviceMonitorConstant.REDIS_KEY_PREFIX;

@Service
@Slf4j
public class DeviceDataServiceImpl implements DeviceDataService {


    @Autowired
    private LogService logService;

    @Autowired
    private RedisTemplate<String, DeviceDataRedisDTO> deviceDataRedisTemplate;

    @Autowired
    private LogCommonCacheService logCommonCacheService;

    @Autowired
    private DeviceLogMapper deviceLogMapper;

    @Autowired
    private AlertLogMapper alertLogMapper;

    /**
     * 获取设备当前状态
     * @param deviceDataQueryDTO
     * @return
     */
    @Override
    public List<DeviceDataVO> getDataByDeviceIdList(DeviceDataQueryDTO deviceDataQueryDTO) {
        List<Long> idList = deviceDataQueryDTO.getIdList();
        List<DeviceDataVO> result = new ArrayList<>();

        for (Long deviceId : idList) {
            if (deviceId == null) {
                log.warn("设备ID为空，无法查询设备数据");
                return null;
            }

            // 构建 Redis Key
            String cacheKey = REDIS_KEY_PREFIX + deviceId;

            // 从 Redis 获取缓存数据
            DeviceDataRedisDTO redisData = deviceDataRedisTemplate.opsForValue().get(cacheKey);

            DeviceDataVO deviceDataVO = new DeviceDataVO();

            // 判断 Redis 数据是否为“实时”
            if (redisData != null) {
                log.info("从 Redis 中获取到设备 {} 的数据", deviceId);
                deviceDataVO.setDeviceId(deviceId);
                deviceDataVO.setStatus(redisData.getStatus());
                deviceDataVO.setModeName(redisData.getModeName());
                deviceDataVO.setPower(redisData.getPower());

                // 从logCommon缓存中获得policyName
                LogCommonDTO logCommonDTO = logCommonCacheService.getWithSyncRefreshAndFallback(deviceId);
                deviceDataVO.setPolicyName(logCommonDTO.getPolicyName());

                LocalDateTime lastUpdatedTime = redisData.getLastUpdatedTime();
                deviceDataVO.setLastUpdatedTime(lastUpdatedTime);

                // 判断数据是否实时
                if (lastUpdatedTime != null
                        && !lastUpdatedTime.isBefore(LocalDateTime.now().minusSeconds(DEVICE_DATA_EXPIRATION_TTL))) {
                    deviceDataVO.setIsRealTime(1); // 实时
                } else {
                    deviceDataVO.setIsRealTime(0); // 非实时
                }

            } else {
                log.info("Redis 中未找到设备 {} 的实时数据，尝试从日志中获取", deviceId);
                DeviceLogDataDTO deviceLogDataDTO = logService.getLatestDataByDeviceId(deviceId);
                if (deviceLogDataDTO != null) {
                    deviceDataVO.setDeviceId(deviceId);
                    deviceDataVO.setStatus(deviceLogDataDTO.getStatus());
                    deviceDataVO.setModeName(deviceLogDataDTO.getModeName());
                    deviceDataVO.setPower(deviceLogDataDTO.getPower());
                    deviceDataVO.setPolicyName(deviceLogDataDTO.getPolicyName());
                    deviceDataVO.setLastUpdatedTime(deviceLogDataDTO.getEndTime());
                    deviceDataVO.setIsRealTime(0); // 非实时
                } else {
                    log.warn("设备 {} 在 Redis 和日志中均未找到数据", deviceId);
                }
            }

            // 加入结果
            result.add(deviceDataVO);
        }
        return result;
    }

    @Override
    public DeviceReportResultDTO getDeviceReportByDeviceId(Long id, ReportQueryDTO reportQueryDTO) {
        LocalDateTime startTime = reportQueryDTO.getStartTime();
        LocalDateTime endTime = reportQueryDTO.getEndTime();

        List<DeviceLogDataDTO> logList = deviceLogMapper.getLogsByDeviceIdAndTimeRange(id, startTime, endTime);

        List<DeviceReportVO> reportVOList = new ArrayList<>();
        long total = 0;
        float totalEnergy = 0.0F;

        for (DeviceLogDataDTO log : logList) {
            DeviceReportVO vo = new DeviceReportVO();
            vo.setTimestamp(log.getEndTime()); // 用 endTime 作为时间戳
            vo.setStatus(log.getStatus());
            vo.setModeName(log.getModeName());
            vo.setPolicyName(log.getPolicyName());
            vo.setPower(log.getPower());
            vo.setEnergyConsumption(log.getEnergyConsumption());

            reportVOList.add(vo);
            totalEnergy += log.getEnergyConsumption() != null ? log.getEnergyConsumption() : 0.0F;

            // TODO:删掉过大的数值
            total++;
        }

        DeviceReportResultDTO result = new DeviceReportResultDTO();
        result.setTotal(total);
        result.setTotalEnergyConsumption(totalEnergy);
        result.setDeviceReports(reportVOList);


        return result;
    }

    @Override
    public AlertReportResultDTO getAlertReportByDeviceId(Long id, ReportQueryDTO reportQueryDTO) {
        LocalDateTime startTime = reportQueryDTO.getStartTime();
        LocalDateTime endTime = reportQueryDTO.getEndTime();

        List<AlertLogDataDTO> logList = alertLogMapper.getLogsByDeviceIdAndTimeRange(id, startTime, endTime);

        List<AlertReportVO> reportVOList = new ArrayList<>();
        long total = 0;

        for (AlertLogDataDTO log : logList) {
            AlertReportVO vo = new AlertReportVO();
            vo.setTimestamp(log.getTime());
            vo.setLevel(log.getLevel());
            vo.setStatus(log.getStatus());
            vo.setModeName(log.getModeName());
            vo.setPolicyName(log.getPolicyName());
            vo.setMessage(log.getMessage());

            reportVOList.add(vo);
            total++;
        }

        AlertReportResultDTO result = new AlertReportResultDTO();
        result.setTotal(total);
        result.setAlertReports(reportVOList);

        return result;
    }


}
