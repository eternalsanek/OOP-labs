-- CREATE (insert user)
INSERT INTO users (name, password_hash, role)
VALUES (:name, :password_hash, :role)
RETURNING id;

-- READ (select by id)
SELECT id, name, password_hash, role
FROM users
WHERE id = :id;

-- READ (select by name)
SELECT id, name, password_hash
FROM users
WHERE name = :name;

-- UPDATE (change name or password)
UPDATE users
SET name = :new_name,
    password_hash = :new_password,
    role = :new_role
WHERE id = :id;

-- DELETE
DELETE FROM users
WHERE id = :id;