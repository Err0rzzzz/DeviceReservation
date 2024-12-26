package cn.cimems.schedule.controller;

import cn.cimems.schedule.model.User;
import cn.cimems.schedule.model.Role;
import cn.cimems.schedule.security.CustomUserDetails;
import cn.cimems.schedule.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // 用户注册接口
    @PostMapping("/register")
    @ResponseBody
    public User registerUser(@RequestBody User user) {

        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.registerUser(user);
    }

    // 用户登录接口
    @PostMapping("/login")
    @ResponseBody
    public User login(@RequestParam String email, @RequestParam String password) {
        // 认证用户
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // 将用户认证信息保存到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 返回登录成功的用户详情
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }

    /**
     * 用户登出接口
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @return 登出成功的消息
     */
    @PostMapping("/logout")
    @ResponseBody
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 清除当前的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            SecurityContextHolder.clearContext();
        }

        // 使当前会话失效
        request.getSession().invalidate();

        return "Logout successful";
    }

    // 用户审批接口
    @PutMapping("/{id}/approve")
    @ResponseBody
    public User approveUser(@PathVariable Long id, @RequestParam boolean approved) {
        return userService.updateApprovalStatus(id, approved);
    }
}
