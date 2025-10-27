package com.restlearningjourney.store.common;

public class ErrorDto {
    private String error;

    public ErrorDto(String error) {
        this.error = error;
    }

    public String getError() {
      return error;
    }
    public void setError(String error) {}
}
