package servicios;

public class GestorLogs 
{
	public static void registrarLogEliminacionOrden(int idOrden, String motivo, String usuario) {
        try 
        {
            java.io.FileWriter writer = new java.io.FileWriter("data/log_eliminaciones.csv", true);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fecha = sdf.format(new java.util.Date());
            
            writer.write(fecha + "," + idOrden + "," + motivo + "," + usuario + "\n");
            writer.close();
        } 
        catch (Exception e) 
        {
            System.out.println("⚠Error al registrar log de eliminación: " + e.getMessage());
        }
    }

}
