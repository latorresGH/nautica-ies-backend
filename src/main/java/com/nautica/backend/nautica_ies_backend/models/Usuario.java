package com.nautica.backend.nautica_ies_backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.nautica.backend.nautica_ies_backend.models.enums.RolUsuario;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Entidad que representa un usuario del sistema.
 * 
 * Contiene informacion personal, de contacto, de autenticacion y de rol.
 */
// @JsonTypeInfo(
//     use = JsonTypeInfo.Id.NAME,
//     include = JsonTypeInfo.As.PROPERTY,
//     property = "tipo" // puedes cambiar este nombre si quieres
// )
// @JsonSubTypes({
//     @JsonSubTypes.Type(value = Cliente.class, name = "cliente"),
//     @JsonSubTypes.Type(value = Operario.class, name = "operario")
// })
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)

public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellido;

    /**
     * Contraseña del usuario. Solo se permite escritura (WRITE_ONLY) para evitar
     * que sea devuelta en respuestas JSON.
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank
    private String contrasena;

    private String direccion;
    private String localidad;
    private String provincia;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String dni;

    @Column(unique = true, nullable = false)
    @Email
    @NotBlank
    private String correo;

    private String telefono;

    /**
     * Rol asignado al usuario (ej: ADMIN, EMPLEADO, CLIENTE, etc.).
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;

    /**
     * Indica si el usuario está activo en el sistema.
     */
    private Boolean activo = true;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private java.util.List<UsuarioEmbarcacion> embarcaciones = new java.util.ArrayList<>();

    /**
     * Obtiene el ID único del usuario.
     *
     * @return ID del usuario.
     */
    public Long getIdUsuario() {
        return idUsuario;
    }

    /**
     * Establece el ID del usuario.
     *
     * @param idUsuario ID a asignar.
     */
    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    /**
     * Obtiene el nombre del usuario.
     *
     * @return Nombre del usuario.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del usuario.
     *
     * @param nombre Nombre a asignar.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del usuario.
     *
     * @return Apellido del usuario.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del usuario.
     *
     * @param apellido Apellido a asignar.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene la contraseña del usuario.
     * 
     * Nota: Esta propiedad solo puede escribirse, no se devuelve en JSON.
     *
     * @return Contraseña del usuario.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Establece la contraseña del usuario.
     *
     * @param contrasena Contraseña a asignar.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Obtiene la dirección del usuario.
     *
     * @return Dirección.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Establece la dirección del usuario.
     *
     * @param direccion Dirección a asignar.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Obtiene la localidad del usuario.
     *
     * @return Localidad.
     */
    public String getLocalidad() {
        return localidad;
    }

    /**
     * Establece la localidad del usuario.
     *
     * @param localidad Localidad a asignar.
     */
    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    /**
     * Obtiene la provincia del usuario.
     *
     * @return Provincia.
     */
    public String getProvincia() {
        return provincia;
    }

    /**
     * Establece la provincia del usuario.
     *
     * @param provincia Provincia a asignar.
     */
    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    /**
     * Obtiene el DNI del usuario.
     *
     * @return DNI.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del usuario.
     *
     * @param dni DNI a asignar.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return Correo electrónico.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param correo Correo a asignar.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Obtiene el número de teléfono del usuario.
     *
     * @return Teléfono.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del usuario.
     *
     * @param telefono Teléfono a asignar.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Obtiene el rol del usuario dentro del sistema.
     *
     * @return Rol del usuario.
     */
    public RolUsuario getRol() {
        return rol;
    }

    /**
     * Establece el rol del usuario dentro del sistema.
     *
     * @param rol Rol a asignar.
     */
    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }

    /**
     * Indica si el usuario está activo.
     *
     * @return true si el usuario está activo, false en caso contrario.
     */
    public Boolean getActivo() {
        return activo;
    }

    /**
     * Establece el estado de actividad del usuario.
     *
     * @param activo true si el usuario debe estar activo, false si debe estar
     *               inactivo.
     */
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    /**
     * Obtiene la lista de embarcaciones asociadas al usuario.
     * @return Lista de embarcaciones.
     * 
     */
    public java.util.List<UsuarioEmbarcacion> getEmbarcaciones() {
        return embarcaciones;
    }

    /**
     * Establece la lista de embarcaciones asociadas al usuario.
     * @param embarcaciones
     */
    public void setEmbarcaciones(java.util.List<UsuarioEmbarcacion> embarcaciones) {
        this.embarcaciones = embarcaciones;
    }
}