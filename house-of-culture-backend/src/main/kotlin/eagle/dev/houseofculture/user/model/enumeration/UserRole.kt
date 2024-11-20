package eagle.dev.houseofculture.user.model.enumeration

enum class UserRole(
    private vararg val allowedRoles: UserRole
) {
    CHILD(),
    CLIENT(CHILD),
    INSTRUCTOR(CLIENT, CHILD),
    EMPLOYEE(INSTRUCTOR, CLIENT, CHILD),
    ADMIN(EMPLOYEE, INSTRUCTOR, CLIENT, CHILD);

    fun isRoleAllowed(role: UserRole): Boolean {
        return this.allowedRoles.contains(role)
    }

    fun getAllRoles(): List<UserRole> {
        return this.allowedRoles.toList() + this
    }
}