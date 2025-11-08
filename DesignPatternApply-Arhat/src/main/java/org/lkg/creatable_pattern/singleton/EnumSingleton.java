package org.lkg.creatable_pattern.singleton;

/**
 * 既保证了反射安全也保证了序列化安全
 * @date: 2025/5/10 22:12
 * @author: li kaiguang
 */
public enum EnumSingleton{
    INSTANCE;

   public static EnumSingleton getInstance() {
       return INSTANCE;
   }
}
