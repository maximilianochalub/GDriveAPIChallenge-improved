import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.Random;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;

/**
 * @author Maximiliano Chalub
 * Esta clase agrupa objetos de búsqueda de palabras en el contenido de un archivo previamente seleccionado
 */
public class WordSearch {
	
	// Atributos
	private ArrayList<String> selectedFileIds; // Array de Ids de archivos para seleccionar uno aleatorio y buscar contenido por ID
	private String word;
	private InputStream iStream;
	private ArrayList<File> selectedFiles; // Array de los archivos para seleccionar uno aleatorio y buscar su contenido
	private int searchMethod; // 0- metodo buscar por archivo usando el arraylist [selectedFiles] | 1- metodo buscar por ID usando el arraylist [selectedFileIds]
    // private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36";
	
	// Constructor
	public WordSearch(String p_word, int p_searchMethod) {
		
		this.setSelectedFiles(new ArrayList<File>());
		this.setSelectedFileIds(new ArrayList<String>());
		this.setSearchMethod(p_searchMethod);
		this.setWord(p_word);
		
	}

	// Setters y getters
	private void setSelectedFiles(ArrayList<File> p_selectedFiles) {
		
		this.selectedFiles = p_selectedFiles;
		
	}
	
	private void setSelectedFileIds(ArrayList<String> p_selectedFileIds) {
		
		this.selectedFileIds = p_selectedFileIds;
		
	}
	
	private void setWord(String p_word) {
		
		this.word = p_word;
	}
	
	private void setSearchMethod(int p_searchMethod) {
		
		this.searchMethod = p_searchMethod;
		
	}
	
	public ArrayList<File> getSelectedFiles() {
		
		return this.selectedFiles;
		
	}
	
	public ArrayList<String> getSelectedFileIds() {
		
		return this.selectedFileIds;
		
	}
	
	public String getWord() {
		
		return this.word;
		
	}
	
	public InputStream getIStream() {
		
		return this.iStream;
		
	}
	
	public int getSearchMethod() {
		
		return this.searchMethod;
		
	}
	
    /**
     * Busca la palabra proporcionada en el archivo con ID elegido llamando a todos los metodos correspondientes
     * @throws IOException
     */
	public boolean execute(int p_mimeTypeCode, int p_dateRangeCode) throws IOException {
		
		// Verificamos si se ha filtrado al menos un archivo con los criterios de busqueda seleccionados
		if (this.selectFiles(p_mimeTypeCode, p_dateRangeCode)) {

			// Verificamos si se puede obtener el contenido del mismo
			if (getFileContent()) {
	
				MeliChallengeFrame.searchResult = 0;
				// Buscamos la palabra en el contenido del archivo y devolvemos el valor booleano si lo ha encontrado
				return searchForWordInSelectedFile();
				
			} else {
				
				// Informamos al usuario y almacenamos valor en variable para el JFrame
				NotificationMessage notifMsg = new NotificationMessage(0, "Error al obtener contenido del archivo", "No es posible obtener el contenido del archivo seleccionado.\nIntente nuevamente");
				notifMsg.show();
				MeliChallengeFrame.searchResult = 1;
			
				return false;
			}
			
		} else {
			
			// Informamos al usuario y almacenamos valor en variable para el JFrame
			NotificationMessage notifMsg = new NotificationMessage(1, "Resultado de búsqueda", "No se ha localizado ningun archivo según el criterio de búsqueda.\nIntente con otras opciones del filtro");
			notifMsg.show();
			MeliChallengeFrame.searchResult = 1;
			
			return false;
			
		}

	}
	
	
	/**
	 * Selecciona todos los archivos que coinciden con el filtro de busqueda (query)
	 * @throws IOException 
	 * @returns boolean
	 */
	public boolean selectFiles(int mimeTypeCode, int dateRangeCode) throws IOException {

		// Realizamos la peticion
		String pageToken = null;
		do {
			FileList result = DriveConnection.driveService.files().list()
			  .setQ(instantiateQuery(mimeTypeCode, dateRangeCode)) // Le enviamos como parametro la query resultante
			  .setSpaces("drive")
		      .setFields("nextPageToken, files(id, name, webContentLink)") // Obtenemos IDs, name y los webContentLink para acceder a su contenido
		      .setPageToken(pageToken)
		      .execute();

			// Recorremos los IDs obtenidos y los almacenamos en el ArrayList 
			if (this.getSearchMethod() % 2 == 0) { // Si el metodo de busqueda es el 0 o el 2 
				
				for (File file : result.getFiles()) {  
					this.getSelectedFiles().add(file); 
				}
				
			} else {
				
				for (File file : result.getFiles()) {  
					this.getSelectedFileIds().add(file.getId());
			    }
				
			}
			
		  pageToken = result.getNextPageToken();
		} while (pageToken != null);
		
		// Determinamos si hubo algun archivo fltrado
		if (this.getSelectedFileIds().size() == 0 && this.getSelectedFiles().size() == 0) {		
			
			return false;
			
		} else {
	
			return true;
		}
	}
	

