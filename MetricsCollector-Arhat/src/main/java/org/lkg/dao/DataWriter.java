package org.lkg.dao;

import org.lkg.core.bo.MeterBo;

import java.util.List;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/8/13 2:50 PM
 */
public interface DataWriter {

    void batchWrite(List<MeterBo> list);
}
