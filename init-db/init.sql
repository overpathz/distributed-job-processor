DO
$$
BEGIN
        IF NOT EXISTS(SELECT 1 FROM pg_database WHERE datname = 'paymentsdb') THEN
            CREATE DATABASE paymentsdb;
END IF;
END
$$;