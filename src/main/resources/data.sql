INSERT INTO ratings (id, name)
SELECT * FROM (
    SELECT 1 AS id, 'G' AS name UNION ALL
    SELECT 2, 'PG' UNION ALL
    SELECT 3, 'PG-13' UNION ALL
    SELECT 4, 'R' UNION ALL
    SELECT 5, 'NC-17'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM ratings);

INSERT INTO genres (id, name)
SELECT * FROM (
    SELECT 1 AS id, 'Comedy' AS name UNION ALL
    SELECT 2, 'Drama' UNION ALL
    SELECT 3, 'Cartoon' UNION ALL
    SELECT 4, 'Thriller' UNION ALL
    SELECT 5, 'Documentary' UNION ALL
    SELECT 6, 'Action'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM genres);