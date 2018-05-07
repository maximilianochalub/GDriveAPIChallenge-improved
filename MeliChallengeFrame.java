import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JRadioButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.GeneralSecurityException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import java.awt.Font;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;
import java.awt.Color;
/**
 * @author Maximiliano Chalub
 * @description Esta clase corresponde al Frame mostrado al ejecutar la aplicación
 */
public class MeliChallengeFrame extends JFrame {

	// Elementos Swing
	// Paneles y subpaneles
	private JPanel pnlMain, pnlDriveConnection, pnlWordSearch, pnlCreateFile; 
	
	// Controles
	public static JLabel lblDriveConnection, lblWordSearch, lblSearchFilter, lblFileMimeType, lblFileYear, lblSearchResult, lblFileTitle, lblFileDescription, lblFileProperties, lblCreatedFileId, lblCreatedFileName, lblCreatedFileDesc, lblWordSearchDescription, lblCreateFileDescription, lblSearchMethod, lblSearchByFile, lblSearchById, lblSearchByIdHttps;
	public static JButton btnDriveDisconnection, btnWordSearch, btnCreateFile;
	public static JRadioButton rdbtnWordSearch, rdbtnCreateFile, rdbtnSearchById, rdbtnSearchByFile, rdbtnSearchByIdHttps;
	public static JComboBox cbxFileMimeType, cbxFileYear;
	public static JTextField txtWordSearch, txtFileTitle, txtFileDescription;
	public final ButtonGroup btnGroupChooseOperation, btnGroupChooseSearchMethod;
	
	// Variables para gestionar las operaciones a realizar
	public int operationSelected, fileMimeType, fileYear, searchMethodSelected;
	String word, fileTitle, fileDescription;
	
	// Variable indicadora de si seleccionaron archivos 
	public static byte searchResult;
	
