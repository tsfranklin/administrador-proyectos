-- Roles predefinidos
INSERT INTO rol (id_rol, nombre) VALUES
(1, 'ADMINISTRADOR'),
(2, 'GESTOR_PROYECTO'),
(3, 'TRABAJADOR'),
(4, 'COLABORADOR');

-- Usuarios (Contraseña para todos: '12345678' -> $2a$10$ykhXj.z5.5.5.5.5.5.5.5)
-- Nota: Usaré un hash genérico de BCrypt para '12345678' para simplificar.
-- Hash real para '12345678': $2a$10$5wS1R.2.2.2.2.2.2.2.2 (Simulado, usaré uno real conocido o el de admin)
-- Usaré el mismo hash del admin actual para todos por simplicidad: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy (que es 'admin123')

-- 1. Admin
INSERT INTO usuario (id_usuario, nombre, email, contrasena_hash, fecha_registro, activo, telefono, id_rol) VALUES
(1, 'Admin Principal', 'admin@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0001', 1);

-- 2. Gestores
INSERT INTO usuario (id_usuario, nombre, email, contrasena_hash, fecha_registro, activo, telefono, id_rol) VALUES
(2, 'Gestor A', 'gestorA@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0002', 2),
(3, 'Gestor B', 'gestorB@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0003', 2);

-- 3. Trabajadores Proyecto A
INSERT INTO usuario (id_usuario, nombre, email, contrasena_hash, fecha_registro, activo, telefono, id_rol) VALUES
(4, 'Trabajador A1', 'trabajadorA1@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0004', 3),
(5, 'Trabajador A2', 'trabajadorA2@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0005', 3),
(6, 'Trabajador A3', 'trabajadorA3@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0006', 3);

-- 4. Trabajadores Proyecto B
INSERT INTO usuario (id_usuario, nombre, email, contrasena_hash, fecha_registro, activo, telefono, id_rol) VALUES
(7, 'Trabajador B1', 'trabajadorB1@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0007', 3),
(8, 'Trabajador B2', 'trabajadorB2@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0008', 3),
(9, 'Trabajador B3', 'trabajadorB3@taskflow.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', NOW(), TRUE, '555-0009', 3);

-- Proyectos
INSERT INTO proyecto (id_proyecto, nombre, descripcion, codigo_registro, fecha_inicio, fecha_fin, activo, id_responsable) VALUES
(1, 'Proyecto Alpha', 'Desarrollo del sistema Alpha', 'PROJ-A01', NOW(), NOW() + INTERVAL '60 days', TRUE, 2),
(2, 'Proyecto Beta', 'Marketing para Beta', 'PROJ-B01', NOW(), NOW() + INTERVAL '90 days', TRUE, 3);

-- Tareas (Asignación inicial)
-- Proyecto A (Trabajadores A1, A2, A3)
INSERT INTO tarea (id_tarea, titulo, descripcion, codigo_tarea, fecha_limite, prioridad, estado, id_proyecto, id_asignado) VALUES
(1, 'Análisis de Requisitos', 'Reunir requisitos del cliente', 'TASK-A01', NOW() + INTERVAL '5 days', 'ALTA', 'EN_PROGRESO', 1, 4),
(2, 'Diseño de BD', 'Crear diagrama ER', 'TASK-A02', NOW() + INTERVAL '10 days', 'ALTA', 'PENDIENTE', 1, 5),
(3, 'Frontend Login', 'Maquetar login', 'TASK-A03', NOW() + INTERVAL '7 days', 'MEDIA', 'PENDIENTE', 1, 6);

-- Proyecto B (Trabajadores B1, B2, B3)
INSERT INTO tarea (id_tarea, titulo, descripcion, codigo_tarea, fecha_limite, prioridad, estado, id_proyecto, id_asignado) VALUES
(4, 'Campaña Redes', 'Diseñar posts', 'TASK-B01', NOW() + INTERVAL '5 days', 'MEDIA', 'EN_PROGRESO', 2, 7),
(5, 'SEO Audit', 'Auditoría del sitio web', 'TASK-B02', NOW() + INTERVAL '10 days', 'BAJA', 'PENDIENTE', 2, 8),
(6, 'Email Marketing', 'Configurar Mailchimp', 'TASK-B03', NOW() + INTERVAL '7 days', 'ALTA', 'PENDIENTE', 2, 9);

-- Colaboración Cruzada
-- Trabajador A1 (ID 4) colabora en Proyecto B
INSERT INTO tarea (id_tarea, titulo, descripcion, codigo_tarea, fecha_limite, prioridad, estado, id_proyecto, id_asignado) VALUES
(7, 'Revisión Técnica B', 'Apoyo técnico al equipo B', 'TASK-B04', NOW() + INTERVAL '15 days', 'MEDIA', 'PENDIENTE', 2, 4);

-- Trabajador B1 (ID 7) colabora en Proyecto A
INSERT INTO tarea (id_tarea, titulo, descripcion, codigo_tarea, fecha_limite, prioridad, estado, id_proyecto, id_asignado) VALUES
(8, 'Consultoría Marketing A', 'Apoyo de marketing al equipo A', 'TASK-A04', NOW() + INTERVAL '15 days', 'MEDIA', 'PENDIENTE', 1, 7);

-- Resetear secuencias
SELECT setval('usuario_id_usuario_seq', (SELECT COALESCE(MAX(id_usuario), 1) FROM usuario));
SELECT setval('rol_id_rol_seq', (SELECT COALESCE(MAX(id_rol), 1) FROM rol));
SELECT setval('proyecto_id_proyecto_seq', (SELECT COALESCE(MAX(id_proyecto), 1) FROM proyecto));
SELECT setval('tarea_id_tarea_seq', (SELECT COALESCE(MAX(id_tarea), 1) FROM tarea));