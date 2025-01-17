package org.lkg.apollo;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.build.ApolloInjector;
import com.ctrip.framework.apollo.core.MetaDomainConsts;
import com.ctrip.framework.apollo.core.enums.Env;
import com.ctrip.framework.apollo.model.ConfigChange;
import com.ctrip.framework.apollo.util.ConfigUtil;
import lombok.extern.slf4j.Slf4j;
import org.lkg.core.DynamicConfigService;
import org.lkg.core.KeyChangeHandler;
import org.lkg.utils.ObjectUtil;

import java.util.*;


/**
 * Description: 基于apollo 实现动态配置变更 & key change
 * Author: 李开广
 * Date: 2024/8/20 3:27 PM
 */
@Slf4j
public class ApolloConfigService implements DynamicConfigService {

    private String env;


    private final Map<String, Config> namespaceMap = new HashMap<>();
    private final Map<String, HashSet<KeyChangeHandler>> keyChangeListener = new HashMap<>();


    private static ApolloConfigService INSTANCE;

    public synchronized static ApolloConfigService getInstance() {
        if (Objects.isNull(INSTANCE)) {
            INSTANCE = new ApolloConfigService();
        }
        return INSTANCE;
    }

    public ApolloConfigService() {
        init();
    }

    private void init() {
        ConfigUtil configUtil = ApolloInjector.getInstance(ConfigUtil.class);
        Env env = configUtil.getApolloEnv();
        String domain = MetaDomainConsts.getDomain(env);
        if (Objects.equals(domain, MetaDomainConsts.DEFAULT_META_URL)) {
            return;
        }
        if (env != Env.UNKNOWN) {
            this.env = env.name();
        }
    }

    public void registerNameSpace(String namespace) {
        Config config = ConfigService.getConfig(namespace);
        if (ObjectUtil.isEmpty(config)) {
            log.debug("ns:{} config empty", namespace);
            return;
        }
        namespaceMap.put(namespace, config);
        config.addChangeListener(ref -> {
            Set<String> changedKeys = ref.changedKeys();
            changedKeys.forEach(key -> {
                if (keyChangeListener.containsKey(key)) {
                    // dispatch event handler
                    ConfigChange change = ref.getChange(key);
                    log.info("config key:{} {}, old value:{}, new val:{}", key, change.getChangeType().name(), change.getOldValue(), change.getNewValue());
                    keyChangeListener.get(key).forEach(val -> val.onChange(change));
                }
            });
        });
    }


    @Override
    public String getStrValue(String key, String def) {
        Collection<Config> values = namespaceMap.values();
        if (ObjectUtil.isEmpty(values)) {
            return null;
        }
        for (Config config : values) {
            String strValue = config.getProperty(key, def);
            if (ObjectUtil.isNotEmpty(strValue)) {
                return strValue;
            }
        }
        return null;
    }

    @Override
    public void addChangeKeyPostHandler(String key, KeyChangeHandler keyChangeHandler) {
        keyChangeListener.computeIfAbsent(key, ref -> new HashSet<>()).add(keyChangeHandler);
    }


    public boolean existKeyChange(String key) {
        HashSet<KeyChangeHandler> keyChangeHandlers = keyChangeListener.get(key);
        if (ObjectUtil.isNotEmpty(keyChangeHandlers)) {
            if (log.isDebugEnabled()) {
                log.debug("key:{} exist handler size:{}", key, keyChangeHandlers.size());
            }
            return true;
        }
        return false;
    }

    @Override
    public Set<KeyChangeHandler> setAllChangeKeySet(String key) {
        return keyChangeListener.get(key);
    }
}
