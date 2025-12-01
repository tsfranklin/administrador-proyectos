-- Roles predefinidos
INSERT INTO rol (id_rol, nombre) VALUES
(1, 'ADMINISTRADOR'),
(2, 'GESTOR_PROYECTO'),
(3, 'TRABAJADOR'),
(4, 'COLABORADOR');

-- Usuarios con roles y contraseñas encriptadas (BCrypt)
INSERT INTO usuario (id_usuario, nombre, email, contrasena_hash, fecha_registro, activo, telefono, id_rol) VALUES
(1, 'Admin Principal', 'admin@taskflow.com', '$2a$12$KQZ4Y9X0/0wDdJ5/4e8f2u8dX7Z7b7QZ4Y9X0/0wDdJ5/4e8f2u8dX7Z7b7QZ4', NOW(), TRUE, '555-0001', 1),
(2, 'Gestor Alpha', 'gestor.alpha@taskflow.com', '$2a$12$XhYl0qZJh0wDdJ5/4e8f2u8dX7Z7b7QZ4Y9X0/0wDdJ5/4e8f2u8dX7Z7b7QZ4', NOW(), TRUE, '555-0002', 2),
(3, 'Desarrollador Senior', 'dev.senior@taskflow.com', '$2a$12$XhYl0qZJh0wDdJ5/4e8f2u8dX7Z7b7QZ4Y9X0/0wDdJ5/4e8f2u8dX7Z7b7QZ4', NOW(), TRUE, '555-0003', 3),
(4, 'Usuario Prueba', 'test@taskflow.com', '123456', NOW(), TRUE, '555-9999', 1);

-- Proyectos
INSERT INTO proyecto (id_proyecto, nombre, descripcion, fecha_inicio, fecha_fin, activo, id_responsable) VALUES
(1, 'Migración Cloud', 'Migrar sistemas a AWS', NOW(), NOW() + INTERVAL '60 days', TRUE, 2),
(2, 'App Móvil', 'Desarrollo app iOS/Android', NOW(), NOW() + INTERVAL '90 days', TRUE, 2);

-- Tareas
INSERT INTO tarea (id_tarea, titulo, descripcion, fecha_limite, prioridad, estado, id_proyecto, id_asignado) VALUES
(1, 'Diseño Arquitectura', 'Definir componentes cloud', NOW() + INTERVAL '15 days', 'ALTA', 'PENDIENTE', 1, 3),
(2, 'Wireframes UI', 'Diseñar interfaces usuario', NOW() + INTERVAL '10 days', 'MEDIA', 'EN_PROGRESO', 2, NULL);

-- Comentarios
INSERT INTO comentario (id_comentario, texto, fecha_comentario, id_tarea, id_usuario) VALUES
(1, 'Necesitamos revisar los requisitos de seguridad', NOW(), 1, 2);

-- Resetear secuencias
SELECT setval('usuario_id_usuario_seq', (SELECT MAX(id_usuario) FROM usuario));
SELECT setval('rol_id_rol_seq', (SELECT MAX(id_rol) FROM rol));
SELECT setval('proyecto_id_proyecto_seq', (SELECT MAX(id_proyecto) FROM proyecto));
SELECT setval('tarea_id_tarea_seq', (SELECT MAX(id_tarea) FROM tarea));
SELECT setval('comentario_id_comentario_seq', (SELECT MAX(id_comentario) FROM comentario));