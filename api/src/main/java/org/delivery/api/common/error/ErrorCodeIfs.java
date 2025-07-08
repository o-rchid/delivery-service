package org.delivery.api.common.error;

public interface ErrorCodeIfs  {

    Integer getHttpsStatusCode();

    Integer getErrorCode();

    String getDescription();
}
