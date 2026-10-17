package ru.astrainteractive.astrarating.gui.api.rating.change.fake

import ru.astrainteractive.astralibs.server.permission.KPermissible
import ru.astrainteractive.astralibs.server.permission.Permission

internal class FakeKPermissible(
    private val grantedPermissions: Set<Permission>,
    private val permissionSizes: Map<Permission, Int>
) : KPermissible {
    override fun hasPermission(permission: Permission): Boolean = permission in grantedPermissions

    override fun maxPermissionSize(permission: Permission): Int? = permissionSizes[permission]

    override fun minPermissionSize(permission: Permission): Int? = permissionSizes[permission]

    override fun permissionSizes(permission: Permission): List<Int> = listOfNotNull(permissionSizes[permission])
}
