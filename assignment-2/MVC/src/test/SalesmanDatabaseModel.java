import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Year;
import java.time.YearMonth;

public class SalesmanDatabaseModel {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/payroll_system";
    private static final String DB_USER = "dbuser";
    private static final String DB_PASSWORD = "password";

    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    public void addSalesman(SalesmanModel salesman) {
        String query = "INSERT INTO salesmen (staffNumber, fullName, icNumber, bankAccountNumber, totalSalesAmount, numberOfCarsSold, salaryMonth, salaryYear) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, salesman.getSalesmanStaffID());
            ps.setString(2, salesman.getSalesmanFullName());
            ps.setString(3, salesman.getSalesmanICNum());
            ps.setString(4, salesman.getSalesmanBankAcc());
            ps.setDouble(5, salesman.getSalesmanTotalSalesAmount());
            ps.setInt(6, salesman.getSalesmanTotalSalesUnit());
            ps.setInt(7, salesman.getSalaryMonth().getMonthValue());
            ps.setInt(8, salesman.getSalaryYear().getValue());
            ps.executeUpdate();
            System.out.println("Salesman information saved successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to save salesman information.");
        }
    }

    public SalesmanModel searchSalesman(String staffNumber) {
        String query = "SELECT * FROM salesmen WHERE staffNumber = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, staffNumber);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                YearMonth salaryMonth = YearMonth.of(rs.getInt("salaryMonth"), 1);
                Year salaryYear = Year.of(rs.getInt("salaryYear"));

                return new SalesmanModel(
                        rs.getString("fullName"),
                        rs.getString("staffNumber"),
                        rs.getString("icNumber"),
                        rs.getString("bankAccountNumber"),
                        rs.getInt("numberOfCarsSold"),
                        rs.getDouble("totalSalesAmount"),
                        salaryMonth,
                        salaryYear,
                        ""
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to search for salesman.");
        }
        return null;
    }

    public void editSalesman(SalesmanModel salesman) {
        String query = "UPDATE salesmen SET fullName = ?, icNumber = ?, bankAccountNumber = ?, totalSalesAmount = ?, numberOfCarsSold = ?, salaryMonth = ?, salaryYear = ? WHERE staffNumber = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, salesman.getSalesmanFullName());
            ps.setString(2, salesman.getSalesmanICNum());
            ps.setString(3, salesman.getSalesmanBankAcc());
            ps.setDouble(4, salesman.getSalesmanTotalSalesAmount());
            ps.setInt(5, salesman.getSalesmanTotalSalesUnit());
            ps.setInt(6, salesman.getSalaryMonth().getMonthValue());
            ps.setInt(7, salesman.getSalaryYear().getValue());
            ps.setString(8, salesman.getSalesmanStaffID());
            ps.executeUpdate();
            System.out.println("Salesman information updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to update salesman information.");
        }
    }

    public void deleteSalesman(String staffNumber) {
        String query = "DELETE FROM salesmen WHERE staffNumber = ?";
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, staffNumber);
            ps.executeUpdate();
            System.out.println("Salesman deleted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to delete salesman.");
        }
    }

    public void reset() {
        String query = "DELETE FROM salesmen";
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(query);
            System.out.println("System reset successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to reset system.");
        }
    }

    public static void main(String[] args) {
        // Example usage:
        SalesmanDatabaseModel dbModel = new SalesmanDatabaseModel();

        // Example of adding a salesman
        SalesmanModel newSalesman = new SalesmanModel(
                "John Doe",
                "S001",
                "123456-78-9012",
                "123-456789-00",
                10,
                50000.0,
                YearMonth.of(2023, 6),
                Year.of(2023),
                "Bank of America"
        );
        dbModel.addSalesman(newSalesman);

        // Example of searching for a salesman
        SalesmanModel foundSalesman = dbModel.searchSalesman("S001");
        if (foundSalesman != null) {
            System.out.println("Found Salesman: " + foundSalesman);
        } else {
            System.out.println("Salesman not found.");
        }

        // Example of editing a salesman
        foundSalesman.setSalesmanFullName("Jane Smith");
        foundSalesman.setSalesmanTotalSalesAmount(60000.0);
        dbModel.editSalesman(foundSalesman);

        // Example of deleting a salesman
        dbModel.deleteSalesman("S001");

        // Example of resetting the system
        dbModel.reset();
    }
}

