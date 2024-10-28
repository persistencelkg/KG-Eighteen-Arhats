package org.lkg.preheat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Description:
 * Author: 李开广
 * Date: 2024/10/23 8:58 PM
 */
public interface WarmupService {

    Logger log = LoggerFactory.getLogger(WarmupService.class.getSimpleName());

    default void warmUp(Supplier<?>... suppliers) {
        for (Supplier<?> supplier : suppliers) {
            try {
                supplier.get();
            } catch (Exception ignored) {
                log.warn("[warm up]:err:{}", ignored.getMessage(), ignored);
            }
        }
    }
}
