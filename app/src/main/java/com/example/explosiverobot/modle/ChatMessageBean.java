package com.example.explosiverobot.modle;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

@Entity//标识实体类，greenDAO会映射成sqlite的一个表，表名为实体类名的大写形式
public class ChatMessageBean {
    @Id(autoincrement = true)//该字段的类型为long或Long类型，autoincrement设置是否自动增长
    private Long id;
    @Property(nameInDb = "messagetype")// 标识该属性在表中对应的列名称, nameInDb设置名称
    private int messagetype;//消息类型（发送或接受）
    @Property(nameInDb = "messageContent")
    private String messageContent;//文本消息
    @Property(nameInDb = "time")
    private String time;//时间
    @Property(nameInDb = "sendState")
    private int sendState;//消息状态（发送中，发送完成，发送失败）
    //图片
    @Property(nameInDb = "imageUrl")
    private String imageUrl;
    @Property(nameInDb = "imageLocal")
    private String imageLocal;
    //语音
    @Property(nameInDb = "voiceAnswer")
    private String voiceAnswer;
    @Property(nameInDb = "action")
    private String action;
    @Property(nameInDb = "expression")
    private String expression;
    @Property(nameInDb = "actionData")
    private String actionData;
    @Property(nameInDb = "expressionData")
    private String expressionData;
    //视频
    @Property(nameInDb = "videoName")
    private String videoName;
    @Property(nameInDb = "videoUrl")
    private String videoUrl;
    @Generated(hash = 1380486628)
    public ChatMessageBean(Long id, int messagetype, String messageContent,
                           String time, int sendState, String imageUrl, String imageLocal,
                           String voiceAnswer, String action, String expression, String actionData,
                           String expressionData, String videoName, String videoUrl) {
        this.id = id;
        this.messagetype = messagetype;
        this.messageContent = messageContent;
        this.time = time;
        this.sendState = sendState;
        this.imageUrl = imageUrl;
        this.imageLocal = imageLocal;
        this.voiceAnswer = voiceAnswer;
        this.action = action;
        this.expression = expression;
        this.actionData = actionData;
        this.expressionData = expressionData;
        this.videoName = videoName;
        this.videoUrl = videoUrl;
    }
    @Generated(hash = 1557449535)
    public ChatMessageBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public int getMessagetype() {
        return this.messagetype;
    }
    public void setMessagetype(int messagetype) {
        this.messagetype = messagetype;
    }
    public String getMessageContent() {
        return this.messageContent;
    }
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public int getSendState() {
        return this.sendState;
    }
    public void setSendState(int sendState) {
        this.sendState = sendState;
    }
    public String getImageUrl() {
        return this.imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getImageLocal() {
        return this.imageLocal;
    }
    public void setImageLocal(String imageLocal) {
        this.imageLocal = imageLocal;
    }
    public String getVoiceAnswer() {
        return this.voiceAnswer;
    }
    public void setVoiceAnswer(String voiceAnswer) {
        this.voiceAnswer = voiceAnswer;
    }
    public String getAction() {
        return this.action;
    }
    public void setAction(String action) {
        this.action = action;
    }
    public String getExpression() {
        return this.expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public String getActionData() {
        return this.actionData;
    }
    public void setActionData(String actionData) {
        this.actionData = actionData;
    }
    public String getExpressionData() {
        return this.expressionData;
    }
    public void setExpressionData(String expressionData) {
        this.expressionData = expressionData;
    }
    public String getVideoName() {
        return this.videoName;
    }
    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
    public String getVideoUrl() {
        return this.videoUrl;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }


}
