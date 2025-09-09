package com.example.ws.microservices.firstmicroservices.oldstructure.serviceImpl.performance_gd;

import com.example.ws.microservices.firstmicroservices.oldstructure.repository.performance_gd.CheckNameFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckNameFileServiceCustom {

    private final CheckNameFileRepository checkNameFileRepository;

    /**
     * Inserts a new record with status "IN_PROGRESS" into the check_name_file table.
     *
     * @param conn     the JDBC connection
     * @param date     the upload date
     * @param userId   the ID of the user uploading the file
     * @param fileName the name of the file to reserve
     * @return the generated record ID
     * @throws SQLException if an SQL error occurs
     */
    public long insertInProgress(Connection conn, LocalDate date, String userId, String fileName) throws SQLException {
        String sql = "INSERT INTO performance_gd.check_name_file(date, id_user_loaded, name_file, status) " +
                "VALUES (?, ?, ?, 'IN_PROGRESS') RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, date);
            ps.setString(2, userId);
            ps.setString(3, fileName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
                throw new SQLException("No record inserted for file: " + fileName);
            }
        } catch (SQLException ex) {
            if ("23505".equals(ex.getSQLState())) {
                throw new SQLException("File " + fileName + " is already in progress or loaded.", ex.getMessage());
            }
            throw ex;
        }
    }


    /**
     * Updates the status of a record in the check_name_file table.
     *
     * @param conn      the JDBC connection
     * @param recordId  the record ID to update
     * @param newStatus the new status (e.g., "DONE" or "FAILED")
     * @throws SQLException if an SQL error occurs
     */
    public void updateStatus(Connection conn, long recordId, String newStatus) throws SQLException {
        String sql = "UPDATE performance_gd.check_name_file SET status = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setLong(2, recordId);
            ps.executeUpdate();
        }
    }

    /**
     * Deletes a record from the check_name_file table.
     *
     * @param conn     the JDBC connection
     * @param recordId the record ID to delete
     * @throws SQLException if an SQL error occurs
     */
    public void remove(Connection conn, long recordId) throws SQLException {
        String sql = "DELETE FROM performance_gd.check_name_file WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recordId);
            ps.executeUpdate();
        }
    }

}
