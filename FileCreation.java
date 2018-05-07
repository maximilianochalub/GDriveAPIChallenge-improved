import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import com.google.api.services.drive.model.File;

/**
 * @author Maximiliano Chalub
 * Esta clase agrupa objetos de acciones al crear un archivo
 */
public class FileCreation {
	
	public static String fileId;
	private String fileTitle;
	private String fileDescription;
	private HttpsURLConnection httpsConnection;
	
	public FileCreation(String p_fileTitle, String p_filesDescription) {
		
		this.setFileTitle(p_fileTitle);
		this.setFileDescription(p_filesDescription);
		
	}
	
	private void setFileTitle(String p_fileTitle) {
		
		this.fileTitle = p_fileTitle;
		
	}
	
	private void setFileDescription(String p_filesDescription) {
		
		this.fileDescription = p_filesDescription;
		
	}
	
	private void setHttpsConnection(HttpsURLConnection p_httpsConnection) {
		
		this.httpsConnection = p_httpsConnection;
		
	}
	
	public String getFileTitle() {
		
		return this.fileTitle;
		
	}
	
	public String getFileDescription() {
		
		return this.fileDescription;
		
	}
	
	public HttpsURLConnection getHttpsConnection() {
		
		return this.httpsConnection;
		
	}
	
	/**
	 * Establece la conexión Http
	 * Setea la URL a la cual enviar contenido y establece el metodo de solicitud Http
	 */
	private void connect() {
		try {
			// Seteamos el endpoint para metadata-only proveida por la API
			String stringURL = "https://www.googleapis.com/upload/drive/v3/files?uploadType=multipart";
			URL direccionURL = new URL(stringURL);
			
			// Instanciamos conexion e indicamos que el metodo sera POST
			this.setHttpsConnection((HttpsURLConnection) direccionURL.openConnection());
			this.getHttpsConnection().setRequestMethod("POST");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Forma la solicitud Http
	 * Establece sus propiedades y llama al metodo que envia la solicitud Http
	 * @param HttpsURLConnection la conexión http establecida
	 */
	private void composeHttpRequest() {
		
		// La metadata del archivo en formato JSON
		JSONObject fileMetadata = new JSONObject();
		fileMetadata.put("name", this.getFileTitle());
		fileMetadata.put("description", this.getFileDescription());
		// Obtenemos longitud del body request
		int bodyReqLength = fileMetadata.toString().length();
		
		// Otras propiedades que conforman el body de la request
		this.getHttpsConnection().setRequestProperty("Authorization", "Bearer");
		this.getHttpsConnection().setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		this.getHttpsConnection().setRequestProperty("Content-Length", Integer.toString(bodyReqLength));

		// Enviamos la solicitud recientemente creada
		this.sendHttpRequest(fileMetadata);
	
	}
	
	/**
	 * Envía la solicitud Http
	 * Creamos un Stream que escribe sobre la conexión establecida
	 * @param conexion
	 */
	private void sendHttpRequest(JSONObject fileMetadata) {

		System.out.println(fileMetadata.toString());
		
		// Indicamos que la misma devolvera resultados de salida
		try {
			this.getHttpsConnection().setDoOutput(true);
			// Escribimos los datos de salida de la solicitud en un Output Stream
			OutputStream outStream = this.getHttpsConnection().getOutputStream();
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
			// Escribimos el string del body
			outStreamWriter.write(fileMetadata.toString());
			// Limpiamos
			outStreamWriter.flush();
			// Cerramos los streams
			outStreamWriter.close();
			outStream.close();
		} catch (IOException io) {
			
			
		}
		
	}
	
	private void getHttpResponse() {
		
		try {
			// Obtenemos el codigo de respuesta http
			int responseCode = this.getHttpsConnection().getResponseCode();

			System.out.println("Codigo HTTP response: " + Integer.toString(responseCode));
			// Creamos un stream para leer la response
			BufferedReader responseRead = new BufferedReader(new InputStreamReader(this.getHttpsConnection().getInputStream()));
		
			// Creamos variable para almacenar las lineas leidas del stream
			String readLine;
			// Instanciamos un buffer de String para almacenar los caracteres leidos
			StringBuffer response = new StringBuffer();
			// Juntamos caracteres hasta que no hayan resultados leidos
			while ((readLine = responseRead.readLine()) != null) {
					response.append(readLine);
			}
			// Cerramos el stream
			responseRead.close();		
		} catch (IOException e) {
			
		}
	}
	
	private void createAFile() throws IOException {
		
		File fileMetadata = new File();
		fileMetadata.setName(this.getFileTitle());
		fileMetadata.setDescription(this.getFileDescription());

		File file = DriveConnection.driveService.files().create(fileMetadata)
		    .setFields("id, name, description")
		    .execute();
		fileId = file.getId();
		System.out.println("File ID: " + file.getId());		
		System.out.println("File Name: " + file.getName());	
		System.out.println("File Desc: " + file.getDescription());	
		
	}

	/**
	 * Crea el archivo con su nombre y descripción
	 * Llama a los demás métodos que realizan esta tarea
	 * @return
	 * @throws IOException 
	 */
	public int createFile() throws IOException {
	
		this.createAFile();
		/**
			// arreglar los try catch
			// Seteamos la conexion
			this.connect();
			
			// Creamos Http POST request
			this.composeHttpRequest();
			
			// Obtenemos respuesta de la solicitud
			this.getHttpResponse();
*/
		
	return 0;
		
	}

}