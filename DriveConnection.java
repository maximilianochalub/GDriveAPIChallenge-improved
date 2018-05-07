import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

/**
 * @author Maximiliano Chalub
 * Esta clase se utiliza para realizar una conexion al drive
 * Cada metodo es llamado desde donde quiera realizarse la conexion
 */

public class DriveConnection {

	public static final String APPLICATION_NAME = "MeliChallenge", CREDENTIALS_FOLDER = "credentials", CLIENT_SECRET_DIR = "client_secret.json";
	public static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	public static List<String> SCOPES;
    public static Drive driveService; // Servicio de drive para gestionar archivos de la cuenta Drive
    
    /**
     * Realiza la conexión al Drive API
     * @return boolean si se pudo realizae la conexion
     */
    public static boolean connect(int flag) {
    	
        try {
			FileUtils.deleteDirectory(new File(CREDENTIALS_FOLDER));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("error al eliminar dir");
		}
    	
        NetHttpTransport HTTP_TRANSPORT;
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, flag))
	                .setApplicationName(APPLICATION_NAME)
	                .build();
		
			// Devolvemos true si se efectua la conexion
			return true;
		} catch (GeneralSecurityException | IOException e) {
			// Devolvemos false si hay excepcion y no se puede conectar
			return false;
		}
		
    }

    /**
     * Crea un objeto de credenciales autorizado
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return Un objeto Credential autorizado
     * @throws IOException Si no hay client_secret.json
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, int flag) throws IOException {

    	if (flag == 0) {
    		
    		SCOPES = Collections.singletonList(DriveScopes.DRIVE_METADATA_READONLY);
    		
    	} else if (flag == 1) {
    		
    		SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    		
    	}
    	
    	// Cargamos datos del cliente usando el archivo client_secret.json
        InputStream in = DriveConnection.class.getResourceAsStream(CLIENT_SECRET_DIR);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
  
        // Obtenemos archivo con credenciales almacenadas
        File storedCredentials = new File(CREDENTIALS_FOLDER + "\\StoredCredential");
        
        // Eliminamos el archivo si deseamos realizar una segunda conexion en una misma ejecucion de la app
        if (!(storedCredentials.delete())) {
        	
        	System.out.println("NO SE PUEDE EELIMINAR EL ARCHIVO");
        	
        }
        
        // Desencadena solicitud de autorización de conexión para el usuario
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        		HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }

}
