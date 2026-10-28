
package campuscare.model;

public class Admin extends User {

    public Admin(int id, String name, String username, String email) {
        super(id, name, username, email);
    }

    @Override
    public String getRole() {
        return "ADMIN";
    }

    @Override
    public String getDashboardMessage() {
        return "Welcome, Administrator! Manage campus facilities.";
    }
}
