import usuarios.*;
import tareas.*;
import restaurante.*;
import excepciones.*;
import database.DatabaseManager;
import java.util.*;
import java.util.stream.Collectors;

public class SistemaTareas {
    private List<Usuario> usuarios;
    private List<Tarea> tareas;
    private List<Platillo> platillos;
    private List<Mesa> mesas;
    private List<Orden> ordenes;
    private Usuario usuarioActual;
    private double ventasDia;
    
    public SistemaTareas() {
        // Cargar datos en orden correcto
        this.mesas = DatabaseManager.cargarMesas();
        this.platillos = DatabaseManager.cargarPlatillos();
        this.usuarios = DatabaseManager.cargarUsuarios();
        this.ordenes = DatabaseManager.cargarOrdenes(mesas, usuarios, platillos);
        this.tareas = new ArrayList<>();
        this.ventasDia = 0.0;
        
        inicializarDatos();
        cargarEstadisticasIniciales();
    }
    
    private void inicializarDatos() {
        // Si no hay platillos, crear algunos predeterminados
        if (platillos.isEmpty()) {
            platillos.add(new Platillo("Hamburguesa Clásica", "Carne, lechuga, tomate, queso", 12.99, 15, "Principal"));
            platillos.add(new Platillo("Ensalada César", "Lechuga, pollo, croutones, aderezo", 9.99, 10, "Entrada"));
            platillos.add(new Platillo("Pasta Alfredo", "Pasta con salsa de crema y pollo", 14.99, 20, "Principal"));
            platillos.add(new Platillo("Sopa del día", "Sopa casera", 6.99, 5, "Entrada"));
            platillos.add(new Platillo("Postre de chocolate", "Pastel de chocolate con helado", 7.99, 8, "Postre"));
            
            DatabaseManager.guardarPlatillos(platillos);
        }
        
        // Calcular ventas del día
        calcularVentasDia();
    }
    
    private void cargarEstadisticasIniciales() {
        // Si hay cocineros, cargar sus estadísticas del día
        for (Usuario usuario : usuarios) {
            if (usuario instanceof Cocinero) {
                Cocinero cocinero = (Cocinero) usuario;
                int platillosHoy = DatabaseManager.cargarPlatillosPreparadosHoy(cocinero.getId());
                // Necesitamos un método para establecer platillos preparados
                // Por ahora, lo manejaremos diferente
            }
        }
    }
    
    private void calcularVentasDia() {
        ventasDia = 0.0;
        for (Orden orden : ordenes) {
            // Solo contar órdenes del día de hoy
            if (esOrdenDeHoy(orden)) {
                ventasDia += orden.getTotal();
            }
        }
    }
    
    private boolean esOrdenDeHoy(Orden orden) {
        Calendar hoy = Calendar.getInstance();
        Calendar fechaOrden = Calendar.getInstance();
        fechaOrden.setTime(orden.getFecha());
        
        return hoy.get(Calendar.YEAR) == fechaOrden.get(Calendar.YEAR) &&
               hoy.get(Calendar.MONTH) == fechaOrden.get(Calendar.MONTH) &&
               hoy.get(Calendar.DAY_OF_MONTH) == fechaOrden.get(Calendar.DAY_OF_MONTH);
    }
    
    // Método para guardar todo el estado del sistema
    public void guardarEstado() {
        DatabaseManager.guardarUsuarios(usuarios);
        DatabaseManager.guardarPlatillos(platillos);
        DatabaseManager.guardarMesas(mesas);
        DatabaseManager.guardarOrdenes(ordenes);
        
        System.out.println("✅ Estado del sistema guardado exitosamente.");
    }
    
    // Método para agregar orden y guardar
    public void agregarOrden(Orden orden) {
        ordenes.add(orden);
        guardarEstado();
    }
    
    // Método para actualizar estadísticas de cocinero
    public void actualizarEstadisticasCocinero(Cocinero cocinero) {
        DatabaseManager.guardarEstadisticasCocinero(cocinero);
    }
    
    // Método para agregar venta
    public void agregarVenta(double monto) { 
        ventasDia += monto; 
        DatabaseManager.registrarVenta(monto, "Venta registrada");
    }
    
    // Resto de los métodos existentes...
    public Usuario autenticarUsuario(String email, String password) {
        return usuarios.stream()
            .filter(u -> u.getEmail().equals(email) && u.verificarPassword(password))
            .findFirst()
            .orElse(null);
    }
    
    public void agregarUsuario(Usuario usuario) throws EmailInvalidoException, NombreInvalidoException {
        validarEmail(usuario.getEmail());
        validarNombre(usuario.getNombre());
        usuarios.add(usuario);
        guardarEstado();
    }
    
    public void eliminarUsuario(int id) {
        if (id == 1) {
            System.out.println("No se puede eliminar al super administrador.");
            return;
        }
        usuarios.removeIf(u -> u.getId() == id);
        guardarEstado();
    }
    
    public void agregarTarea(Tarea tarea) {
        tareas.add(tarea);
    }
    
    public void asignarTarea(Tarea tarea, Empleado empleado) {
        tarea.setUsuarioAsignado(empleado);
        empleado.agregarTarea(tarea);
    }
    
    public void listarUsuarios(boolean mostrarPasswords) {
        System.out.println("\n=== LISTA DE USUARIOS ===");
        System.out.println("Total usuarios: " + usuarios.size());
        System.out.println("-------------------");
        
        for (Usuario usuario : usuarios) {
            usuario.mostrarInfo(mostrarPasswords);
            System.out.println("-------------------");
        }
    }
    
    public void listarTareas() {
        if (tareas.isEmpty()) {
            System.out.println("No hay tareas registradas.");
            return;
        }
        
        System.out.println("\n=== LISTA DE TAREAS ===");
        System.out.println("Total tareas: " + tareas.size());
        System.out.println("-------------------");
        
        for (Tarea tarea : tareas) {
            tarea.mostrarDetalles();
            System.out.println("-------------------");
        }
    }
    
    public List<Empleado> getEmpleados() {
        return usuarios.stream()
            .filter(u -> u instanceof Empleado)
            .map(u -> (Empleado) u)
            .collect(Collectors.toList());
    }
    
    public List<Administrador> getAdministradores() {
        return usuarios.stream()
            .filter(u -> u instanceof Administrador)
            .map(u -> (Administrador) u)
            .collect(Collectors.toList());
    }
    
    public Usuario buscarUsuarioPorId(int id) {
        return usuarios.stream()
            .filter(u -> u.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public Tarea buscarTareaPorId(int id) {
        return tareas.stream()
            .filter(t -> t.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    private void validarEmail(String email) throws EmailInvalidoException {
        if (!email.contains("@") || !email.endsWith(".com")) {
            throw new EmailInvalidoException("El email debe contener @ y terminar en .com");
        }
    }
    
    private void validarNombre(String nombre) throws NombreInvalidoException {
        if (nombre == null || nombre.trim().length() < 3) {
            throw new NombreInvalidoException("El nombre debe tener al menos 3 letras");
        }
    }
    
    // Getters
    public List<Usuario> getUsuarios() { return usuarios; }
    public List<Tarea> getTareas() { return tareas; }
    public List<Platillo> getPlatillos() { return platillos; }
    public List<Mesa> getMesas() { return mesas; }
    public List<Orden> getOrdenes() { return ordenes; }
    public Usuario getUsuarioActual() { return usuarioActual; }
    public double getVentasDia() { return ventasDia; }
    
    // Setters
    public void setUsuarioActual(Usuario usuarioActual) { this.usuarioActual = usuarioActual; }
}
