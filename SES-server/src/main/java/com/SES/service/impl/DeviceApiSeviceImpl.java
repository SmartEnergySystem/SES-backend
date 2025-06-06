package com.SES.service.impl;

import com.SES.dto.deviceApi.DeviceInitApiResultDTO;
import com.SES.dto.deviceApi.DeviceQueryApiResultDTO;
import com.SES.entity.SimDevice;
import com.SES.entity.SimDeviceMode;
import com.SES.exception.BaseException;
import com.SES.mapper.SimDeviceMapper;
import com.SES.mapper.SimDeviceModeMapper;
import com.SES.service.DeviceApiService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


@Service
@Slf4j
public class DeviceApiSeviceImpl implements DeviceApiService {

    @Autowired
    private SimDeviceMapper simDeviceMapper;

    @Autowired
    private SimDeviceModeMapper simDeviceModeMapper;

    /**
     * 初始化api：
     * 为deviceId的设备填写sim_device表和sim_device_mode表
     * @param deviceId
     * @param type
     * @return 返回该设备的默认模式名与模式名列表，用于填写device_mode
     */
    @Override
    public DeviceInitApiResultDTO deviceInitApi(Long deviceId, String type) {
        TypeInfo typeInfo = this.getModeInfoByType(type);

        // 1.封装返回值
        DeviceInitApiResultDTO deviceInitApiResultDTO = new DeviceInitApiResultDTO();
        deviceInitApiResultDTO.setDefaultModeName(typeInfo.getDefaultModeName());
        deviceInitApiResultDTO.setModeList(typeInfo.getModeList());

        // 2.填写模拟设备表
        SimDevice simDevice = new SimDevice();

        simDevice.setDeviceId(deviceId);
        simDevice.setStatus(0); // 初始为关机
        simDevice.setModeName(typeInfo.getDefaultModeName()); // 默认模式

        simDeviceMapper.insert(simDevice);

        // 3.填写模拟设备模式表
        List<String> modeList = typeInfo.getModeList();
        List<Integer> powerList = typeInfo.getPowerList();

        // 遍历每个模式
        for (int idx = 0; idx < modeList.size(); idx++) {
            String modeName = modeList.get(idx);
            Integer power = powerList.get(idx);

            SimDeviceMode simDeviceMode = new SimDeviceMode();

            simDeviceMode.setDeviceId(deviceId);
            simDeviceMode.setName(modeName);
            simDeviceMode.setPower(power);

            simDeviceModeMapper.insert(simDeviceMode);
        }

        return deviceInitApiResultDTO;
    }

    /**
     * 设备控制api：
     * 为deviceId的设备修改sim_device表中状态
     * status 或 modeName可以为空
     * @param deviceId
     * @param status
     * @param modeName
     */
    @Override
    public void deviceControlApi(Long deviceId, Integer status, String modeName) {
        // 查询对应模拟设备
        SimDevice simDevice = simDeviceMapper.getByDeviceId(deviceId);
        if (simDevice == null) {
            throw new BaseException("找不到模拟设备");
        }

        if (status != null) {
            simDevice.setStatus(status);
        }

        if (modeName != null) {
            simDevice.setModeName(modeName);
        }

        simDeviceMapper.update(simDevice);

    }

    /**
     * 设备查询api：
     * 查询模拟设备的当前状态
     * @param deviceId
     * @return
     */
    @Override
    public DeviceQueryApiResultDTO deviceQueryApi(Long deviceId) {
        // 查询对应模拟设备
        SimDevice simDevice = simDeviceMapper.getByDeviceId(deviceId);
        if (simDevice == null) {
            throw new BaseException("找不到模拟设备");
        }

        DeviceQueryApiResultDTO deviceQueryApiResultDTO = new DeviceQueryApiResultDTO();
        Integer status = simDevice.getStatus();
        deviceQueryApiResultDTO.setStatus(status);

        // 关机
        if (status == 0) {
            deviceQueryApiResultDTO.setPower(0);
            return deviceQueryApiResultDTO;
        }

        String modeName = simDevice.getModeName();
        deviceQueryApiResultDTO.setModeName(modeName);

        // 查询对应模拟设备模式
        SimDeviceMode simDeviceMode = simDeviceModeMapper.getByDeviceIdAndModeName(deviceId,modeName);
        if (simDeviceMode == null) {
            throw new BaseException("找不到模拟设备模式");
        }
        deviceQueryApiResultDTO.setPower(simDeviceMode.getPower());

        // 若为故障模式，设置设备状态为故障
        if (modeName != null && modeName.startsWith("故障模式")) {
            deviceQueryApiResultDTO.setStatus(-1);
        }

        return deviceQueryApiResultDTO;
    }


