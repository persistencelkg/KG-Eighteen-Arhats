package org.lkg.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SysConfig implements Serializable {
    private String variable;

    private String value;

    private LocalDateTime setTime;

    private String setBy;

    private static final long serialVersionUID = 1L;

    public String getVariable() {
        return variable;
    }

    public void setVariable(String variable) {
        this.variable = variable == null ? null : variable.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public LocalDateTime getSetTime() {
        return setTime;
    }

    public void setSetTime(LocalDateTime setTime) {
        this.setTime = setTime;
    }

    public String getSetBy() {
        return setBy;
    }

    public void setSetBy(String setBy) {
        this.setBy = setBy == null ? null : setBy.trim();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SysConfig other = (SysConfig) that;
        return (this.getVariable() == null ? other.getVariable() == null : this.getVariable().equals(other.getVariable()))
            && (this.getValue() == null ? other.getValue() == null : this.getValue().equals(other.getValue()))
            && (this.getSetTime() == null ? other.getSetTime() == null : this.getSetTime().equals(other.getSetTime()))
            && (this.getSetBy() == null ? other.getSetBy() == null : this.getSetBy().equals(other.getSetBy()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getVariable() == null) ? 0 : getVariable().hashCode());
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        result = prime * result + ((getSetTime() == null) ? 0 : getSetTime().hashCode());
        result = prime * result + ((getSetBy() == null) ? 0 : getSetBy().hashCode());
        return result;
    }
}