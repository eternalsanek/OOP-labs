CREATE TABLE functions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_owner UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    expression TEXT NOT NULL
);

CREATE INDEX idx_functions_owner ON functions(id_owner);