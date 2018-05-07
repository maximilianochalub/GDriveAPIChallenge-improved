import java.util.ArrayList;

/**
 * @author Maximiliano Chalub
 * @description Esta clase agrupa objetos para realizar el filtrado de búsqueda en archivos de la cuenta Drive
 * Los objetos agrupados por esta clase son Queries realizadas sobre la API
 */
public class APIQuery {
	
	// Atributo que contiene los posibles mimeTypes por los cuales consultar
	private ArrayList<String> mimeTypes;
	// Atributos con fechas de busqueda que definen un intervalo especifico
	private String startDate, endDate;

	// Constructor de la clase
	public APIQuery(ArrayList<String> p_mimeTypes, String p_startDate, String p_endDate) {
		
		this.setMimeTypes(p_mimeTypes);
		this.setStartDate(p_startDate);
		this.setEndDate(p_endDate);
		
	}
	
	// Definimos setters y getters
	private void setMimeTypes(ArrayList<String> p_mimeTypes) {
		
		this.mimeTypes = p_mimeTypes;
		
	}
	
	private void setStartDate(String p_startDate) {
		
		this.startDate = p_startDate;
		
	}
	
	private void setEndDate(String p_endDate) {
		
		this.endDate = p_endDate;
		
	}
	
	public ArrayList<String> getMimeTypes() {
		
		return this.mimeTypes;
		
	}
	
	public String getStartDate() {
		
		return this.startDate;
		
	}
	
	public String getEndDate() {
		
		return this.endDate;
		
	}
	
	/**
	 * Conforma la query segun el/los mimetypes y las fechas
	 * @return la query resultante
	 * 
	 */
	public String compose() {
		
		// Variable que almacenara la query
		String finalQuery = "";
		
		// Para el atributo mimeTypes
		for (int i = 0; i < this.getMimeTypes().size(); i++) {
				
			if (i == 0) {
					
				finalQuery = "(mimeType = '" + this.getMimeTypes().get(i) + "'";
					
			} else {
					
				finalQuery += " or mimeType = '" + this.getMimeTypes().get(i) + "'";
					
			}
				
		} 
		
		// Conformamos el rango de fecha
		if (this.getEndDate().equals("")) {
			// Caso 1 - solo tenemos la fecha de inicio (se busca desde esa fecha inclusive en adelante)
			finalQuery += ") and (createdTime >= '" + this.getStartDate() + "')";		
		} else if (this.getStartDate().equals("")) {	
			// Caso 2 - solo tenemos la fecha de fin (se busca desde esa fecha para atras)	
			finalQuery += ") and (createdTime < '" + this.getEndDate() + "')";		
		} else  {
			// Caso 3 - tenemos ambas fechas (se busca en el rango desde la fecha de inicio inclusive hasta la fecha fin)
			finalQuery += ") and (createdTime >= '" + this.getStartDate() + "' and createdTime < '" + this.getEndDate() + "')";
		}
		
		// Devolvemos la query completa
		return finalQuery;
			
	}
	
}