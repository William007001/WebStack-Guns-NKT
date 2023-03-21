package com.nikati.manage;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.stylefeng.roses.kernel.model.auth.context.LoginUserHolder;
import com.baomidou.mybatisplus.toolkit.IdWorker;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 智云单点工具类
 *
 * @author William
 * @date 2023/2/17 16:55
 */
@Data
@Component
@ConfigurationProperties(prefix = "platform.zcloud.sso")
public class ZCloudSSOKit {


    @Value(value = "${" + GlobalConsts.ZCLOUD_SSO_CLIENT_ID + "}")
    private String ssoClientId;

    @Value(value = "${" + GlobalConsts.ZCLOUD_SSO_SERVER_URL + "}")
    private String ssoServerUrl;

    @Value(value = "${" + GlobalConsts.ZCLOUD_SSO_REDIRECT_URL + "}")
    private String ssoRedirectUrl;

    @Value(value = "${" + GlobalConsts.ZCLOUD_SSO_USER_INFO_URL + "}")
    private String ssoUserInfoUrl;

    @Value(value = "${" + GlobalConsts.ZCLOUD_SSO_SUCCESS_URL + "}")
    private String ssoSuccessUrl;

    @Value(value = "${" + GlobalConsts.ZCLOUD_SSO_LOGOUT_URL + "}")
    private String ssoLogoutUrl;


    public String getLoginUrl() {
        return StrUtil.format(
                "{}?client_id={}&redirect_url={}&state={}&scope=read",
                ssoServerUrl,
                ssoClientId,
                URLUtil.encode(ssoRedirectUrl),
                IdWorker.getId()
        );
    }

    public String getAuthorizeUrl(String accessToken) {
        return StrUtil.format("{}?accessToken={}", ssoUserInfoUrl, accessToken);
    }


    public void logout(String accessToken) {
        HttpUtil.get(StrUtil.format("{}?accessToken={}", ssoLogoutUrl, accessToken));
    }

}
