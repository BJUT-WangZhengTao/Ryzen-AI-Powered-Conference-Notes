package com.siteapplet.siteproject.domain;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author: danghongbo
 * ===============================
 * Created with IDEA
 * Date: 2024/3/12
 * Time: 15:57
 * ===============================
 */
@Setter
@Getter
public class ChatAiMessage {
    private String id;

    private String chatAiId;

    private String uid;

    // 聊天内容
    private String message;

    // 聊天类型：user bot
    private String type;

    private Date createdDt;//创建时间
    private Date updatedDt;//更新时间
}