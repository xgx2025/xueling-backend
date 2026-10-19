package com.hope.xueling.common.controller;


import com.hope.xueling.common.domain.dto.UserDTO;
import com.hope.xueling.common.domain.vo.Result;
import com.hope.xueling.common.domain.vo.UserVO;
import com.hope.xueling.common.service.impl.UserServiceImpl;
import com.hope.xueling.common.util.ThreadLocalUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户控制器(用户修改用户信息、获取用户信息、用户注销)
 * @author 谢光益
 * @since 2026/1/20
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    /**
     * 根据用户ID获取用户信息
     * @return Result<UserVO> 结果对象，包含用户信息
     */
    @GetMapping("/info")
    public Result<UserVO> info() {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        UserVO userInfo = userService.getUserInfo(userId);
        log.info("用户信息获取成功: {}", userInfo);
        return Result.success(userInfo);
    }

    /**
     * 编辑用户信息
     * @param userDTO 用户数据传输对象
     * @return Result<String> 结果对象，包含更新成功的消息
     */
    @PostMapping("/profile")
    public Result<String> profile(@RequestBody UserDTO userDTO) {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        // 用户ID一律以令牌为准，忽略请求体中的 id；DTO 对外约定为字符串
        userDTO.setId(String.valueOf(userId));
        userService.updateUserById(userDTO);
        log.info("用户信息更新成功");
        return Result.success("用户信息更新成功");
    }


    @PutMapping("/avatar")
    public Result<String> updateAvatar(@RequestParam("file")MultipartFile avatar){
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        String url = userService.uploadAvatar(userId, avatar);
        log.info("用户{}头像更新成功: {}", userId, url);
        return Result.success(url, "用户头像更新成功");
    }

    /**
     * 注销用户(发起注销 → 手机验证码 → 执行注销)
     * @param verificationCode 验证码
     * @return Result<String> 结果对象，包含注销成功的消息
     */
    @PostMapping("/cancel/")
    public Result<String> cancel(@RequestParam String verificationCode) {
        Claims claims = ThreadLocalUtils.get();
        Long userId = claims.get("userId", Long.class);
        log.info("正在注销用户{}", userId);
        userService.cancelUserAccount(userId, verificationCode);
        log.info("用户{}注销成功", userId);
        return Result.success("用户注销成功");
    }
}
