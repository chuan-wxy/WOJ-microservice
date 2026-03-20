package com.chuan.wojuserservice.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.chuan.wojcommon.common.BaseResponse;
import com.chuan.wojcommon.common.enums.EmailEnum;
import com.chuan.wojcommon.constant.RedisContant;
import com.chuan.wojcommon.exception.StatusFailException;
import com.chuan.wojcommon.utils.EmailUtil;
import com.chuan.wojcommon.utils.JwtUtil;
import com.chuan.wojcommon.utils.RedisUtils;
import com.chuan.wojcommon.utils.ResultUtils;
import com.chuan.wojmodel.pojo.dto.user.*;
import com.chuan.wojmodel.pojo.entity.User;
import com.chuan.wojmodel.pojo.entity.UserRole;
import com.chuan.wojmodel.pojo.vo.user.UserLoginVO;
import com.chuan.wojmodel.pojo.vo.user.UserVO;
import com.chuan.wojuserservice.mapper.RoleMapper;
import com.chuan.wojuserservice.mapper.UserMapper;
import com.chuan.wojuserservice.mapper.UserRoleMapper;
import com.chuan.wojuserservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Author chuan-wxy
 * @Create 2024/8/14 20:25
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    public static final String SALT = "wxy";
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 用户注册
     *
     * @param userAddDTO
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Void> register(UserRegisterDTO userAddDTO) throws StatusFailException {
        // todo 检查网站是否开启注册
        String userAccount = userAddDTO.getUserAccount();
        String userPassword = userAddDTO.getUserPassword();
        String rePassword = userAddDTO.getRePassword();
        String captcha = userAddDTO.getCaptcha();

        String redisCaptcha = (String) redisUtils.get(EmailEnum.REGISTER_KEY_PREFIX.getValue() + userAccount);

        if (!captcha.equals(redisCaptcha)) {
            log.info(userAccount + "注册时验证码有误");
            return new BaseResponse(400, "验证码错误");
        }
        if (!rePassword.equals(userPassword)) {
            log.info(userAccount + "注册时两次密码不一致");
            return new BaseResponse(400, "两次密码不一致");
        }
        if (StringUtils.isEmpty(userPassword)) {
            log.info(userAccount + "注册时验证码有误");
            return new BaseResponse(400, "密码不能为空");
        }
        if (userPassword.length() < 8 || userPassword.length() > 20) {
            return new BaseResponse(400, "密码长度应为6-20位");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("userAccount", userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return new BaseResponse(400, "改邮箱已被注册");
        }

        String md5Password = SecureUtil.md5().digestHex((SALT + userPassword).getBytes());
        user = new User();
        BeanUtils.copyProperties(userAddDTO, user);

        int insertResult = userMapper.insert(user);

        if(insertResult == 0) {
            throw new StatusFailException("插入用户失败");
        }

        UserRole userRole = new UserRole();

        userRole.setUid(user.getId());
        // todo 常量
        userRole.setRoleId(2);

        insertResult = userRoleMapper.insert(userRole);
        if(insertResult == 0) {
            throw new StatusFailException("插入用户失败");
        }
        return ResultUtils.success();
    }

    /**
     * 用户登录
     *
     * @param userLoginDTO
     * @return UserLoginVO
     */
    @Override
    public BaseResponse<UserLoginVO> login(UserLoginDTO userLoginDTO, HttpServletRequest request) {
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();

        String key = RedisContant.TRY_LOGIN_NUM + userAccount;
        Integer tryLoginCount = (Integer) redisUtils.get(key);

        if(tryLoginCount != null && tryLoginCount >= 5) {
            return new BaseResponse(400,"登录失败次数过多！您的账号有风险，半个小时内暂时无法登录！");
        }

        User user = getUser(userAccount, userPassword);

        if(user!=null) {
            UserLoginVO userLoginVO = new UserLoginVO();
            UserVO userVO = new UserVO();

            BeanUtils.copyProperties(user,userVO);
            userLoginVO.setUserInfo(userVO);

            // 登陆成功清楚异常记录
            if (tryLoginCount != null) {
                redisUtils.del(key);
            }

            String JWT = JwtUtil.generateJwt(userAccount, getUserRole(userAccount).getData());
            userLoginVO.setJwt(JWT);

            List<String> userRoleList = getUserRole(userAccount).getData();
            userLoginVO.getUserInfo().setRoles(userRoleList);

            // 时间与 JWT 令牌的时间相同（3天）
            redisUtils.set(RedisContant.USER_TOKEN + JWT, userAccount,72*60*60);

            request.getSession().setAttribute("user_login", user);

            return ResultUtils.success(userLoginVO);
        } else {
            if(tryLoginCount == null) {
                redisUtils.set(key, 1,30*60);
            } else {
                redisUtils.set(key,tryLoginCount+1,30*60);
            }
            return ResultUtils.error(400,"账号与密码不匹配！");
        }

//
//        // 会话记录
//        sessionEntityService.save(new Session()
//                .setUid(userRolesVo.getUid())
//                .setIp(IpUtils.getUserIpAddr(request))
//                .setUserAgent(request.getHeader("User-Agent")));
//
//        // 登录成功，清除锁定限制
//        if (tryLoginCount != null) {
//            redisUtils.del(key);
//        }
//
//        // 异步检查是否异地登录
//        sessionEntityService.checkRemoteLogin(userRolesVo.getUid());
//
//        UserInfoVO userInfoVo = new UserInfoVO();
//        BeanUtil.copyProperties(userRolesVo, userInfoVo, "roles");
//        userInfoVo.setRoleList(userRolesVo.getRoles()
//                .stream()
//                .map(Role::getRole)
//                .collect(Collectors.toList()));
//        return userInfoVo;
    }

    /**
     * 修改个人信息
     *
     * @param userProfileDTO
     * @return
     */
    @Override
    public BaseResponse<Void> updateProfile(UserProfileDTO userProfileDTO) {
        String userAccount = userProfileDTO.getUserAccount();

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return ResultUtils.error(400,"修改失败，没有该用户");
        }

        UpdateWrapper<User> updateWrapper = new UpdateWrapper<User>();
        updateWrapper.eq("userAccount",userAccount);

        User newUser = new User();
        BeanUtils.copyProperties(userProfileDTO,newUser);
        newUser.setUpdateTime(DateTime.now());
        int row = userMapper.update(newUser,updateWrapper);
        if(row != 1) {
            log.info("{}修改个人信息失败",userAccount);
            return ResultUtils.error(400,"修改失败");
        }



        return ResultUtils.success();
    }

    /**
     * 退出功能
     *
     * @param request
     * @return
     */
    @Override
    public BaseResponse<Void> logout(HttpServletRequest request) {
        String token = String.valueOf(request.getHeader("Authorization"));

        redisUtils.del(RedisContant.USER_TOKEN + token);

        return ResultUtils.success();
    }

    /**
     * 获取注销验证码
     * @param userLogoutDTO
     * @return
     */
    @Override
    public BaseResponse<Void> getLogoutCode(UserLogoutDTO userLogoutDTO) {
        String userAccount = userLogoutDTO.getUserAccount();
        boolean isEmail = Validator.isEmail(userAccount);
        if (!isEmail) {
            return new BaseResponse(400, "您的邮箱格式不正确！");
        }


        String lockKey = EmailEnum.REGISTER_EMAIL_LOCK.getValue() + userAccount;
        if(redisUtils.hasKey(lockKey)) {
            return new BaseResponse(400,"对不起，您的操作频率过快，请在" + redisUtils.getExpire(lockKey) + "秒后再次发送注册邮件！");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",userAccount);

        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return new BaseResponse(400,"改邮箱未被注册");
        }

        String numbers = RandomUtil.randomNumbers(6);

        redisUtils.set(EmailEnum.LOGOUT_KEY_PREFIX.getValue() + userAccount, numbers, 10 * 60);//默认验证码有效10分钟
        redisUtils.set(lockKey, 0, 60);

        try {
            EmailUtil.send(userAccount,numbers,"正在修改注销账号，若非本人操作，请立即修改密码");
            return ResultUtils.success();
        }catch (Exception e){
            log.info("{}注销账户时，发送验证码失败。{}",userAccount,e.getMessage());
            return ResultUtils.error("发送验证码失败");
        }
    }

    /**
     * 用户注销
     *
     * @param userLogoutDTO
     * @return
     */

    @Override
    public BaseResponse<Void> logoutForever(UserLogoutDTO userLogoutDTO) {
        String userAccount = userLogoutDTO.getUserAccount();
        String code = userLogoutDTO.getCode();

        Integer tryLogoutNum = (Integer) redisUtils.get(RedisContant.TRY_LOGIN_NUM + userAccount);

        if(tryLogoutNum != null && tryLogoutNum > 3) {
            log.info("{}多次注销，异常行为被拦截",userAccount);
            return ResultUtils.error("用户异常，请30分钟后再试！");
        }

        String redisKey = (String) redisUtils.get(EmailEnum.LOGOUT_KEY_PREFIX.getValue() + userAccount);
        if(!redisKey.equals(code)) {
            if (tryLogoutNum != null) {
                redisUtils.set(RedisContant.TRY_LOGIN_NUM + userAccount, tryLogoutNum + 1, 30 * 60);
            } else {
                redisUtils.set(RedisContant.TRY_LOGIN_NUM + userAccount, 1, 30 * 60);
            }
            return ResultUtils.error("验证码错误");
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.eq("userAccount",userAccount);
        int delete = userMapper.delete(queryWrapper);
        if(delete!=1){
            log.info("{}注销账号失败");
            return ResultUtils.error("注销失败");
        }
        return ResultUtils.success();
    }

    @Override
    public BaseResponse<Void> editPassword(UserPasswordDTO userPasswordDTO, HttpServletRequest request) throws StatusFailException {
        String newPassword = userPasswordDTO.getNewPassword();
        String confirmPassword = userPasswordDTO.getConfirmPassword();
        String oldPassword = userPasswordDTO.getPassword();

        // 校验两次新密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            throw new StatusFailException("两次输入的密码不一致");
        }

        String token = request.getHeader("Authorization");

        String redisKey = RedisContant.USER_TOKEN + token;
        String userAccount = (String) redisUtils.get(redisKey);
        if (userAccount == null) {
            throw new StatusFailException("会话已过期，请重新登录");
        }

        // 校验旧密码是否正确

        User user = getUser(userAccount, oldPassword);
        if (user == null) {
            throw new StatusFailException("原密码错误");
        }

        // 设置新密码
        String encryptedNewPassword = SecureUtil.md5().digestHex((SALT + newPassword).getBytes());
        user.setPassword(encryptedNewPassword);

        boolean success = this.updateById(user);
        if (!success) {
            throw new StatusFailException("系统繁忙，修改失败");
        }

        // 修改成功后，删除旧 Token，强制用户重新登录
        redisUtils.del(redisKey);

        return ResultUtils.success();
    }

    @Override
    public BaseResponse<List<String>> getUserRole(String userAccount) {
        if(userAccount.isBlank()) {
            log.info("获取角色时参数为空");
            return ResultUtils.error("参数为空");
        }
        return ResultUtils.success(roleMapper.SelectRoleByUserAccount(userAccount));
    }

    @Override
    public BaseResponse<UserLoginVO> getLoginUser(HttpServletRequest request) throws StatusFailException {

        Object userObj = request.getSession().getAttribute("user_login");

        User currentUser = (User) userObj;

        if (currentUser == null || currentUser.getAccount() == null) {
            throw new StatusFailException("未登录");
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        Long userId = currentUser.getId();

        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new StatusFailException("未登录");
        }
        UserLoginVO userLoginVO = new UserLoginVO();
        UserVO userVO = new UserVO();

        BeanUtils.copyProperties(currentUser,userLoginVO);
        userLoginVO.setUserInfo(userVO);

        return ResultUtils.success(userLoginVO);

    }


    /**
     *
     * @param JWT
     * @return true 过期
     */
    @Override
    public BaseResponse<Boolean> checkJWT(String JWT) {
        log.debug("UserServiceImpl---->checkJWT()");
        return ResultUtils.success(!redisUtils.hasKey(JWT));
    }

    /**
     * 根据用户账号密码返回用户
     *
     * @param userAccount
     * @param userPassword
     * @return User
     */
    private User getUser(String userAccount, String userPassword) {
        String md5Password = SecureUtil.md5().digestHex((SALT + userPassword).getBytes());

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getAccount, userAccount).eq(User::getPassword, md5Password);

        return userMapper.selectOne(lambdaQueryWrapper);
    }

}
