
package campuscare.model;

public class Reporter extends User {

    public Reporter(int id, String name, String username, String email) {
        super(id, name, username, email);
    }

    @Override
    public String getRole() {
        return "REPORTER";
    }

    @Override
    public String getDashboardMessage() {
        return "Welcome! Submit an issue and track your reports.";
    }
}

