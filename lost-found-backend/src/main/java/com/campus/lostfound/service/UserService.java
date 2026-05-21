package com.campus.lostfound.service;

import com.campus.lostfound.common.Constants;
import com.campus.lostfound.dao.UserMapper;
import com.campus.lostfound.entity.User;
import com.campus.lostfound.entity.dto.LoginDTO;
import com.campus.lostfound.entity.dto.RegisterDTO;
import com.campus.lostfound.exception.BusinessException;
import com.campus.lostfound.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务层，处理用户注册、登录、信息管理等业务逻辑
 */
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     * @param dto 注册信息
     * @return 注册成功的用户信息
     * @throws BusinessException 用户名已存在时抛出
     */
    public User register(RegisterDTO dto) {
        User existUser = userMapper.selectByUsername(dto.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(Constants.ROLE_USER);
        user.setStatus(1);

        userMapper.insert(user);
        user.setPassword(null);
        log.info("用户注册成功: {}", user.getUsername());
        return user;
    }

    /**
     * 用户登录
     * @param dto 登录信息
     * @return 包含Token和用户信息的Map
     * @throws BusinessException 用户名或密码错误时抛出
     */
    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null || !dto.getPassword().equals(user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        user.setPassword(null);
        result.put("user", user);

        log.info("用户登录成功: {}", user.getUsername());
        return result;
    }

    /**
     * 根据ID查询用户信息
     * @param id 用户ID
     * @return 用户实体（密码置空）
     */
    public User getUserById(Long id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return user;
    }

    /**
     * 更新用户信息
     * @param user 用户实体
     * @return 更新后的用户信息
     */
    public User updateUser(User user) {
        userMapper.updateById(user);
        return getUserById(user.getId());
    }

    /**
     * 删除用户
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        userMapper.deleteById(id);
        log.info("用户已删除: id={}", id);
    }
}
