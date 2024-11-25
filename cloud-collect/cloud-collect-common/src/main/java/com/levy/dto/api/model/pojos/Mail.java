package com.levy.dto.api.model.pojos;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sca_cloud_mail")
public class Mail {

    @TableId("mail_id")
    private String mailId;

    @TableField("subject")
    private String subject;

    @TableField("text")
    private String text;

    @TableField("status")
    private Integer status;

    @TableField("send_time")
    private LocalDateTime sendTime;

    @TableField("to_address")
    private String toAddress; // 收件人邮箱

    @TableField("from_address")
    private String fromAddress;

}

