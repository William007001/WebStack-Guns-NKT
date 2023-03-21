/**
 * Copyright 2018-2020 stylefeng & fengshuonan (https://gitee.com/stylefeng)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nikati.manage.modular.system.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.stylefeng.roses.core.base.controller.BaseController;
import cn.stylefeng.roses.core.util.ToolUtil;
import com.google.code.kaptcha.Constants;
import com.nikati.manage.GlobalConsts;
import com.nikati.manage.ZCloudSSOKit;
import com.nikati.manage.core.common.exception.InvalidKaptchaException;
import com.nikati.manage.core.common.node.MenuNode;
import com.nikati.manage.core.log.LogManager;
import com.nikati.manage.core.log.factory.LogTaskFactory;
import com.nikati.manage.core.shiro.ShiroKit;
import com.nikati.manage.core.shiro.ShiroUser;
import com.nikati.manage.core.util.ApiMenuFilter;
import com.nikati.manage.core.util.KaptchaUtil;
import com.nikati.manage.modular.system.model.User;
import com.nikati.manage.modular.system.service.IMenuService;
import com.nikati.manage.modular.system.service.IUserService;

import net.sf.ehcache.Ehcache;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.stylefeng.roses.core.util.HttpContext.getIp;

/**
 * 登录控制器
 *
 * @author fengshuonan
 * @Date 2017年1月10日 下午8:25:24
 */
@Controller
public class LoginController extends BaseController {

    @Autowired
    private IMenuService menuService;

    @Autowired
    private IUserService userService;

    @Resource
    private ZCloudSSOKit zCloudSSOKit;
    @Resource
    private EhCacheManager ehCacheManager;

    /**
     * 跳转到主页
     */
    @RequestMapping(value = "/authorize", method = RequestMethod.GET)
    public String authorize(HttpServletRequest request, Model model) {
        User user = userService.initUser(request.getParameter(GlobalConsts.ACCESS_TOKEN));
        ehCacheManager.getCache("CONSTANT").put(user.getAccount(), request.getParameter(GlobalConsts.ACCESS_TOKEN));
        //获取菜单列表
//        if (StrUtil.isNotEmpty(user.getRoleid())) {
//            List<Integer> roleList = Arrays.asList(user.getRoleid().split(",")).stream().map(item -> {
//                return Integer.parseInt(item);
//            }).collect(Collectors.toList());
//            if (CollUtil.isNotEmpty(roleList)) {
//                List<MenuNode> menus = menuService.getMenusByRoleIds(roleList);
//                List<MenuNode> titles = MenuNode.buildTitle(menus);
//                titles = ApiMenuFilter.build(titles);
//
//                model.addAttribute("titles", titles);
//            }
//        }

        Subject currentUser = ShiroKit.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getAccount(), user.getAccount().toCharArray());

        token.setRememberMe(true);

        currentUser.login(token);

        ShiroUser shiroUser = ShiroKit.getUser();
        super.getSession().setAttribute("shiroUser", shiroUser);
        super.getSession().setAttribute("username", shiroUser.getAccount());

        LogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser.getId(), getIp()));

        ShiroKit.getSession().setAttribute("sessionFlag", true);

        return REDIRECT + "/admin";
    }

    /**
     * 跳转到主页
     */
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String index(Model model) {
        //获取菜单列表
        List<Integer> roleList = ShiroKit.getUser().getRoleList();
        if (roleList == null || roleList.size() == 0) {
            ShiroKit.getSubject().logout();
            model.addAttribute("tips", "该用户没有角色，无法登陆");
            return "/login.html";
        }
        List<MenuNode> menus = menuService.getMenusByRoleIds(roleList);
        List<MenuNode> titles = MenuNode.buildTitle(menus);
        titles = ApiMenuFilter.build(titles);

        model.addAttribute("titles", titles);

        //获取用户头像
        Integer id = ShiroKit.getUser().getId();
        User user = userService.selectById(id);
        String avatar = user.getAvatar();
        model.addAttribute("avatar", avatar);

        return "/admin.html";
    }

    /**
     * 跳转到登录页面
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletResponse response) {
        if (ShiroKit.isAuthenticated() || ShiroKit.getUser() != null) {
            return REDIRECT + "/admin";
        } else {
            try {
                response.sendRedirect(zCloudSSOKit.getLoginUrl());
            } catch (Exception e) {

            }
            return null;
        }
    }

    /**
     * 点击登录执行的动作
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginVali() {

        String username = super.getPara("username").trim();
        String password = super.getPara("password").trim();
        String remember = super.getPara("remember");

        //验证验证码是否正确
        if (KaptchaUtil.getKaptchaOnOff()) {
            String kaptcha = super.getPara("kaptcha").trim();
            String code = (String) super.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
            if (ToolUtil.isEmpty(kaptcha) || !kaptcha.equalsIgnoreCase(code)) {
                throw new InvalidKaptchaException();
            }
        }

        Subject currentUser = ShiroKit.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(username, password.toCharArray());

        if ("on".equals(remember)) {
            token.setRememberMe(true);
        } else {
            token.setRememberMe(false);
        }

        currentUser.login(token);

        ShiroUser shiroUser = ShiroKit.getUser();
        super.getSession().setAttribute("shiroUser", shiroUser);
        super.getSession().setAttribute("username", shiroUser.getAccount());

        LogManager.me().executeLog(LogTaskFactory.loginLog(shiroUser.getId(), getIp()));

        ShiroKit.getSession().setAttribute("sessionFlag", true);

        return REDIRECT + "/admin";
    }

    /**
     * 退出登录
     */
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOut() {
        ShiroUser shiroUser =(ShiroUser) ShiroKit.getSubject().getPrincipal();
        Object obj = ehCacheManager.getCache("CONSTANT").get(shiroUser.getAccount());
        if (obj != null) {
            String accessToke = obj.toString();
            LogManager.me().executeLog(LogTaskFactory.exitLog(ShiroKit.getUser().getId(), getIp()));
            ShiroKit.getSubject().logout();
            deleteAllCookie();
            zCloudSSOKit.logout(accessToke);
        }
        return REDIRECT + zCloudSSOKit.getLoginUrl();
    }
}
