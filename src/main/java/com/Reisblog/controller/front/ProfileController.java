package com.Reisblog.controller.front;

import com.Reisblog.dto.Result;
import com.Reisblog.dto.user.UserProfileDTO;
import com.Reisblog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "用户主页接口")
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "获取用户个人主页")
    public Result<UserProfileDTO> getUserProfile(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserProfileDTO profile = userService.getUserProfile(userId, page, size);
        return Result.success(profile);
    }
}
