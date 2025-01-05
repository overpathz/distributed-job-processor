CREATE TABLE IF NOT EXISTS payment_intent (
                                              id SERIAL PRIMARY KEY,
                                              amount NUMERIC(10, 2) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
    );


DO
$$
DECLARE
i INT;
BEGIN
FOR i IN 1..1000000 LOOP
        INSERT INTO payment_intent (amount, status, created_at)
        VALUES ((100 + i % 10), 'NEW', NOW());
END LOOP;
END
$$;
