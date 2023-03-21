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
package com.nikati.manage.modular.system.service.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.stylefeng.roses.core.datascope.DataScope;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.Condition;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.nikati.manage.ZCloudSSOKit;
import com.nikati.manage.core.common.constant.state.ManagerStatus;
import com.nikati.manage.core.shiro.ShiroKit;
import com.nikati.manage.modular.system.dao.UserMapper;
import com.nikati.manage.modular.system.model.Role;
import com.nikati.manage.modular.system.model.User;
import com.nikati.manage.modular.system.service.IRoleService;
import com.nikati.manage.modular.system.service.IUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <p>
 * 管理员表 服务实现类
 * </p>
 *
 * @author stylefeng123
 * @since 2018-02-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private ZCloudSSOKit zCloudSSOKit;
    @Resource
    private IRoleService roleService;

    @Override
    public int setStatus(Integer userId, int status) {
        return this.baseMapper.setStatus(userId, status);
    }

    @Override
    public int changePwd(Integer userId, String pwd) {
        return this.baseMapper.changePwd(userId, pwd);
    }

    @Override
    public List<Map<String, Object>> selectUsers(DataScope dataScope, String name, String beginTime, String endTime, Integer deptid) {
        return this.baseMapper.selectUsers(dataScope, name, beginTime, endTime, deptid);
    }

    @Override
    public int setRoles(Integer userId, String roleIds) {
        return this.baseMapper.setRoles(userId, roleIds);
    }

    @Override
    public User getByAccount(String account) {
        return this.baseMapper.getByAccount(account);
    }

    @Override
    public User initUser(String accessToken) {

        String result = HttpUtil.get(zCloudSSOKit.getAuthorizeUrl(accessToken));
        if (!JSONUtil.isJson(result) || JSONUtil.parseObj(result).getInt("code") != 200) {
            throw new RuntimeException("请稍后，服务器蛮忙");
        }
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getInteger("code") != 200) {
            throw new RuntimeException("请稍后，服务器蛮忙");
        }

        JSONObject data = jsonObject.getJSONObject("data");
        String account = data.getJSONObject("personInfoVO").getString("employeeNum");
        User user = baseMapper.getByAccount(account);
        if (user == null) {
            user = new User();
            user.setAccount(account);
            // 完善账号信息
            user.setSalt(ShiroKit.getRandomSalt(5));
            user.setPassword(ShiroKit.md5(account, user.getSalt()));
            user.setName(data.getJSONObject("personInfoVO").getString("displayName"));
            Role role = roleService.selectOne(new EntityWrapper<Role>().eq("tips", "INIT_DEFAULT"));
            user.setRoleid(role.getId().toString());
            user.setDeptid(24);
            user.setCreatetime(new Date());
            user.setStatus(ManagerStatus.OK.getCode());
            baseMapper.insert(user);
        }
        return user;
    }
}
