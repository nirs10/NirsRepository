/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mavenproject3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Harry
 */
public class Mavenproject3 {

    // Credenciales de la base de datos
    private static final String URL = "jdbc:mysql://localhost:3306/projecto_final_prueba";
    private static final String USUARIO = "root";
    private static final String CONTRASEÑA = "root";

    public static void main(String[] args) {
        // Conectar con la base de datos
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
            System.out.println("Conexión exitosa a la base de datos.");

            // Módulo de ingreso de datos
            ingresarEstudiante(conexion, "Juan", "Pérez", "12345678");
            inscribirMateria(conexion, "12345678", "Matemáticas I");
            inscribirMateria(conexion, "12345678", "Física I");

            // Función de verificación de inscripciones
            System.out.println(verificarInscripcion(conexion, "12345678", "Matemáticas II"));
            System.out.println(verificarInscripcion(conexion, "12345678", "Física II"));

            // Funcionalidad de modificación de la base de datos
            modificarEstudiante(conexion, "12345678", "Juan", "González", "87654321");
            modificarMateria(conexion, "Matemáticas I", "Cálculo I");
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    // Función para ingresar un estudiante en la base de datos
    public static void ingresarEstudiante(Connection conexion, String nombre, String apellido, String dni) throws SQLException {
        String consulta = "INSERT INTO estudiantes (nombre, apellido, dni) VALUES (?, ?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, nombre);
            sentencia.setString(2, apellido);
            sentencia.setString(3, dni);

            sentencia.executeUpdate();
            System.out.println("Estudiante " + nombre + " " + apellido + " agregado con éxito.");
        }
    }

    // Función para inscribir un estudiante en una materia
    public static void inscribirMateria(Connection conexion, String dni, String materia) throws SQLException {
        String consulta = "INSERT INTO inscripciones (dni_estudiante, materia) VALUES (?, ?)";

        try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
            sentencia.setString(1, dni);
            sentencia.setString(2, materia);

            sentencia.executeUpdate();
            System.out.println("Estudiante con DNI " + dni + " inscrito en " + materia + " con éxito.");
        }
    }

    // Función para verificar si un estudiante puede inscribirse en una materia
    public static boolean verificarInscripcion(Connection conexion, String dni, String materia) throws SQLException {
        String consultaCorrelatividades = "SELECT correlativa FROM correlatividades WHERE materia = ?";
        String consultaInscripciones = "SELECT materia FROM inscripciones WHERE dni_estudiante = ?";

    // Obtener las correlativas de la materia
    try (PreparedStatement sentencia = conexion.prepareStatement(consultaCorrelatividades)) {
        sentencia.setString(1, materia);
        ResultSet resultado = sentencia.executeQuery();

        while (resultado.next()) {
            String correlativa = resultado.getString("correlativa");

            // Verificar si el estudiante ya aprobó las correlativas
            try (PreparedStatement sentencia2 = conexion.prepareStatement(consultaInscripciones)) {
                sentencia2.setString(1, dni);
                ResultSet resultado2 = sentencia2.executeQuery();

                boolean aprobada = false;
                while (resultado2.next()) {
                    String materiaInscripta = resultado2.getString("materia");
                    if (materiaInscripta.equals(correlativa)) {
                        aprobada = true;
                        break;
                    }
                }

                // Si el estudiante no aprobó alguna correlativa, no puede inscribirse en la materia
                if (!aprobada) {
                    System.out.println("Inscripción rechazada. El estudiante con DNI " + dni + " no aprobó la correlativa " + correlativa + ".");
                    return false;
                }
            }
        }

        // Si el estudiante aprobó todas las correlativas, puede inscribirse en la materia
        System.out.println("Inscripción aceptada. El estudiante con DNI " + dni + " puede inscribirse en " + materia + ".");
        return true;
    }
}

// Función para modificar los datos de un estudiante en la base de datos
public static void modificarEstudiante(Connection conexion, String dniAntiguo, String nuevoNombre, String nuevoApellido, String nuevoDNI) throws SQLException {
    String consulta = "UPDATE estudiantes SET nombre = ?, apellido = ?, dni = ? WHERE dni = ?";

    try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
        sentencia.setString(1, nuevoNombre);
        sentencia.setString(2, nuevoApellido);
        sentencia.setString(3, nuevoDNI);
        sentencia.setString(4, dniAntiguo);

        sentencia.executeUpdate();
        System.out.println("Estudiante con DNI " + dniAntiguo + " modificado con éxito.");
    }
}

// Función para modificar los datos de una materia en la base de datos
public static void modificarMateria(Connection conexion, String materiaAntigua, String nuevaMateria) throws SQLException {
    String consulta = "UPDATE inscripciones SET materia = ? WHERE materia = ?";

    try (PreparedStatement sentencia = conexion.prepareStatement(consulta)) {
        sentencia.setString(1, nuevaMateria);
        sentencia.setString(2, materiaAntigua);

        sentencia.executeUpdate();
        System.out.println("Materia " + materiaAntigua + " modificada con éxito.");
    }
}
}

    

