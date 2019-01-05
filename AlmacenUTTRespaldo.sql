

CREATE DATABASE almacenutt;
USE almacenutt;

CREATE TABLE areas (
id_area INT NOT NULL AUTO_INCREMENT,
nombre NVARCHAR(60) NOT NULL UNIQUE,
responsable NVARCHAR(100) NOT NULL,
status SMALLINT DEFAULT 1,
id_usuario INT NOT NULL,
PRIMARY KEY(id_area),
CONSTRAINT fk_id_usuario_3 FOREIGN KEY (id_usuario) 
REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE=InnoDB;

CREATE TABLE usuarios(

id_usuario INT NOT NULL AUTO_INCREMENT,
nombre NVARCHAR(20)  NOT NULL,
apellido_paterno NVARCHAR(20) NOT NULL,
apellido_materno NVARCHAR(20) NOT NULL,
role enum('ROLE_ADMIN', 'ROLE_USER') not null,
nombre_usuario NVARCHAR(20) NOT NULL UNIQUE,
password NVARCHAR(100) NOT NULL,
status SMALLINT DEFAULT 1,
PRIMARY KEY(id_usuario)
)ENGINE=InnoDB;

CREATE TABLE privilegios(
    id_privilegio int not null auto_increment,
    nombre nvarchar(60) not null,
    primary key(id_privilegio)
)ENGINE=InnoDB;

CREATE TABLE privilegios_usuarios(
    id_privilegio_usuario int not null auto_increment,
    id_privilegio int not null,
    id_usuario int null,
    primary key(id_privilegio_usuario),
    CONSTRAINT fk_id_usuario99 FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_privilegio9 FOREIGN KEY (id_privilegio) 
    REFERENCES privilegios(id_privilegio) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE=InnoDB;

CREATE TABLE proveedores(
    id_proveedor int not null auto_increment,
    nombre nvarchar(40) not null,
    calle nvarchar(70) null,
    numero_lote nvarchar(15) null,
    numero_interior nvarchar(10) null,
    colonia nvarchar(60) null,
    municipio nvarchar(50) null,
    estado nvarchar(40) null,
    codigo_postal nvarchar(10) null,
    regimen_fiscal nvarchar(15) null,
    telefono nvarchar(15) null,
    rfc nvarchar(40) null,
    contacto nvarchar(30) null,
    correo nvarchar(35) null,
    status SMALLINT DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_usuario int not null,
    primary key(id_proveedor),
    CONSTRAINT fk_id_id_usuario19 FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE productos(
    id_producto int not null auto_increment,
    clave nvarchar(20) not null,
    id_proveedor int not null,
    descripcion nvarchar(100) not null,
    cantidad int not null,
    unidad_medida enum('pieza', 'caja') not null,
    precio decimal(12,2) not null,
    id_usuario int not null,
    status SMALLINT DEFAULT 1,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    primary key (id_producto),
    CONSTRAINT fk_id_usuario4 FOREIGN KEY (id_usuario) 
    REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_proveedor3 FOREIGN KEY (id_proveedor) 
    REFERENCES proveedores(id_proveedor) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE facturas(
    id_factura int not null auto_increment,
    folio nvarchar(20) not null,
    documento nvarchar(150) null,
    fecha_expedicion datetime not null,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    descripcion nvarchar(300) null,
    id_proveedor int not null,
    id_usuario int not null,
    status SMALLINT DEFAULT 1,
    completada SMALLINT DEFAULT 0,
    primary key (id_factura),
    CONSTRAINT FOREIGN KEY (id_proveedor)
    REFERENCES proveedores(id_proveedor) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_usuario5 FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE facturas
DROP documento;

   
CREATE TABLE facturas_productos(
    id_factura_producto int not null auto_increment,
    id_factura int null,
    id_producto int null,
    cantidad INT NOT NULL,
    cantidad_restante INT NOT NULL,
    primary key(id_factura_producto),
    CONSTRAINT fk_id_factura5 FOREIGN KEY (id_factura)
    REFERENCES facturas(id_factura) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_factura_producto5 FOREIGN KEY (id_producto)
    REFERENCES productos(id_producto) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE vales_salidas(
    id_vale_salida int not null auto_increment,
    numero_requisicion int not null,
    fecha_entrega date NOT NULL,
    id_usuario int not null,
    id_factura int not null,
    status SMALLINT DEFAULT 1,
    id_area int not null,
    primary key (id_vale_salida),
    CONSTRAINT fk_id_factura125 FOREIGN KEY (id_factura)
    REFERENCES facturas(id_factura) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_id_usuario85 FOREIGN KEY (id_usuario)
    REFERENCES usuarios(id_usuario) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_id_area5 FOREIGN KEY (id_area)
    REFERENCES areas(id_area) ON UPDATE CASCADE ON DELETE NO ACTION
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE vales_salidas
ADD COLUMN documento nvarchar(150) null;

CREATE TABLE salidas_productos(
    id_salida_producto int auto_increment,
    cantidad_entregada int not null,
    cantidad_solicitada int not null,
    unidad_medida nvarchar(20) NOT NULL,
    id_vale_salida int null,
    id_factura_producto int not null,
    primary key(id_salida_producto),
    CONSTRAINT fk_id_salida11 FOREIGN KEY (id_vale_salida )
    REFERENCES vales_salidas(id_vale_salida) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_vale_producto11 FOREIGN KEY (id_factura_producto)
    REFERENCES facturas_productos(id_factura_producto) ON UPDATE NO ACTION ON DELETE CASCADE
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

select p.nombre, u.nombre from privilegios p
    inner join privilegios_usuarios pu on p.id_privilegio = pu.id_privilegio
    inner join usuarios u on u.id_usuario = pu.id_usuario
    where pu.id_usuario = 1;

    select sp.cantidad_entregada as cantidadEntregada
            ,sp.cantidad_solicitada as cantidadSolicitada, sp.unidad_medida as unidadMedida, p.descripcion
            from vales_salidas v inner join salidas_productos sp on v.id_vale_salida = sp.id_vale_salida
            inner join facturas_productos fp on fp.id_factura_producto = sp.id_factura_producto
            inner join productos p on p.id_producto = fp.id_producto and v.id_vale_salida = 14;

CREATE TABLE producto_area(
    id_producto_area INT NOT NULL,
    id_area INT NOT NULL,
    id_factura INT NOT NULL,
    primary key(id_producto_area),
    CONSTRAINT fk_id_area_2 FOREIGN KEY (id_area) 
    REFERENCES areas(id_area) ON UPDATE CASCADE ON DELETE NO ACTION,
    CONSTRAINT fk_id_factura_2 FOREIGN KEY (id_factura) 
    REFERENCES facturas(id_factura) ON UPDATE CASCADE ON DELETE NO ACTION
)
