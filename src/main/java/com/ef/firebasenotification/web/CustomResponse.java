package com.ef.firebasenotification.web;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CustomResponse {

    private String status;

    public CustomResponse(String status) {
        this.status = status;
    }

}
