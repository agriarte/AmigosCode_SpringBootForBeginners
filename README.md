# AmigosCode_SpringBootForBeginners

[Algunas anotaciones para los "olvidos"]


Comandos usados mínimos para moverse en PostGresDB y crear la base de datos.
Importante: para empezar a usar la aplicación, se debe crear manualmente la base de datos "customers"

Para crear contenedor y levantarlo desde Docker Compose:

docker compose up -d

Para comprobar que está activo y saber ID o nombre:

docker ps -a

Para iniciar sesión en contenedor:

docker exec -it postgres bash

Para iniciar sesión dentro del servicio PostGres:

psql -U amigoscode (entra como superusuario amigoscode)

Para listar bases de datos existentes

\l

Para crear la base de datos customers

CREATE DATABASE customers;

Para seleccionar la base de datos "customers"

\c customers

Para salir de Postgres \d  o también \q

Para crear la tabla cliente desde SQL. Si arrancamos antes la aplicación con la clase @Entity Cliente, 
Hibernate la creará automáticamente, por lo que este código SQL no será necesario.

CREATE TABLE clientes (
  id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nombre VARCHAR(255),
  email VARCHAR(255),
  edad INTEGER
);
(Hibernate definirá el id como integer not null.) 

Por defecto, el autoincremento es de 50 en 50. Se puede modificar directamente la tabla para que sea de 1, mediante SQL:

ALTER SEQUENCE customer_id_sequence INCREMENT BY 1;

(También se puede fijar el autoincremento de 1 en 1, modificando la clase que crea automaticamente la tabla desde Hibernate, añadiendo allocationSize = 1)

@Id
	@SequenceGenerator(name = "cliente_id_sequence", 
		sequenceName = "cliente_id_sequence", 
		allocationSize = 1
	// por defecto, el autoincremento salía en 50)


Tras arrancar la aplicación si conecta con la base de datos Customers, se creará la tabla "cliente" automaticamente. Sin embargo, 
para poblar algunos registros mediante SQL da error si lo hacemos mediante sentencias SQL:

INSERT INTO cliente (nombre, email, edad)
VALUES 
('Ana', 'ana@demo.com', 28),
('Luis', 'luis@demo.com', 35),
('Marta', 'marta@demo.com', 22);

ERROR:  null value in column "id" of relation "cliente" violates not-null constraint
DETAIL:  Failing row contains (null, 28, ana@demo.com, Ana).

El motivo de este error es porque la clase cliente está configurada para generar IDs mediante una secuencia(SEQUENCE) personalizada. En cambio, 
funciona bien si se hace desde solicitudes Post mediante postman insertando un JSON en el Body del envío.

Las sentencias en SQL no funcionan porque la columna Default del ID está en blanco:

customers=# \d cliente
                      Table "public.cliente"
 Column |          Type          | Collation | Nullable | Default
--------+------------------------+-----------+----------+---------
 id     | integer                |           | not null |
 edad   | integer                |           |          |
 email  | character varying(255) |           |          |
 nombre | character varying(255) |           |          |
Indexes:
    "cliente_pkey" PRIMARY KEY, btree (id)

Una solución es modificando el valor DEFAULT para indicar de donde tiene que obtener el ID. 

Nos conectamos a la tabla customers:
\c customers
y modificamos la columna:
customers=# ALTER TABLE cliente ALTER COLUMN id SET DEFAULT nextval('cliente_id_sequence');

Con esta operación, default = nextval('cliente_id_sequence'::regclass)

customers=# \d cliente
                                      Table "public.cliente"
 Column |          Type          | Collation | Nullable |                 Default
--------+------------------------+-----------+----------+------------------------------------------
 id     | integer                |           | not null | nextval('cliente_id_sequence'::regclass)
 edad   | integer                |           |          |
 email  | character varying(255) |           |          |
 nombre | character varying(255) |           |          |
Indexes:
    "cliente_pkey" PRIMARY KEY, btree (id)

Ya funcionan las sentencias por SQL:

customers=# SELECT * FROM cliente;
 id | edad |     email      | nombre
----+------+----------------+--------
  1 |   27 | laura@demo.com | Laura
  2 |   18 | pedro@demo.com | Pedro
  3 |   28 | ana@demo.com   | Ana
  4 |   35 | luis@demo.com  | Luis
  5 |   22 | marta@demo.com | Marta
(5 rows)

OTRA SOLUCIÓN: NO UTILIZAR @SequenceGenerator para gestionar manualmente los ID

En PostgreSQL, podemos usar un ID secuencial con autoincremento de forma muy similar a MySQL.
Este enfoque es mucho más sencillo de configurar y mantener. A menos que necesitemos coordinar IDs entre distintas tablas (por ejemplo, para tener IDs únicos globales), podemos desarrollar la base de datos con un ID autoincremental estándar.

En este caso, PostgreSQL se encarga automáticamente de crear una secuencia interna y asignarla como valor por defecto al campo ID.
No es necesario definir @SequenceGenerator, ni configurar nextval(), ni hacer alteraciones manuales en la base de datos.

Para aplicar esta estrategia, basta con hacer dos cambios en la clase Cliente que genera la tabla:


1- Eliminar anotación @SequenceGenerator
// 
// @SequenceGenerator(name = "cliente_id_sequence", sequenceName = "cliente_id_sequence", allocationSize = 1)

2- Cambiando la estrategia de @GeneratedValue
// antes: @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cliente_id_sequence")

@GeneratedValue(strategy = GenerationType.IDENTITY)

ahora al levantar la aplicación creará  la tabla con ID por generación por identity
customers=# \d cliente
                                      Table "public.cliente"
 Column |          Type          | Collation | Nullable |                Default                
--------+------------------------+-----------+----------+---------------------------------------
 id     | integer                |           | not null | generated by default as identity
 edad   | integer                |           |          | 
 email  | character varying(255) |           |          | 
 nombre | character varying(255) |           |          | 
Indexes:
    "cliente_pkey" PRIMARY KEY, btree (id)

MODIFICACION DE application.properties

En el video, cada vez que se arranca la aplicación se borra la tabla y se vuelve a crear. Para que esto no ocurra hay que cambiar:

# Hibernate para actualizar la base de datos sin borrar datos
spring.jpa.hibernate.ddl-auto=update (aquí estaba como create-drop

# Configuración de dialecto de PostgreSQL. Hibernate soporta MySQL, PostgresDB, H2, etc Es recomendable indicarle que driver 
de base de datos estamos usando para que trabaje de manera nativa y no tengamos problemas raros o incompatibilidades con 
sentencias CREATE TABLE, INSERT, SEQUENCE, etc., usando las funciones y tipos nativos de PostgreSQL.
spring.jpa.properties.dialect=org.hibernate.dialect.PostgreSQLDialect

