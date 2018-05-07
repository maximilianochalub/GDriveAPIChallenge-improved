import javax.swing.JOptionPane;

/**
 * @author Maximiliano Chalub
 * Esta clase agrupa objetos que envian notificación en la interfaz [info boxes]
 *
 */
public class NotificationMessage {

	private int type; 
	private String title;
	private String message;
	
	// Constructor
	public NotificationMessage(int p_type, String p_title, String p_message) {
		
		this.setType(p_type);
		this.setTitle(p_title);
		this.setMessage(p_message);
	
	}
	
	// Setters y getters
	private void setType(int p_type) {
		
		this.type = p_type;
		
	}
	
	private void setTitle(String p_title) {
		
		this.title = p_title;
		
	}
	
	private void setMessage(String p_message) {
		
		this.message = p_message;
		
	}
	
	public int getType() {
		
		return this.type;
		
	}
	
	public String getTitle() {
		
		return this.title;
		
	}
	
	public String getMessage() {
		
		return this.message;
		
	}
	
	/**
	 * Muestra el box creado
	 * Le pasa los atributos mensaje, titulo, y el tipo de mensaje [0-error | 1-info]
	 */
	public void show() {
		
		JOptionPane.showMessageDialog(null, this.getMessage(), this.getTitle(), this.getType());
		
	}
	
}


