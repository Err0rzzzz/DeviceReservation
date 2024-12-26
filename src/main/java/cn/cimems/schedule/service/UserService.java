package cn.cimems.schedule.service;

import cn.cimems.schedule.model.Role;
import cn.cimems.schedule.model.User;
import cn.cimems.schedule.repository.UserRepository;
import cn.cimems.schedule.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        user.setApproved(false); // 默认未审核

        // 检查用户是否选择了有效角色
        if (user.getRole() == null || (!user.getRole().equals(Role.USER) && !user.getRole().equals(Role.ADMIN))) {
            user.setRole(Role.USER); // 默认角色
        }

        return userRepository.save(user);
    }

    public User updateApprovalStatus(Long userId, boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    
        // 获取当前登录用户的角色
        Role approverRole = getCurrentUserRole();
    
        // 检查是否有权限审批目标用户
        if (!approverRole.canApprove(user.getRole())) {
            throw new RuntimeException("You do not have permission to approve this account.");
        }
    
        // 更新审批状态
        user.setApproved(approved);
        return userRepository.save(user);
    }
    
    private Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
    
        // 判断 principal 是否为 CustomUserDetails 类型
        if (!(principal instanceof CustomUserDetails)) {
            throw new RuntimeException("Unauthorized user or principal type mismatch");
        }
    
        CustomUserDetails userDetails = (CustomUserDetails) principal;
        return userDetails.getUser().getRole();
    }
    
    
}
