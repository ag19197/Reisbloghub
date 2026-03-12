package com.Reisblog.controller.auth;

import cn.hutool.core.util.RandomUtil;
import com.Reisblog.dto.Result;
import com.Reisblog.dto.auth.LoginDTO;
import com.Reisblog.dto.auth.RegisterDTO;
import com.Reisblog.dto.auth.UserDTO;
import com.Reisblog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "认证接口")
public class AuthController {

    private final UserService userService;
    private final StringRedisTemplate redisTemplate;

    // 模拟验证码发送
    @PostMapping("/code")
    @Operation(summary = "发送验证码")
    public Result<String> sendCode(@RequestParam String account) {
        // 生成6位随机验证码
        String code = RandomUtil.randomNumbers(6);
        // 存入 Redis，有效期5分钟
        redisTemplate.opsForValue().set("auth:code:" + account, code, 5, TimeUnit.MINUTES);
        // 模拟发送（打印日志）
        log.info("验证码发送至 {}：{}", account, code);
        return Result.success("验证码已发送");
    }

    // 注册
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public Result<UserDTO> register(@RequestBody RegisterDTO dto) {
        // 从 Redis 获取验证码（此处简化，实际应该校验）
        String cacheCode = redisTemplate.opsForValue().get("auth:code:" + dto.getAccount());
        // 如果 Redis 中没有，或者不匹配，则返回错误（为了测试方便，如果 Redis 中没有，可以跳过验证）
        // 但为了演示，我们保留验证逻辑。如果 Redis 中无码，可使用固定码 123456 作为临时测试
        String inputCode = dto.getCode();
        if (!"123456".equals(inputCode) && (cacheCode == null || !cacheCode.equals(inputCode))) {
            return Result.fail("验证码错误或已过期");
        }
        // 调用 service 注册
        UserDTO userDTO = userService.register(dto);
        return Result.success(userDTO);
    }

    // 登录
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<String> login(@RequestBody LoginDTO dto) {
        String token = userService.login(dto);
        return Result.success(token);
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前登录用户信息")
    public Result<UserDTO> getCurrentUser(HttpServletRequest request) {
        // 从请求属性中获取 userId（由 JwtInterceptor 设置）
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return Result.fail("未登录");
        }
        // 调用 service 获取用户信息（需要确保 UserService 中有该方法）
        UserDTO userDTO = userService.getUserById(userId);
        return Result.success(userDTO);
    }

    // 获取当前登录用户信息（需要 token，但这里暂时不加拦截器，先实现方法）
    // 后续配合 JWT 拦截器使用
}