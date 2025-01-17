package org.lkg.core;

import lombok.extern.slf4j.Slf4j;
import org.lkg.config.DynamicConfigOption;
import org.lkg.config.EnableOpenArhatOptionConfig;
import org.lkg.utils.ObjectUtil;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.Set;

/**
 * Description:
 * Author: 李开广
 * Date: 2025/1/3 4:50 PM
 */
@Slf4j
public class DynamicConfigImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        MultiValueMap<String, Object> allAnnotationAttributes = importingClassMetadata.getAllAnnotationAttributes(EnableOpenArhatOptionConfig.class.getName());
        if (ObjectUtil.isEmpty(allAnnotationAttributes)) {
            return new String[0];
        }
        Set<String> classConfigList = new HashSet<>();
        DynamicConfigOption[] configOptions = (DynamicConfigOption[]) allAnnotationAttributes.get("type").get(0);
        for (DynamicConfigOption dynamicConfigOption : configOptions) {
            Class<?>[] configClass = dynamicConfigOption.getConfigClass();
            for (Class<?> aClass : configClass) {
                classConfigList.add(aClass.getName());
            }
        }
        if (log.isDebugEnabled()) {
            log.info("注入配置:{}", classConfigList);
        }
        return classConfigList.toArray(new String[0]);
    }
}
