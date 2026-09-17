package com.banking.authservice.utils;

import com.banking.authservice.entity.UserPermission;
import com.banking.authservice.entity.UserRole;

import java.util.Map;
import java.util.Set;


public class RolePermissionMapping {
	private static final Map<UserRole, Set<UserPermission>> roleBasedPermission = Map.of(UserRole.CUSTOMER,
			Set.of(UserPermission.SEND_MONEY, UserPermission.RECEIVE_MONEY),
			UserRole.ADMIN,
			Set.of(UserPermission.BLOCK_ACCOUNT, UserPermission.UNBLOCK_ACCOUNT));

	public static Set<UserPermission> getPermissionsByRole(UserRole role) {
		return roleBasedPermission.get(role);
	}
}
