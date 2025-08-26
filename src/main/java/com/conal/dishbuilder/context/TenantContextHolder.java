package com.conal.dishbuilder.context;

import java.util.UUID;

public class TenantContextHolder {
    private static final ThreadLocal<UUID> tenantContext = new ThreadLocal<>();

    public static void setTenantContext(UUID tenantId) {
        tenantContext.set(tenantId);
    }

    public static UUID getTenantContext() {
        return tenantContext.get();
    }

    public static void clearTenantContext() {
        tenantContext.remove();
    }
}
