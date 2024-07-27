package com.nnu.chatrobot.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nnu.chatrobot.common.OssTemplate;
import com.nnu.chatrobot.common.R;
import com.nnu.chatrobot.entity.User;
import com.nnu.chatrobot.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private final OssTemplate ossTemplate;


    /**
     * 用户登录
     * @param request   登录请求
     * @param user      登录的user信息
     * @return
     */
    @PostMapping("/login")
    public R<User> login(HttpServletRequest request, @RequestBody User user){

        //1、将页面提交的密码password进行md5加密处理
        String password = user.getPassword();
        //password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User u = userService.getOne(queryWrapper);
        System.out.println(u.getUsername());
        //3、如果没有查询到则返回登录失败结果
        if(u == null){
            return R.error("登录失败");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if(!u.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
//        if(u.getStatus() == 0){
//            return R.error("账号已禁用");
//        }

        //6、登录成功，将员工id存入Session并返回登录成功结果
//        request.getSession().setAttribute("user",u.getId());
        return R.success(u);
    }
    /**
     * 注册
     * @param request   注册请求
     * @param user      注册的user信息
     * @return
     */
    @PostMapping("/register")
    public String register(HttpServletRequest request, @RequestBody User user){
        System.out.println(user);
        //1、将页面提交的密码password进行md5加密处理
//        String password = user.getPassword();
        //password = DigestUtils.md5DigestAsHex(password.getBytes());
        userService.save(user);
        //6、登录成功，将员工id存入Session并返回登录成功结果
//        request.getSession().setAttribute("user",user.getId());
        return "success";
    }
    /**
     * 用户退出
     * @param request   退出登录请求
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理Session中保存的当前登录员工的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }

    /**
     * 新增用户
     * @param request   新增用户请求
     * @param user      新增的user
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody User user){
        log.info("新增用户，用户信息：{}", user.toString());

        //设置初始密码123456，需要进行md5加密处理
        //user.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //user.setPassword("666666");
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //获得当前登录用户的id
        //Long empId = (Long) request.getSession().getAttribute("employee");

        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        userService.save(user);

        return R.success("新增用户成功");
    }

    /**
     * 管理员查看用户信息，分页查询
     * @param page          当前页面
     * @param pageSize      页面大小
     * @param name          过滤条件
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize = {},name = {}" ,page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);

        //构造条件构造器
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name), User::getUsername,name);
        queryWrapper.ne(User::getUsername,"admin");
        //添加排序条件
//        queryWrapper.orderByDesc(User::getUpdatedAt);

        //执行查询
        userService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改用户信息
     * @param request   修改请求
     * @param user      修改后的user信息
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody User user){
        log.info(user.toString());
        //userService.updateById(user);
        User u=userService.getUserByName(user.getUsername());
        user.setId(u.getId());

        userService.updateById(user);
        System.out.println("?????"+user.toString());
        return R.success("用户信息修改成功");
    }

    /**
     * 根据id查询用户信息
     * @param id    用户id
     * @return
     */
    @GetMapping("/{id}")
    public R<User> getById(@PathVariable int id){
        log.info("根据id查询用户信息...");
        User user = userService.getById(id);
        if(user != null){
            return R.success(user);
        }
        return R.error("没有查询到对应用户信息");
    }
    /**
     * 根据username查询用户信息
     * @param username  用户名
     * @return
     */
    @GetMapping
    public R<User> getUserByName(@RequestParam String username){
        log.info("根据username查询用户信息...");

        User user = userService.getUserByName(username);
        if(user != null){
            return R.success(user);
        }
        return R.error("用户不存在");
    }
}