	/**
	 * Selecciona un archivo aleatoriamente dentro de los que coincidieron con el criterio de busqueda
	 * Obtiene el contenido del tal archivo seleccionado
	 * @return boolean true si el contenido del archivo es encontrado y false en caso contrario
	 * @throws IOException 
	 */
	private boolean getFileContent() throws IOException {
		
		// Creamos un random para seleccionar un archivo en los metodos (identificados con getSearchMethod() == 0 y getSearchMethod() == 1)
		int indexFile;
		Random randomIdFile = new Random();
		File fileToSearch;
	
		if (this.getSearchMethod() == 0 || this.getSearchMethod() == 1) {
			
			if (this.getSearchMethod() == 0) { // Metodo buscar por archivo completo con getWebContentLink()
				
				 indexFile = randomIdFile.nextInt(this.getSelectedFiles().size());
				 fileToSearch = this.getSelectedFiles().get(indexFile);
				 NotificationMessage notifMsg = new NotificationMessage(1, "Proceso de búsqueda", "Se ha seleccionado 1 de " + Integer.toString(this.getSelectedFiles().size()) + " archivos.\n\nArchivo seleccionado:\n\nID: " + fileToSearch.getId() + "\nNombre: " + fileToSearch.getName());
				 notifMsg.show();				
				
			} else { // Metodo buscar por archivo con su ID con webGetContentLink()
				 
				indexFile = randomIdFile.nextInt(this.getSelectedFileIds().size());
				fileToSearch = DriveConnection.driveService.files().get(this.getSelectedFileIds().get(indexFile)).execute();
				NotificationMessage notifMsg = new NotificationMessage(1, "Proceso de búsqueda", "Se ha seleccionado 1 de " + Integer.toString(this.getSelectedFileIds().size()) + " archivos.\n\nArchivo seleccionado:\n\nID: " + fileToSearch.getId() + "\nNombre: " + fileToSearch.getName());
				// Informamos al usuario y almacenamos valor en variable para el JFrame
				notifMsg.show();		

			}
			
			// Para ambos metodos de busqueda se aplica la busqueda con getWebContentLink()
			if (fileToSearch.getWebContentLink() != null && fileToSearch.getWebContentLink().length() > 0) {
				
				try {
					// Conformamos respuesta http y ejecutamos
					HttpResponse responseHttp = DriveConnection.driveService.getRequestFactory().buildGetRequest(new GenericUrl(this.getSelectedFiles().get(indexFile).getWebContentLink()))
							.execute();
					// Almacenamos respuesta en un InputStream
					iStream = responseHttp.getContent();
						
				} catch (HttpResponseException e) {
					// Informamos al usuario y almacenamos valor en variable para el JFrame
					NotificationMessage notifMsgExc = new NotificationMessage(0, "Buscar palabra", "Se ha producido un error en la solicitud http.\nIntente nuevamente");
					notifMsgExc.show();
					MeliChallengeFrame.lblSearchResult.setText("");
					e.printStackTrace();
				}
					
				return true;
					
			} else { // Informamos que el webContentLink no fue posible obtener
					
				return false;
				
			}

		} else { // Metodo buscar por ID enviando una https request
			
			indexFile = randomIdFile.nextInt(this.getSelectedFiles().size());
			fileToSearch = this.getSelectedFiles().get(indexFile);
			NotificationMessage notifMsg = new NotificationMessage(1, "Proceso de búsqueda", "Se ha seleccionado 1 de " + Integer.toString(this.getSelectedFiles().size()) + " archivos.\n\nArchivo seleccionado:\n\nID: " + fileToSearch.getId() + "\nNombre: " + fileToSearch.getName());
			notifMsg.show();	
			String uriViewContentFile = "https://www.googleapis.com/drive/v3/files?fileId=" + fileToSearch.getId();
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(uriViewContentFile);

			// add request header
			// request.addHeader("User-Agent", USER_AGENT);

			try {
			
				org.apache.http.HttpResponse response = client.execute(request);
			
				iStream = response.getEntity().getContent();
			
				return true;
				
			} catch (IOException e) {
				
				// Error en la solicitud http, devolvemos valor boolean para informar
				return false;
				
			}
			
			/**
			 * *****Alternativa a realizar la request*****
			HttpsURLConnection httpsurlGetFileContent = (HttpsURLConnection) urlGetFileContent.openConnection();
			httpsurlGetFileContent.setRequestMethod("GET");
			httpsurlGetFileContent.setRequestProperty("User-Agent", USER_AGENT);
			httpsurlGetFileContent.connect();
			
			iStream = httpsurlGetFileContent.getInputStream();	
			
			if (httpsurlGetFileContent.getResponseCode() != 200) {
				
				return false;
				
			} else {
				
				return true;
				
			}
			*/

		}
 	
	} 
	
