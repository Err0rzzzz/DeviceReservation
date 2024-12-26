package cn.cimems.schedule.config;

import cn.cimems.schedule.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    /**
     * 密码加密配置，使用 BCrypt 算法
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 AuthenticationManager，用于认证用户
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * 配置安全过滤链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // 禁用 CSRF（如果需要防护，请启用并配置）
            .authorizeHttpRequests(auth -> auth
                // 开放的端点
                .antMatchers("/api/users/register").permitAll() // 注册接口允许匿名访问
                .antMatchers("/api/users/login").permitAll()    // 登录接口允许匿名访问
                // 必须认证的端点
                .antMatchers("/api/users/logout").authenticated() // 登出接口需要认证
                .antMatchers("/api/devices/**").hasAnyRole("ADMIN", "SUPER_ADMIN") // 设备管理接口仅管理员可用
                .antMatchers("/api/reservations/**").authenticated() // 预约功能需登录
                .anyRequest().authenticated() // 其他接口需要认证
            )
            // 配置登出逻辑
            .logout(logout -> logout
                .logoutUrl("/api/users/logout") // 设置登出端点
                .invalidateHttpSession(true) // 使会话无效
                .clearAuthentication(true)  // 清除认证信息
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.setStatus(200); // 返回成功状态码
                    response.getWriter().write("Logout successful");
                })
            )
            .httpBasic(); // 使用 HTTP Basic Authentication
        return http.build();
    }
}
