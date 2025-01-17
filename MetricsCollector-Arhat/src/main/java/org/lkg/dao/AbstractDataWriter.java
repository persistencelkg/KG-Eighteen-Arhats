package org.lkg.dao;

import org.lkg.core.bo.MeterBo;
import org.lkg.core.config.LongHongConst;
import org.lkg.utils.ObjectUtil;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 3:22 PM
 */
public abstract class AbstractDataWriter<T> implements DataWriter {

    @Override
    public void batchWrite(List<MeterBo> list) {
        if (ObjectUtil.isEmpty(list)) {
            return;
        }
        try {
            // 分部门写入
            Map<String, List<MeterBo>> collect = list.stream().collect(Collectors.groupingBy(ref -> ref.getTagMap().get(LongHongConst.TagConst.DEPT)));
            collect.forEach((k, v) -> {
                List<T> collect1 = v.stream().map(this::convert).collect(Collectors.toList());
                this.customBatchWrite(k, collect1);
            });
        } finally {
            list.clear();
        }
    }

    protected abstract void customBatchWrite(String dept, List<T> list);

    protected abstract T convert(MeterBo meterBo);
}
