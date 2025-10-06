CREATE SCHEMA IF NOT EXISTS commute;

CREATE TABLE commute.employee_card (
    id BIGSERIAL PRIMARY KEY,
    card_number VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE commute.route (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE commute.bus_boarding (
    id BIGSERIAL PRIMARY KEY,
    boarded_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    employee_card_id BIGINT NOT NULL,
    route_id BIGINT NOT NULL,
    CONSTRAINT fk_bus_boarding_employee_card
        FOREIGN KEY (employee_card_id)
        REFERENCES commute.employee_card (id)
        ON DELETE CASCADE,
    CONSTRAINT fk_bus_boarding_route
        FOREIGN KEY (route_id)
        REFERENCES commute.route (id)
        ON DELETE CASCADE
);
