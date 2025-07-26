package org.delivery.db.storeuser.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum StoreUserStatus {

    REGISTERD("등록"),
    UNREGISTERD("해지"),
    ;

    private String description;
}