	/**
	 * Instancia un objeto de la clase Query para su futura composicion
	 * Recibe como parametros los indices de los items seleccionados en los comboboxes de filtro
	 * @param mimeTypeCode el codigo de mimeType que indica el tipo de archivo seleccionado en el combobox
	 * @param dateRangeCode el codigo de dateRange que indica el año seleccionado en el combobox
	 * @return la Query creada con un metodo de la clase APIQuery
	 */
	public String instantiateQuery(int mimeTypeCode, int dateRangeCode) {
		
		// Inicializamos los componentes donde almacenaremos los valores a filtrar
		ArrayList<String> mimeTypes = new ArrayList<String>();
		String startDate = "";
		String endDate = "";
		
		// Analizamos los criterios de filtrado seleccionados por el usuario
		// Tipo de archivo
		switch (mimeTypeCode) {
		
			case 0:
				mimeTypes.add("image/jpeg");
				mimeTypes.add("image/png");
				mimeTypes.add("image/bmp");				
				mimeTypes.add("image/gif");
			break;
	
			case 1:		
				mimeTypes.add("application/vnd.google-apps.presentation");
				mimeTypes.add("application/vnd.google-apps.spreadsheet");
				mimeTypes.add("application/vnd.google-apps.document");				
			break;			
	
			case 2:
				mimeTypes.add("application/pdf");
			break;		
			
			case 3:
				mimeTypes.add("application/vnd.google-apps.form");
			break;				
			
			case 4:
				mimeTypes.add("application/vnd.google-apps.audio");
				mimeTypes.add("application/vnd.google-apps.video");
				mimeTypes.add("application/vnd.google-apps.script");
				mimeTypes.add("application/vnd.google-apps.folder");
			break;						
		
		}
		
		// Fecha de creacion
		switch (dateRangeCode) {

			case 0:
				startDate = "2018-01-01T12:00:00";
			break;
	
			case 1:
				startDate = "2017-01-01T12:00:00";
				endDate = "2018-01-01T12:00:00";
			break;			
	
			case 2:
				startDate = "2016-01-01T12:00:00";
				endDate = "2017-01-01T12:00:00";
			break;		
			
			case 3:
				startDate = "2015-01-01T12:00:00";
				endDate = "2016-01-01T12:00:00";
			break;				
			
			case 4:
				endDate = "2015-01-01T12:00:00";
			break;			
	
		}
		
		// Instanciamos y creamos la Query
		APIQuery nuevaQuery = new APIQuery(mimeTypes, startDate, endDate);
		
		return nuevaQuery.compose();

	}
	
	/**
	 * Busca la palabra en el contenido del archivo
	 * @return boolean si ha encontrado la palabra en el contenido del archivo seleccionado
	 */
	public boolean searchForWordInSelectedFile() {
		InputStreamReader in = new InputStreamReader(iStream);
		BufferedReader bReader = new BufferedReader(in);
		// Creamos un buffer de String para conformar el contenido
		StringBuilder sBuilder = new StringBuilder();
		// Creamos variable que almacenara por linea los resultados del buffer
		String bufferLine = "";
		
		// Confomamos contenido del archivo
		try {
			while ((bufferLine = bReader.readLine()) != null) {
				sBuilder.append(bufferLine);	
			}
			bReader.close();
			return sBuilder.toString().contains(word);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	
	}
	
}
