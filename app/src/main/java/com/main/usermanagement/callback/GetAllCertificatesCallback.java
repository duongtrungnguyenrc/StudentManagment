package com.main.usermanagement.callback;

import com.main.usermanagement.models.Certificate;

import java.util.List;

public interface GetAllCertificatesCallback {
    void onSuccess(List<Certificate> certificates);
    void onError(Exception e);
}
