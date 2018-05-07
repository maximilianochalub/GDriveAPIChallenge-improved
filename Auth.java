import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Maximiliano Chalub
 * Esta clase valida que la autenticación realiza sea verídica
 * Una vez validada la autenticación procede a realizar la conexión
 */
public class Auth {
	
	public static final String redirectUri = System.getProperty("user.home") + "/Desktop//meliManageAuth.txt", APPLICATION_NAME = "MeliChallenge", pathAuthFile = System.getProperty("user.home") + "/Desktop/", discoveryDocUri = "https://accounts.google.com/.well-known/openid-configuration";
	public static String authorizationCode, accessToken, gmailAccount, googleUserName, clientId, clientSecret, tokenUri, stateTokenCreated, stateTokenReceived, authorizationEndPoint, tokenEndPoint, grantTypeCode, nonce;
	private static final String redirectUri_2 = "https://localhost";
	private static JSONObject clientSecretJson, idToken;
	private static JSONArray accessTokenInfo;
	public static boolean successfullyAuthenticated, successfullyConnected;

	/**
	 * Crea un archivo en la ruta C:\ para manejar la autenticación
	 * @throws IOException 
	 */
	// ver lo de derechos de administrador akl crear en C:\
	public static boolean createFileToManageAuth() {

		// Creamos el archivo
        String[] fileContent = new String[]{"CLIENT_ID", "STATE", "APPLICATION_NAME"};
        File manageAuth = new File(pathAuthFile + "meliManageAuth.txt");
        
        try {
	        manageAuth.createNewFile();
	       
	        // Creamos el archivo html con la response obtenida para autentificar
	        FileWriter fWriter = new FileWriter(manageAuth.getAbsoluteFile());
	        BufferedWriter bWriter = new BufferedWriter(fWriter);
	
	        // Escribimos y cerramos conexion
	        for (String word:fileContent) {
		        bWriter.write(word);
		        bWriter.newLine();
	        }
	        bWriter.close();
	        
	        return true;
        } catch (IOException e) {
        	e.printStackTrace();
        	return false;
        	
        }
        
	}
	
	/**
	 * Obtiene el ID del cliente/usuario tomandolo del archivo cliente_secret.json
	 * @return String ID del usuario
	 */
	private static String getClientID() {
		
		// Obtenemos el archivo client_secret.json que contiene el ID del cliente en formato String
		ClassLoader classloader = Thread.currentThread().getContextClassLoader();
		InputStream is = classloader.getResourceAsStream("client_secret.json");
		StringBuilder sBuilder = new StringBuilder();
		InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
		BufferedReader bReader = new BufferedReader(streamReader);
		try {
			for (String line; (line = bReader.readLine()) != null;) {
			    sBuilder.append(line);
			}
			bReader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String clientSecretString = sBuilder.toString();
		
		System.out.println(clientSecretString);
		
		// Convertimos lo obtenido en formato JSON
		clientSecretJson = new JSONObject(clientSecretString);
		
		// Obtenemos el elemento JSON que almacena el ID del cliente y el client_secret
		clientId = clientSecretJson.getJSONObject("installed").getString("client_id");
		clientSecret = clientSecretJson.getJSONObject("installed").getString("client_secret");
		tokenUri = clientSecretJson.getJSONObject("installed").getString("token_uri");
		
		return clientId;
	
	}
	
	/**
	 * Crea un anti-forgery state token para evitar solicitudes falsas
	 * @throws IOException 
	 */
	private static void generateToken() throws IOException {
		
		  // Creamos el token
		stateTokenCreated = new BigInteger(130, new SecureRandom()).toString(32);
		  
		// Leemos el  archivo html creado con el ID del cliente, el token, y el nombre de la app
		
		Path path = Paths.get(redirectUri);
		Charset charset = StandardCharsets.UTF_8;

		String content = new String(Files.readAllBytes(path), charset);
		content = content.replaceAll("CLIENT_ID", getClientID());
		content = content.replaceAll("STATE", stateTokenCreated);
		content = content.replaceAll("APPLICATION_NAME", APPLICATION_NAME);
		Files.write(path, content.getBytes(charset));

	}
	
	/**
	 * Obtiene los end points del "discovery document" proporcionado por Google
	 * @throws IOException 
	 */
	public static void getEndPoints() throws IOException {

		URL discoveryDocContent = new URL(discoveryDocUri);
		HttpsURLConnection httpDiscoveryDocContent = (HttpsURLConnection) discoveryDocContent.openConnection();
		httpDiscoveryDocContent.setRequestMethod("GET");
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(httpDiscoveryDocContent.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = bReader.readLine()) != null) {
			response.append(inputLine);
		}
		bReader.close();
		
		// Almacenamos la respuesta http
		JSONObject responseHttp = new JSONObject(response.toString());	
		
		authorizationEndPoint = responseHttp.getString("authorization_endpoint");
		tokenEndPoint =  responseHttp.getString("token_endpoint");
	}
	
