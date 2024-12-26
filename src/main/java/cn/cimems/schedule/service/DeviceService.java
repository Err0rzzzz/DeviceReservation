package cn.cimems.schedule.service;

import cn.cimems.schedule.model.Device;
import cn.cimems.schedule.model.Role;
import cn.cimems.schedule.repository.DeviceRepository;
import cn.cimems.schedule.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import javax.imageio.ImageIO;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * 添加设备的服务逻辑。
     * 检查当前用户的角色是否为 ADMIN 或 SUPER_ADMIN，只有具备权限的用户才能添加设备。
     *
     * @param name        设备名称
     * @param description 设备描述
     * @param image       设备图片文件
     * @return 保存的设备对象
     * @throws IOException 如果图片保存失败
     */
    public Device addDevice(String name, String description, MultipartFile image) throws IOException {
        // 验证权限：只有 ADMIN 和 SUPER_ADMIN 能够添加设备
        Role currentRole = getCurrentUserRole();
        if (currentRole != Role.ADMIN && currentRole != Role.SUPER_ADMIN) {
            throw new RuntimeException("Only ADMIN or SUPER_ADMIN can add devices.");
        }

        // 保存图片到文件系统
        String imagePath = saveImage(image);

        // 创建设备对象并保存到数据库
        Device device = new Device();
        device.setName(name);
        device.setDescription(description);
        device.setImagePath(imagePath);

        return deviceRepository.save(device);
    }

    /**
     * 保存图片到文件系统。
     *
     * @param image 图片文件
     * @return 图片的保存路径
     * @throws IOException 如果保存图片失败
     */
    private String saveImage(MultipartFile image) throws IOException {
        // 检查 MIME 类型
        String mimeType = image.getContentType();
        if (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png")) {
            throw new RuntimeException("Invalid file type! Only JPG and PNG images are allowed.");
        }
    
        // 验证文件内容是否为图片
        if (ImageIO.read(image.getInputStream()) == null) {
            throw new RuntimeException("Invalid image content!");
        }
    
        // 动态检查和创建目录
        String uploadDir = "C:/uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Failed to create directory for uploads.");
        }
    
        // 保存图片
        String filePath = uploadDir + UUID.randomUUID() + "_" + image.getOriginalFilename();
        File file = new File(filePath);
        image.transferTo(file);
    
        return filePath;
    }
    

    /**
     * 获取当前登录用户的角色。
     *
     * @return 当前用户的角色
     */
    private Role getCurrentUserRole() {
        // 从 SecurityContextHolder 中获取当前认证对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        // 从认证对象中提取用户信息
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getRole();
    }
}