    /**
     * 内部函数
     * 根据类型获得内置模拟设备的模式信息
     * @param type 设备类型，如“空调”、“冰箱”等
     * @return TypeInfo 包含默认模式、模式列表、功率列表
     */
    private TypeInfo getModeInfoByType(String type) {
        TypeInfo info = new TypeInfo();

        List<String> modeList = new ArrayList<>();
        List<Integer> powerList = new ArrayList<>();

        switch (type) {
            case "空调":
                info.setDefaultModeName("制冷模式（26°C）");
                modeList.addAll(Arrays.asList(
                        "制冷模式（26°C）",
                        "制冷模式（24°C）",
                        "制冷模式（22°C）",
                        "制冷模式（20°C）",
                        "制热模式（28°C）",
                        "除湿模式",
                        "送风模式"
                ));
                powerList.addAll(Arrays.asList(800, 1000, 1200, 1400, 1500, 600, 200));
                break;

            case "冰箱":
                info.setDefaultModeName("正常运行");
                modeList.add("正常运行");
                modeList.add("快速冷冻");
                modeList.add("节能模式");
                powerList.add(150);
                powerList.add(300);
                powerList.add(100);
                break;

            case "洗衣机":
                info.setDefaultModeName("标准洗");
                modeList.addAll(Arrays.asList("标准洗", "快速洗", "强力洗", "脱水模式"));
                powerList.addAll(Arrays.asList(500, 400, 600, 300));
                break;

            case "电视机":
                info.setDefaultModeName("标准模式");
                modeList.addAll(Arrays.asList("高亮度模式", "标准模式", "节能模式"));
                powerList.addAll(Arrays.asList(120, 80, 50));
                break;

            case "微波炉":
                info.setDefaultModeName("高火加热");
                modeList.addAll(Arrays.asList("高火加热", "中火加热", "解冻模式", "保温模式"));
                powerList.addAll(Arrays.asList(1200, 800, 600, 300));
                break;

            case "电热水器":
                info.setDefaultModeName("加热模式");
                modeList.addAll(Arrays.asList("加热模式", "保温模式", "快速加热"));
                powerList.addAll(Arrays.asList(1500, 200, 2000));
                break;

            case "吸尘器":
                info.setDefaultModeName("高功率吸尘");
                modeList.addAll(Arrays.asList("高功率吸尘", "中功率吸尘", "节能模式"));
                powerList.addAll(Arrays.asList(1200, 800, 500));
                break;

            case "电风扇":
                info.setDefaultModeName("中速模式");
                modeList.addAll(Arrays.asList("高速模式", "中速模式", "低速模式"));
                powerList.addAll(Arrays.asList(60, 40, 20));
                break;

            case "电饭煲":
                info.setDefaultModeName("煮饭模式");
                modeList.addAll(Arrays.asList("煮饭模式", "保温模式", "快煮模式"));
                powerList.addAll(Arrays.asList(700, 50, 900));
                break;

            case "空气净化器":
                info.setDefaultModeName("标准净化");
                modeList.addAll(Arrays.asList("高效净化", "标准净化", "睡眠模式"));
                powerList.addAll(Arrays.asList(80, 50, 20));
                break;

            case "LED灯":
                info.setDefaultModeName("中光模式（60%亮度）");
                modeList.addAll(Arrays.asList(
                        "强光模式（100%亮度）",
                        "中光模式（60%亮度）",
                        "柔光模式（30%亮度）",
                        "夜灯模式（10%亮度）"
                ));
                powerList.addAll(Arrays.asList(60, 36, 18, 6));
                break;

            default:
                throw new BaseException("无效的内置设备类型");
        }

        // 统一添加故障模式
        modeList.addAll(Arrays.asList("故障（短路）", "故障（设备故障）"));
        powerList.addAll(Arrays.asList(9999, 0));

        // 最后再设置回 info 对象中
        info.setModeList(modeList);
        info.setPowerList(powerList);

        return info;
    }

    /**
     * 内部使用的辅助类，用于封装模式返回结果，包括功率
     */
    @Data
    private static class TypeInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private String defaultModeName;
        private List<String> modeList;
        private List<Integer> powerList;
    }
}