	/**
	 * Envía la solicitud de autenticación a Google mediante un GET request
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static void sendAuthRequest() throws IOException {
	
		getEndPoints();
		
		generateToken();

		// Valor randomizado que habilita la proteccion frente al ataque de replay
		nonce = new BigInteger(100, new SecureRandom()).toString(32);
		
		String stringAuthURL = authorizationEndPoint;
		// Cargamos los parametros de la request
	    String authURLParams = "client_id=" + getClientID() + "&response_type=code&scope=openid%20email&redirect_uri=" + redirectUri_2 +  "&state=" + stateTokenCreated + "&nonce=" + nonce;
		URL authUrl = new URL(stringAuthURL + "?" + authURLParams);
		HttpsURLConnection authHttpConnection = (HttpsURLConnection) authUrl.openConnection();
		authHttpConnection.setRequestMethod("GET");
		
		authHttpConnection.setDoOutput(true);
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(authHttpConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = bReader.readLine()) != null) {
			response.append(inputLine);
		}
		bReader.close();

		// Almacenamos la respuesta http
		String responseHttp = response.toString();
		
		// Abrimos el browser con el contenido de la response para autenticar
		File htmlFile = new File(pathAuthFile + "loginWithGoogle.html");
		FileWriter fileWriter = new FileWriter(htmlFile);
		fileWriter.write(responseHttp);
		Desktop.getDesktop().browse(htmlFile.toURI());
	
		/**
		 * En esta sección se debería obtener el string de la query de la URI redireccionada por el browser 
		 * posterior a la autenticación del usuario luego de abrir el archivo "loginWithGoogle.html"
		 * Luego tal como se visualiza en el código, se debería obtener el parametro state de esa URI redireccionada
		 * que sería el token recibido luego de la autenticación y compararlo con el random token creado
		 * y así verificar que se trata de una autenticación verídica.
		 */
		//setTokenReceived(responseHttp);
	}
	
	/**
	 * Almacena el token recibido de Google para compararlo con el state token creado 
	 * @return tokenReceived | token recibido por Google
	 */
	public static void setTokenReceived(String responseHttp) {
	
		// Creamos un hashmap que almacene pares <param, valor> de la response
		Map<String, String> results = new HashMap<String, String>();
		// Cargamos el hashmap
		for (String param : responseHttp.split("&")) {
		        String pair[] = param.split("=");
		        if (pair.length > 1) {
		        	results.put(pair[0], pair[1]);
		        }else{
		        	results.put(pair[0], "");
		        }
		    }
		
		// Obtenemos el token recibido por google y el code de la response y almacenamos
		stateTokenReceived = results.get("state");
		authorizationCode = results.get("code");
	}

	/**
	 * Verifica que el token generado y el token recibido por Google sean iguales
	 * Con la verificación asegura la veracidad del usuario redireccionado
	 * @return boolean si ambos token son coincidentes
	 */
	public static boolean verifyToken() {
		
		if (stateTokenCreated.equals(stateTokenReceived)) {
			
			return true;
		
		} else {
			
			return false;
		}
		
	}
	
	
	/**
	 * Obtiene el token del usuario, incluyendo sus propiedades
	 * Envia una solicitud POST al server para obtener dichos datos
	 * @throws IOException 
	 */
	public static void getAccessToken() throws IOException {
		
		// Creamos el metodo POST
		String stringAccessTokenURL = tokenEndPoint;
		// Cargamos los parametros de la request
		URL accessTokenURL = new URL(stringAccessTokenURL);
		HttpsURLConnection accessTokenHttpConnection = (HttpsURLConnection) accessTokenURL.openConnection();
		accessTokenHttpConnection.setRequestMethod("POST");		
		
		// Añadimos headers
		accessTokenHttpConnection.setRequestProperty("Host", "www.googleapis.com");
		accessTokenHttpConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		
		grantTypeCode = new BigInteger(130, new SecureRandom()).toString(32);
		
		// Body de la request
		String bodyRequest = "code=" + authorizationCode + "&client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + redirectUri + "&grant_type=" + grantTypeCode;
		
		// Enviamos la request
		try {
			accessTokenHttpConnection.setDoOutput(true);
			// Escribimos los datos de salida de la solicitud en un Output Stream
			OutputStream outStream = accessTokenHttpConnection.getOutputStream();
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8");
			// Escribimos el string del body
			outStreamWriter.write(bodyRequest);
			// Limpiamos
			outStreamWriter.flush();
			// Cerramos los streams
			outStreamWriter.close();
			outStream.close();
		} catch (IOException io) {
			
		}
		
		// Creamos un stream para leer la response
		BufferedReader responseRead = new BufferedReader(new InputStreamReader(accessTokenHttpConnection.getInputStream()));
				
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
		
		accessTokenInfo = new JSONArray(response);
		
		accessToken = accessTokenInfo.getString(0);
		idToken = new JSONObject(accessTokenInfo.getString(1));
		googleUserName = idToken.getString("name");
		gmailAccount = idToken.getString("email");

	}
	
	/**
	 * Valida el ID token 
	 * @return
	 * @throws IOException 
	 */
	private static boolean validateIdToken() throws IOException {
		
		// Declaramos las variables de validacion
		boolean tokenIsGoogleIssued = false;
		boolean issIsValid = false;
		boolean audIsValid = false;
		boolean expIsValid = false;
		
		String iss = idToken.getString("iss");
		String aud = idToken.getString("aud");
		long exp = Long.parseLong(idToken.getString("exp"));
	
		// Verificamos que el ID token sea un google-issued token 
		String stGoogleIssuedTokenURL = "https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idToken;
		// Cargamos los parametros de la request
		URL googleIssuedTokenURL = new URL(stGoogleIssuedTokenURL);
		HttpsURLConnection googleIssuedHttpConnection = (HttpsURLConnection) googleIssuedTokenURL.openConnection();
		googleIssuedHttpConnection.setRequestMethod("GET");			
		
		BufferedReader bReader = new BufferedReader(new InputStreamReader(googleIssuedHttpConnection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = bReader.readLine()) != null) {
			response.append(inputLine);
		}
		bReader.close();

		// Almacenamos la respuesta http
		String responseHttp = response.toString();	

		// Si la response devuelve el token en formato JSON coincidente con el id token del usuario
		if (responseHttp.equals(idToken.toString())) {
			
			tokenIsGoogleIssued = true;
			
		} 
		
		// Validamos el valor iss
		if (iss.equals("https://accounts.google.com") || iss.equals("accounts.google.com")) {	
			issIsValid = true;
			
		}
		
		// Validamos el valor aud
		if (aud.equals(clientId)) {	
			audIsValid = true;
			
		}
		
		// Validamos el valor exp
		if (exp > 0) {	
			expIsValid = true;	
			
		}
		
		// Validamos el ID token
		if (tokenIsGoogleIssued && issIsValid && audIsValid && expIsValid) {
			return true;
			
		} else {
			return false;
		}	
	}
	
	/** Método principal que autentica al usuario utilizando OpenIDConnect
	 * Llama a los demás métodos de autenticación
	 * @throws IOException 
	 */
	public static boolean authenticate() throws IOException {
		
		successfullyAuthenticated = false;
		
		// Enviamos la solicitud de autenticación
		sendAuthRequest();
		
		// Verificamos ambos tokens
		if (verifyToken()) {
			
			getAccessToken();
			
			if (validateIdToken()) {
				
				successfullyAuthenticated = true;
			
			}
			
		}
		
		return successfullyAuthenticated;

	}
	
	/**
	 * Valida la autenticación realizada por el usuario
	 * Una vez validada la misma realiza la conexión al usuario con la aplicación
	 * @param flag código de la operación elegida [0 buscar palabra | 1 crear archivo]
	 * @return boolean si la conexión fue exitosa
	 * @throws IOException
	 */
	public static boolean connect(int flag) throws IOException {
		
		successfullyConnected = false;
		
		// if (authenticate()) {
			
			if (DriveConnection.connect(flag)) {
				
				successfullyConnected = true;
				
			}
			
		// }
		
		return successfullyConnected;
		
	}
	
}

