-- Vehicle table
CREATE TABLE vehicle (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         vehicle_number VARCHAR(255)
);

-- CleaningProfessional table
CREATE TABLE cleaning_professional (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       name VARCHAR(255),
                                       vehicle_id BIGINT,
                                       FOREIGN KEY (vehicle_id) REFERENCES vehicle(id)
);

-- AvailabilityStatus enum simulation (using VARCHAR in H2)
CREATE TABLE availability (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                              cleaning_professional_id BIGINT,
                              date DATE,
                              status VARCHAR(50),  -- ENUM simulated as VARCHAR in H2
                              start_time TIME,
                              end_time TIME,
                              FOREIGN KEY (cleaning_professional_id) REFERENCES cleaning_professional(id)
);

-- Booking table
CREATE TABLE booking (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         start_date_time TIMESTAMP,
                         end_date_time TIMESTAMP
);

-- Booking_Professional Join table
CREATE TABLE booking_professional (
                                      booking_id BIGINT,
                                      professional_id BIGINT,
                                      FOREIGN KEY (booking_id) REFERENCES booking(id),
                                      FOREIGN KEY (professional_id) REFERENCES cleaning_professional(id),
                                      PRIMARY KEY (booking_id, professional_id)
);
