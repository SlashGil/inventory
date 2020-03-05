package com.chava.inventorymdys.Utilities



/**
 * Clase generada para Almacenar variables de la DB
 */
class Utilities {
    val TABLE_MATERIALES = "materiales"
    val TABLE_IMG = "imagen_material"
    val TABLE_USER = "us"
    val TABLE_EMP = "empresas"
    val TABLE_INV = "inventarios"

    //Campos para la tabla inventarios
    val CAMPO_ID_CLIENTE = "id_cliente"
    val CAMPO_NOM_CLIENTE ="nombre_cliente"
    val CAMPO_NUM_INVENTORY = "id_inventario"
    val FECHA_INVENTARIO = "fecha_inventario"
    //Campos para la tabla Materiales
    val CAMPO_NUM_INVENTARIO = "num_inventario"
    val CAMPO_ID_MATERIAL = "id_material"
    val CAMPO_DESCRIP_MATERIAL = "descripcion_material"
    val CAMPO_MARCA = "marca"
    val CAMPO_MODELO = "modelo"
    val CAMPO_SERIE = "serie"
    val CAMPO_UBICACION = "ubicacion"
    val CAMPO_PEDIMENTO = "pedimento"
    val CAMPO_FACTURA_UUID = "factura_uuid"
    val CAMPO_NUM_ACTIVO = "num_activo"
    val CAMPO_LINEA = "linea"
    val CAMPO_AREA = "area"
    val CAMPO_ID_USER = "id_user"
    val CAMPO_COMENTARIOS = "comentarios"
    val CAMPO_EXTRAS = "extras"
    val CAMPO_JSON = "json"
    //Campos para la tabla Imagen
    val CAMPO_DESCRIPCION = "descripcion"
    val CAMPO_IMAGEN = "imagen"
    val CAMPO_ID_IMAGEN = "id"
    //Campos para la tabla User
    val CAMPO_ID_EMPRESA = "id_empresa"
    val CAMPO_ID_APP = "id_app"
    val CAMPO_USER = "user"
    val CAMPO_CONTRA = "contra"
    val CAMPO_NAME_USER = "nameuser"
    val CAMPO_ID_AREA = "id_area"
    val CAMPO_STATUS_USER = "status_user"
    val CAMPO_EMAIL_USER = "email_user"
    val CAMPO_ID_ROL = "id_rol"
    val CAMPO_DAT_USE_REG = "dat_use_reg"
    val CAMPO_DAT_USER_MOD = "dat_user_mod"
    //Campos de la tabla Empresa
    val CAMPO_ID_EMP = "id_empresa"
    val CAMPO_NOMBRE = "name_empresa"
    val CREATE_TABLE_MATERIALES: String = "CREATE TABLE " + TABLE_MATERIALES + " (" +CAMPO_ID_MATERIAL +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ CAMPO_NUM_INVENTARIO + " VARCHAR(15), " + CAMPO_DESCRIP_MATERIAL +
            " VARCHAR(80), " + CAMPO_MARCA + " VARCHAR(50), " + CAMPO_MODELO + " VARCHAR(50), " + CAMPO_SERIE + " VARCHAR(30), " + CAMPO_UBICACION + " VARCHAR(50), " +
            CAMPO_PEDIMENTO + " VARCHAR(15), " + CAMPO_FACTURA_UUID + " VARCHAR(36), " + CAMPO_NUM_ACTIVO + " VARCHAR(30), " +
            CAMPO_LINEA + " VARCHAR(30), " + CAMPO_AREA + " VARCHAR(50), " + CAMPO_ID_USER + " INT(11), " + CAMPO_COMENTARIOS + " TEXT, " + CAMPO_EXTRAS + " TEXT)"

    val CREATE_TABLE_IMG: String = "CREATE TABLE " + TABLE_IMG + " (" + CAMPO_ID_IMAGEN + " INT(11), " + CAMPO_ID_MATERIAL +" INT(11), "+ CAMPO_DESCRIP_MATERIAL + " VARCHAR(80), " + CAMPO_DESCRIPCION +
            " VARCHAR(20), " + CAMPO_IMAGEN + " TEXT)"
    val CREATE_TABLE_USER: String =  "CREATE TABLE " + TABLE_USER + " (" + CAMPO_ID_EMPRESA + " INT, " + CAMPO_ID_USER +
                " INT auto_increment, " + CAMPO_ID_APP + " INT, " + CAMPO_USER +
                " VARCHAR(40), " + CAMPO_CONTRA + " TEXT, " + CAMPO_NAME_USER +
                " TEXT, " + CAMPO_ID_AREA + " VARCHAR(25), " + CAMPO_STATUS_USER + " INT, " + CAMPO_EMAIL_USER +
                " TEXT, " + CAMPO_ID_ROL + " VARCHAR(3), " + CAMPO_DAT_USE_REG + " DATE, " + CAMPO_DAT_USER_MOD + " DATE, " +
                "PRIMARY KEY (`$CAMPO_ID_EMPRESA`,`$CAMPO_ID_USER`,`$CAMPO_ID_APP`,`$CAMPO_USER`), UNIQUE('$CAMPO_ID_USER') )"
    val CREATE_TABLE_EMP = "CREATE TABLE " + TABLE_EMP + " (" + CAMPO_ID_EMPRESA + " INT primary key, " + CAMPO_NOMBRE + " VARCHAR(40))"
    val CREATE_TABLE_INVENTORY = "CREATE TABLE " + TABLE_INV + " (" + CAMPO_ID_CLIENTE + " varchar(20), " +
            CAMPO_NOM_CLIENTE + " VARCHAR(20), " + CAMPO_NUM_INVENTORY + " VARCHAR(20), " + FECHA_INVENTARIO + " VARCHAR(20))"
    val ADD_USER: String = "INSERT INTO " + TABLE_USER +
            " (" + CAMPO_ID_EMPRESA + ", " + CAMPO_ID_USER +
            ", " + CAMPO_ID_APP + ", " + CAMPO_USER +
            ", " + CAMPO_CONTRA + ", " + CAMPO_NAME_USER +
            ", " + CAMPO_ID_AREA + ", " + CAMPO_STATUS_USER +
            ", " + CAMPO_EMAIL_USER + ", " + CAMPO_ID_ROL +
            ", " + CAMPO_DAT_USE_REG + ", " + CAMPO_DAT_USER_MOD + ") values "


}