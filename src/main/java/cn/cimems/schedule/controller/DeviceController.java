package cn.cimems.schedule.controller;

import cn.cimems.schedule.model.Device;
import cn.cimems.schedule.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    /**
     * 添加设备的接口，只有管理员或超级管理员可以调用。
     *
     * @param name        设备名称
     * @param description 设备描述
     * @param image       设备图片（文件上传）
     * @return 新添加的设备对象
     * @throws IOException 如果图片保存失败
     */
    @PostMapping
    @ResponseBody
    public Device addDevice(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam MultipartFile image) throws IOException {
        // 调用服务层逻辑，添加设备
        return deviceService.addDevice(name, description, image);
    }
}
