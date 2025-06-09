package com.SES.service.impl;

import com.SES.config.RabbitMQConfig;
import com.SES.constant.MessageConstant;
import com.SES.constant.StatusConstant;
import com.SES.constant.UserTypeConstant;
import com.SES.context.BaseContext;
import com.SES.dto.logCommonCache.RefreshLogCommonCacheUserMessageDTO;
import com.SES.dto.user.*;
import com.SES.entity.User;
import com.SES.exception.*;
import com.SES.mapper.UserMapper;
import com.SES.result.PageResult;
import com.SES.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    public User login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        User user = userMapper.getByUsername(username); //

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException("账号"+MessageConstant.NOT_EXISTS);
        }

        //密码比对
        // 进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (user.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return user;
    }

    /**
     * 新用户注册
     * @param userDTO
     * @return
     */
    @Override
    public void register(UserDTO userDTO) {
        // 1. 校验用户名是否已存在
        User existingUser = userMapper.getByUsername(userDTO.getUsername());
        if (existingUser != null) {
            throw new DuplicateException("用户名"+MessageConstant.ALREADY_EXISTS);
        }

        User user = new User();

        // 对象属性拷贝
        BeanUtils.copyProperties(userDTO,user);

        user.setStatus(StatusConstant.ENABLE); // 默认账号状态为可用
        user.setType(UserTypeConstant.NORMAL); // 默认账号类型为普通

        // 密码加密
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));

        userMapper.insert(user);
    }

    @Override
    public void editUsername(UsernameEditDTO usernameEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();

        // 根据id查询对应数据
        User user = userMapper.getById(currentUserId);

        if (user == null) {
            // 账号不存在
            throw new AccountNotFoundException("账号" + MessageConstant.NOT_EXISTS);
        }

        String newUsername = usernameEditDTO.getNewUsername();
        String oldUsername = user.getUsername();

        // 如果新旧用户名一致，无需修改
        if (newUsername.equals(oldUsername)) {
            return;
        }

        // 更新用户名
        user.setUsername(newUsername);
        userMapper.update(user);

        // 构建要发送的消息体
        Map<String, Object> messageBody = new HashMap<>();
        messageBody.put("userId", currentUserId);
        messageBody.put("username", newUsername);

        // 发送消息到 RabbitMQ 队列
        RefreshLogCommonCacheUserMessageDTO dto = new RefreshLogCommonCacheUserMessageDTO();
        dto.setUserId(currentUserId);
        dto.setUsername(newUsername);

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_LOG_COMMON_REFRESH_USER, dto);
            log.info("已发送用户名变更消息: {}", dto);
        } catch (Exception e) {
            log.error("发送用户名变更消息失败", e);
        }
    }

    /**
     * 修改密码
     * @param passwordEditDTO
     */
    @Override
    public void editPassword(PasswordEditDTO passwordEditDTO) {
        Long currentUserId = BaseContext.getCurrentId();
        String oldPassword = passwordEditDTO.getOldPassword();
        String newPassword = passwordEditDTO.getNewPassword();

        // 根据id查询对应数据
        User user = userMapper.getById(currentUserId);

        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException("账号"+MessageConstant.NOT_EXISTS);
        }

        //密码比对
        // 进行md5加密，然后再进行比对
        oldPassword = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!oldPassword.equals(user.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));

        userMapper.update(user);
    }

    /**
     * 修改账号权限
     * @param id
     * @param type
     */
    @Override
    public void editType(Long id, Integer type) {
        // 校验管理员权限
        this.checkCurrentUserIsAdmin();
        if (id == BaseContext.getCurrentId()) {
            //不能操作自身
            throw new AccountNotFoundException(MessageConstant.ADMIN_COULD_NOT_EDIT_SELF);
        }

        User user = userMapper.getById(id);

        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException("账号"+MessageConstant.NOT_EXISTS);
        }
        if (!(type == UserTypeConstant.ADMIN || type == UserTypeConstant.NORMAL)) {
            // 无效的类型
            throw new AccountNotFoundException(MessageConstant.INVALID_USER_TYPE);
        }

        user.setType(type);

        userMapper.update(user);
    }

    /**
     * 用户分页查询
     * @param userPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQueny(UserPageQueryDTO userPageQueryDTO) {
        // 校验管理员权限
        this.checkCurrentUserIsAdmin();

        PageHelper.startPage(userPageQueryDTO.getPage(),userPageQueryDTO.getPageSize());

        Page<User> page = userMapper.pageQueny(userPageQueryDTO);

        long total = page.getTotal();
        List<User> records = page.getResult();

        return new PageResult(total,records);
    }

    /**
     * 启用禁用账号
     * @param id
     * @param status
     */
    @Override
    public void startOrStop(Long id, Integer status) {
        // 校验管理员权限
        this.checkCurrentUserIsAdmin();
        if (id == BaseContext.getCurrentId()) {
            //不能操作自身
            throw new AccountNotFoundException(MessageConstant.ADMIN_COULD_NOT_EDIT_SELF);
        }


        User user = userMapper.getById(id);

        if (user == null) {
            //账号不存在
            throw new AccountNotFoundException("账号"+MessageConstant.NOT_EXISTS);
        }
        if (!(status == StatusConstant.ENABLE || status == StatusConstant.DISABLE)) {
            // 无效的状态
            throw new AccountNotFoundException(MessageConstant.INVALID_STATUS);
        }

        user.setStatus(status);

        userMapper.update(user);
    }



    /**
     * 判断当前操作用户是否为管理员
     * @return
     */
    @Override
    public void checkCurrentUserIsAdmin() {
        Long currentUserId = BaseContext.getCurrentId();

        User user = userMapper.getById(currentUserId);

        if (user == null) {
            //账号不存在
            throw new AdminCheckException(MessageConstant.ADMIN_CHECK_FAILED+"（账号"+MessageConstant.NOT_EXISTS+"）");
        }

        if (user.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AdminCheckException(MessageConstant.ADMIN_CHECK_FAILED+"（"+MessageConstant.ACCOUNT_LOCKED+"）");
        }

        if (user.getType() != UserTypeConstant.ADMIN) {
            throw new AdminCheckException(MessageConstant.ADMIN_CHECK_FAILED);
        }
    }
}
