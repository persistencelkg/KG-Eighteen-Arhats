package org.lkg.ding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lkg.utils.ObjectUtil;
import org.lkg.utils.RegxUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * 1. 每个机器人每分钟最多发送20条消息到群里，如果超过20条，会限流10分钟。
 * 2. 钉钉标准版接口累计可调用次数为1万次/月
 * Author: 李开广
 * Date: 2024/2/28 4:40 PM
 */
@Data
public abstract class DingDingMsg {

    public Map<String, Object> buildRequestBody(boolean atAll, String... at) {
        Map<String, Object> map = new HashMap<>();
        map.put("msgtype", getKey());
        map.put("at", Ding.buildDing(atAll, at));
        map.put(getKey(), this);
        return map;
    }

    protected abstract String getKey();

    public static Text createText(String content) {
        return new Text(content);
    }

    public static MarkDown createMarkDown(String title, String content) {
        return new MarkDown(title, content);
    }

    public static Link createLink(String title, String text, String picUrl, String actionUrl) {
        return new Link(title, text, actionUrl, picUrl);
    }

    @Data
    @Builder
    @AllArgsConstructor
    private static class Ding {

        private boolean isAtAll;

        private List<String> atUserIds;

        private List<String> atMobiles;

        public static Ding buildDing(boolean atAll, String... art) {
            Ding build = Ding.builder().isAtAll(atAll).build();
            if (ObjectUtil.isEmpty(art)) {
                return build;
            }
            List<String> list = Arrays.asList(art);
            if (RegxUtil.isValidPhoneNumber(art[0])) {
                build.setAtMobiles(list);
            } else {
                build.setAtUserIds(list);
            }
            return build;
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @AllArgsConstructor
    public static class Text extends DingDingMsg {
        private String content;

        @Override
        protected String getKey() {
            return "text";
        }
    }


    @EqualsAndHashCode(callSuper = false)
    @Data
    @AllArgsConstructor
    public static class Link extends DingDingMsg {
        private String title;
        private String text;
        private String messageUrl;
        private String picUrl;

        @Override
        protected String getKey() {
            return "link";
        }
    }


    @EqualsAndHashCode(callSuper = false)
    @Data
    @AllArgsConstructor
    public static class MarkDown extends DingDingMsg {
        private String title;
        private String text;

        @Override
        protected String getKey() {
            return "markdown";
        }
    }

    @EqualsAndHashCode(callSuper = false)
    @Data
    @AllArgsConstructor
    public static class ActionCard extends DingDingMsg {
        /**
         * 是否显示头像
         */
        private String hideAvatar;
        /**
         * 按钮位置：0 纵向  1 横向
         */
        private String btnOrientation;
        private String text;
        private String title;
        private List<ActionBtn> btns;

        @Override
        protected String getKey() {
            return "actionCard";
        }

        @Data
        @AllArgsConstructor
        private static class ActionBtn {
            /**
             * 跳转url
             */
            private String actionURL;
            /**
             * 按钮文案
             */
            private String title;
        }
    }

    public static void main(String[] args) {
        MarkDown test = new MarkDown("test", "ssss");
        System.out.println(test.buildRequestBody(false, "18830262673"));
//        System.out.println(test.buildRequestBody(false, "18830262673"));
        DingDingUtil.sendMessage(DingDingMsg.createMarkDown("--", "@18830262673 ## 测试\n\n ### <font color=\"#113311\">哈哈哈</font> "), "https://oapi.dingtalk.com/robot/send?access_token=37c083e9fffc155f5a5014cca52f01a07c8fee318da79e9a3f339bfd6a102e98", "SEC43e3ed7ae08747996b1d2a336c852b6482d50b2fac107e68269c329bb503c1f9", false, "18830262673");
    }
}
