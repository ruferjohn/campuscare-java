
package campuscare.service;

import campuscare.model.User;

public final class AuthorizationService {

    private AuthorizationService() {
    }

    public static boolean canViewAllReports(User user) {
        return hasRole(user, "ADMIN")
            || hasRole(user, "MAINTENANCE");
    }

    public static boolean canUpdateReportStatus(User user) {
        return hasRole(user, "ADMIN")
            || hasRole(user, "MAINTENANCE");
    }

    // Whether the user can delete at least one report.
    public static boolean canDeleteReports(User user) {
        return hasRole(user, "ADMIN")
            || hasRole(user, "REPORTER");
    }

    // Whether the user can delete this specific report.
    public static boolean canDeleteReport(
            User user, int reporterId) {

        if (user == null) {
            return false;
        }

        if (hasRole(user, "ADMIN")) {
            return true;
        }

        return hasRole(user, "REPORTER")
            && user.getUserId() == reporterId;
    }

    public static boolean canSubmitReports(User user) {
        return hasRole(user, "ADMIN")
            || hasRole(user, "MAINTENANCE")
            || hasRole(user, "REPORTER");
    }

    private static boolean hasRole(User user, String role) {
        return user != null
            && user.getRole() != null
            && role.equalsIgnoreCase(user.getRole());
    }
}
