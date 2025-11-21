-- CREATE (insert function)
INSERT INTO functions (id_owner, name, type, expression)
VALUES (:id_owner, :name, :type, :expression)
RETURNING id;

-- READ (select by id)
SELECT id, id_owner, name, type, expression
FROM functions
WHERE id = :id;

-- READ (all functions of user)
SELECT id, name, type, expression
FROM functions
WHERE id_owner = :id_owner
ORDER BY name;

-- UPDATE (edit name / type / expression)
UPDATE functions
SET name = :new_name,
    type = :new_type,
    expression = :new_expression
WHERE id = :id;

-- DELETE
DELETE FROM functions
WHERE id = :id;