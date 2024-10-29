package org.lkg.simple;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lkg.enums.StringEnum;

/**
 * Description: 版本控制工具
 * Author: 李开广
 * Date: 2024/10/29 8:36 PM
 */
public class VersionControlUtil {

    /**
     * @param versionBase base
     * @param version     target version
     * @return target version >= versionBase
     */
    public static boolean gte(VersionBase versionBase, String version) {
        return atLeast(versionBase, version, 3, true);
    }

    public static boolean gt(VersionBase versionBase, String version) {
        return atLeast(versionBase, version, 3, false);
    }


    /**
     * @param versionBase base
     * @param version     target version
     * @return target version <= versionBase
     */
    public static boolean lte(VersionBase versionBase, String version) {
        return !atLeast(versionBase, version, 3, true);
    }

    public static boolean lt(VersionBase versionBase, String version) {
        return atLeast(versionBase, version, 3, false);
    }


    public static boolean atLeast(VersionBase versionBase, String version, int length, boolean includeEq) {
        if (ObjectUtil.isEmpty(version) || length < 2) {
            return false;
        }
        String s1 = versionBase.getVersion().substring(1);
        String s2 = version;
        if (s2.startsWith("v")) {
            s2 = version.substring(1);
        }
        String[] from = s1.split(StringEnum.getEscapeDot());
        String[] to = s2.split(StringEnum.getEscapeDot());
        int minLen = Math.min(from.length, to.length);
        // 版本划分不合法
        if (minLen > length) {
            return false;
        }
        for (int i = 0; i < length; i++) {
            try {
                int f1 = Integer.parseInt(from[i]);
                int f2 = Integer.parseInt(to[i]);
                if (f1 < f2) {
                    return true;
                } else if (f1 > f2) {
                    return false;
                }
            } catch (Exception ignored) {
                return false;
            }
        }
        return includeEq ? from.length <= to.length : from.length < to.length;
    }


    @Getter
    @AllArgsConstructor
    public enum VersionBase {
        V_1_0_0("v1.0.0"),
        V_1_13_14("v1.13.14");
        private final String version;
    }

    public static void main(String[] args) {
        System.out.println(VersionControlUtil.gt(VersionBase.V_1_13_14, "v1.23.1"));
        System.out.println(VersionControlUtil.gt(VersionBase.V_1_13_14, "1.13.14"));
        System.out.println(VersionControlUtil.gt(VersionBase.V_1_13_14, "1.3.14"));

    }
}
