-- CREATE (insert point)
INSERT INTO points (id_function, x_val, y_val)
VALUES (:id_function, :x_val, :y_val)
RETURNING id;

-- READ (all points of a function, sorted by x)
SELECT id, x_val, y_val
FROM points
WHERE id_function = :id_function
ORDER BY x_val;

-- READ (specific point by x)
SELECT id, x_val, y_val
FROM points
WHERE id_function = :id_function AND x_val = :x_val;

-- UPDATE (change y-value)
UPDATE points
SET y_val = :new_y
WHERE id = :id;

-- DELETE (one point)
DELETE FROM points
WHERE id = :id;

-- DELETE (all points of a function)
DELETE FROM points
WHERE id_function = :id_function;