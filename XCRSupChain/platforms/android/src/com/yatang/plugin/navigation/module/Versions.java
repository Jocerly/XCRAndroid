package com.yatang.plugin.navigation.module;

/**
 * Created by liuping on 2017/10/20.
 */

public class Versions {
   public String moduleId;
    public String version;

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Versions(String moduleId, String version) {
        this.moduleId = moduleId;
        this.version = version;
    }

    public boolean isEmpty() {
        return moduleId == null || version == null || moduleId.trim().isEmpty() || version.trim().isEmpty() || "null".equals(moduleId) || "null".equals(version);
    }

    public boolean isInvalid() {
        return isEmpty() || "0".equals(moduleId) || "null".equals(moduleId) || "0".equals(version) || "null".equals(version);
    }

    public void copy(Versions version) {
        this.moduleId = version.moduleId;
        this.version = version.version;
    }
}

