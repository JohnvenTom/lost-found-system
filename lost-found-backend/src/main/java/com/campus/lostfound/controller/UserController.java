package com.campus.lostfound.controller;

import com.campus.lostfound.common.Result;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.entity.dto.LoginDTO;
import com.campus.lostfound.entity.dto.RegisterDTO;
import com.campus.lostfound.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 用户控制器，提供用户注册、登录、信息管理接口
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户注册
     * @param dto 注册信息
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public Result<User> register(@Validated @RequestBody RegisterDTO dto) {
        User user = userService.register(dto);
        return Result.success(user);
    }

    /**
     * 用户登录
     * @param dto 登录信息
     * @return Token和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Validated @RequestBody LoginDTO dto) {
        Map<String, Object> data = userService.login(dto);
        return Result.success(data);
    }

    /**
     * 获取当前登录用户信息
     * @param request HTTP请求
     * @return 用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("currentUserId");
        User user = userService.getUserById(userId);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * @param user 用户实体
     * @param request HTTP请求
     * @return 更新后的用户信息
     */
    @PutMapping("/update")
    public Result<User> updateUser(@RequestBody User user, HttpServletRequest request) {
        Long currentUserId = (Long) request.getAttribute("currentUserId");
        user.setId(currentUserId);
        user.setPassword(null);
        user.setRole(null);
        User updated = userService.updateUser(user);
        return Result.success(updated);
    }

    /**
     * 管理员：获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/detail/{id}")
    public Result<User> getUserDetail(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 管理员：删除用户
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
