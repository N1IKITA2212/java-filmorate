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
    SELECT 2, 'Action' UNION ALL
    SELECT 3, 'Adventure' UNION ALL
    SELECT 4, 'Drama' UNION ALL
    SELECT 5, 'Fantasy' UNION ALL
    SELECT 6, 'Historical' UNION ALL
    SELECT 7, 'Horror' UNION ALL
    SELECT 8, 'Melodrama'
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM genres);