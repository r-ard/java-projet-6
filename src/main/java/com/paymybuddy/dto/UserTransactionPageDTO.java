package com.paymybuddy.dto;

public class UserTransactionPageDTO {
    private String pageValue;

    private boolean isCurrent;

    public UserTransactionPageDTO(String pageValue, boolean isCurrent) {
        this.pageValue = pageValue;
        this.isCurrent = isCurrent;
    }

    public String getPageValue() {
        return pageValue;
    }

    public void setPageValue(String pageValue) {
        this.pageValue = pageValue;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
