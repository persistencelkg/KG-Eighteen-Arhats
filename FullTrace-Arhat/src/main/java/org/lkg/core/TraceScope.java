package org.lkg.core;

import java.io.Closeable;

/**
 * Description: 具备自动关闭资源的trace
 * 同时具备保存上文的能力
 * Author: 李开广
 * Date: 2024/9/24 1:49 PM
 */
public interface TraceScope extends Closeable {

}