	/**
	 * Ejecuta la aplicacion
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MeliChallengeFrame frame = new MeliChallengeFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Crea el frame con sus componentes
	 */
	public MeliChallengeFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 621, 700);
	
		// Creamos panel principal dentro del frame
		pnlMain = new JPanel();
		pnlMain.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Seteamos layout en null para mover controles
		pnlMain.setLayout(null);
		setContentPane(pnlMain);
		
		// Creamos demas panels
		pnlDriveConnection = new JPanel();
		pnlWordSearch = new JPanel(); //panel para ejecutar la operación "Buscar palabra"
		pnlCreateFile = new JPanel(); //panel para ejecutar la operación "Crear archivo"
		
		// Seteamos layouts en null para mover controles
		pnlDriveConnection.setLayout(null);
		pnlWordSearch.setLayout(null);
		pnlCreateFile.setLayout(null);
		
		// Especificamos orientación y tamaño de los panels
		pnlDriveConnection.setBounds(10, 11, 583, 145);
		pnlWordSearch.setBounds(10, 167, 583, 315);
		pnlCreateFile.setBounds(10, 493, 583, 160);
		
		// Añadimos paneles-subpaneles
		pnlMain.add(pnlDriveConnection);
		pnlMain.add(pnlWordSearch);
		pnlMain.add(pnlCreateFile);
		
        // Establecemos los titulos de los panels
        setTitulo("Inicio", pnlDriveConnection);
        setTitulo("Buscar palabra en un documento", pnlWordSearch);
        setTitulo("Crear archivo en Drive", pnlCreateFile);
		
		// Creamos demas componentes swing para los frames
		lblDriveConnection = new JLabel("Autentique y proporcione permisos a su cuenta de Google para comenzar");
		lblWordSearch = new JLabel("Ingresar palabra");
		lblWordSearchDescription = new JLabel("Ingresar una palabra y buscarla en el contenido de un archivo eligiendo un método de selección del archivo\r\n");
		lblCreateFileDescription = new JLabel("Ingresar título y descripción de un archivo y crearlo en la cuenta de Google Drive del usuario");
		lblSearchFilter = new JLabel("Filtro de búsqueda");
		lblFileMimeType = new JLabel("Tipo de archivo");
		lblSearchMethod = new JLabel("Método de selección");
		lblSearchByFile = new JLabel("Selección aleatoria de un archivo de los que coinciden con el filtro especificado");
		lblSearchById = new JLabel("Selección de un archivo con su ID de los que coinciden con el filtro vía el método getWebContentLink()");
		lblFileYear = new JLabel("Año de creación");
		lblFileTitle = new JLabel("Título");
		lblFileDescription = new JLabel("Descripción");	
		lblCreatedFileId = new JLabel("");
		lblCreatedFileName = new JLabel("");
		lblCreatedFileDesc = new JLabel("");
		lblFileProperties = new JLabel("Propiedades del archivo creado: ");
		lblFileProperties.setEnabled(false);
		lblSearchResult = new JLabel("");
		lblSearchByIdHttps = new JLabel("Selección de un archivo con su ID de los que coinciden con el filtro vía una https request");
	
		cbxFileMimeType = new JComboBox();
		cbxFileYear = new JComboBox();
		
		cbxFileMimeType.addItem("Imagenes .png, .jpg, .bmp, etc."); 
		cbxFileMimeType.addItem("Documentos MS (.docx, .xslx, .pptx)"); 
		cbxFileMimeType.addItem("Documentos de Adobe (.pdf)"); 
		cbxFileMimeType.addItem("Formularios (Google Forms)"); 
		cbxFileMimeType.addItem("Otros (folders, audio, video, scripts)"); 
		
		cbxFileYear.addItem("2018"); 
		cbxFileYear.addItem("2017"); 
		cbxFileYear.addItem("2016"); 
		cbxFileYear.addItem("2015"); 
		cbxFileYear.addItem("2014 o anterior"); 

		rdbtnWordSearch = new JRadioButton("Autenticar para buscar palabra");
		rdbtnCreateFile = new JRadioButton("Autenticar para crear archivo");
		rdbtnSearchById = new JRadioButton("Obtener contenido del archivo por ID con getWebContentLink()");
		rdbtnSearchByFile = new JRadioButton("Obtener contenido del archivo con getWebContentLink()");
		rdbtnSearchByIdHttps = new JRadioButton("Obtener contenido del archivo por ID con GET https request");
		
		btnGroupChooseOperation = new ButtonGroup();
		btnGroupChooseSearchMethod = new ButtonGroup();
		
		btnGroupChooseOperation.add(rdbtnWordSearch);
		btnGroupChooseOperation.add(rdbtnCreateFile);	
		btnGroupChooseOperation.add(rdbtnWordSearch);
		btnGroupChooseOperation.add(rdbtnCreateFile);
		btnGroupChooseSearchMethod.add(rdbtnSearchByFile);
		btnGroupChooseSearchMethod.add(rdbtnSearchById);
		btnGroupChooseSearchMethod.add(rdbtnSearchByIdHttps);
		
		btnWordSearch = new JButton("Buscar");
		btnCreateFile = new JButton("Crear archivo");
	
		txtWordSearch = new JTextField();
		txtFileTitle = new JTextField();
		txtFileDescription = new JTextField();

		// Establecemos un grupo de botones y añadimos

		lblDriveConnection.setBounds(10, 23, 533, 23);
		lblSearchFilter.setBounds(10, 27, 128, 14);
		lblWordSearchDescription.setBounds(32, 74, 451, 14);
		lblCreateFileDescription.setBounds(32, 117, 451, 14);
		lblSearchMethod.setBounds(10, 104, 225, 14);
		cbxFileMimeType.setBounds(107, 52, 221, 23);
		cbxFileYear.setBounds(442, 52, 113, 23);
		lblFileMimeType.setBounds(10, 56, 107, 14);
		lblFileYear.setBounds(339, 56, 98, 14);
		btnWordSearch.setBounds(245, 256, 83, 23);
		lblSearchResult.setBounds(10, 290, 371, 14);
		txtWordSearch.setBounds(107, 256, 128, 23);
		lblWordSearch.setBounds(10, 260, 210, 14);
		lblFileTitle.setBounds(10, 26, 139, 14);
		lblFileDescription.setBounds(204, 26, 68, 14);
		txtFileDescription.setBounds(277, 23, 204, 23);
		txtFileTitle.setBounds(52, 22, 128, 23);
		btnCreateFile.setBounds(364, 51, 117, 23);
		lblCreatedFileId.setBounds(10, 85, 469, 14);
		lblCreatedFileName.setBounds(10, 105, 494, 14);
		lblSearchByFile.setBounds(153, 140, 327, 14);
		lblSearchById.setBounds(153, 182, 427, 14);
		lblCreatedFileDesc.setBounds(10, 125, 494, 14);
		lblFileProperties.setBounds(10, 55, 196, 14);
		rdbtnSearchById.setBounds(129, 160, 437, 23);
		rdbtnSearchByFile.setBounds(129, 114, 426, 29);
		rdbtnWordSearch.setBounds(10, 53, 208, 18);
		rdbtnCreateFile.setBounds(10, 95, 234, 23);
		rdbtnSearchByIdHttps.setBounds(129, 201, 426, 23);
		lblSearchByIdHttps.setBounds(153, 223, 402, 14);
	
		lblWordSearchDescription.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblCreateFileDescription.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblSearchByFile.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblSearchById.setFont(new Font("Tahoma", Font.PLAIN, 9));

		pnlDriveConnection.add(lblWordSearchDescription);
		pnlDriveConnection.add(lblCreateFileDescription);
		pnlWordSearch.add(lblSearchByFile);
		pnlWordSearch.add(lblSearchById);
		pnlDriveConnection.add(lblDriveConnection);
		pnlDriveConnection.add(rdbtnWordSearch);
		pnlDriveConnection.add(rdbtnCreateFile);
		pnlWordSearch.add(btnWordSearch);
		pnlWordSearch.add(txtWordSearch);
		pnlWordSearch.add(lblSearchFilter);
		pnlWordSearch.add(cbxFileMimeType);
	    pnlWordSearch.add(cbxFileYear);
		pnlWordSearch.add(lblFileMimeType);
		pnlWordSearch.add(lblFileYear);
		pnlWordSearch.add(lblWordSearch);
		pnlWordSearch.add(lblSearchResult);
		pnlWordSearch.add(rdbtnSearchById);
		pnlWordSearch.add(rdbtnSearchByFile);
		pnlWordSearch.add(rdbtnSearchByIdHttps);
		pnlWordSearch.add(lblSearchMethod);
		
		lblSearchByIdHttps.setFont(new Font("Tahoma", Font.PLAIN, 9));
		
		pnlWordSearch.add(lblSearchByIdHttps);
		pnlCreateFile.add(lblFileTitle);
		pnlCreateFile.add(lblFileDescription);
		pnlCreateFile.add(txtFileDescription);
		pnlCreateFile.add(txtFileTitle);
		pnlCreateFile.add(btnCreateFile);
        pnlCreateFile.add(lblFileDescription);
        pnlCreateFile.add(lblCreatedFileId);
        pnlCreateFile.add(lblCreatedFileName);
        pnlCreateFile.add(lblCreatedFileDesc);
        pnlCreateFile.add(lblFileProperties);

        // Estado inicial de componentes Swing
        initState();
        
		// Eventos para los componentes Swing
        // --> Click sobre una opcion en el Radio Button Group [seleccionar operacion]
		ActionListener al = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				
				// Si se selecciona "Buscar palabra"
				if (rdbtnWordSearch.isSelected()) {
					
					operationSelected = 0; // Buscar palabra operacion ID 0
					enableOptionsSearchMethod(true);
					enableCreateFile(false);	
					lblSearchResult.setText("Status:");
					connect();
                } 
				// Si se selecciona "Crear archivo"
				else if (rdbtnCreateFile.isSelected()) {
					
					operationSelected = 1; // Crear archivo operacion ID 1
					enableAllWordSearch(false);   
       				enableCreateFile(true); 
       				lblSearchResult.setText("Status:");
       				lblSearchResult.setEnabled(false);
       				connect();
					
                } 
               }
           };
           
           rdbtnWordSearch.addActionListener(al);
           rdbtnCreateFile.addActionListener(al);
          
        // --> Click sobre una opcion en el Radio Button Group [seleccionar metodo de busqueda]
       	ActionListener al2 = new ActionListener(){
			public void actionPerformed(ActionEvent ae) {
				
				 if (rdbtnSearchByFile.isSelected()) {
                	enableWordSearch(true);
                	searchMethodSelected = 0;
                
                } else if (rdbtnSearchById.isSelected()) {
                	enableWordSearch(true);
                	searchMethodSelected = 1;             	
                } else {
                	enableWordSearch(true);
                	searchMethodSelected = 2;  
                }				 
				 
               }
           };

           rdbtnSearchByFile.addActionListener(al2);
           rdbtnSearchById.addActionListener(al2);
           rdbtnSearchByIdHttps.addActionListener(al2);
           
   		// Obtenemos los indices de las opciones seleccionadas
        cbxFileMimeType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				fileMimeType = cbxFileMimeType.getSelectedIndex();
				
			}
		});
        
        cbxFileYear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				fileYear = cbxFileYear.getSelectedIndex();
				
			}
		});
           
        // --> Click sobre el boton "Buscar palabra"
        btnWordSearch.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				// Verificamos que no este vacio el campo
				if (!(isEmpty(txtWordSearch))) {
		
					// En una nueva busqueda ponemos en blanco la label de resultado
					lblSearchResult.setText("");
					
					// Instanciamos objeto de la clase WordSearch y le pasamos la palabra		
					WordSearch ws = new WordSearch(txtWordSearch.getText(), searchMethodSelected);								
					
					// Verificamos el resultado de la busqueda e informamos el status
					try {
						
						if (ws.execute(fileMimeType, fileYear)) {
							
							NotificationMessage notifMsg = new NotificationMessage(1, "Resultado de la búsqueda", "Se ha localizado la palabra buscada!");
							notifMsg.show();
							lblSearchResult.setEnabled(true);
							lblSearchResult.setText("Status: 200 OK - Palabra encontrada");
							
						} else {	
							
							if (searchResult != 1) {
							
								NotificationMessage notifMsg = new NotificationMessage(1, "Resultado de la búsqueda", "No se ha localizado la palabra buscada");
								notifMsg.show();
								lblSearchResult.setEnabled(true);
								lblSearchResult.setText("Status: 404 NOT FOUND - Palabra no encontrada");
							
							}
		
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}				
					
				} 
			}
		});
        
         // --> Click sobre el boton "Crear archivo"
		btnCreateFile.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				
				if (!(isEmpty(txtFileTitle)) || !isEmpty(txtFileDescription)) {
					
					if (txtFileTitle.getText().length() <= 40 && txtFileDescription.getText().length() <= 80) {
						
						// Almacenamos metadata del archivo a crear
						fileTitle = txtFileTitle.getText();
						fileDescription = txtFileDescription.getText();
						
						FileCreation newFileCreation = new FileCreation(fileTitle, fileDescription);
						
						// Creamos el archivo
						try {
							newFileCreation.createFile();
							
							NotificationMessage notifMsg = new NotificationMessage(1, "Creación del archivo", "Se ha creado correctamente el archivo con los datos ingresados.\n Visualice sus propiedades en la ventana de la aplicación");
							notifMsg.show();	
							
							lblCreatedFileId.setText("ID: " + FileCreation.fileId);
							lblCreatedFileName.setText("Título: " + fileTitle);
							lblCreatedFileDesc.setText("Descripción: " + fileDescription);
							lblFileProperties.setEnabled(true);
							
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					
					} else {
							
						NotificationMessage notifMsg = new NotificationMessage(0, "Completar título y nombre de archivo", "Se ha superado el límite máximo de caracteres ingresados en alguno de los campos.\nIntente nuevamente.\n(máx. título: 40 caracteres)\n(máx. descripción: 80 caracteres)");
						notifMsg.show();							
						
					}
					
				}

			}
		});
	}
	
	/**
	 * Conecta al usuario con la app
	 */
	public void connect() {
	
		// Si la conexion fue exitosa
		try {

			// Verificamos si podemos crear el archivo para autenticación
			if (Auth.createFileToManageAuth()) {

				if (Auth.connect(operationSelected)) {
					// Informamos al usuario
					
					if (operationSelected == 0) {

						NotificationMessage notifMsg = new NotificationMessage(1, "Resultado de la conexión", "Busqueda palabra\n\nHa sincronizado exitosamente su cuenta de Google Drive con la aplicación!\nPresione \"Aceptar\" para comenzar a utilizarla");
						notifMsg.show();
					
					} else {

						NotificationMessage notifMsg = new NotificationMessage(1, "Resultado de la conexión", "Creación de un archivo\n\nHa sincronizado exitosamente su cuenta de Google Drive con la aplicación!\nPresione \"Aceptar\" para comenzar a utilizarla");
						notifMsg.show();
					}
	
					// Habilitamos opciones para seleccionar la operación deseada
					enableOptions(true);
					
				// Conexion no exitosa - problema en la conexion
				} else if (Auth.successfullyAuthenticated) {
					// Informamos

					NotificationMessage notifMsg = new NotificationMessage(0, "Resultado de la conexión", "Error al conectar\n Intente nuevamente");
					notifMsg.show();	
				// Conexion no exitosa - problema en la autenticacion
				} else {
					// Informamos
		
					NotificationMessage notifMsg = new NotificationMessage(0, "Resultado de la conexión - Error de autenticación", "Error al autenticar al usuario'\n Intente nuevamente");
					notifMsg.show();	
				}
				
			} else {
				// Informamos

				NotificationMessage notifMsg = new NotificationMessage(0, "Error al intentar autenticar", "Error al intentar crear archivo temporal en su directorio C:\\ \nVerifique que tenga permisos de administrador para crear o copiar archivos en esa ruta e intentelo nuevamente");
				notifMsg.show();	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	/**
	 * Setea los controles en su estado inicial
	 */
	public void initState() {
	
		enableOptions(true);
        enableWordSearch(false);
        enableCreateFile(false);
        enableAllWordSearch(false); 
        lblSearchResult.setEnabled(false);
        lblSearchResult.setText("Status:");
	}

    /**
     * Verifica si el textfield enviado como parametro esta vacio
     * @param txtField
     * @return boolean si esta vacio el txtfield
     */
	public boolean isEmpty(JTextField txtField) {
		
		if (txtField.getText().isEmpty()) {

			// Informamos al usuario
			NotificationMessage notifMsg = new NotificationMessage(1, "Campos vacíos", "Complete el campo para poder continuar");
			notifMsg.show();
			return true;
			
		} else {
			return false;
			
		}

	}

	/**
	 * Establece el titulo del jpanel
	 * @param titulo el titulo a mostrar
	 * @param pnl el jpanel donde se mostrara
	 */
	public void setTitulo(String titulo, JPanel pnl) {
		
		Border border = BorderFactory.createTitledBorder(titulo);
		pnl.setBorder(border);

	}
   
    /**
     * Habilita o deshabilita las opciones para realizar las operaciones
     * @param boolean e que indica si deben habilitarse o deshabilitarse
     */   
    public void enableOptions(boolean e) {
    	
		rdbtnWordSearch.setEnabled(e);
		rdbtnCreateFile.setEnabled(e);
    }
    
    /**
     * Habilita o deshabilita los controles para la busqueda de palabra
     * @param e que indica si deben habilitarse o deshabilitarse
     */
    public void enableWordSearch(boolean e) {
    
		btnWordSearch.setEnabled(e);
		txtWordSearch.setEnabled(e);
		txtWordSearch.setText("");		
    }
    
    /**
     * Habilita o deshabilita las opciones de método de búsqueda
     * @param e
     */
    public void enableOptionsSearchMethod(boolean e) {
    	
		rdbtnSearchByFile.setEnabled(e);
		rdbtnSearchById.setEnabled(e);
		rdbtnSearchByIdHttps.setEnabled(e);
		cbxFileMimeType.setEnabled(e);
		cbxFileYear.setEnabled(e);
    }
    
    /**
     * Habilita o deshabilita los controles del panel de la operacion "Buscar palabra"
     * @param boolean e que indica si deben habilitarse o deshabilitarse
     */   
    public void enableAllWordSearch(boolean e) {

		btnWordSearch.setEnabled(e);
		txtWordSearch.setEnabled(e);
		txtWordSearch.setText("");
		cbxFileMimeType.setEnabled(e);
		cbxFileYear.setEnabled(e);
		rdbtnSearchByFile.setEnabled(e);
		rdbtnSearchById.setEnabled(e);	
		rdbtnSearchByIdHttps.setEnabled(e);
    }
    
    /**
     * Habilita o deshabilita los controles del panel de la operacion "Crear archivo"
     * @param boolean e que indica si deben habilitarse o deshabilitarse
     */   
    public void enableCreateFile(boolean e) {

		txtFileTitle.setEnabled(e);
		txtFileTitle.setText("");
		txtFileDescription.setEnabled(e);
		txtFileDescription.setText("");
		btnCreateFile.setEnabled(e);	
    }
}
