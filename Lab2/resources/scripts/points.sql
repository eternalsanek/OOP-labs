CREATE TABLE points (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_function UUID NOT NULL REFERENCES functions(id) ON DELETE CASCADE,
    x_val DECIMAL NOT NULL,
    y_val DECIMAL NOT NULL,
    UNIQUE(id_function, x_val)
);

CREATE INDEX idx_points_function ON points(id_function);
CREATE INDEX idx_points_x ON points(x_val);