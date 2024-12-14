package com.hospital.quickfit;

import java.sql.*;

public class HospitalQuickFitAllocation {
    private static final String DB_URL = "jdbc:mysql://localhost:3308/hospital_memory";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "password";

    public String runQuickFitAlgorithm() {
        StringBuilder result = new StringBuilder();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // Starting transaction
            connection.setAutoCommit(false);

            // Fetching categorized free blocks
            ResultSet block50List = connection.createStatement().executeQuery("SELECT * FROM memory_blocks WHERE block_size = 50 AND allocated = FALSE");
            ResultSet block100List = connection.createStatement().executeQuery("SELECT * FROM memory_blocks WHERE block_size = 100 AND allocated = FALSE");
            ResultSet block200List = connection.createStatement().executeQuery("SELECT * FROM memory_blocks WHERE block_size = 200 AND allocated = FALSE");

            // Fetching all hospital service requests
            ResultSet services = connection.createStatement().executeQuery("SELECT * FROM hospital_services");

            // SQL Queries for allocation
            String allocateBlockQuery = "UPDATE memory_blocks SET allocated = TRUE WHERE block_id = ?";
            String updateServiceQuery = "UPDATE hospital_services SET allocated_block = ? WHERE service_id = ?";

            try (PreparedStatement allocateBlock = connection.prepareStatement(allocateBlockQuery);
                 PreparedStatement updateService = connection.prepareStatement(updateServiceQuery)) {

                // Processing each hospital service
                while (services.next()) {
                    int serviceId = services.getInt("service_id");
                    int serviceSize = services.getInt("memory_requirement");
                    boolean allocated = false;

                    // Allocating based on size
                    ResultSet targetList = null;
                    if (serviceSize == 50) targetList = block50List;
                    else if (serviceSize == 100) targetList = block100List;
                    else if (serviceSize == 200) targetList = block200List;

                    if (targetList != null) {
                        while (targetList.next()) {
                            int blockId = targetList.getInt("block_id");

                            // Allocating block to the hospital service
                            allocateBlock.setInt(1, blockId);
                            if (allocateBlock.executeUpdate() > 0) {
                                updateService.setInt(1, blockId);
                                updateService.setInt(2, serviceId);
                                updateService.executeUpdate();
                                result.append("Service ").append(serviceId).append(" allocated to Block ").append(blockId).append("\n");
                                allocated = true;
                                break;
                            }
                        }
                    }

                    // If not allocated, report failure
                    if (!allocated) {
                        result.append("Service ").append(serviceId).append(" could not be allocated.\n");
                    }
                }

                // Commit transaction
                connection.commit();
                result.append("Memory Allocation Complete.\n");

            } catch (SQLException e) {
                connection.rollback();
                result.append("Error during allocation: ").append(e.getMessage()).append("\n");
            }
        } catch (SQLException e) {
            result.append("Database connection error: ").append(e.getMessage()).append("\n");
        }

        return result.toString();
    }

    public static void main(String[] args) {
        HospitalQuickFitAllocation allocator = new HospitalQuickFitAllocation();
        String result = allocator.runQuickFitAlgorithm();
        System.out.println(result);
    }
}
