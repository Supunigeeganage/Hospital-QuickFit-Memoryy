-- table for memory blocks
CREATE TABLE memory_blocks (
    block_id INT AUTO_INCREMENT PRIMARY KEY,
    block_size INT NOT NULL,
    allocated BOOLEAN DEFAULT FALSE
);

-- table for hospital services
CREATE TABLE hospital_services (
    service_id INT AUTO_INCREMENT PRIMARY KEY,
    service_name VARCHAR(50) NOT NULL,
    memory_requirement INT NOT NULL,
    allocated_block INT DEFAULT NULL,
    FOREIGN KEY (allocated_block) REFERENCES memory_blocks(block_id)
);

-- sample data for memory blocks
INSERT INTO memory_blocks (block_size, allocated) VALUES 
(50, FALSE), (50, FALSE), 
(100, FALSE), (100, FALSE), 
(200, FALSE);

--sample data for hospital services
INSERT INTO hospital_services (service_name, memory_requirement) VALUES 
('Patient Records', 50), 
('Medical Imaging', 100), 
('Lab Reports', 200), 
('Emergency Data', 50);
