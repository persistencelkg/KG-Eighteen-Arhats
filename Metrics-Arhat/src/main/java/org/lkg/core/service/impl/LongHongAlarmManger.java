package org.lkg.core.service.impl;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.util.TimeUtils;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.bo.MeterBo;
import org.lkg.core.bo.MeterTypeEnum;
import org.lkg.core.config.LongHongAlarmConfig;
import org.lkg.core.expression.JexlExpressionManger;
import org.lkg.core.init.LongHengMeterRegistry;
import org.lkg.core.service.AlarmWindowManger;
import org.lkg.ding.DingDingMsg;
import org.lkg.ding.DingDingUtil;
import org.lkg.simple.JacksonUtil;
import org.lkg.simple.ObjectUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/15 4:26 PM
 */
@Slf4j
public class LongHongAlarmManger {
    public static void alarm(Map<Meter.Id, MeterBo> meterBoMap) {
        LongHongAlarmConfig instance = LongHongAlarmConfig.getInstance();
        Map<String, List<LongHongAlarmConfig.AlarmRule>> alarmRulesMap = Optional.ofNullable(instance).map(LongHongAlarmConfig::getAlarmRule).orElse(null);
        if (ObjectUtil.isEmpty(alarmRulesMap)) {
            log.debug("not config alarm rules");
            return;
        }
        String phone =  Objects.isNull(instance.getDuty()) ? "" : instance.getDuty().getPhone();
        meterBoMap.forEach((k, v) -> {
            List<LongHongAlarmConfig.AlarmRule> alarmRules = alarmRulesMap.get(k.getName());
            for (LongHongAlarmConfig.AlarmRule alarmRule : alarmRules) {
                if (needAlarm(alarmRule, k, v)) {
                    // http.request.success_p995:
                    String alarmContent = getAlarmContent(alarmRule, v);
                    DingDingUtil.sendMessage(DingDingMsg.createText(alarmContent), instance.getUrl(), instance.getSecret(), false, phone);
                }
            }
        });

    }

    private static String getAlarmContent(LongHongAlarmConfig.AlarmRule alarmRule, MeterBo vo) {
        Map<String, String> notInternalTag = vo.getNotInternalTag();
        long baseTimeUnit = LongHengMeterRegistry.getInstance().getInterval().getSeconds();
        String msg = String.format("连续%s秒，命中规则:%s", baseTimeUnit, alarmRule.getExpression());
        return String.format("[指标:%s][tag:%s][msg:%s]", vo.getNamespace(), notInternalTag, msg);
    }

    private static boolean needAlarm(LongHongAlarmConfig.AlarmRule alarmRule, Meter.Id id, MeterBo v) {
        String expression = alarmRule.getExpression();
        Map<String, Object> map = JacksonUtil.objToMap(v);
        boolean isCount = expression.contains(MeterTypeEnum.count.name);
        if (id.getType() == Meter.Type.TIMER || id.getType() == Meter.Type.LONG_TASK_TIMER) {
            if (!isCount) {
                map.replaceAll((k, value) -> TimeUtils.convert((Double) value, LongHengMeterRegistry.getInstance().getBaseTimeUnit(), TimeUnit.MILLISECONDS));
            }
        }
        String key = id.getName() + expression;
        if (isCount && !matchExpression(expression, map)) {
            AlarmWindowManger.remove(key);
            return false;
        }
        AlarmWindowManger orCreate = AlarmWindowManger.getOrCreate(key);
        orCreate.failCount();
        if (matchExpression(expression, orCreate.paramForCountMap(id.getName()))) {
            AlarmWindowManger.remove(key);
            return true;
        }
        // 周期统计，TODO 通过RX统计窗口
        return false;
    }

    private static boolean matchExpression(String expression, Map<String, Object> map) {
        JexlExpressionManger instance = JexlExpressionManger.getInstance();
        instance.setExpression(expression);
        return instance.match(map);
    }


}
