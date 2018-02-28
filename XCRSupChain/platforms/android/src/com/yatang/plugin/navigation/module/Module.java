package com.yatang.plugin.navigation.module;

import java.io.Serializable;

/**
 * Created by liuping on 2017/10/19.
 */

public class Module  implements Serializable{

    private String moduleId;
    private Versions version = new Versions("0","0");
    private  volatile int  status = UpdateStatus.UPDATE_STATUS_IDLE;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Module(String moduleId, Versions version) {
        this.moduleId = moduleId;
        this.version = version;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public Versions getVersion() {
        return version;
    }

    public void setVersion(Versions version) {
        this.version = version;
    }

    public Module() {

    }

    public void copy(Module module) {
        this.moduleId = module.moduleId;
        this.version = module.version;
    }
}
