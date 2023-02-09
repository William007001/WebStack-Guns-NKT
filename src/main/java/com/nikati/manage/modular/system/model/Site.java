package com.nikati.manage.modular.system.model;

import lombok.Data;

/**
 * @Author jsnjfz
 * @Date 2019/7/21 14:44
 * 网站表
 */
@Data
public class Site {
    private Integer id;

    private Integer categoryId;

    private String title;

    private String categoryTitle;

    private String thumb;

    private String description;

    private String sitUrl;

    private String uatUrl;

    private String prodUrl;

    private String createTime;

    private String updateTime;
}
