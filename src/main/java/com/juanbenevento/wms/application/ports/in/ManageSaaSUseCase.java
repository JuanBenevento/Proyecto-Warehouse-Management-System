package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.Tenant;
import java.util.List;

public interface ManageSaaSUseCase {
    void onboardNewCustomer(OnboardCompanyCommand command);
    List<Tenant> getAllTenants();
}