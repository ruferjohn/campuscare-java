
package campuscare.model;

public class MaintenanceStaff extends User {

    public MaintenanceStaff(int id, String name, String username, String email) {
        super(id, name, username, email);
    }

    @Override
    public String getRole() {
        return "MAINTENANCE";
    }

    @Override
    public String getDashboardMessage() {
        return "Welcome! Review your assigned maintenance issues.";
    }
}
